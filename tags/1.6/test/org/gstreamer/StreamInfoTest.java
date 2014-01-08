package org.gstreamer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.gstreamer.StreamInfo.StreamType;
import org.gstreamer.elements.PlayBin;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class StreamInfoTest {

	private static File testSrc;

	private PlayBin playBin;

	private StreamInfo videoStream;

	private StreamInfo audioStream;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Gst.init("StreamInfoTest", new String[]{});
		
		final Object sync = "";
		final AtomicBoolean success = new AtomicBoolean();
		final AtomicReference<String> error = new AtomicReference<String>("");
		
		testSrc = File.createTempFile("StreamInfoTest-", ".ogg");
		testSrc.deleteOnExit();
		
		Pipeline p = Pipeline.launch(
				String.format(
						"videotestsrc num-buffers=25 ! theoraenc ! oggmux name=mux ! filesink location=%s " +
						"audiotestsrc num-buffers=44 !  vorbisenc ! mux."
						, testSrc.getAbsolutePath().replace("\\", "\\\\")
						)
						);
		
		Bus bus = p.getBus();
		bus.connect(new Bus.EOS() {
			
			public void endOfStream(GstObject source) {
				synchronized (sync) {
					sync.notify();
				}
				success.set(true);
			}
		});
		
		bus.connect(new Bus.ERROR() {
			
			public void errorMessage(GstObject source, int code, String message) {
				error.set(": " + message);
			}
		});
		
		synchronized (sync) {
			p.play();
			sync.wait(10000);
			p.stop();
		}
		
		if (!success.get()) {
			throw new Exception("could not create test src file" + error.get());
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Gst.deinit();
	}

	@Before
	public void setUp() throws Exception {
		playBin = new PlayBin("playbin", testSrc.toURI());
		Element videoSink = ElementFactory.make("fakesink", "videosink");
		Element audioSink = ElementFactory.make("fakesink", "audiosink");
		
		playBin.setVideoSink(videoSink);
		playBin.setAudioSink(audioSink);
		
		final State state = State.PAUSED;
		final long timeout = 1000;
		
		waitState(state, timeout);
		
		StreamInfo[] streams = getStreamInfo();

		videoStream = streams[0];
		audioStream = streams[1];
		
	}

	private StreamInfo[] /* [video, audio] */ getStreamInfo() throws Exception {
		List<StreamInfo> list = playBin.getStreamInfo();
		
		assertEquals(2, list.size());

		StreamInfo s1 = list.get(0);
		StreamInfo s2 = list.get(1);

		String codec1 = s1.getCodec();
		String codec2 = s2.getCodec();
		
		StreamInfo video = "Theora".equals(codec1) ? s1 : s2;
		StreamInfo audio = "Vorbis".equals(codec2) ? s2 : s1;
		
		return new StreamInfo[] {video, audio};
		
	}
	
	private void waitState(final State state, final long timeout)
			throws Exception {
		
		Exception exception = new Exception(String.format("playbin refuses to go into the '%s' state", state));
		
		switch (playBin.setState (state)) {
		case SUCCESS:
			break;
		case ASYNC:

			if (playBin.getState(timeout, TimeUnit.MILLISECONDS) != state) {
				throw exception;
			}
			break;
			default:
				throw exception;
		}
	}
	
	@Test
	public void testGetStreamType() {
		assertEquals(StreamType.VIDEO, videoStream.getStreamType());
		assertEquals(StreamType.AUDIO, audioStream.getStreamType());
	}
	
	@Test
	public void testGetMute() {
		assertEquals(false, audioStream.getMute());
		audioStream.set("mute", true);
		assertEquals(true, audioStream.getMute());
	}
	
	@Test
	public void testGetCaps() {
		Caps videoCaps = videoStream.getCaps();
		Caps audioCaps = audioStream.getCaps();
		
		Caps videoYuv = Caps.fromString("video/x-raw-yuv");
		Caps audioFloat = Caps.fromString("audio/x-raw-float");
		
		assertTrue(videoCaps.isSubset(videoYuv));
		assertTrue(audioCaps.isSubset(audioFloat));
	}
	
    @Test
    public void testUseStreamInfoAfterBinStop() throws Exception {
    	   	 
        waitState(State.NULL, 1000);
        
        assertEquals(State.NULL, playBin.getState());
       
        Tracker binTracker = new Tracker(playBin);

        
        playBin = null;
         
        assertTrue("Bin not garbage collected", binTracker.waitGC());        
        
        assertTrue("Bin not destroyed",binTracker.waitDestroyed());
        
        assertTrue(!videoStream.getMute());
    }
    
    
    @Test
    public void testStreamInfoGC() throws Exception {

    	StreamInfo[] streams = getStreamInfo();

    	for (int i = 0; i < 100; ++i) {
    		getStreamInfo();
    	}
    	
    	waitState(State.NULL, 1000);

    	assertEquals(State.NULL, playBin.getState());

    	Tracker binTracker = new Tracker(playBin);

    	playBin = null;

    	GObjectRipper videoRipper  = new GObjectRipper("local Video StreamInfo", streams[0]);
    	GObjectRipper audioRipper  = new GObjectRipper("local Audio StreamInfo", streams[1]);
    	streams[0] = null;
    	streams[1] = null;
    	
    	GObjectRipper videoRipper0  = new GObjectRipper("Video StreamInfo", videoStream);
    	videoStream = null;
    	GObjectRipper audioRipper0  = new GObjectRipper("Audio StreamInfo", audioStream);
    	audioStream = null;

    	videoRipper.rip(false);    	
    	videoRipper0.rip(true);

    	audioRipper.rip(false);
    	audioRipper0.rip(true);



    	assertTrue("Bin not garbage collected", binTracker.waitGC());        

    	assertTrue("Bin not destroyed",binTracker.waitDestroyed());
    }

   private static class GObjectRipper {
	   GObject obj;
	   final String name;



	   public GObjectRipper(String name, GObject obj) {
		   this.obj = obj;
		   this.name = name;
	   }



	   void rip(boolean expectSuccess) {

		   Tracker tracker = new Tracker(obj);

		   obj = null;

		   if (expectSuccess) {
			   assertTrue(name + " not garbage collected", tracker.waitGC());        

			   assertTrue(name + " not destroyed",tracker.waitDestroyed());
		   } else {
			   assertFalse(name + " should not have been garbage collected", tracker.waitGC());        

			   assertFalse(name + " should not have been destroyed",tracker.waitDestroyed());			   
		   }
	   }

   }
}

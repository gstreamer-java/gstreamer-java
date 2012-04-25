/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.elements;

import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Format;
import org.gstreamer.Fraction;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.StreamInfo;
import org.gstreamer.Video;
import org.gstreamer.lowlevel.GValueAPI;
import org.gstreamer.lowlevel.GValueAPI.GValueArray;

import com.sun.jna.Pointer;

/**
 * @deprecated This element is deprecated and no longer supported. You should use the PlayBin2 element instead.
 * 
 * Playbin provides a stand-alone everything-in-one abstraction for an audio 
 * and/or video player.
 * <p>
 * It can handle both audio and video files and features
 * <ul>
 * <li>
 * automatic file type recognition and based on that automatic
 * selection and usage of the right audio/video/subtitle demuxers/decoders
 * <li> visualisations for audio files
 * <li> subtitle support for video files
 * <li> stream selection between different audio/subtitles streams
 * <li> meta info (tag) extraction
 * <li> easy access to the last video frame
 * <li> buffering when playing streams over a network
 * <li> volume control
 * </ul>
 * <h3>Usage</h3>
 * <p>
 * A playbin element can be created just like any other element using
 * {@link ElementFactory#make}, although to call PlayBin specific methods, it 
 * is best to create one via a {@link #PlayBin(String)} or {@link #PlayBin(String, URI)} constructor.
 * 
 * <p>
 * The file/URI to play should be set via {@link #setInputFile} or {@link #setURI}
 * 
 * <p>
 * Playbin is a {@link org.gstreamer.Pipeline}. It will notify the application of everything
 * that's happening (errors, end of stream, tags found, state changes, etc.)
 * by posting messages on its {@link org.gstreamer.Bus}. The application needs to watch the
 * bus.
 * 
 * <p>
 * Playback can be initiated by setting the PlayBin to PLAYING state using
 * {@link #setState setState} or {@link #play play}. Note that the state change will take place in
 * the background in a separate thread, when the function returns playback
 * is probably not happening yet and any errors might not have occured yet.
 * Applications using playbin should ideally be written to deal with things
 * completely asynchroneous.
 * </p>
 * 
 * <p>
 * When playback has finished (an EOS message has been received on the bus)
 * or an error has occured (an ERROR message has been received on the bus) or
 * the user wants to play a different track, playbin should be set back to
 * READY or NULL state, then the input file/URI should be set to the new
 * location and then playbin be set to PLAYING state again.
 * </p>
 * 
 * <p>
 * Seeking can be done using {@link #seek seek} on the playbin element. 
 * Again, the seek will not be executed instantaneously, but will be done in a
 * background thread. When the seek call returns the seek will most likely still
 * be in process. An application may wait for the seek to finish (or fail) using 
 * {@link #getState(long)} with -1 as the timeout, but this will block the user 
 * interface and is not recommended at all.
 * 
 * <p>
 * Applications may query the current position and duration of the stream
 * via {@link #queryPosition} and {@link #queryDuration} and
 * setting the format passed to {@link Format#TIME}. If the query was successful,
 * the duration or position will have been returned in units of nanoseconds.
 * </p>
 * 
 * <h3>Advanced Usage: specifying the audio and video sink</h3>
 * <p>
 * By default, if no audio sink or video sink has been specified via {@link #setAudioSink} 
 * and {@link #setVideoSink}, playbin will use the autoaudiosink and autovideosink
 * elements to find the first-best available output method.
 * This should work in most cases, but is not always desirable. Often either
 * the user or application might want to specify more explicitly what to use
 * for audio and video output.
 * </p>
 * <p>
 * If the application wants more control over how audio or video should be
 * output, it may create the audio/video sink elements itself (for example
 * using {@link ElementFactory#make}) and provide them to playbin using {@link #setAudioSink} 
 * and {@link #setVideoSink}
 * </p>
 * <p>
 * GNOME-based applications, for example, will usually want to create
 * gconfaudiosink and gconfvideosink elements and make playbin use those,
 * so that output happens to whatever the user has configured in the GNOME
 * Multimedia System Selector confinguration dialog.
 * </p>
 * <p>
 * The sink elements do not necessarily need to be ready-made sinks. It is
 * possible to create container elements that look like a sink to playbin,
 * but in reality contain a number of custom elements linked together. This
 * can be achieved by creating a {@link Bin} and putting elements in there and
 * linking them, and then creating a sink {@link GhostPad} for the bin and pointing
 * it to the sink pad of the first element within the bin. This can be used
 * for a number of purposes, for example to force output to a particular
 * format or to modify or observe the data before it is output.
 * </p>
 * <p>
 * It is also possible to 'suppress' audio and/or video output by using
 * 'fakesink' elements (or capture it from there using the fakesink element's
 * "handoff" signal, which, nota bene, is fired from the streaming thread!).
 * </p>
 * <h3>Retrieving Tags and Other Meta Data</h3>
 * <p>
 * Most of the common meta data (artist, title, etc.) can be retrieved by
 * watching for TAG messages on the pipeline's bus (see above).
 * </p>
 * <p>
 * Other more specific meta information like width/height/framerate of video
 * streams or samplerate/number of channels of audio streams can be obtained
 * using the "stream-info" property, which will return a GList of stream info
 * objects, one for each stream. These are opaque objects that can only be
 * accessed via the standard GObject property interface, ie. g_object_get().
 * Each stream info object has the following properties:
 * <ul>
 * <li>"object" (GstObject) (the decoder source pad usually)</li>
 * <li>"type" (enum) (if this is an audio/video/subtitle stream)</li>
 * <li>"decoder" (string) (name of decoder used to decode this stream)</li>
 * <li>"mute" (boolean) (to mute or unmute this stream)</li>
 * <li>"caps" (GstCaps) (caps of the decoded stream)</li>
 * <li>"language-code" (string) (ISO-639 language code for this stream, mostly used for audio/subtitle streams)</li>
 * <li>"codec" (string) (format this stream was encoded in)</li>
 * </ul>
 * <p>
 * Stream information from the stream-info properties is best queried once
 * playbin has changed into PAUSED or PLAYING state (which can be detected
 * via a state-changed message on the bus where old_state=READY and
 * new_state=PAUSED), since before that the list might not be complete yet or
 * not contain all available information (like language-codes).
 * </>
 * <h3>Buffering</h3>
 * <p>
 * Playbin handles buffering automatically for the most part, but applications
 * need to handle parts of the buffering process as well. Whenever playbin is
 * buffering, it will post BUFFERING messages on the bus with a percentage
 * value that shows the progress of the buffering process. Applications need
 * to set playbin to PLAYING or PAUSED state in response to these messages.
 * They may also want to convey the buffering progress to the user in some
 * way. Here is how to extract the percentage information from the message
 * (requires GStreamer >= 0.10.11):
 * </p>
 * <p>
 * <pre>
 * PlayBin playbin = new PlayBin("player");
 * playbin.getBus().connect(new Bus.BUFFERING() {
 *     public void bufferingMessage(GstObject element, int percent) {
 *         System.out.printf("Buffering (%u percent done)\n", percent);
 *     }
 * }
 * </pre>
 * Note that applications should keep/set the pipeline in the PAUSED state when
 * a BUFFERING message is received with a buffer percent value < 100 and set
 * the pipeline back to PLAYING state when a BUFFERING message with a value
 * of 100 percent is received (if PLAYING is the desired state, that is).
 * </>
 * <h3>Embedding the video window in your application</h3>
 * <p>
 * By default, playbin (or rather the video sinks used) will create their own
 * window. Applications will usually want to force output to a window of their
 * own, however. This can be done using the GstXOverlay interface, which most
 * video sinks implement. See the documentation there for more details.
 * </p>
 * <h3>Specifying which CD/DVD device to use</h3>
 * <p>
 * The device to use for CDs/DVDs needs to be set on the source element
 * playbin creates before it is opened. The only way to do this at the moment
 * is to connect to playbin's "notify::source" signal, which will be emitted
 * by playbin when it has created the source element for a particular URI.
 * In the signal callback you can check if the source element has a "device"
 * property and set it appropriately. In future ways might be added to specify
 * the device as part of the URI, but at the time of writing this is not
 * possible yet.
 * </p>
 * <h3>Examples</h3>
 * <p>
 * Here is a simple pipeline to play back a video or audio file:
 * <p>
 * <code>
 * gst-launch -v playbin uri=file:///path/to/somefile.avi
 * </code>
 * <p>
 * This will play back the given AVI video file, given that the video and
 * audio decoders required to decode the content are installed. Since no
 * special audio sink or video sink is supplied (not possible via gst-launch),
 * playbin will try to find a suitable audio and video sink automatically
 * using the autoaudiosink and autovideosink elements.
 * </p>
 * <p>
 * Here is a another pipeline to play track 4 of an audio CD:
 * <p>
 * <code>
 * gst-launch -v playbin uri=cdda://4
 * </code>
 * <p>
 * This will play back track 4 on an audio CD in your disc drive (assuming
 * the drive is detected automatically by the plugin).
 * </p>
 * <p>
 * Here is a another pipeline to play title 1 of a DVD:
 * <code>
 * gst-launch -v playbin uri=dvd://1
 * </code>
 * This will play back title 1 of a DVD in your disc drive (assuming
 * the drive is detected automatically by the plugin).
 * </p>
 */
@Deprecated
public class PlayBin extends Pipeline {
    public static final String GST_NAME = "playbin";
    public static final String GTYPE_NAME = "GstPlayBin";
    
    /**
     * Creates a new PlayBin.
     * 
     * @param name The name used to identify this pipeline.
     */
    public PlayBin(String name) {
        this(makeRawElement(GST_NAME, name));
    }
    
    /**
     * Creates a new PlayBin.
     * 
     * @param name The name used to identify this pipeline.
     * @param uri The URI of the media file to load.
     */
    public PlayBin(String name, URI uri) {
        this(name);
        setURI(uri);
    }
    
    /**
     * Creates a new PlayBin proxy.
     * 
     * @param init proxy initialization args
     * 
     */
    public PlayBin(Initializer init) {
        super(init);
    }


	/*private static String slashify(String path, boolean isDirectory) {
		String p = path;
		if (File.separatorChar != '/')
			p = p.replace(File.separatorChar, '/');
		if (!p.startsWith("/"))
			p = "/" + p;
		if (!p.endsWith("/") && isDirectory)
			p = p + "/";
		return p;
	}*/

    /**
     * Sets the media file to play.
     * 
     * @param file The {@link java.io.File} to play.
     */
    public void setInputFile(File file) {
        setURI(file.toURI());
    }
    
    /**
     * Sets the media URI to play.
     * 
     * @param uri The {@link java.net.URI} to play.
     */
    public void setURI(URI uri) {
        set("uri", uri);
    }
    
    /**
     * Sets the audio output Element.
     * <p> To disable audio output, call this method with a <tt>null</tt> argument.
     * 
     * @param element The element to use for audio output.
     */
    public void setAudioSink(Element element) {
        setElement("audio-sink", element);
    }
    
    /**
     * Sets the video output Element.
     * <p> To disable video output, call this method with a <tt>null</tt> argument.
     * 
     * @param element The element to use for video output.
     */
    public void setVideoSink(Element element) {
        setElement("video-sink", element);
    }
    
    /**
     * Sets the visualization output Element.
     * 
     * @param element The element to use for visualization.
     */
    public void setVisualization(Element element) {
        setElement("vis-plugin", element);
    }
    
    /**
     * Sets an output {@link Element} on the PlayBin.
     * 
     * @param key The name of the output to change.
     * @param element The Element to set as the output.
     */
    private void setElement(String key, Element element) {
        if (element == null) {
            element = ElementFactory.make("fakesink", "fake-" + key);
        }
        set(key, element);
    }
    
    /**
     * Set the volume for the PlayBin.
     * 
     * @param percent Percentage (between 0 and 100) to set the volume to.
     */
    public void setVolumePercent(int percent) {
        setVolume(Math.max(Math.min((double) percent, 100d), 0d) / 100d);
    }
    
    /**
     * Get the current volume.
     * @return The current volume as a percentage between 0 and 100 of the max volume.
     */
    public int getVolumePercent() {
        return (int) ((getVolume() * 100d) + 0.5);
    }
    /**
     * Sets the audio playback volume.
     * 
     * @param volume value between 0.0 and 1.0 with 1.0 being full volume.
     */
    public void setVolume(double volume) {
        set("volume", Math.max(Math.min(volume, 1d), 0d));
    }
    
    /**
     * Gets the current volume.
     * @return The current volume as a percentage between 0 and 100 of the max volume.
     */
    public double getVolume() {
        return ((Number) get("volume")).doubleValue();
    }

  /**
   * Returns a list with with specific meta information like
   * width/height/framerate of video streams or samplerate/number of channels of
   * audio streams. This stream information from the "stream-info" property is
   * best queried once playbin has changed into PAUSED or PLAYING state (which
   * can be detected via a state-changed message on the GstBus where
   * old_state=READY and new_state=PAUSED), since before that the list might not
   * be complete yet or not contain all available information (like
   * language-codes).
   */
  public List<StreamInfo> getStreamInfo() {
    Pointer ptr = getPointer("stream-info-value-array");
    if (ptr != null) {
      GValueArray garray = new GValueArray(ptr);
      List<StreamInfo> list = new ArrayList<StreamInfo>(garray.getNValues());

      final int len = garray.getNValues();
      
      for (int i = 0; i < len; ++i) {
          GValueAPI.GValue value = garray.nth(i);
          StreamInfo streamInfo;
          { /*
           * this is a work-around gst_stream_info_get_type() symbols not
           * available in one of the top-level shared objects (libgstreamer or
           * libgstbase). As a result, StreamInfo.class can not be registered in
           * GstTypes even though if is an instance of GObject. value.getValue()
           * will fail to resolve to an instance of StreamInfo. Here we bypass
           * JNA type mapping that would occur had we called
           * GValueAPI.g_value_get_object()
           */
              Pointer p = GValueAPI.GVALUE_NOMAPPER_API.g_value_get_object(value);
              streamInfo = objectFor(p, StreamInfo.class, -1, true);
          }
          list.add(streamInfo);
      }
      
      return list;
    }
    return null;
  }

  /**
   * Retrieves the framerate from the caps of the video sink's pad.
   * 
   * @return frame rate (frames per second), or 0 if the framerate is not
   *         available
   */
  public double getVideoSinkFrameRate() {
    for (Element sink : getSinks()) {
      for (Pad pad : sink.getPads()) {
        Fraction frameRate = Video.getVideoFrameRate(pad);
        if (frameRate != null) {
          return frameRate.toDouble();
        }
      }
    }
    return 0;
  }

  /**
   * Retrieves the width and height of the video frames configured in the caps
   * of the video sink's pad.
   * 
   * @return dimensions of the video frames, or null if the video frame size is
   *         not available
   */
  public Dimension getVideoSize() {
    for (Element sink : getSinks()) {
      for (Pad pad : sink.getPads()) {
        Dimension size = Video.getVideoSize(pad);
        if (size != null) {
          return size;
        }
      }
    }
    return null;
  }
}

/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.gstreamer;

import org.gstreamer.event.EOSEvent;
import org.gstreamer.event.EOSListener;
import org.gstreamer.event.MessageAdapter;
import org.gstreamer.event.MessageEvent;
import org.gstreamer.event.MessageListener;
import org.gstreamer.event.StateChangeEvent;
import org.gstreamer.event.StateChangeListener;
import org.gstreamer.event.TagEvent;
import org.gstreamer.event.TagListener;
import org.gstreamer.lowlevel.GstAPI;
import org.gstreamer.lowlevel.GstAPI.GErrorStruct;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.gstreamer.lowlevel.GstAPI.gst;

public class BusTest {
    
    public BusTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("BusTest", new String[] {});
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        Gst.deinit();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    private static class TestPipe {
        public final Pipeline pipe = new Pipeline("pipe");
        public Element src = ElementFactory.make("fakesrc", "src");
        public Element sink = ElementFactory.make("fakesink", "sink");
        public final MainLoop loop = new MainLoop();
        public TestPipe() {
            pipe.addMany(src, sink);
            Element.linkMany(src, sink);
        }
        public void run() {
            // Create a timer to quit out of the test so it does not hang
            new Timeout(100, new Runnable() {

                public void run() {
                    loop.quit();
                }
            }).start();
            pipe.play();
        }
        public Bus getBus() { 
            return pipe.getBus(); 
        }
        public void quit() {
            loop.quit();
        }
    }
    @Test
    public void eosMessage() {
        final TestPipe pipe  = new TestPipe();
        
        final boolean[] signalFired = { false };
        Bus.EOS eosSignal = new Bus.EOS() {

            public void eosMessage(GstObject source) {
                signalFired[0] = true;
                pipe.quit();
            }
            
        };
        pipe.getBus().connect(eosSignal);
        final boolean[] listenerFired = { false };
        EOSListener eosListener = new EOSListener() {

            public void endOfStream(EOSEvent evt) {
                listenerFired[0] = true;
                pipe.quit();
            }
        };
        pipe.getBus().addEOSListener(eosListener);
        //
        // For the pipeline to post an EOS message, all sink elements must post it
        //
        for (Element elem : pipe.pipe.getSinks()) {
            GstAPI.gst.gst_element_post_message(elem, GstAPI.gst.gst_message_new_eos(elem));
        }
        pipe.run();
        pipe.getBus().disconnect(eosSignal);
        pipe.getBus().removeEOSListener(eosListener);
        assertTrue("EOS signal not received", signalFired[0]);
        assertTrue("EOS listener not called", listenerFired[0]);
    }
    @Test
    public void stateChangeMessage() {
        final TestPipe pipe = new TestPipe();
        final boolean[] signalFired = { false };
        final boolean[] listenerCalled = { false };
        
        Bus.STATE_CHANGED stateChanged = new Bus.STATE_CHANGED() {
           
            public void stateMessage(GstObject source, State old, State current, State pending) {
                if (pending == State.PLAYING) {
                    signalFired[0] = true;
                }
                pipe.quit();
            }
            
        };
        pipe.getBus().connect(stateChanged);

        StateChangeListener listener = new StateChangeListener() {

            public void stateChange(StateChangeEvent evt) {
                listenerCalled[0] = true;
                pipe.quit();
            }
        };
        pipe.getBus().addStateChangeListener(listener);
        
        pipe.run();
        pipe.getBus().disconnect(stateChanged);
        pipe.getBus().removeStateChangeListener(listener);
        assertTrue("STATE_CHANGED signal not received", signalFired[0]);
        assertTrue("StateChangeListener not called", listenerCalled[0]);
    }
    
    @Test
    public void errorMessage() {
        final TestPipe pipe = new TestPipe();
       
        final boolean[] signalFired = { false };
        final boolean[] listenerCalled = { false };
        final GstObject[] signalSource = { null };
        final Object[] listenerSource = { null };
        Bus.ERROR errorSignal = new Bus.ERROR() {

            public void errorMessage(GstObject source, int code, String message) {
                signalFired[0] = true;
                signalSource[0] = source;
                pipe.quit();
            }
        };
        pipe.getBus().connect(errorSignal);
        MessageListener listener = new MessageAdapter() {
            @Override
            public void errorMessage(MessageEvent e) {
                listenerCalled[0] = true;
                listenerSource[0] = e.getSource();
                pipe.quit();
            }
        };
        pipe.getBus().addMessageListener(listener);
        
        GErrorStruct msg = new GErrorStruct();
        gst.gst_element_post_message(pipe.src, gst.gst_message_new_error(pipe.src, msg, "testing error messages"));
        pipe.run();
        pipe.getBus().disconnect(errorSignal);
        pipe.getBus().removeMessageListener(listener);
        assertTrue("ERROR signal not received", signalFired[0]);
        assertTrue("MessageListener not called", listenerCalled[0]);
        assertEquals("Incorrect source object on signal", pipe.src, signalSource[0]);
        assertEquals("Incorrect source object on listener", pipe.src, listenerSource[0]);
    }
    @Test
    public void warningMessage() {
        final TestPipe pipe = new TestPipe();
       
        final boolean[] signalFired = { false };
        final boolean[] listenerCalled = { false };
        final GstObject[] signalSource = { null };
        final Object[] listenerSource = { null };
        Bus.WARNING signal = new Bus.WARNING() {

            public void warningMessage(GstObject source, int code, String message) {
                signalFired[0] = true;
                signalSource[0] = source;
                pipe.quit();
            }
        };
        pipe.getBus().connect(signal);
        MessageListener listener = new MessageAdapter() {
            @Override
            public void warningMessage(MessageEvent e) {
                listenerCalled[0] = true;
                listenerSource[0] = e.getSource();
                pipe.quit();
            }
        };
        pipe.getBus().addMessageListener(listener);
        
        GErrorStruct msg = new GErrorStruct();
        gst.gst_element_post_message(pipe.src, gst.gst_message_new_warning(pipe.src, msg, "testing warning messages"));
        pipe.run();
        pipe.getBus().disconnect(signal);
        pipe.getBus().removeMessageListener(listener);
        assertTrue("WARNING signal not received", signalFired[0]);
        assertTrue("MessageListener not called", listenerCalled[0]);
        assertEquals("Incorrect source object on signal", pipe.src, signalSource[0]);
        assertEquals("Incorrect source object on listener", pipe.src, listenerSource[0]);
    }
    @Test
    public void infoMessage() {
        final TestPipe pipe = new TestPipe();
       
        final boolean[] signalFired = { false };
        final boolean[] listenerCalled = { false };
        final GstObject[] signalSource = { null };
        final Object[] listenerSource = { null };
        Bus.INFO signal = new Bus.INFO() {

            public void infoMessage(GstObject source, int code, String message) {
                signalFired[0] = true;
                signalSource[0] = source;
                pipe.quit();
            }
        };
        pipe.getBus().connect(signal);
        MessageListener listener = new MessageAdapter() {

            @Override
            public void informationMessage(MessageEvent evt) {
                listenerCalled[0] = true;
                listenerSource[0] = evt.getSource();
                pipe.quit();
            }
        };
        pipe.getBus().addMessageListener(listener);
        
        GErrorStruct msg = new GErrorStruct();
        gst.gst_element_post_message(pipe.src, gst.gst_message_new_info(pipe.src, msg, "testing warning messages"));
        pipe.run();
        pipe.getBus().disconnect(signal);
        pipe.getBus().removeMessageListener(listener);
        assertTrue("INFO signal not received", signalFired[0]);
        assertTrue("MessageListener not called", listenerCalled[0]);
        assertEquals("Incorrect source object on signal", pipe.src, signalSource[0]);
        assertEquals("Incorrect source object on listener", pipe.src, listenerSource[0]);
    }
    @Test
    public void bufferingMessage() {
        final TestPipe pipe = new TestPipe();
       
        final boolean[] signalFired = { false };
        final int[] signalValue = { -1 };
        final boolean[] listenerCalled = { false };
        final GstObject[] signalSource = { null };
        final int PERCENT = 95;
        Bus.BUFFERING signal = new Bus.BUFFERING() {

            public void bufferingMessage(GstObject source, int percent) {
                signalFired[0] = true;
                signalValue[0] = percent;
                signalSource[0] = source;
                pipe.quit();
            }
        };
        pipe.getBus().connect(signal);
        gst.gst_element_post_message(pipe.src, gst.gst_message_new_buffering(pipe.src, PERCENT));
        pipe.run();
        pipe.getBus().disconnect(signal);
        
        assertTrue("BUFFERING signal not received", signalFired[0]);
        assertEquals("Wrong percent value received for signal", PERCENT, signalValue[0]);
        assertEquals("Incorrect source object on signal", pipe.src, signalSource[0]);
    }
    @Test
    public void tagMessage() {
        final TestPipe pipe = new TestPipe();
       
        final boolean[] signalFired = { false };
        final boolean[] listenerCalled = { false };
        final int[] signalValue = { 0 };
        final GstObject[] signalSource = { null };
        final Object[] listenerSource = { null };
        final int MAGIC = 0xdeadbeef;
        Bus.TAG signal = new Bus.TAG() {

            public void tagMessage(GstObject source, TagList tagList) {
                signalFired[0] = true;
                signalSource[0] = source;
//                signalValue[0] = tagList.get
                pipe.quit();
            }
        };
        pipe.getBus().connect(signal);
        TagListener listener = new TagListener() {

            public void tagsFound(TagEvent evt) {
                listenerCalled[0] = true;
                listenerSource[0] = evt.getSource();
                pipe.quit();
            }
            
        };
        pipe.getBus().addTagListener(listener);
        TagList tagList = new TagList();
//        tagList.setInteger("foobar", MAGIC);
        gst.gst_element_post_message(pipe.src, gst.gst_message_new_tag(pipe.src, tagList));
        pipe.run();
        pipe.getBus().disconnect(signal);
        pipe.getBus().removeTagListener(listener);
        assertTrue("TAG signal not received", signalFired[0]);
//        assertEquals("Wrong tag value received in signal", MAGIC, signalValue[0]);
        assertEquals("Incorrect source object on signal", pipe.src, signalSource[0]);
        assertTrue("TagListener not called", listenerCalled[0]);
        assertEquals("Incorrect source passed to listener", pipe.src, listenerSource[0]);
    }
}

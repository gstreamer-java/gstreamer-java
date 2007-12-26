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

import com.sun.jna.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

    @Test
    public void eosListener() {
        // This test won't work on windows yet
        if (Platform.isWindows()) {
            return;
        }
        Pipeline pipe = new Pipeline("pipe");
        Element src = ElementFactory.make("filesrc", "/dev/null");
        src.set("location", "/dev/null");
        final MainLoop loop = new MainLoop();
        final boolean[] result = {false};
        Element sink = ElementFactory.make("fakesink", "sink");
        pipe.addMany(src, sink);
        Element.linkMany(src, sink);

        Bus.EOS eos = new Bus.EOS() {

            public void eosMessage(GstObject source) {
                result[0] = true;
                loop.quit();
            }
            
        };
        pipe.getBus().connect(eos);
        // Create a timer to quit out of the test so it does not hang
        new Timeout(100, new Runnable() {

            public void run() {
                loop.quit();
            }
        }).start();
        pipe.play();
        loop.run();
        pipe.getBus().disconnect(eos);
        assertTrue("EOS not received", result[0]);
    }
    @Test
    public void stateChangeListener() {
        Pipeline pipe = new Pipeline("pipe");
        Element src = ElementFactory.make("filesrc", "/dev/null");
        src.set("location", "/dev/null");
        final MainLoop loop = new MainLoop();
        final boolean[] result = {false};
        Element sink = ElementFactory.make("fakesink", "sink");
        pipe.addMany(src, sink);
        Element.linkMany(src, sink);

        Bus.STATE_CHANGED stateChanged = new Bus.STATE_CHANGED() {
           
            public void stateMessage(GstObject source, State old, State current, State pending) {
                if (pending == State.PLAYING) {
                    result[0] = true;
                }                
                loop.quit();
            }
            
        };
        pipe.getBus().connect(stateChanged);
        // Create a timer to quit out of the test so it does not hang
        new Timeout(100, new Runnable() {

            public void run() {
                loop.quit();
            }
        }).start();
        pipe.play();
        loop.run();
        pipe.getBus().disconnect(stateChanged);
        assertTrue("STATECHANGED not received", result[0]);
    }
}

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

import com.sun.jna.Pointer;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gstreamer.Bus;
import org.gstreamer.event.EOSEvent;
import org.gstreamer.event.EOSListener;
import org.gstreamer.lowlevel.GObjectAPI;
import org.gstreamer.lowlevel.IntPtr;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author wayne
 */
public class GarbageCollectionTest {
    
    public GarbageCollectionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("test", new String[] {});
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
    
    public static boolean waitGC(WeakReference<?> ref) {
        System.gc();
        for (int i = 0; ref.get() != null && i < 10; ++i) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
            System.gc();
        }
        return ref.get() == null;
    }
    
    private static class Tracker {
        public Tracker(GObject obj) {
            GObjectAPI.gobj.g_object_weak_ref(obj, notify, new IntPtr(System.identityHashCode(this)));
            ref = new WeakReference<GObject>(obj);
        }
        WeakReference<GObject> ref;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        GObjectAPI.GWeakNotify notify = new GObjectAPI.GWeakNotify() {
            public void callback(IntPtr id, Pointer obj) {
                destroyed.set(true);
            }
        };
        public boolean waitGC() {
            return GarbageCollectionTest.waitGC(ref);
        }
        public boolean waitDestroyed() {
            for (int i = 0; !destroyed.get() && i < 10; ++i) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            return destroyed.get();
        }
    }
    @Test
    public void testElement() throws Exception {
        
        Element e = ElementFactory.make("fakesrc", "test element");
        Tracker tracker = new Tracker(e);
        e = null;        
        assertTrue("Element not garbage collected", tracker.waitGC());        
        assertTrue("GObject not destroyed", tracker.waitDestroyed());
    }
    @Test
    public void testBin() throws Exception {
        Bin bin = new Bin("test");
        Element e1 = ElementFactory.make("fakesrc", "source");
        Element e2 = ElementFactory.make("fakesink", "sink");
        bin.addMany(e1, e2);
        
        assertEquals("source not returned", e1, bin.getElementByName("source"));
        assertEquals("sink not returned", e2, bin.getElementByName("sink"));
        WeakReference<Element> binRef = new WeakReference<Element>(bin);
        bin = null;
        assertTrue("Bin not garbage collected", waitGC(binRef));
        WeakReference<Element> e1Ref = new WeakReference<Element>(e1);
        WeakReference<Element> e2Ref = new WeakReference<Element>(e2);
        e1 = null;
        e2 = null;
        
        assertTrue("First Element not garbage collected", waitGC(e1Ref));
        assertTrue("Second Element not garbage collected", waitGC(e2Ref));
        
    }
    @Test
    public void testBinRetrieval() throws Exception {
        Bin bin = new Bin("test");
        Element e1 = ElementFactory.make("fakesrc", "source");
        Element e2 = ElementFactory.make("fakesink", "sink");
        bin.addMany(e1, e2);
        int id1 = System.identityHashCode(e1);
        int id2 = System.identityHashCode(e2);
        
        e1 = null;
        e2 = null;
        System.gc();
        Thread.sleep(10);
        // Should return the same object that was put into the bin
        assertEquals("source ID does not match", id1, System.identityHashCode(bin.getElementByName("source")));
        assertEquals("sink ID does not match", id2, System.identityHashCode(bin.getElementByName("sink")));       
    }
    @Test
    public void pipeline() {
        Pipeline pipe = new Pipeline("test");
        Tracker pipeTracker = new Tracker(pipe);
        pipe = null;
        assertTrue("Pipe not garbage collected", pipeTracker.waitGC());
        System.out.println("checking if pipeline is destroyed");
        assertTrue("Pipe not destroyed", pipeTracker.waitDestroyed());
    }
    @Test
    public void pipelineBus() {
        Pipeline pipe = new Pipeline("test");
        Bus bus = pipe.getBus();
        Tracker busTracker = new Tracker(bus);
        Tracker pipeTracker = new Tracker(pipe);
        
        pipe = null;
        bus = null;
        assertTrue("Bus not garbage collected", busTracker.waitGC());
        assertTrue("Bus not destroyed", busTracker.waitDestroyed());
        assertTrue("Pipe not garbage collected", pipeTracker.waitGC());
        assertTrue("Pipe not destroyed", pipeTracker.waitDestroyed());

    }
    @Test
    public void busWithListeners() {
        Pipeline pipe = new Pipeline("test");
        Bus bus = pipe.getBus();
        bus.connect(new Bus.EOS() {

            public void eosMessage(GstObject source) {
            }
        });
        bus.addEOSListener(new EOSListener() {

            public void endOfStream(EOSEvent evt) {
            }
        });
        Tracker busTracker = new Tracker(bus);
        Tracker pipeTracker = new Tracker(pipe);
        bus = null;
        pipe = null;
        assertTrue("Bus not garbage collected", busTracker.waitGC());
        assertTrue("Bus not destroyed", busTracker.waitDestroyed());
        assertTrue("Pipe not garbage collected", pipeTracker.waitGC());
        assertTrue("Pipe not destroyed", pipeTracker.waitDestroyed());
    }
}

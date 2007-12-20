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

import org.gstreamer.elements.PlayBin;
import java.io.File;
import java.lang.ref.WeakReference;
import org.gstreamer.lowlevel.GObjectAPI.GObjectStruct;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wayne
 */
public class PipelineTest {
    
    public PipelineTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("PipelineTest", new String[] {});
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
    public boolean waitGC(WeakReference<? extends Object> ref) throws InterruptedException {
        System.gc();
        for (int i = 0; ref.get() != null && i < 20; ++i) {
            Thread.sleep(10);
            System.gc();
        }
        return ref.get() == null;
    }
    public boolean waitRefCnt(GObjectStruct struct, int refcnt) throws InterruptedException {
        System.gc();
        struct.read();
        for (int i = 0; struct.ref_count != refcnt && i < 20; ++i) {
            Thread.sleep(10);
            System.gc();
            struct.read();
        }
        return struct.ref_count == refcnt;
    }
    @Test
    public void testBusGC() throws Exception {
        PlayBin pipe = new PlayBin("test playbin");
        pipe.setInputFile(new File("/dev/null"));
        pipe.play();
        Bus bus = pipe.getBus();
        GObjectStruct struct = new GObjectStruct(bus.handle());
        int refcnt = struct.ref_count;
        assertTrue(refcnt > 1);
        // reget the Bus - should return the same object and not increment ref count
        Bus bus2 = pipe.getBus();
        assertTrue("Did not get same Bus object", bus == bus2);
        struct.read(); // update struct fields
        assertEquals("ref_count not equal", refcnt, struct.ref_count);   
        bus2 = null;
        
        WeakReference<Bus> bref = new WeakReference<Bus>(bus);
        bus = null;      
        // Since the pipeline holds a reference to the GstBus, the proxy should not be disposed
        assertFalse("bus disposed prematurely", waitGC(bref));
        assertFalse("ref_count decremented prematurely", waitRefCnt(struct, refcnt - 1));
        
        
        WeakReference<GObject> pref = new WeakReference<GObject>(pipe);
        pipe.stop();
        bus = pipe.getBus();
        bref = new WeakReference<Bus>(bus);
        pipe = null;
        assertTrue("pipe not disposed", waitGC(pref));
        struct.read();
        System.out.println("bus ref_count=" + struct.ref_count);
        bus = null;
        assertTrue("bus not disposed", waitGC(bref));
        // This is a bit dangerous, since that memory could have been reused
        assertTrue("ref_count not decremented", waitRefCnt(struct, 0));
    } /* Test of getBus method, of class Pipeline. */
    
}

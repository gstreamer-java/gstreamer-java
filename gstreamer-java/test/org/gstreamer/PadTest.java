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

import java.lang.ref.WeakReference;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class PadTest {
    
    public PadTest() {
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
    public boolean waitGC(WeakReference<? extends Object> ref) throws InterruptedException {
        System.gc();
        for (int i = 0; ref.get() != null && i < 100; ++i) {
            Thread.sleep(10);
            System.gc();
        }
        return ref.get() == null;
    }
    
    // Does not work yet
    @Test
    public void getPad() throws Exception {
        Element src = ElementFactory.make("fakesrc", "src");
        Element sink = ElementFactory.make("fakesink", "sink");
        Pad srcPad = src.getStaticPad("src");
        Pad sinkPad = sink.getStaticPad("sink");
        assertNotNull("Could not get src pad", srcPad);
        assertNotNull("Could not get sink pad", sinkPad);
        src = null;
        sink = null;
        WeakReference<Pad> srcRef = new WeakReference<Pad>(srcPad);
        WeakReference<Pad> sinkRef = new WeakReference<Pad>(sinkPad);
        srcPad = null;
        sinkPad = null;
        assertTrue("Src pad not garbage collected", waitGC(srcRef));
        assertTrue("Sink pad not garbage collected", waitGC(sinkRef));
    }
    @Test
    public void padLink() throws Exception {
        Element src = ElementFactory.make("fakesrc", "src");
        Element sink = ElementFactory.make("fakesink", "src");
        Pad srcPad = src.getStaticPad("src");
        Pad sinkPad = sink.getStaticPad("sink");
        assertEquals("Could not link pads", PadLinkReturn.OK, srcPad.link(sinkPad));
    }
    
}

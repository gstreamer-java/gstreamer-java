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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
public class BinTest {
    
    public BinTest() {
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
        for (int i = 0; ref.get() != null && i < 20; ++i) {
            Thread.sleep(10);
            System.gc();
        }
        return ref.get() == null;
    }
    @Test
    public void testGetElements() {
        Bin bin = new Bin("test");
        Element e1 = ElementFactory.make("fakesrc", "source");
        Element e2 = ElementFactory.make("fakesink", "sink");
        bin.addMany(e1, e2);
        List<Element> elements = bin.getElements();
        assertFalse("Bin returned empty list from getElements", elements.isEmpty());
        assertTrue("Element list does not contain e1", elements.contains(e1));
        assertTrue("Element list does not contain e2", elements.contains(e2));
    }
    @Test
    public void testGetSinks() throws Exception {
        Bin bin = new Bin("test");
        Element e1 = ElementFactory.make("fakesrc", "source");
        Element e2 = ElementFactory.make("fakesink", "sink");
        bin.addMany(e1, e2);
        List<Element> elements = bin.getSinks();
        assertFalse("Bin returned empty list from getElements", elements.isEmpty());
        assertTrue("Element list does not contain sink", elements.contains(e2));
    }
    
    @Test
    public void testGetSources() throws Exception {
        Bin bin = new Bin("test");
        Element e1 = ElementFactory.make("fakesrc", "source");
        Element e2 = ElementFactory.make("fakesink", "sink");
        bin.addMany(e1, e2);
        List<Element> elements = bin.getSources();
        assertFalse("Bin returned empty list from getElements", elements.isEmpty());
        assertTrue("Element list does not contain source", elements.contains(e1));
    }
    @Test
    public void testGetElementByName() throws Exception {
        Bin bin = new Bin("test");
        Element e1 = ElementFactory.make("fakesrc", "source");
        Element e2 = ElementFactory.make("fakesink", "sink");
        bin.addMany(e1, e2);
        
        assertEquals("source not returned", e1, bin.getElementByName("source"));
        assertEquals("sink not returned", e2, bin.getElementByName("sink"));
    }
    
    @Test
    public void testElementAddedCallback() throws Exception {
        Bin bin = new Bin("test");
        final Element e1 = ElementFactory.make("fakesrc", "source");
        final Element e2 = ElementFactory.make("fakesink", "sink");
        final AtomicInteger added = new AtomicInteger(0);
        
        bin.connect(new Bin.ELEMENTADDED() {
            public void elementAdded(Bin bin, Element elem) {
                if (elem == e1 || elem == e2) {
                    added.incrementAndGet();
                }
            }
        });
        bin.addMany(e1, e2);
        
        assertEquals("Callback not called", 2, added.get());
    }
    @Test
    public void testElementRemovedCallback() {
        Bin bin = new Bin("test");
        final Element e1 = ElementFactory.make("fakesrc", "source");
        final Element e2 = ElementFactory.make("fakesink", "sink");
        final AtomicInteger removed = new AtomicInteger(0);
        
        bin.connect(new Bin.ELEMENTADDED() {
            public void elementAdded(Bin bin, Element elem) {
                if (elem == e1 || elem == e2) {
                    removed.incrementAndGet();
                }
            }
        });
        bin.addMany(e1, e2);
        
        assertEquals("Callback not called", 2, removed.get());
    }
}

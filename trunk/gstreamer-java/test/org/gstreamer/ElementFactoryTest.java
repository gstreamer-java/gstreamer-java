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
 * @author wayne
 */
public class ElementFactoryTest {
    
    public ElementFactoryTest() {
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
    @Test
    public void testMakeFakesrc() {
        Element e = ElementFactory.make("fakesrc", "source");
        assertNotNull("Failed to create fakesrc", e);
    }
    @Test
    public void testMakeBin() {
        Element e = ElementFactory.make("bin", "bin");
        assertNotNull("Failed to create bin", e);
        assertTrue("Element not a subclass of Bin", e instanceof Bin);
    }
    @Test
    public void testMakePipeline() {
        Element e = ElementFactory.make("pipeline", "bin");
        assertNotNull("Failed to create pipeline", e);
        assertTrue("Element not a subclass of Bin", e instanceof Bin);
        assertTrue("Element not a subclass of Pipeline", e instanceof Pipeline);
    }
    @Test
    public void testMakePlaybin() {
        Element e = ElementFactory.make("playbin", "bin");
        assertNotNull("Failed to create playbin", e);
        assertTrue("Element not a subclass of Bin", e instanceof Bin);
        assertTrue("Element not a subclass of Pipeline", e instanceof Pipeline);
        assertTrue("Element not a subclass of PlayBin", e instanceof PlayBin);
    }
    @Test
    public void testCreateFakesrc() {
        ElementFactory factory = ElementFactory.find("fakesrc");
        assertNotNull("Could not locate fakesrc factory", factory);
        Element e = factory.create("source");
        assertNotNull("Failed to create fakesrc", e);
    }
    @Test
    public void testCreateBin() {
        ElementFactory factory = ElementFactory.find("bin");
        assertNotNull("Could not locate bin factory", factory);
        Element e = factory.create("bin");
        assertNotNull("Failed to create bin", e);
        assertTrue("Element not a subclass of Bin", e instanceof Bin);
    }
    @Test
    public void testCreatePipeline() {
        ElementFactory factory = ElementFactory.find("pipeline");
        assertNotNull("Could not locate pipeline factory", factory);
        Element e = factory.create("bin");
        assertNotNull("Failed to create pipeline", e);
        assertTrue("Element not a subclass of Bin", e instanceof Bin);
        assertTrue("Element not a subclass of Pipeline", e instanceof Pipeline);
    }
    @Test
    public void testCreatePlaybin() {
        ElementFactory factory = ElementFactory.find("playbin");
        assertNotNull("Could not locate pipeline factory", factory);
        System.out.println("PlayBin factory name=" + factory.getName());
        Element e = factory.create("bin");
        assertNotNull("Failed to create playbin", e);
        assertTrue("Element not a subclass of Bin", e instanceof Bin);
        assertTrue("Element not a subclass of Pipeline", e instanceof Pipeline);
        assertTrue("Element not a subclass of PlayBin", e instanceof PlayBin);
    }
    public boolean waitGC(WeakReference<? extends Object> ref) throws InterruptedException {
        System.gc();
        for (int i = 0; ref.get() != null && i < 10; ++i) {
            Thread.sleep(10);
            System.gc();
        }
        return ref.get() == null;
    }
    // gst_element_factory_find returns objects with a ref_count of 2, so the proxy never gets GC'd
    //@Test
    public void testGarbageCollection() throws Throwable {
        ElementFactory factory = ElementFactory.find("fakesrc");
        assertNotNull("Could not locate fakesrc factory", factory);
        WeakReference<ElementFactory> ref = new WeakReference<ElementFactory>(factory);
        factory = null;
        assertTrue("Factory not garbage collected", waitGC(ref));
    }
    
    @Test
    public void testMakeGarbageCollection() throws Throwable {
        Element e = ElementFactory.make("fakesrc", "test");
        WeakReference<Element> ref = new WeakReference<Element>(e);
        e = null;
        assertTrue("Element not garbage collected", waitGC(ref));
        
    }
    @Test
    public void testCreateGarbageCollection() throws Throwable {
        ElementFactory factory = ElementFactory.find("fakesrc");
        assertNotNull("Could not locate fakesrc factory", factory);
        Element e = factory.create("bin");
        WeakReference<Element> ref = new WeakReference<Element>(e);
        e = null;
        assertTrue("Element not garbage collected", waitGC(ref));
    }
}

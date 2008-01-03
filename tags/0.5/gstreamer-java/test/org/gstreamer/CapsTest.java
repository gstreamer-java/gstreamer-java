/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gstreamer;

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
public class CapsTest {

    public CapsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("CapsTest", new String[] {});
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        Gst.deinit();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    //@Test
    public void capsMerge() {
        Caps caps1 = new Caps("video/x-raw-rgb, bpp=32, depth=24");
        Caps caps2 = new Caps("video/x-raw-rgb, width=640, height=480");
        caps1.merge(caps2);
        // Verify that the victim caps were invalidated and cannot be used.
        try {
            caps2.toString();
            fail("merged caps not invalidated");
        } catch (IllegalStateException ex) {}
        
        boolean widthFound = false, heightFound = false;
        for (int i = 0; i < caps1.size(); ++i) {
            Structure s = caps1.getStructure(i);
            if (s.hasIntField("width")) {
                widthFound = true;
            }
            if (s.hasIntField("height")) {
                heightFound = true;
            }
        }
        assertTrue("width not appended", widthFound);
        assertTrue("height not appended", heightFound);
    }
    
    @Test
    public void capsAppend() {
        Caps caps1 = new Caps("video/x-raw-rgb, bpp=32, depth=24");
        Caps caps2 = new Caps("video/x-raw-rgb, width=640, height=480");
        caps1.append(caps2);
        // Verify that the victim caps were invalidated and cannot be used.
        try {
            caps2.toString();
            fail("appended caps not invalidated");
        } catch (IllegalStateException ex) {}
        boolean widthFound = false, heightFound = false;
        for (int i = 0; i < caps1.size(); ++i) {
            Structure s = caps1.getStructure(i);
            if (s.hasIntField("width")) {
                widthFound = true;
            }
            if (s.hasIntField("height")) {
                heightFound = true;
            }
        }
        assertTrue("width not appended", widthFound);
        assertTrue("height not appended", heightFound);
    }
}
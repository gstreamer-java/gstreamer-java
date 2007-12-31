/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gstreamer;

import java.util.List;
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
public class ElementTest {

    public ElementTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("ElementTest", new String[] {});
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
    @Test
    public void getPads() {
        Element element = ElementFactory.make("fakesink", "fs");
        List<Pad> pads = element.getPads();
        assertTrue("no pads found", !pads.isEmpty());
    }
    @Test
    public void getSinkPads() {
        Element element = ElementFactory.make("fakesink", "fs");
        List<Pad> pads = element.getSinkPads();
        assertTrue("no pads found", !pads.isEmpty());
    }
    @Test
    public void getSrcPads() {
        Element element = ElementFactory.make("fakesrc", "fs");
        List<Pad> pads = element.getSrcPads();
        assertTrue("no pads found", !pads.isEmpty());
    }
    @Test 
    public void setState() {
        Element element = ElementFactory.make("fakesrc", "fs");
        // This should exercise EnumMapper.intValue()
        element.setState(State.PLAYING);
    }
    @Test 
    public void getState() {
        Element element = ElementFactory.make("fakesrc", "fs");
        // This should exercise EnumMapper.intValue()
        element.setState(State.PLAYING);
        State state = element.getState(-1);
        assertEquals("Element state not set correctly", State.PLAYING, element.getState(-1));
    }
}
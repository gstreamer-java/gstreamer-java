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
public class GhostPadTest {

    public GhostPadTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("GhostPadTest", new String[] {});
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
    public void newGhostPad() {
        Element fakesink = ElementFactory.make("fakesink", "fs");
        GhostPad gpad = new GhostPad("ghostsink", fakesink.getStaticPad("sink"));
    }
}
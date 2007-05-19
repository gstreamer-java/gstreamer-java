/*
 * InitTest.java
 * JUnit 4.x based test
 */

package org.gstreamer;

import org.gstreamer.Gst;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class InitTest {
    
    public InitTest() {
        
    }
    @Test
    public void testInit() {
        String[] args = Gst.init("foo", new String[] { "--gst-plugin-spew" });
        assertTrue(args.length == 0);
        Gst.deinit();
    }
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}

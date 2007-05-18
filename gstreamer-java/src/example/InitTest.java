/*
 * GstInitTest.java
 */

package example;

import org.gstreamer.Gst;

/**
 *
 */
public class InitTest {
    
    /** Creates a new instance of GstInitTest */
    public InitTest() {
    }
    public static void main(String[] args) {
        args = Gst.init("foo", args);
        System.out.println("Gstreamer initialized!");
    }
}

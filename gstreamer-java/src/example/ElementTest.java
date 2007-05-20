/*
 * GstElementTest.java
 */

package example;

import com.sun.jna.Function;
import org.gstreamer.Gst;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GMainLoop;

/**
 *
 */
public class ElementTest {
    
    /** Creates a new instance of GstElementTest */
    public ElementTest() {
    }
    public static void main(String[] args) {
        // Load some gstreamer dependencies
        GMainLoop loop = new GMainLoop();
        args = Gst.init("foo", args);
        System.out.println("Creating fakesrc element");
        Element fakesrc = ElementFactory.make("fakesrc", "fakesrc");
        System.out.println("fakesrc element created");
        System.out.println("Creating fakesink element");
        Element fakesink = ElementFactory.make("fakesink", "fakesink");
        System.out.println("fakesink element created");
    }
}

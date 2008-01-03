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

package example;

import org.gstreamer.Gst;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;

/**
 *
 */
public class ElementTest {
    
    /** Creates a new instance of GstElementTest */
    public ElementTest() {
    }
    public static void main(String[] args) {
        // Load some gstreamer dependencies
        args = Gst.init("foo", args);
        System.out.println("Creating fakesrc element");
        Element fakesrc = ElementFactory.make("fakesrc", "fakesrc");
        System.out.println("fakesrc element created");
        System.out.println("Element name = " + fakesrc.getName());
        System.out.println("Creating fakesink element");
        Element fakesink = ElementFactory.make("fakesink", "fakesink");
        System.out.println("fakesink element created");
        System.out.println("Element name = " + fakesink.getName());
    }
}

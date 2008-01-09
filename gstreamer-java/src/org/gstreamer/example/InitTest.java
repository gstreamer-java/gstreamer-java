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

package org.gstreamer.example;

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

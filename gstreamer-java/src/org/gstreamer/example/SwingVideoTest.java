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

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import org.gstreamer.Bin;
import org.gstreamer.MainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swing.GstVideoComponent;

/**
 *
 */
public class SwingVideoTest {
    
    /** Creates a new instance of SwingVideoTest */
    public SwingVideoTest() {
    }
    public static void main(String[] args) {
        int width = 720, height = 576;
        //System.setProperty("sun.java2d.opengl", "true");
        // Quartz is abysmally slow at scaling video for some reason, so turn it off.
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        
        args = Gst.init("SwingVideoTest", args);        
        System.out.println("Creating MainLoop");
        MainLoop loop = new MainLoop();
        Element fakesrc, flt, videosink;
        
        /* setup pipeline */
        System.out.println("Creating pipeline");
        Pipeline pipeline = new Pipeline("pipeline");
        System.out.println("Pipeline created");
        System.out.flush();
        pipeline.connect(new Bin.ELEMENT_ADDED() {
            public void  elementAdded(Bin bin, Element elem) {
                System.out.println("Element " + elem + " added");
            }
        });
        
        System.out.println("Creating videotestsrc");
        fakesrc = ElementFactory.make("videotestsrc", "source");
        System.out.println("Creating capsfilter");
        flt = ElementFactory.make("capsfilter", "flt");
        
        System.out.println("Creating caps");
        Caps fltcaps = new Caps("video/x-raw-yuv, width=" + width + ", height=" + height +
                ", bpp=32, depth=32, framerate=50/1");
        System.out.println("Setting caps");
        flt.setCaps(fltcaps);
        System.out.println("Creating GstVideoComponent");
        GstVideoComponent panel = new GstVideoComponent();
        panel.setPreferredSize(new Dimension(width, height));
        JFrame frame = new JFrame("Swing Video Test");
        frame.add(panel, BorderLayout.CENTER);
        
        videosink = panel.getElement();
        
        System.out.println("Adding elements to pipeline");
        pipeline.addMany(fakesrc, flt, videosink);
        fakesrc.link(flt, videosink);
        pipeline.setState(State.PLAYING);
        loop.startInBackground();
        frame.setSize(width, height);
        //frame.getRootPane().setDoubleBuffered(false);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

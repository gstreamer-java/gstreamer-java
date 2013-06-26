/* 
 * Copyright (c) 2007, 2008 Wayne Meissner
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.gstreamer.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.swing.VideoComponent;

/**
 *
 */
public class NavigationTest {
    private static Pipeline pipeline;
    private static final int width = 720, height = 576;
    public static void main(String[] args) {
        
        //System.setProperty("sun.java2d.opengl", "true");
        // Quartz is abysmally slow at scaling video for some reason, so turn it off.
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        
        args = Gst.init("SwingVideoTest", args);        
        System.out.println("Creating MainLoop");
        
        /* setup pipeline */
        System.out.println("Creating pipeline");
        pipeline = new Pipeline("pipeline");
        System.out.println("Pipeline created");
        System.out.flush();
        
        System.out.println("Creating videotestsrc");
        final Element fakesrc = ElementFactory.make("videotestsrc", "source");
        System.out.println("Creating capsfilter");
        final Element flt = ElementFactory.make("capsfilter", "flt");
        
        System.out.println("Creating caps");
        Caps fltcaps = new Caps("video/x-raw-yuv, width=" + width + ", height=" + height +
                ", bpp=32, depth=24, framerate=30/1");
        System.out.println("Setting caps");
        flt.setCaps(fltcaps);
        final Element navtest = ElementFactory.make("navigationtest", "navtest");
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                System.out.println("Creating GstVideoComponent");
                final VideoComponent videoComponent = new VideoComponent();
                videoComponent.setPreferredSize(new Dimension(width, height));
                JFrame frame = new JFrame("Swing Video Test");
                frame.add(videoComponent, BorderLayout.CENTER);
                //
                // Propagate key events.  This could be done with focus, but this
                // test uses the same code as used in VideoPlayer.
                //
                frame.addKeyListener(new KeyAdapter() {

                    @Override
					public void keyPressed(KeyEvent evt) {
                        for (KeyListener l : videoComponent.getKeyListeners()) {
                            l.keyPressed(evt);
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent evt) {
                        for (KeyListener l : videoComponent.getKeyListeners()) {
                            l.keyReleased(evt);
                        }
                    }
                    
                });
                
                Element videosink = videoComponent.getElement();

                System.out.println("Adding elements to pipeline");
                pipeline.addMany(fakesrc, flt, navtest, videosink);
                Element.linkMany(fakesrc, flt, navtest, videosink);
                pipeline.play();

                frame.setSize(width, height);
                //frame.getRootPane().setDoubleBuffered(false);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}

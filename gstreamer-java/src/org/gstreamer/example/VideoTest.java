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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.swing.VideoComponent;

/**
 * A Simple videotest example.
 */
public class VideoTest {
    public VideoTest() {
    }
    private static Pipeline pipe;
    public static void main(String[] args) {
        // Quartz is abysmally slow at scaling video for some reason, so turn it off.
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        
        args = Gst.init("SwingVideoTest", args);
        
        pipe = new Pipeline("pipeline");
        final Element videosrc = ElementFactory.make("videotestsrc", "source");
        final Element videofilter = ElementFactory.make("capsfilter", "flt");
        videofilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576"
                + ", bpp=32, depth=32, framerate=25/1"));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Create the video component and link it in
                VideoComponent videoComponent = new VideoComponent();
                Element videosink = videoComponent.getElement();
                pipe.addMany(videosrc, videofilter, videosink);
                Element.linkMany(videosrc, videofilter, videosink);
                
                // Now create a JFrame to display the video output
                JFrame frame = new JFrame("Swing Video Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(videoComponent, BorderLayout.CENTER);
                videoComponent.setPreferredSize(new Dimension(720, 576));
                frame.pack();
                frame.setVisible(true);
                
                // Start the pipeline processing
                pipe.play();
            }
        });
    }
}

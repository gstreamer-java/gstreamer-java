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
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.gstreamer.Gst;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.swing.VideoComponent;

/**
 * A basic video player.
 */
public class VideoPlayer {
    public static void main(String[] args) {
        //
        // Initialize the gstreamer framework, and let it interpret any command
        // line flags it is interested in.
        //
        args = Gst.init("VideoPlayer", args);
        
        if (args.length < 1) {
            System.out.println("Usage: VideoPlayer <file to play>");
            System.exit(1);
        }
        
        //
        // Create a PlayBin2 to play the media file.  A PlayBin2 is a Pipeline that
        // creates all the needed elements and automatically links them together.
        //
        final PlayBin2 playbin = new PlayBin2("VideoPlayer");
        
        // Set the file to play
        playbin.setInputFile(new File(args[0]));

        //
        // We now have to do the rest in the context of the Swing EDT, because
        // we are constructing Swing components.
        //
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                //
                // A VideoComponent displays video in a lightweight swing component
                //
                VideoComponent videoComponent = new VideoComponent();
                
                // Add the video component as the playbin video output
                playbin.setVideoSink(videoComponent.getElement());
                
                // Start the pipeline playing
                playbin.play();
                
                //
                // Initialise the top-level frame and add the video component
                //
                JFrame frame = new JFrame("VideoPlayer");
                frame.getContentPane().add(videoComponent, BorderLayout.CENTER);
                frame.setPreferredSize(new Dimension(640, 480));
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        
        //
        // Wait until Gst.quit() is called.
        //
        Gst.main();
        playbin.stop();
    }
}

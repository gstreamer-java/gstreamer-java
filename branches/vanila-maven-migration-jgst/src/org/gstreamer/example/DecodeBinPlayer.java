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

import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.Element.PAD_ADDED;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.elements.DecodeBin2;
import org.gstreamer.swing.VideoComponent;

/**
 * An example of playing audio/video files using a decodebin and manually linking
 * the pads, instead of using a playbin element.
 */
public class DecodeBinPlayer {
    static final String name = "DecodeBinPlayer";
    static JFrame frame;
    static VideoComponent videoComponent;
    static Pipeline pipe;
    
    public static void main(String[] args) {
        // Quartz is abysmally slow at scaling video for some reason, so turn it off.
        System.setProperty("apple.awt.graphics.UseQuartz", "false");
        
        args = Gst.init(name, args);
        if (args.length < 1) {
            System.err.println("Usage: " + name + " <filename>");
            System.exit(1);
        }
        Element src = ElementFactory.make("filesrc", "Input File");
        src.set("location", args[0]);
        
        DecodeBin2 decodeBin = new DecodeBin2("Decode Bin");
        pipe = new Pipeline("main pipeline");
        Element decodeQueue = ElementFactory.make("queue", "Decode Queue");
        pipe.addMany(src, decodeQueue, decodeBin);
        Element.linkMany(src, decodeQueue, decodeBin);

        /* create audio output */
        final Bin audioBin = new Bin("Audio Bin");

        Element conv = ElementFactory.make("audioconvert", "Audio Convert");
        Element resample = ElementFactory.make("audioresample", "Audio Resample");
        Element sink = ElementFactory.make("autoaudiosink", "sink");
        audioBin.addMany(conv, resample, sink);
        Element.linkMany(conv, resample, sink);
        audioBin.addPad(new GhostPad("sink", conv.getStaticPad("sink")));
        pipe.add(audioBin);
        decodeBin.connect(new PAD_ADDED() {
			public void padAdded(Element element, Pad pad) {
                /* only link once */
                if (pad.isLinked()) {
                    return;
                }
                /* check media type */
                Caps caps = pad.getCaps();
                Structure struct = caps.getStructure(0);
                if (struct.getName().startsWith("audio/")) {
                    System.out.println("Linking audio pad: " + struct.getName());
                    pad.link(audioBin.getStaticPad("sink"));
                } else if (struct.getName().startsWith("video/")) {
                    System.out.println("Linking video pad: " + struct.getName());
                    pad.link(videoComponent.getElement().getStaticPad("sink"));
                    
                    // Make the video frame visible
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            frame.setVisible(true);
                        }
                    });
                } else {
                    System.out.println("Unknown pad [" + struct.getName() + "]");
                }
			}
		});
        Bus bus = pipe.getBus();
        
        bus.connect(new Bus.ERROR() {
            public void errorMessage(GstObject source, int code, String message) {
                System.out.println("Error: code=" + code + " message=" + message);
            }
        });
        bus.connect(new Bus.EOS() {

            public void endOfStream(GstObject source) {
                pipe.stop();
                System.exit(0);
            }

        });
        //
        // Do the remainder of the initialization on the Swing EDT
        //
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                frame = new JFrame("DecodeBin Player");

                videoComponent = new VideoComponent();
                videoComponent.setPreferredSize(new Dimension(640, 480));

                frame.add(videoComponent, BorderLayout.CENTER);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                // Wait until a video pad is linked to make it visible
                frame.setVisible(false);
                pipe.add(videoComponent.getElement());
                pipe.play();
            }
        });
        
    }
}

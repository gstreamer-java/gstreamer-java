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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFrame;
import org.gstreamer.GMainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.PlayBin;
import org.gstreamer.State;
import org.gstreamer.swing.GstVideoComponent;

/**
 *
 */
public class SwingPlayer {
    
    /** Creates a new instance of SwingPlayer */
    public SwingPlayer() {
    }
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");
        JFrame frame = new JFrame("Swing Test");
        args = Gst.init("Swing Player", args);
        
        GMainLoop loop = new GMainLoop();
        PlayBin playbin = new PlayBin("play");
        GstVideoComponent panel = new GstVideoComponent();
        panel.setPreferredSize(new Dimension(640, 480));
        frame.add(panel, BorderLayout.CENTER);
        
        if (args.length < 1) {
            System.err.println("Usage: SwingPlayer <filename>");
            System.exit(1);
        }
        playbin.setInputFile(new File(args[0]));
        
        Element xsink = ElementFactory.make("ximagesink", "xsink");
        //Element xvsink = ElementFactory.make("xvimagesink", "xvsink");
        //Element sdlsink = ElementFactory.make("sdlvideosink", "sdlsink");
        //Element audio = ElementFactory.make("gconfaudiosink", "audio");
        //playbin.setAudioSink(audio);
        
        playbin.setVideoSink(panel.getElement());
        //playbin.setVideoSink(xsink);
        //playbin.setVideoSink(getGLSink());
        
        playbin.setState(State.PLAYING);
        loop.startInBackground();
        frame.setSize(640, 480);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    private static Element getGLSink() {
        Bin glbin = new Bin("glbin");
        Element glsink = ElementFactory.make("glimagesink", "glsink");
        //        Element videorate = ElementFactory.make("videorate", "video rate limiter");
        //glbin.add(videorate);
        Element rateflt = ElementFactory.make("capsfilter", "rateflt");
        rateflt.setCaps(new Caps("video/x-raw-rgb"));
        //        glbin.add(rateflt);
        glbin.add(glsink);
        //rateflt.link(glsink);
        glsink.setCaps(new Caps("framerate=25/1"));
        glbin.addPad(new GhostPad("sink", glsink.getPad("sink")));
        glsink.set("sync", true);
        return glbin;
    }
}

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
import org.gstreamer.swing.GstVideoPlayer;

/**
 *
 */
public class SwingPlayer {
    
    /** Creates a new instance of SwingPlayer */
    public SwingPlayer() {
    }
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");
        if (args.length < 1) {
            System.err.println("Usage: SwingPlayer <filename>");
            System.exit(1);
        }
        
        args = Gst.init("Swing Player", args);
        GMainLoop loop = new GMainLoop();
        
        JFrame frame = new JFrame("Swing Test");        
        
        GstVideoPlayer player = new GstVideoPlayer(new File(args[0]));
        player.setPreferredSize(new Dimension(640, 480));
        player.setControlsVisible(true);
        frame.add(player, BorderLayout.CENTER);
        player.play();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        loop.startInBackground();
    }
}

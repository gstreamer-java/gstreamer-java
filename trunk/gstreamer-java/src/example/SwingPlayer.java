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
import javax.swing.SwingUtilities;
import org.gstreamer.MainLoop;
import org.gstreamer.Gst;
import org.gstreamer.media.MediaPlayer;
import org.gstreamer.media.event.EndOfMediaEvent;
import org.gstreamer.media.event.MediaAdapter;
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
        
        args = Gst.init("Swing Player", args);
        if (args.length < 1) {
            System.err.println("Usage: SwingPlayer <filename>");
            System.exit(1);
        }
        final MainLoop loop = new MainLoop();
        final String[] playList = args;
        final String file = args[0];

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                JFrame frame = new JFrame("Swing Test");
                
                final GstVideoPlayer player = new GstVideoPlayer(file);
                player.setPreferredSize(new Dimension(640, 480));
                player.setControlsVisible(true);
                player.getMediaPlayer().addMediaListener(new MediaAdapter() {
                    int next = 0;
                    public void endOfMedia(EndOfMediaEvent evt) {
                        System.out.println("Finished playing");
                        if (++next >= playList.length) {
                            next = 0;
                        }
                        System.out.println("Playing next file: " + playList[next]);
                        ((MediaPlayer) evt.getSource()).setURI(new File(playList[next]).toURI());
                    }
                });
                frame.add(player, BorderLayout.CENTER);
                player.getMediaPlayer().play();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }  
        });
        loop.run();
    }
}
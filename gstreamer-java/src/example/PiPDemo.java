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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gstreamer.MainLoop;
import org.gstreamer.Gst;
import org.gstreamer.swing.GstVideoPlayer;

/**
 *
 */
public class PiPDemo {
    
    /** Creates a new instance of SwingPlayer */
    public PiPDemo() {
    }
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");
        
        args = Gst.init("Swing Player", args);
        if (args.length < 1) {
            System.err.println("Usage: SwingPlayer <filename>");
            System.exit(1);
        }
        final String[] files = args;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Swing Video Player");
                JLayeredPane layeredPane = new JLayeredPane();
                frame.add(layeredPane, BorderLayout.CENTER);
                final GstVideoPlayer full = new GstVideoPlayer(files[0]);
                full.setPreferredSize(new Dimension(1024, 768));
                full.setControlsVisible(true);
                full.setBounds(0, 0, 640, 480);
                full.getMediaPlayer().play();
                layeredPane.add(full, JLayeredPane.DEFAULT_LAYER);
                layeredPane.setPreferredSize(new Dimension(640, 480));
                
                // Add a panel just above the main video
                final JPanel pip = new JPanel();
                pip.setOpaque(false);
                pip.setBackground(new Color(0, 0, 0, 0));
                layeredPane.add(pip, new Integer(1));
                pip.setBounds(0, 0, 640, 480);
                pip.setLayout(new FlowLayout());
                layeredPane.addComponentListener(new ComponentAdapter() {

                    @Override
                    public void componentResized(ComponentEvent e) {
                        JComponent c = (JComponent) e.getComponent();
                        Rectangle r = c.getBounds();
                        full.setBounds(r);
                        pip.setBounds(r);
                        c.revalidate();
                    }
                    
                });
                for (int i = 1; i < files.length; ++i) {
                    String uri = files[i];                    
                    final GstVideoPlayer player = new GstVideoPlayer(uri);
                    player.setPreferredSize(new Dimension(200, 150));
                    player.setAlpha(0.4f);
                    player.setOpaque(false);
                    player.setControlsVisible(false);
                    pip.add(player);
                    // Stagger the start times, so gstreamer doesn't choke
                    javax.swing.Timer timer = new javax.swing.Timer(1000 * i, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            player.getMediaPlayer().play();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        new MainLoop().run();
    }
}
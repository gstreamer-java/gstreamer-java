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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.gstreamer.MainLoop;
import org.gstreamer.Gst;
import org.gstreamer.swing.GstVideoPlayer;

/**
 *
 */
public class ButtonDemo {
    static final String name = "ButtonDemo";
    /** Creates a new instance of SwingPlayer */
    public ButtonDemo() {
    }
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");
        
        args = Gst.init(name, args);
        if (args.length < 1) {
            System.err.println("Usage: " + name + " <filename>");
            System.exit(1);
        }
        final File[] files = new File[args.length];
        for (int i = 0; i < args.length; ++i) {
            files[i] = new File(args[i]);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                JFrame frame = new JFrame(name);
                
                JPanel panel = new JPanel();
                //frame.add(new JScrollPane(panel), BorderLayout.CENTER);
                frame.add(panel);
                panel.setLayout(new FlowLayout(FlowLayout.LEFT));
                for (int i = 0; i < files.length; ++i) {
                    final File file = files[i];
                    final GstVideoPlayer player = new GstVideoPlayer(file);
                    player.setPreferredSize(new Dimension(160, 100));
                    player.setControlsVisible(false);
                    JButton button = new JButton(file.getName());
                    button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
                    button.add(player);
                   
                    button.addActionListener(new ActionListener() {
                        boolean playing = true;
                        public void actionPerformed(ActionEvent evt) {
                            System.out.println("Button " + file.getName() + " clicked");
                            if (!playing) {
                                player.getMediaPlayer().play();
                                playing = true;
                            } else {
                                playing = false;
                                player.getMediaPlayer().pause();
                            }
                        }
                    });
                    button.setVerticalTextPosition(SwingConstants.BOTTOM);
                    JLabel label = new JLabel(file.getName());
                    label.setHorizontalTextPosition(SwingConstants.CENTER);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    JPanel box = new JPanel();
                    box.setLayout(new BorderLayout());
                    box.add(label, BorderLayout.SOUTH);
                    box.setOpaque(false);
                    button.add(box);
                    panel.add(button);
                    // Delay the startup a bit so gstreamer doesn't get swamped
                    javax.swing.Timer timer = new javax.swing.Timer(2000 * i, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            player.getMediaPlayer().play();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        new MainLoop().run();
    }
}
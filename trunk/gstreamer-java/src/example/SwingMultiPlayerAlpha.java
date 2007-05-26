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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import org.gstreamer.GMainLoop;
import org.gstreamer.Gst;
import org.gstreamer.swing.GstVideoPlayer;

/**
 *
 */
public class SwingMultiPlayerAlpha {
    
    /** Creates a new instance of SwingPlayer */
    public SwingMultiPlayerAlpha() {
    }
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");
        
        args = Gst.init("Swing Player", args);
        if (args.length < 1) {
            System.err.println("Usage: SwingPlayer <filename>");
            System.exit(1);
        }
        final File[] files = new File[args.length];
        for (int i = 0; i < args.length; ++i) {
            files[i] = new File(args[i]);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame window = new JFrame("Swing Video Player");
                JDesktopPane panel = new JDesktopPane();
                window.add(panel);
                for (int i = files.length - 1; i >= 0; --i) {
                    File file = files[i];
                    JInternalFrame frame = new JInternalFrame(file.getName());
                    panel.add(frame);
                    frame.setResizable(true);
                    frame.setClosable(true);
                    frame.setIconifiable(true);
                    frame.setMaximizable(true);
                    
                    frame.setLocation(i * 100, i * 100);
                    final float alpha = 0.6f;
                    final GstVideoPlayer player = new GstVideoPlayer(file) {
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2d = (Graphics2D) g.create();
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                            g2d.setColor(Color.BLACK);
                            g2d.fillRect(0, 0, getWidth(), getHeight());
                            g2d.dispose();
                        }
                    };
                    player.setPreferredSize(new Dimension(640, 480));
                    player.setControlsVisible(true);
                    player.setOpaque(false);
                    player.setAlpha(alpha);
                    
                    frame.setOpaque(false);
                    frame.setContentPane(player);
                    frame.pack();
                    
                    frame.setVisible(true);
                    javax.swing.Timer timer = new javax.swing.Timer(5000 * i, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            player.play();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                window.setPreferredSize(new Dimension(1024, 768));
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.pack();
                window.setVisible(true);
            }
        });
        new GMainLoop().run();
    }
  
}
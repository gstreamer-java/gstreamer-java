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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.swing.VideoPlayer;

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
                    final VideoPlayer player = new VideoPlayer(file) {

                        private static final long serialVersionUID = 4925431893247320169L;

                        @Override
						protected void paintComponent(Graphics g) {
                            Graphics2D g2d = (Graphics2D) g.create();
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                            g2d.setColor(Color.BLACK);
                            g2d.fillRect(0, 0, getWidth(), getHeight());
                            g2d.dispose();
                        }
                    };
                    player.setPreferredSize(new Dimension(400, 250));
                    player.setControlsVisible(true);
                    player.setOpaque(false);
                    player.setOpacity(alpha);
                    Pipeline pipe = player.getMediaPlayer().getPipeline();
                    if (pipe instanceof PlayBin2) {
                        ((PlayBin2) pipe).setAudioSink(ElementFactory.make("fakesink", "audio"));
                    }
                    
                    frame.setOpaque(false);
                    frame.setContentPane(player);
                    frame.pack();
                    
                    frame.setVisible(true);
                    javax.swing.Timer timer = new javax.swing.Timer(5000 * i, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            player.getMediaPlayer().play();
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
    }
  
}
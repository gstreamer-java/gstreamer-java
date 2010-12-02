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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.swing.VideoPlayer;

/**
 *
 */
public class PiPDemo {
    
    /** Creates a new instance of SwingPlayer */
    public PiPDemo() {
    }
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");
        
        // Quartz is abysmally slow at scaling video for some reason, so turn it off.
        // System.setProperty("apple.awt.graphics.UseQuartz", "false");
        
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
                final VideoPlayer full = new VideoPlayer(files[0]);
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
                
                // Add a button panel above that
                final JPanel controls = new JPanel();
                controls.setBounds(full.getBounds());
                controls.setLayout(new BorderLayout());
                layeredPane.add(controls, Integer.valueOf(2));
                final JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new BorderLayout());
                JButton quit = new JButton("Quit");
                buttonPanel.add(quit, BorderLayout.WEST);
                quit.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        System.exit(0);
                    }
                });
                quit.setOpaque(false);
                controls.add(buttonPanel, BorderLayout.NORTH);
                controls.setOpaque(false);
                buttonPanel.setOpaque(false);
                
                layeredPane.addComponentListener(new ComponentAdapter() {

                    @Override
                    public void componentResized(ComponentEvent e) {
                        JComponent c = (JComponent) e.getComponent();
                        Rectangle r = c.getBounds();
                        full.setBounds(r);
                        pip.setBounds(r);
                        controls.setBounds(r);
                        c.revalidate();
                    }
                    
                });
                for (int i = 1; i < files.length; ++i) {
                    String uri = files[i];                    
                    final VideoPlayer player = new VideoPlayer(uri);
                    player.setPreferredSize(new Dimension(200, 150));
                    player.setOpacity(0.4f);
                    player.setOpaque(false);
                    player.setControlsVisible(false);
                    Pipeline pipe = player.getMediaPlayer().getPipeline();
                    // Turn off sound - otherwise everything goes slow.
                    if (pipe instanceof PlayBin2) {
                        ((PlayBin2) pipe).setAudioSink(ElementFactory.make("fakesink", "audio"));
                    }
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
    }
}
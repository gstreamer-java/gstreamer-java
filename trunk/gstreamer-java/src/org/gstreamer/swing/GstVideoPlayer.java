/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.gstreamer.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import org.gstreamer.PlayBin;

/**
 *
 */
public class GstVideoPlayer extends javax.swing.JPanel {
    
    public GstVideoPlayer(URI uri) {
        playbin = new PlayBin(uri.toString());
        playbin.setURI(uri);
        videoComponent = new GstVideoComponent();
        playbin.setVideoSink(videoComponent.getElement());
        setLayout(new BorderLayout());
        add(videoComponent, BorderLayout.CENTER);
        controls = Box.createHorizontalBox();
        add(controls, BorderLayout.SOUTH);
        
        controls.add(new JButton(playAction));
        controls.add(new JButton(pauseAction));
        controls.add(new JButton(stopAction));
        playAction.setEnabled(true);
        pauseAction.setEnabled(false);
        stopAction.setEnabled(false);
        Object oldValue = UIManager.get("Slider.paintValue");
        UIManager.put("Slider.paintValue", Boolean.FALSE);
        JSlider slider = new JSlider(new ElementPositionModel(playbin));
        UIManager.put("Slider.paintValue", oldValue);
        controls.add(slider);
        controls.setVisible(false);
    }
    public GstVideoPlayer(File file) {
        this(file.toURI());
    }
    public void setKeepAspect(boolean keepAspect) {
        videoComponent.setKeepAspect(keepAspect);
    }
    public void setControlsVisible(boolean visible) {
        controls.setVisible(visible);
        revalidate();
    }
    public void pause() {
        if (playbin.isPlaying()) {
            playbin.pause();
        }
        pauseAction.setEnabled(false);
        playAction.setEnabled(true);
        stopAction.setEnabled(false);
    }
    public void play() {
        if (!playbin.isPlaying()) {
            playbin.play();
        }
        playAction.setEnabled(false);
        pauseAction.setEnabled(true);
        stopAction.setEnabled(true);
    }
    public void stop() {
        if (playbin.isPlaying()) {
            playbin.stop();
        }
        playAction.setEnabled(true);
        pauseAction.setEnabled(false);
        stopAction.setEnabled(false);
    }
    private AbstractAction playAction = new AbstractAction("Play") {
        public void actionPerformed(ActionEvent e) {
            play();
        }
    };
    private AbstractAction pauseAction = new AbstractAction("Pause") {
        public void actionPerformed(ActionEvent e) {
            pause();
        }
    };
    private AbstractAction stopAction = new AbstractAction("Stop") {
        public void actionPerformed(ActionEvent e) {
            stop();
        }
    };
    private PlayBin playbin;
    private JComponent controls;
    private GstVideoComponent videoComponent;
    
}

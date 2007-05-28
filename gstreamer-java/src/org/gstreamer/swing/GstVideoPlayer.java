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
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import org.gstreamer.PlayBin;
import org.gstreamer.State;

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
        controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        add(controls, BorderLayout.SOUTH);
        
        controls.add(new JButton(rewAction));
        controls.add(new JButton(togglePlayAction));
        controls.add(new JButton(fwdAction));
        controls.add(new JButton(stopAction));
        playAction.setEnabled(true);
        pauseAction.setEnabled(false);
        stopAction.setEnabled(false);
        
        Object oldValue = UIManager.get("Slider.paintValue");
        UIManager.put("Slider.paintValue", Boolean.FALSE);
        controls.add(new JSlider(positionModel = new ElementPositionModel(playbin)));
        UIManager.put("Slider.paintValue", oldValue);
        controls.setVisible(false);
    }
    public GstVideoPlayer(File file) {
        this(file.toURI());
    }
    public GstVideoPlayer(String uri) {
        this(parseURI(uri));
    }
    private static URI parseURI(String uri) {
        try {
            URI u = new URI(uri);
            if (u.getScheme() == null) {
                throw new URISyntaxException(uri, "Invalid URI scheme");
            }
            return u;
        } catch (URISyntaxException e) {
            File f = new File(uri);
            if (!f.exists()) {
                throw new IllegalArgumentException("Invalid URI/file " + uri, e);
            }
            return f.toURI();
        }
    }
    public GstVideoComponent getVideoComponent() {
        return videoComponent;
    }
    public void setAlpha(float alpha) {
        float[] c = getBackground().getColorComponents(new float[3]);
        setBackground(new Color(c[0], c[1], c[2], alpha));
        videoComponent.setAlpha(alpha);
    }
    public float getAlpha() {
        return videoComponent.getAlpha();
    }
    
    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (videoComponent != null) {
            videoComponent.setOpaque(isOpaque);
        }
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
        togglePlayAction.setState(State.PAUSED);
    }
    public void play() {
        if (!playbin.isPlaying()) {
            playbin.play();
        }
        playAction.setEnabled(false);
        pauseAction.setEnabled(true);
        stopAction.setEnabled(true);
        togglePlayAction.setState(State.PLAYING);
    }
    public void stop() {
        playbin.stop();
        playAction.setEnabled(true);
        pauseAction.setEnabled(false);
        stopAction.setEnabled(false);
        togglePlayAction.setState(State.NULL);
    }
    ImageIcon playIcon = loadIcon("actions/media-playback-start");
    ImageIcon pauseIcon = loadIcon("actions/media-playback-pause");
    ImageIcon stopIcon = loadIcon("actions/media-playback-stop");
    ImageIcon fwdIcon = loadIcon("actions/media-seek-forward");
    ImageIcon rewIcon = loadIcon("actions/media-seek-backward");
    
    private AbstractAction playAction = new AbstractAction("", playIcon) {
        public void actionPerformed(ActionEvent e) {
            play();
        }
    };
    private AbstractAction pauseAction = new AbstractAction("", pauseIcon) {
        public void actionPerformed(ActionEvent e) {
            pause();
        }
    };
    private AbstractAction stopAction = new AbstractAction("", stopIcon) {
        public void actionPerformed(ActionEvent e) {
            stop();
        }
    };
    private AbstractAction fwdAction = new AbstractAction("", fwdIcon) {
        public void actionPerformed(ActionEvent e) {
            positionModel.setValue(positionModel.getValue() + 60);
        }
    };
    private AbstractAction rewAction = new AbstractAction("", rewIcon) {
        public void actionPerformed(ActionEvent e) {
            positionModel.setValue(positionModel.getValue() - 60);
        }
    };
    private class TogglePlayAction extends AbstractAction {
        public TogglePlayAction() {
            super("", pauseIcon);
        }
        public void setState(State state) {
            switch (state) {
            case PAUSED:
            case NULL:
                putValue(SMALL_ICON, playIcon);
                break;
            case PLAYING:
            default:
                putValue(SMALL_ICON, pauseIcon);
                break;
            }
        }
        public void actionPerformed(ActionEvent e) {
            if (playbin.isPlaying()) {
                pause();
                setState(State.PAUSED);
            } else {
                play();
                setState(State.PLAYING);
            }
        }
    }
    private TogglePlayAction togglePlayAction = new TogglePlayAction();
    private static ImageIcon loadIcon(String name) {
        return loadIcon(16, name);
    }
    private static ImageIcon loadIcon(int size, String name) {
        String path = "/org/freedesktop/tango/" + size + "x" + size + "/" + name + ".png";
        URL url = GstVideoPlayer.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            throw new RuntimeException("Cannot locate icon for " + name);
        }
    }
    ElementPositionModel positionModel;
    private PlayBin playbin;
    private JComponent controls;
    private GstVideoComponent videoComponent;
    
}

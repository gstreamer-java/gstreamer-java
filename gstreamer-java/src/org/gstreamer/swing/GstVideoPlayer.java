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
import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gstreamer.ElementFactory;
import org.gstreamer.PlayBin;
import org.gstreamer.State;

/**
 *
 */
public class GstVideoPlayer extends javax.swing.JPanel {
    
    public GstVideoPlayer(URI uri) {
        playbin = new PlayBin(uri.toString());
        playbin.setURI(uri);
        //        playbin.setAudioSink(ElementFactory.make("gconfaudiosink", "audio"));
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
        playAction.setEnabled(true);
        pauseAction.setEnabled(false);
        stopAction.setEnabled(false);
        
        Object paintValue = UIManager.put("Slider.paintValue", Boolean.FALSE);
        controls.add(new JSlider(positionModel = new ElementPositionModel(playbin)));
        
        /*
         * Construct the popup for the volume slider
         */
        volumePanel = new JPanel();
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.Y_AXIS));
        
        volumeSlider = new JSlider();
        volumeSlider.addChangeListener(volumeChanged);
        volumeSlider.setOrientation(SwingConstants.VERTICAL);
        volumeSlider.setValue(playbin.getVolume());
        volumeSlider.setMaximum(100);
        volumePanel.add(new JLabel(highVolumeIcon));
        volumeSlider.setAlignmentX(0.25f);
        volumePanel.add(volumeSlider);
        volumePanel.add(new JLabel(lowVolumeIcon));
        volumePanel.validate();
        
        
        controls.add(positionLabel = new JLabel("00:00:00"));
        controls.add(volumeButton = new JToggleButton(volumeAction));
        controls.setVisible(false);
        UIManager.put("Slider.paintValue", paintValue);
        //
        // Add a listener to update the media position label
        //
        positionModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                BoundedRangeModel m = (BoundedRangeModel)e.getSource();
                int value = m.getValue();
                String text = String.format("%02d:%02d:%02d", value / 3600, (value / 60) % 60, value % 60);
                positionLabel.setText(text);
            }
            
        });
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
    private ImageIcon lowVolumeIcon = loadIcon("status/audio-volume-low");
    private ImageIcon medVolumeIcon = loadIcon("status/audio-volume-medium");
    private ImageIcon highVolumeIcon = loadIcon("status/audio-volume-high");
    private ChangeListener volumeChanged = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            JSlider s = (JSlider) e.getSource();
            playbin.setVolume(s.getValue());
            if (s.getValue() < 33) {
                volumeAction.putValue(Action.SMALL_ICON, lowVolumeIcon);
            } else if (s.getValue() < 66) {
                volumeAction.putValue(Action.SMALL_ICON, medVolumeIcon);
            } else {
                volumeAction.putValue(Action.SMALL_ICON, highVolumeIcon);
            }
        }
    };
    Popup volumePopup;
    private AbstractAction volumeAction = new AbstractAction("", loadIcon("status/audio-volume-medium")) {
        public void actionPerformed(ActionEvent e) {
            JToggleButton b = (JToggleButton) e.getSource();
            if (!b.isSelected() && volumePopup != null) {
                volumePopup.hide();
                volumePopup = null;
            } else {
                Dimension panelSize = volumePanel.getPreferredSize();
                // Right-align it with the volume button, so it pops up just above it
                Point location = new Point(0 - panelSize.width + volumeButton.getPreferredSize().width,
                        0 - panelSize.height);
                SwingUtilities.convertPointToScreen(location, volumeButton);
                volumeSlider.setValue(playbin.getVolume());
                volumePopup = PopupFactory.getSharedInstance().getPopup(volumeButton,
                        volumePanel, location.x, location.y);
                Object paintValue = UIManager.put("Slider.paintValue", Boolean.FALSE);
                volumePopup.show();
                UIManager.put("Slider.paintValue", paintValue);
            }
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
    private JLabel positionLabel;
    private JToggleButton volumeButton;
    private JPanel volumePanel;
    private JSlider volumeSlider;
    private GstVideoComponent videoComponent;
    
}

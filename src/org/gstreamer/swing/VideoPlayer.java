/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gstreamer.State;
import org.gstreamer.media.MediaPlayer;
import org.gstreamer.media.PlayBinMediaPlayer;
import org.gstreamer.media.event.EndOfMediaEvent;
import org.gstreamer.media.event.MediaAdapter;
import org.gstreamer.media.event.MediaListener;
import org.gstreamer.media.event.StartEvent;
import org.gstreamer.media.event.StopEvent;

import com.sun.jna.Platform;

/**
 *
 */
public class VideoPlayer extends javax.swing.JPanel {
    
    private static final long serialVersionUID = 1964694975227372646L;
    private static final SwingExecutorService swingExec = new SwingExecutorService();

    public VideoPlayer(MediaPlayer player) {
        mediaPlayer = player;
        videoComponent = new VideoComponent();
        mediaPlayer.setVideoSink(videoComponent.getElement());
        setLayout(new BorderLayout());
        add(videoComponent, BorderLayout.CENTER);
        controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        add(controls, BorderLayout.SOUTH);
        
        controls.add(new PopupVolumeButton(volumeModel));
        controls.add(new JButton(rewAction));
        controls.add(new JButton(togglePlayAction));
        controls.add(new JButton(fwdAction));
        playAction.setEnabled(true);
        pauseAction.setEnabled(false);
        stopAction.setEnabled(false);
        
        Object paintValue = UIManager.put("Slider.paintValue", Boolean.FALSE);
        controls.add(new JSlider(positionModel = new PipelinePositionModel(mediaPlayer.getPipeline())));
        
        UIManager.put("Slider.paintValue", paintValue);
        
        controls.add(positionLabel = new JLabel("00:00:00"));
        //
        // On MacOSX, need a spacer so the window resize control is visible
        //
        if (Platform.isMac()) {
            Box spacer = Box.createHorizontalBox();
            spacer.add(Box.createHorizontalStrut(16));
            controls.add(spacer);
        }
        controls.setVisible(false);
        
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
        mediaPlayer.addMediaListener(swingExec.wrap(MediaListener.class, mediaListener));
        // Propate key events to the video component without giving it focus
        addKeyListener(new KeyAdapter() {

            @Override
			public void keyPressed(KeyEvent evt) {
                for (KeyListener l : videoComponent.getKeyListeners()) {
                    l.keyPressed(evt);
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                for (KeyListener l : videoComponent.getKeyListeners()) {
                    l.keyReleased(evt);
                }
            }
        });
    }
    public VideoPlayer(File file) {
        this(file.toURI());
    }
    public VideoPlayer(String uri) {
        this(parseURI(uri));
    }
    public VideoPlayer(URI uri) {
        this(new PlayBinMediaPlayer("swing player", swingExec));
        mediaPlayer.setURI(uri);
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
    public VideoComponent getVideoComponent() {
        return videoComponent;
    }
    public void setOpacity(float alpha) {
        float[] c = getBackground().getColorComponents(new float[3]);
        setBackground(new Color(c[0], c[1], c[2], alpha));
        videoComponent.setOpacity(alpha);
    }
    public float getOpacity() {
        return videoComponent.getOpacity();
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
    
    public void setURI(URI uri) {
        mediaPlayer.setURI(uri);
    }
    public void setInputFile(File file) {
        mediaPlayer.setURI(file.toURI());
    }
    
    /**
     * Obtain the MediaPlayer instance used by this VideoPlayer
     * @return The MediaPlayer used
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    ImageIcon playIcon = loadIcon("actions/media-playback-start");
    ImageIcon pauseIcon = loadIcon("actions/media-playback-pause");
    ImageIcon stopIcon = loadIcon("actions/media-playback-stop");
    ImageIcon fwdIcon = loadIcon("actions/media-seek-forward");
    ImageIcon rewIcon = loadIcon("actions/media-seek-backward");
    
    private AbstractAction playAction = new AbstractAction("", playIcon) {

        private static final long serialVersionUID = 1714683114563367993L;

        public void actionPerformed(ActionEvent e) {
            mediaPlayer.play();
        }
    };
    private AbstractAction pauseAction = new AbstractAction("", pauseIcon) {

        private static final long serialVersionUID = 1045795748586088103L;

        public void actionPerformed(ActionEvent e) {
            mediaPlayer.pause();
        }
    };
    private AbstractAction stopAction = new AbstractAction("", stopIcon) {

        private static final long serialVersionUID = 881154743003615413L;

        public void actionPerformed(ActionEvent e) {
            mediaPlayer.stop();
        }
    };
    private AbstractAction fwdAction = new AbstractAction("", fwdIcon) {

        private static final long serialVersionUID = -6661129153122209564L;

        public void actionPerformed(ActionEvent e) {
            positionModel.setValue(positionModel.getValue() + 60);
        }
    };
    private AbstractAction rewAction = new AbstractAction("", rewIcon) {

        private static final long serialVersionUID = 4450598460336990513L;

        public void actionPerformed(ActionEvent e) {
            positionModel.setValue(positionModel.getValue() - 60);
        }
    };
    
    private class TogglePlayAction extends AbstractAction {

        private static final long serialVersionUID = -333774735255804279L;

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
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setState(State.PAUSED);
            } else {
                mediaPlayer.play();
                setState(State.PLAYING);
            }
        }
    }
    private MediaListener mediaListener = new MediaAdapter() {

        @Override
        public void endOfMedia(EndOfMediaEvent evt) {
            this.stop(evt);
        }

        @Override
        public void pause(StopEvent evt) {
            pauseAction.setEnabled(false);
            playAction.setEnabled(true);
            togglePlayAction.setState(State.PAUSED);
        }

        @Override
        public void start(StartEvent evt) {
            playAction.setEnabled(false);
            pauseAction.setEnabled(true);
            stopAction.setEnabled(true);
            togglePlayAction.setState(State.PLAYING);
        }

        @Override
        public void stop(StopEvent evt) {
            playAction.setEnabled(true);
            pauseAction.setEnabled(false);
            stopAction.setEnabled(false);
            togglePlayAction.setState(State.NULL);
        }
        
    };
    private TogglePlayAction togglePlayAction = new TogglePlayAction();
    private static ImageIcon loadIcon(String name) {
        return loadIcon(16, name);
    }
    private static ImageIcon loadIcon(int size, String name) {
        String path = "/org/freedesktop/tango/" + size + "x" + size + "/" + name + ".png";
        URL url = VideoPlayer.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        }
        throw new RuntimeException("Cannot locate icon for " + name);
    }
    
    private BoundedRangeModel volumeModel = new DefaultBoundedRangeModel() {

        private static final long serialVersionUID = -4728390119456427400L;

        @Override
        public int getMaximum() {
            return 100;
        }

        @Override
        public int getMinimum() {
            return 0;
        }

        @Override
        public int getValue() {
            return Math.min(100, (int) ((mediaPlayer.getVolume() * 100d) + 0.5));
        }

        @Override
        public void setValue(int percent) {
            double volume = Math.max(Math.min((double) percent, 100d), 0d) / 100.0d;
            mediaPlayer.setVolume(volume);
        }
        
    };
    PipelinePositionModel positionModel;
    private MediaPlayer mediaPlayer;
    private JComponent controls;
    private JLabel positionLabel;
    private VideoComponent videoComponent;    
}

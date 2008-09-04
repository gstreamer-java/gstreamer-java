/* 
 * Copyright (c) 2007,2008 Wayne Meissner
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

package org.gstreamer.media;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gstreamer.Bus;
import org.gstreamer.ClockTime;
import org.gstreamer.Element;
import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.media.event.DurationChangedEvent;
import org.gstreamer.media.event.EndOfMediaEvent;
import org.gstreamer.media.event.MediaListener;
import org.gstreamer.media.event.PauseEvent;
import org.gstreamer.media.event.PositionChangedEvent;
import org.gstreamer.media.event.StartEvent;
import org.gstreamer.media.event.StopEvent;

/**
 * Basic implementation of a MediaPlayer
 */
abstract public class AbstractMediaPlayer implements MediaPlayer {
    private PlayBin playbin;
    private Executor eventExecutor;
    private volatile ScheduledFuture<?> positionTimer = null;
    private Queue<URI> playList = new ConcurrentLinkedQueue<URI>();
    private State currentState = State.NULL;
    private final MediaPlayer mediaPlayer = AbstractMediaPlayer.this;
    private final Map<MediaListener, MediaListener> mediaListeners = new HashMap<MediaListener, MediaListener>();
    private final List<MediaListener> listeners = new CopyOnWriteArrayList<MediaListener>();
    
    protected AbstractMediaPlayer(Executor eventExecutor) {
        playbin = new PlayBin(getClass().getSimpleName());
        this.eventExecutor = eventExecutor;
        playbin.getBus().connect(eosSignal);
        playbin.getBus().connect(stateChanged);
    }
    
    /*
     * Handle EOS signals.  We wrap all gst signals so they are executed on a separate thread.
     */
    private Bus.EOS eosSignal = new Bus.EOS() {
        public void endOfStream(GstObject source) {
            URI next = playList.poll();
            if (next != null) {
                setURI(next);
            } else {
                final EndOfMediaEvent evt = new EndOfMediaEvent(mediaPlayer, 
                            State.PLAYING, State.NULL, State.VOID_PENDING);

                // Notify any listeners that the last media file is finished
                for (MediaListener l : getMediaListeners()) {
                    l.endOfMedia(evt);
                }
            }
            
        }
    };
    
    private final Bus.STATE_CHANGED stateChanged = new Bus.STATE_CHANGED() {
        public void stateChanged(GstObject source, State old, State newState, State pending) {
          if (false) System.out.println("stateEvent: new=" + newState 
                    + " old=" + old
                    + " pending=" + pending);
            final ClockTime position = playbin.queryPosition();
            switch (newState) {
            case PLAYING:
                if (currentState == State.NULL || currentState == State.PAUSED) {
                    for (MediaListener listener : getMediaListeners()) {
                        listener.start(new StartEvent(mediaPlayer, 
                            currentState, newState, State.VOID_PENDING, position));
                    }
                    currentState = State.PLAYING;
                }
                break;
            case PAUSED:
                if (currentState == State.PLAYING) {
                    for (MediaListener listener : getMediaListeners()) {
                        listener.pause(new PauseEvent(mediaPlayer, 
                                currentState, newState, State.VOID_PENDING, position));
                    }
                    currentState = State.PAUSED;
                }
                break;
            case NULL:
            case READY:
                if (currentState == State.PLAYING) {
                    for (MediaListener listener : getMediaListeners()) {
                        listener.stop(new StopEvent(mediaPlayer, 
                                currentState, newState, State.VOID_PENDING, position));
                    }
                    currentState = State.NULL;
                }
                break;
            }
        }
    };
    
    /**
     * Sets the sink element to use for video output.
     * 
     * @param sink The sink to use for video output.
     */
    public void setVideoSink(Element sink) {
        playbin.setVideoSink(sink);
    }
    
    /**
     * Gets the {@link Pipeline} that the MediaPlayer uses to play media.
     * 
     * @return A Pipeline
     */
    public Pipeline getPipeline() {
        return playbin;
    }
    
    /**
     * Tests if this media player is currently playing a media file.
     * 
     * @return true if a media file is being played.
     */
    public boolean isPlaying() {
        return playbin.isPlaying();
    }
    
    /**
     * Pauses playback of a media file.
     */
    public void pause() {
        if (playbin.isPlaying()) {
            playbin.pause();
        }
    }
    
    /**
     * Starts or resumes playback of a media file.
     */
    public void play() {
        if (!playbin.isPlaying()) {
            playbin.play();
        }
    }
    
    /**
     * Stops playback of a media file.
     */
    public void stop() {
        playbin.stop();
    }
    
    /**
     * Sets the media file to play.
     * 
     * @param uri The URI that describes the location of the media file.
     */
    public void setURI(URI uri) {
        State old = playbin.getState();
        playbin.setState(State.READY);
        playbin.setURI(uri);
        playbin.setState(old);
    }
    
    /**
     * Adds a uri to the playlist
     * 
     * @param uri The uri to add to the playlist.
     */
    public void enqueue(URI uri) {
        playList.add(uri);
    }
    
    /**
     * Adds a list of media files to the playlist.
     * 
     * @param playlist The list of media files to add.
     */
    public void enqueue(Collection<URI> playlist) {
        this.playList.addAll(playlist);
    }
    
    /**
     * Replaces the current play list with a new play list.
     * 
     * @param playlist The new playlist.
     */
    public void setPlaylist(Collection<URI> playlist) {
        this.playList.clear();
        this.playList.addAll(playlist);
    }
    
    /**
     * Removes a file from the play list.
     * 
     * @param uri The uri to remove.
     */
    public void remove(URI uri) {
        this.playList.remove(uri);
    }
    
    /**
     * Sets the current file to play.
     * 
     * @param file the {@link java.io.File} to play.
     */
    public void setInputFile(File file) {
        setURI(file.toURI());
    }
    /**
     * Sets the audio output volume.
     * 
     * @param volume a number between 0.0 and 1.0 representing the percentage of 
     * the maximum volume.
     */
    public void setVolume(double volume) {
        playbin.setVolume(volume);
    }
    
    /**
     * Gets the current audio output volume.
     * 
     * @return a number between 0.0 and 1.0 representing the percentage of 
     * the maximum volume.
     */
    public double getVolume() {
        return playbin.getVolume();
    }
    
    /**
     * Adds a {@link MediaListener} that will be notified of media events.
     * 
     * @param listener the MediaListener to add.
     */
    public synchronized void addMediaListener(MediaListener listener) {
        // Only run the timer when needed
        if (mediaListeners.isEmpty()) {
            positionTimer = Gst.getScheduledExecutorService().scheduleAtFixedRate(positionUpdater, 1, 1, TimeUnit.SECONDS);
        }
        // Wrap the listener in a swing EDT safe version
        MediaListener proxy = wrapListener(MediaListener.class, listener, eventExecutor);
        mediaListeners.put(listener, proxy);
        listeners.add(proxy);
    }
    
    /**
     * Adds a {@link MediaListener} that will be notified of media events.
     * 
     * @param listener the MediaListener to add.
     */
    public synchronized void removeMediaListener(MediaListener listener) {
        MediaListener proxy = mediaListeners.remove(listener);
        listeners.remove(proxy);
        // Only run the timer when needed
        
        if (mediaListeners.isEmpty() && positionTimer != null) {
            positionTimer.cancel(true);
            positionTimer = null;
        }
    }
    
    /**
     * Gets the current list of media listeners
     * @return a list of {@link MediaListener}
     */
    private List<MediaListener> getMediaListeners() {
        return listeners;
    }
    
    private Runnable positionUpdater = new Runnable() {
        private long lastPosition = 0;
        private ClockTime lastDuration = ClockTime.ZERO;
        public void run() {
            final long position = playbin.queryPosition(Format.TIME);
            final long percent = playbin.queryPosition(Format.PERCENT);
            final ClockTime duration = playbin.queryDuration();
            final boolean durationChanged = !duration.equals(lastDuration) 
                    && !duration.equals(ClockTime.ZERO)
                    && !duration.equals(ClockTime.NONE);
            lastDuration = duration;
            final boolean positionChanged = position != lastPosition && position >= 0;
            lastPosition = position;
            final PositionChangedEvent pue = new PositionChangedEvent(AbstractMediaPlayer.this, 
                    ClockTime.valueOf(position, TimeUnit.NANOSECONDS), (int) percent);
            final DurationChangedEvent due = new DurationChangedEvent(AbstractMediaPlayer.this, 
                    duration);
            for (MediaListener l : getMediaListeners()) {
                if (durationChanged) {
                    l.durationChanged(due);
                }
                if (positionChanged) {
                    l.positionChanged(pue);
                }
            }
        }
    };
    
    /**
     * Parses the URI in the String.
     * <p>
     * This method will check if the uri is a file and return a valid URI for that file.
     * 
     * @param uri the string representation of the URI.
     * @return a {@link java.net.URI}
     */
    protected static URI parseURI(String uri) {
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
    private static <T> T wrapListener(Class<T> interfaceClass, T instance, Executor executor) {
        return interfaceClass.cast(Proxy.newProxyInstance(interfaceClass.getClassLoader(), 
                new Class[]{ interfaceClass }, 
                new ExecutorInvocationProxy(instance, executor)));
    }
    
    /**
     * Provides a way of automagically executing methods on an interface on a 
     * different thread.
     */
    private static class ExecutorInvocationProxy implements InvocationHandler {

        private final Executor executor;
        private final Object object;

        public ExecutorInvocationProxy(Object object, Executor executor) {
            this.object = object;
            this.executor = executor;
        }

        public Object invoke(Object self, final Method method, final Object[] argArray) throws Throwable {
            if (method.getName().equals("hashCode")) {
                return object.hashCode();
            }
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        method.invoke(object, argArray);
                    } catch (Throwable t) {}
                }
            });
            return null;
        }
    }

}

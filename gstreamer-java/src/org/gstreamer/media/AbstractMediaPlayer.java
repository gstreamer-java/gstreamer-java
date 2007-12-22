/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.media;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import org.gstreamer.Element;
import org.gstreamer.Format;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.State;
import org.gstreamer.Time;
import org.gstreamer.Timeout;
import org.gstreamer.event.BusAdapter;
import org.gstreamer.event.BusListener;
import org.gstreamer.event.ErrorEvent;
import org.gstreamer.event.StateEvent;
import org.gstreamer.media.event.DurationChangedEvent;
import org.gstreamer.media.event.EndOfMediaEvent;
import org.gstreamer.media.event.MediaListener;
import org.gstreamer.media.event.PauseEvent;
import org.gstreamer.media.event.PositionChangedEvent;
import org.gstreamer.media.event.StartEvent;
import org.gstreamer.media.event.StopEvent;

/**
 *
 * @author wayne
 */
abstract public class AbstractMediaPlayer implements MediaPlayer {
    private PlayBin playbin;
    private Executor eventExecutor;
    private Timeout positionTimer;
    protected AbstractMediaPlayer(Executor eventExecutor) {
        playbin = new PlayBin(getClass().getSimpleName());
        this.eventExecutor = eventExecutor;
        positionTimer = new Timeout(1000, positionUpdater);
    }
    public void setVideoSink(Element sink) {
        playbin.setVideoSink(sink);
    }
    public Pipeline getPipeline() {
        return getPlayBin();
    }
    public PlayBin getPlayBin() {
        return playbin;
    }
    public boolean isPlaying() {
        return playbin.isPlaying();
    }
    public void pause() {
        if (playbin.isPlaying()) {
            playbin.pause();
        }
    }
    public void play() {
        if (!playbin.isPlaying()) {
            playbin.play();
        }
    }
    public void stop() {
        playbin.stop();
    }
    
    public void setURI(URI uri) {
        State old = playbin.getState();
        playbin.setState(State.READY);
        playbin.setURI(uri);
        playbin.setState(old);
    }
    public void setInputFile(File file) {
        setURI(file.toURI());
    }
    private class PlayerBusListener extends BusAdapter {
        private MediaListener listener;
        private State currentState = State.NULL;
        private State prevState = State.NULL;
        private final MediaPlayer mediaPlayer = AbstractMediaPlayer.this;
        
        PlayerBusListener(MediaListener listener) {
            this.listener = listener;
        }

        @Override
        public void bufferingEvent(int percent) {
            System.out.println("Buffering");
        }

        @Override
        public void durationEvent(Format format, long percent) {
            System.out.println("duration = " + percent);
        }

        @Override
        public void segmentDone(Format format, long position) {
            System.out.println("segment done=" + position);
        }

        @Override
        public void segmentStart(Format format, long position) {
            System.out.println("segment start=" + position);
        }

        @Override
        public void infoEvent(ErrorEvent e) {
            System.out.println("info event");
        }

        @Override
        public void stateEvent(StateEvent e) {
            
            if (false) System.out.println("stateEvent: new=" + e.newState 
                    + " old=" + e.oldState
                    + " pending=" + e.pendingState);
            final Time position = new Time(playbin.getPosition(Format.TIME));
            switch (e.newState) {
            case PLAYING:
                if (currentState == State.NULL || currentState == State.PAUSED) {
                    listener.start(new StartEvent(mediaPlayer, 
                            currentState, e.newState, State.VOID_PENDING, position));
                    prevState = currentState;
                    currentState = State.PLAYING;
                }
                break;
            case PAUSED:
                if (currentState == State.PLAYING) {
                    listener.pause(new PauseEvent(mediaPlayer, 
                            currentState, e.newState, State.VOID_PENDING, position));
                    prevState = currentState;
                    currentState = State.PAUSED;
                }
                break;
            case NULL:
            case READY:
                if (currentState == State.PLAYING) {
                    listener.stop(new StopEvent(mediaPlayer, 
                            currentState, e.newState, State.VOID_PENDING, position));
                    prevState = State.PLAYING;
                    currentState = State.NULL;
                }
                break;
            }
        }
        
        @Override
        public void eosEvent() {
            listener.endOfMedia(new EndOfMediaEvent(mediaPlayer, currentState, State.NULL, State.VOID_PENDING));
        }

    }
    private List<MediaListener> mediaListeners = Collections.synchronizedList(new ArrayList<MediaListener>());
    private Map<MediaListener, BusListener> mediaBusListeners = new HashMap<MediaListener, BusListener>();
    public void addMediaListener(MediaListener listener) {
        // Only run the timer when needed
        if (mediaListeners.isEmpty()) {
            positionTimer.start();
        }
        mediaListeners.add(listener);
        BusListener busListener = new PlayerBusListener(wrapListener(MediaListener.class, listener, eventExecutor));
        mediaBusListeners.put(listener, busListener);
        playbin.getBus().addBusListener(busListener);
    }
    public void removeMediaListener(MediaListener listener) {
        mediaListeners.remove(listener);
        mediaBusListeners.remove(listener);
        // Only run the timer when needed
        if (mediaListeners.isEmpty()) {
            positionTimer.stop();
        }
    }
    private Runnable positionUpdater = new Runnable() {
        private long lastPosition = 0;
        private Time lastDuration = new Time(0);
        public void run() {
            final long position = playbin.getPosition(Format.TIME);
            final long percent = playbin.getPosition(Format.PERCENT);
            final Time duration = playbin.getDuration();
            final boolean durationChanged = !duration.equals(lastDuration) 
                    && !duration.equals(Time.ZERO)
                    && !duration.equals(Time.NONE);
            lastDuration = duration;
            final boolean positionChanged = position != lastPosition && position >= 0;
            lastPosition = position;
            eventExecutor.execute(new Runnable() {
                public void run() {
                    PositionChangedEvent pue = new PositionChangedEvent(AbstractMediaPlayer.this, 
                            new Time(position), (int) percent);
                    DurationChangedEvent due = new DurationChangedEvent(AbstractMediaPlayer.this, 
                            duration);
                    MediaListener[] listeners = mediaListeners.toArray(new MediaListener[mediaListeners.size()]);
                    for (MediaListener l : listeners) {
                        if (durationChanged) {
                            l.durationChanged(due);
                        }
                        if (positionChanged) {
                            l.positionChanged(pue);
                        }
                    }

                }
            });
        }
    };
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

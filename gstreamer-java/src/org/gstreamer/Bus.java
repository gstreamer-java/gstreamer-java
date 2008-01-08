/* 
 * Copyright (C) 2007 Wayne Meissner
 * Copyright (C) 2004 Wim Taymans <wim@fluendo.com>
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

package org.gstreamer;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import java.util.EventListenerProxy;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.gstreamer.event.BusSyncHandler;
import org.gstreamer.event.EOSEvent;
import org.gstreamer.event.EOSListener;
import org.gstreamer.event.ErrorEvent;
import org.gstreamer.event.MessageEvent;
import org.gstreamer.event.MessageListener;
import org.gstreamer.event.StateChangeEvent;
import org.gstreamer.event.StateChangeListener;
import org.gstreamer.event.StateEvent;
import org.gstreamer.event.TagEvent;
import org.gstreamer.event.TagListener;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import org.gstreamer.lowlevel.GstAPI.GErrorStruct;
import static org.gstreamer.lowlevel.GstAPI.gst;
import static org.gstreamer.lowlevel.GlibAPI.glib;


/**
 * The {@link Bus} is an object responsible for delivering {@link Message}s in
 * a first-in first-out way from the streaming threads to the application.
 * <p>
 * Since the application typically only wants to deal with delivery of these
 * messages from one thread, the Bus will marshall the messages between
 * different threads. This is important since the actual streaming of media
 * is done in another thread than the application.
 * <p>
 * The Bus provides support for GSource based notifications. This makes it
 * possible to handle the delivery in the glib mainloop.
 * <p>
 * A message is posted on the bus with the gst_bus_post() method. With the
 * gst_bus_peek() and gst_bus_pop() methods one can look at or retrieve a
 * previously posted message.
 * <p>
 * The bus can be polled with the gst_bus_poll() method. This methods blocks
 * up to the specified timeout value until one of the specified messages types
 * is posted on the bus. The application can then _pop() the messages from the
 * bus to handle them.
 * <p>
 * Alternatively the application can register an asynchronous bus function
 * using gst_bus_add_watch_full() or gst_bus_add_watch(). This function will
 * install a #GSource in the default glib main loop and will deliver messages 
 * a short while after they have been posted. Note that the main loop should 
 * be running for the asynchronous callbacks.
 * <p>
 * It is also possible to get messages from the bus without any thread
 * marshalling with the {@link #setSyncHandler} method. This makes it
 * possible to react to a message in the same thread that posted the
 * message on the bus. This should only be used if the application is able
 * to deal with messages from different threads.
 * <p>
 * Every {@link Pipeline} has one bus.
 * <p>
 * Note that a Pipeline will set its bus into flushing state when changing
 * from READY to NULL state.
 */
public class Bus extends GstObject {
    static final Logger log = Logger.getLogger(Bus.class.getName());
    static final Level LOG_DEBUG = Level.FINE;
    
    /**
     * Creates a new instance of Bus
     */
    public Bus(Initializer init) { 
        super(init); 
        gst.gst_bus_enable_sync_message_emission(this);
        gst.gst_bus_set_sync_handler(this, Pointer.NULL, null);
        gst.gst_bus_set_sync_handler(this, syncCallback, null);
    }
    
    /**
     * Adds a listener for all message types transmitted on the Bus.
     * 
     * @param listener
     */
    @SuppressWarnings("deprecation") 
    public void addBusListener(org.gstreamer.event.BusListener listener) {
        addListenerProxy(org.gstreamer.event.BusListener.class, listener, new BusListenerProxy(this, listener));
    }
    
    /**
     * Removes the listener for all message types transmitted on the Bus.
     * 
     * @param listener
     */
    @SuppressWarnings("deprecation") 
    public void removeBusListener(org.gstreamer.event.BusListener listener) {
        EventListenerProxy proxy = removeListenerProxy(org.gstreamer.event.BusListener.class, listener);
        if (proxy != null) {
            ((BusListenerProxy) proxy).disconnect();
        }
    }
    
    /**
     * Instructs the bus to flush out any queued messages.
     * 
     * If flushing, flush out any messages queued in the bus. Will flush future 
     * messages until {@link #setFlushing} is called with false.
     * 
     * @param flushing true if flushing is desired.
     */
    public void setFlushing(boolean flushing) {
        gst.gst_bus_set_flushing(this, flushing ? 1 : 0);
    }
    
    /**
     * Signal emitted when end-of-stream is reached in a pipeline.
     * 
     * The application will only receive this message in the PLAYING state and 
     * every time it sets a pipeline to PLAYING that is in the EOS state. 
     * The application can perform a flushing seek in the pipeline, which will 
     * undo the EOS state again. 
     */
    public static interface EOS {
        public void eosMessage(GstObject source);
    }
    
    /**
     * Signal emitted when an error occurs.
     * 
     * When the application receives an error message it should stop playback
     * of the pipeline and not assume that more data will be played.
     */
    public static interface ERROR {
        public void errorMessage(GstObject source, int code, String message);
    }
    
    /**
     * Signal emitted when a warning message is delivered.
     */
    public static interface WARNING {
        public void warningMessage(GstObject source, int code, String message);
    }
    
    /**
     * Signal emitted when an informational message is delivered.
     */
    public static interface INFO {
        public void infoMessage(GstObject source, int code, String message);
    }
    
    /**
     * Signal emitted when a new tag is identified on the stream.
     */
    public static interface TAG {
        public void tagMessage(GstObject source, TagList tagList);
    }
    
    /**
     * Signal emitted when a state change happens.
     */
    public static interface STATE_CHANGED {
        public void stateMessage(GstObject source, State old, State current, State pending);
    }
    
    /**
     * Signal emitted when the pipeline is buffering data. 
     * When the application receives a buffering message in the PLAYING state 
     * for a non-live pipeline it must PAUSE the pipeline until the buffering 
     * completes, when the percentage field in the message is 100%. For live 
     * pipelines, no action must be performed and the buffering percentage can
     * be used to inform the user about the progress.
     */
    public static interface BUFFERING {
        public void bufferingMessage(GstObject source, int percent);
    }
    /**
     * Signal emitted when the duration of a pipeline changes. 
     * 
     * The application can get the new duration with a duration query.
     */
    public static interface DURATION {
        public void durationMessage(GstObject source, Format format, long duration);
    }
    public static interface SEGMENT_START {
        public void segmentStart(GstObject source, Format format, long position);
    }
    /**
     * Signal emitted when the pipeline has completed playback of a segment.
     */
    public static interface SEGMENT_DONE {
        public void segmentDone(GstObject source, Format format, long position);
    }
    
    /**
     * Add a listener for end-of-stream messages.
     * 
     * @param listener The listener to be called when end-of-stream is encountered.
     */
    public void connect(final EOS listener) {
        connect("sync-message::eos", EOS.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bus bus, Message msg, Pointer user_data) {
                listener.eosMessage(msg.getSource());
            }
        });
    }
    
    /**
     * Disconnect the listener for end-of-stream messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(EOS listener) {
        super.disconnect(EOS.class, listener);
    }
    
    /**
     * Add a listener for error messages.
     * 
     * @param listener The listener to be called when an error in the stream is encountered.
     */
    public void connect(final ERROR listener) {
        connect("sync-message::error", ERROR.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bus bus, Message msg, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_error(msg, err, null);
                glib.g_error_free(err.getValue());
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.errorMessage(msg.getSource(), error.code, error.message);
            }
        });
    }
    
    /**
     * Disconnect the listener for error messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(ERROR listener) {
        super.disconnect(ERROR.class, listener);
    }
    
    /**
     * Add a listener for warning messages.
     * 
     * @param listener The listener to be called when an {@link Element} emits a warning.
     */
    public void connect(final WARNING listener) {
        connect("sync-message::warning", WARNING.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bus bus, Message msg, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_warning(msg, err, null);                
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.warningMessage(msg.getSource(), error.code, error.message);
                glib.g_error_free(err.getValue());
            }
        });
    }
    
    /**
     * Disconnect the listener for warning messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(WARNING listener) {
        super.disconnect(WARNING.class, listener);
    }
    
    /**
     * Add a listener for informational messages.
     * 
     * @param listener The listener to be called when an {@link Element} emits a an informational message.
     */
    public void connect(final INFO listener) {
        connect("sync-message::info", INFO.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bus bus, Message msg, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_info(msg, err, null);                
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.infoMessage(msg.getSource(), error.code, error.message);
                glib.g_error_free(err.getValue());
            }
        });
    }
    
    /**
     * Disconnect the listener for informational messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(INFO listener) {
        super.disconnect(INFO.class, listener);
    }
    
    /**
     * Add a listener for {@link State} changes in the Pipeline.
     * 
     * @param listener The listener to be called when the Pipeline changes state.
     */
    public void connect(final STATE_CHANGED listener) {
        connect("sync-message::state-changed", STATE_CHANGED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Message msg, Pointer user_data) {
                IntByReference o = new IntByReference();
                IntByReference n = new IntByReference();
                IntByReference p = new IntByReference();
                gst.gst_message_parse_state_changed(msg, o, n, p);
                listener.stateMessage(msg.getSource(), State.valueOf(o.getValue()),
                        State.valueOf(n.getValue()), State.valueOf(p.getValue()));
            }
        });
    }
    /**
     * Disconnect the listener for {@link State} change messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(STATE_CHANGED listener) {
        super.disconnect(STATE_CHANGED.class, listener);
    }
    /**
     * Add a listener for new media tags.
     * 
     * @param listener The listener to be called when new media tags are found.
     */
    public void connect(final TAG listener) {
        connect("sync-message::tag", TAG.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Message msg, Pointer user_data) {
                PointerByReference list = new PointerByReference();
                gst.gst_message_parse_tag(msg, list);
                listener.tagMessage(msg.getSource(), new TagList(TagList.initializer(list.getValue(), false, false)));
            }
        });
    }
    
    /**
     * Disconnect the listener for tag messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(TAG listener) {
        super.disconnect(TAG.class, listener);
    }
    
    /**
     * Add a listener for {@link BUFFERING} messages in the Pipeline.
     * 
     * @param listener The listener to be called when the Pipeline buffers data.
     */
    public void connect(final BUFFERING listener) {
        connect("sync-message::buffering", BUFFERING.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Message msg, Pointer user_data) {
                IntByReference percent = new IntByReference(0);
                gst.gst_message_parse_buffering(msg, percent);
                listener.bufferingMessage(msg.getSource(), percent.getValue());
            }
        });
    }
    
    /**
     * Disconnect the listener for buffering messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(BUFFERING listener) {
        super.disconnect(BUFFERING.class, listener);
    }
    
    /**
     * Add a listener for duration changes.
     * 
     * @param listener The listener to be called when the duration changes.
     */
    public void connect(final DURATION listener) {
        connect("sync-message::duration", DURATION.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bus bus, Message msg, Pointer user_data) {
                System.out.println("duration update");
                IntByReference format = new IntByReference(0);
                LongByReference duration = new LongByReference(0);
                gst.gst_message_parse_duration(msg, format, duration);
                listener.durationMessage(msg.getSource(), 
                        Format.valueOf(format.getValue()), duration.getValue());
            }
        });
    }
    /**
     * Disconnect the listener for duration change messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(DURATION listener) {
        super.disconnect(DURATION.class, listener);
    }
    
    /**
     * Add a listener for {@link SEGMENT_START} messages in the Pipeline.
     * 
     * @param listener The listener to be called when the Pipeline has started a segment.
     */
    public void connect(final SEGMENT_START listener) {
        connect("sync-message::segment-start", SEGMENT_START.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bus bus, Message msg, Pointer user_data) {
                IntByReference format = new IntByReference(0);
                LongByReference position = new LongByReference(0);
                gst.gst_message_parse_segment_start(msg, format, position);
                listener.segmentStart(msg.getSource(), 
                        Format.valueOf(format.getValue()), position.getValue());
            }
        });
    }
    
    /**
     * Disconnect the listener for segment-start messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(SEGMENT_START listener) {
        super.disconnect(SEGMENT_START.class, listener);
    }
    
    /**
     * Add a listener for {@link SEGMENT_DONE} messages in the Pipeline.
     * 
     * @param listener The listener to be called when the Pipeline has finished a segment.
     */
    public void connect(final SEGMENT_DONE listener) {
        connect("sync-message::segment-done", SEGMENT_DONE.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bus bus, Message msg, Pointer user_data) {
                IntByReference format = new IntByReference(0);
                LongByReference position = new LongByReference(0);
                gst.gst_message_parse_segment_done(msg, format, position);
                listener.segmentDone(msg.getSource(), 
                        Format.valueOf(format.getValue()), position.getValue());
            }
        });
    }
    
    /**
     * Disconnect the listener for segment-done messages.
     * 
     * @param listener The listener that was registered to receive the message.
     */
    public void disconnect(SEGMENT_DONE listener) {
        super.disconnect(SEGMENT_DONE.class, listener);
    }
    public void setSyncHandler(BusSyncHandler handler) {
        syncHandler = handler;
    }

    private BusSyncHandler syncHandler = new BusSyncHandler() {
        public BusSyncReply syncMessage(Message msg) {
            return BusSyncReply.PASS;
        }
    };
    private static GstCallback syncCallback = new GstCallback() {
        @SuppressWarnings("unused")
        public int callback(Bus bus, Pointer msgPtr, Pointer data) {
            //
            // If the Bus proxy has been disposed, just ignore
            //
            if (bus == null) {
                return BusSyncReply.PASS.intValue();
            }
            // Manually manage the refcount here
            Message msg = new Message(msgPtr, false, false);            
            BusSyncReply reply = bus.syncHandler.syncMessage(msg);
            
            //
            // If the message is to be dropped, unref it, otherwise it needs to 
            // keep its ref to be passed on
            //
            if (reply == BusSyncReply.DROP) {
                gst.gst_mini_object_unref(msg);
            }
            return reply.intValue();
        }
    };
    
    /**
     * Adds the specified message listener to receive messages sent on the bus. 
     *
     * @param listener the message listener
     */
    public void addMessageListener(MessageListener listener) {
        MessageListenerProxy proxy = new MessageListenerProxy(listener);
        addListenerProxy(MessageListener.class, listener, proxy);
        connect((Bus.ERROR) proxy);
        connect((Bus.WARNING) proxy);
        connect((Bus.INFO) proxy);
    }
    
    /**
     * Removes the message listener so it no longer receives messages posted on this bus.
     * 
     * @param listener the message listener
     */
    public void removeMessageListener(MessageListener listener) {
        EventListenerProxy proxy = removeListenerProxy(MessageListener.class, listener);
        if (proxy != null) {
            disconnect((Bus.ERROR) proxy);
            disconnect((Bus.WARNING) proxy);
            disconnect((Bus.INFO) proxy);
        }
    }
    
    /**
     * Adds the specified state change listener to receive state-changed events sent on the bus. 
     *
     * @param listener the state change listener
     */
    public void addStateChangeListener(StateChangeListener listener) {
        StateChangeListenerProxy proxy = new StateChangeListenerProxy(listener);
        addListenerProxy(StateChangeListener.class, listener, proxy);
        connect((Bus.STATE_CHANGED) proxy);
    }
    
    /**
     * Removes the state change listener so it no longer receives state change 
     * messages posted on this bus.
     * 
     * @param listener the state change listener
     */
    public void removeStateChangeListener(StateChangeListener listener) {
        EventListenerProxy proxy = removeListenerProxy(StateChangeListener.class, listener);
        if (proxy != null) {
            disconnect((Bus.STATE_CHANGED) proxy);
        }
    }
    
    /**
     * Adds the specified EOS listener to receive end-of-stream events posted on this bus. 
     *
     * @param listener the end of stream listener
     */
    public void addEOSListener(EOSListener listener) {
        EOSListenerProxy proxy = new EOSListenerProxy(listener);
        addListenerProxy(EOSListener.class, listener, proxy);
        connect(proxy);
    }
    
    /**
     * Removes the end of stream listener so it no longer receives end of stream events posted on this bus.
     * @param listener the end of stream listener
     */
    public void removeEOSListener(EOSListener listener) {
        EventListenerProxy proxy = removeListenerProxy(EOSListener.class, listener);
        if (proxy != null) {
            disconnect((Bus.EOS) proxy);
        }
    }
    
    /**
     * Adds the specified tag listener to receive tag events sent on the bus. 
     *
     * @param listener the tag listener
     */
    public void addTagListener(TagListener listener) {
        TagListenerProxy proxy = new TagListenerProxy(listener);
        addListenerProxy(TagListener.class, listener, proxy);
        connect(proxy);
    }
    
    /**
     * Removes the tag listener so it no longer receives tag events posted on this bus.
     * 
     * @param listener the tag listener
     */
    public void removeTagListener(TagListener listener) {
        EventListenerProxy proxy = removeListenerProxy(TagListener.class, listener);
        if (proxy != null) {
            disconnect((Bus.TAG) proxy);
        }
    }
    private class MessageListenerProxy extends java.util.EventListenerProxy implements ERROR, WARNING, INFO {
        private MessageListener listener;
        public MessageListenerProxy(MessageListener listener) {
            super(listener);
            this.listener = listener;
        }
        public void errorMessage(GstObject source, int code, String message) {
            listener.errorMessage(new MessageEvent(source, code, message));
        }

        public void warningMessage(GstObject source, int code, String message) {
            listener.warningMessage(new MessageEvent(source, code, message));
        }

        public void infoMessage(GstObject source, int code, String message) {
            listener.informationMessage(new MessageEvent(source, code, message));
        } 
    }
    private class StateChangeListenerProxy extends java.util.EventListenerProxy implements STATE_CHANGED {
        public StateChangeListenerProxy(StateChangeListener listener) {
            super(listener);
        }
        public void stateMessage(GstObject source, State old, State current, State pending) {
            ((StateChangeListener) getListener()).stateChange(new StateChangeEvent(source, old, current, pending));
        } 
    }
    private class EOSListenerProxy extends java.util.EventListenerProxy implements EOS {
        public EOSListenerProxy(EOSListener listener) {
            super(listener);
        }
        public void eosMessage(GstObject source) {
            ((EOSListener) getListener()).endOfStream(new EOSEvent(source));
        }
    }
    private class TagListenerProxy extends java.util.EventListenerProxy implements TAG {
        public TagListenerProxy(TagListener listener) {
            super(listener);
        }
        public void tagMessage(GstObject source, TagList tagList) {
            ((TagListener) getListener()).tagsFound(new TagEvent(source, tagList));
        }
    }
}


class BusListenerProxy extends EventListenerProxy implements Bus.EOS, Bus.STATE_CHANGED, Bus.ERROR, Bus.WARNING, 
        Bus.INFO, Bus.TAG, Bus.BUFFERING, Bus.DURATION, Bus.SEGMENT_START, Bus.SEGMENT_DONE {
    /**
     * @deprecated
     * @param bus
     * @param listener
     */
    @SuppressWarnings("deprecation") 
    public BusListenerProxy(Bus bus, final org.gstreamer.event.BusListener listener) {
        super(listener);
        this.bus = bus;
        this.listener = listener;
        bus.connect((Bus.EOS) this);
        bus.connect((Bus.STATE_CHANGED) this);
        bus.connect((Bus.ERROR) this);
        bus.connect((Bus.WARNING) this);
        bus.connect((Bus.INFO) this);
        bus.connect((Bus.TAG) this);
        bus.connect((Bus.BUFFERING) this);
        bus.connect((Bus.DURATION) this);
        bus.connect((Bus.SEGMENT_START) this);
        bus.connect((Bus.SEGMENT_DONE) this);
    }
    public void eosMessage(GstObject source) {
        listener.eosEvent();
    }
    public void stateMessage(GstObject source, State old, State current, State pending) {
        listener.stateEvent(new StateEvent(source, old, current, pending));
    }
    public void errorMessage(GstObject source, int code, String message) {
        listener.errorEvent(new ErrorEvent(source, code, message));
    }
    public void warningMessage(GstObject source, int code, String message) {
        listener.warningEvent(new ErrorEvent(source, code, message));
    }
    public void infoMessage(GstObject source, int code, String message) {
        listener.infoEvent(new ErrorEvent(source, code, message));
    }
    public void tagMessage(GstObject source, TagList tagList)  {
        listener.tagEvent(tagList);
    }
    public void bufferingMessage(GstObject source, int percent) {
        listener.bufferingEvent(percent);
    }
    public void durationMessage(GstObject source, Format format, long duration) {
        listener.durationEvent(format, duration);
    }
    public void segmentStart(GstObject source, Format format, long position) {
        listener.segmentStart(format, position);
    }
    public void segmentDone(GstObject source, Format format, long position) {
        listener.segmentDone(format, position);
    }
    public void disconnect() {
        bus.disconnect((Bus.EOS) this);
        bus.disconnect((Bus.STATE_CHANGED) this);
        bus.disconnect((Bus.ERROR) this);
        bus.disconnect((Bus.WARNING) this);
        bus.disconnect((Bus.INFO) this);
        bus.disconnect((Bus.TAG) this);
        bus.disconnect((Bus.BUFFERING) this);
        bus.disconnect((Bus.SEGMENT_START) this);
        bus.disconnect((Bus.SEGMENT_DONE) this);
    }
    private Bus bus;
    @SuppressWarnings("deprecation")
    private org.gstreamer.event.BusListener listener;

}

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

package org.gstreamer;

import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.Map;
import com.sun.jna.ptr.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gstreamer.event.BusListener;
import org.gstreamer.event.BusSyncHandler;
import org.gstreamer.event.ErrorEvent;
import org.gstreamer.event.StateEvent;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import org.gstreamer.lowlevel.GstAPI.GErrorStruct;
import static org.gstreamer.lowlevel.GstAPI.gst;
import static org.gstreamer.lowlevel.GlibAPI.glib;
import org.gstreamer.lowlevel.GstAPI.MessageStruct;


/**
 *
 */
public class Bus extends GstObject {
    static final Logger log = Logger.getLogger(Bus.class.getName());
    static final Level LOG_DEBUG = Level.FINE;
        
    /**
     * Creates a new instance of Bus
     */
    public Bus(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    public Bus(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
        gst.gst_bus_enable_sync_message_emission(this);
        gst.gst_bus_set_sync_handler(this, Pointer.NULL, null);
        gst.gst_bus_set_sync_handler(this, syncCallback, null);
    }
    public void addBusListener(BusListener l) {
        listeners.put(l, new BusListenerProxy(this, l));
    }
    public void removeBusListener(BusListener l) {
        BusListenerProxy proxy = listeners.remove(l);
        if (proxy != null) {
            proxy.disconnect();
        }
    }
    
    public void setFlushing(boolean flushing) {
        gst.gst_bus_set_flushing(this, flushing ? 1 : 0);
    }
    
    public static interface EOS {
        public void eosMessage(GstObject source);
    }
    public static interface ERROR {
        public void errorMessage(GstObject source, int code, String message);
    }
    public static interface WARNING {
        public void warningMessage(GstObject source, int code, String message);
    }
    public static interface INFO {
        public void infoMessage(GstObject source, int code, String message);
    }
    public static interface TAG {
        public void tagMessage(GstObject source, TagList tagList);
    }
    public static interface STATECHANGED {
        public void stateMessage(GstObject source, State old, State current, State pending);
    }
    public static interface BUFFERING {
        public void bufferingMessage(GstObject source, int percent);
    }
    public static interface DURATION {
        public void durationMessage(GstObject source, Format format, long duration);
    }
    public static interface SEGMENT_START {
        public void segmentStart(GstObject source, Format format, long position);
    }
    public static interface SEGMENT_DONE {
        public void segmentDone(GstObject source, Format format, long position);
    }
    public void connect(final EOS listener) {
        connect("sync-message::eos", EOS.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                listener.eosMessage(messageSource(msgPtr));
            }
        });
    }
    public void disconnect(EOS listener) {
        super.disconnect(EOS.class, listener);
    }
    public void connect(final ERROR listener) {
        connect("sync-message::error", ERROR.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_error(msgPtr, err, null);
                glib.g_error_free(err.getValue());
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.errorMessage(messageSource(msgPtr), error.code, error.message);
            }
        });
    }
    public void disconnect(ERROR listener) {
        super.disconnect(ERROR.class, listener);
    }
    public void connect(final WARNING listener) {
        connect("sync-message::warning", WARNING.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_warning(msgPtr, err, null);                
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.warningMessage(messageSource(msgPtr), error.code, error.message);
                glib.g_error_free(err.getValue());
            }
        });
    }
    public void disconnect(WARNING listener) {
        super.disconnect(WARNING.class, listener);
    }
    public void connect(final INFO listener) {
        connect("sync-message::info", INFO.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_info(msgPtr, err, null);                
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.infoMessage(messageSource(msgPtr), error.code, error.message);
                glib.g_error_free(err.getValue());
            }
        });
    }
    public void disconnect(INFO listener) {
        super.disconnect(INFO.class, listener);
    }
    public void connect(final STATECHANGED listener) {
        connect("sync-message::state-changed", STATECHANGED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                IntByReference o = new IntByReference();
                IntByReference n = new IntByReference();
                IntByReference p = new IntByReference();
                gst.gst_message_parse_state_changed(msgPtr, o, n, p);
                listener.stateMessage(messageSource(msgPtr), State.valueOf(o.getValue()),
                        State.valueOf(n.getValue()), State.valueOf(p.getValue()));
            }
        });
    }
    public void disconnect(STATECHANGED listener) {
        super.disconnect(STATECHANGED.class, listener);
    }
    public void connect(final TAG listener) {
        connect("sync-message::tag", TAG.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                PointerByReference list = new PointerByReference();
                gst.gst_message_parse_tag(msgPtr, list);
                listener.tagMessage(messageSource(msgPtr), new TagList(list.getValue(), true, false));
            }
        });
    }
    public void disconnect(TAG listener) {
        super.disconnect(TAG.class, listener);
    }
    public void connect(final BUFFERING listener) {
        connect("sync-message::buffering", BUFFERING.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                IntByReference percent = new IntByReference(0);
                gst.gst_message_parse_buffering(msgPtr, percent);
                listener.bufferingMessage(messageSource(msgPtr), percent.getValue());
            }
        });
    }
    public void disconnect(BUFFERING listener) {
        super.disconnect(BUFFERING.class, listener);
    }
    public void connect(final DURATION listener) {
        connect("sync-message::duration", DURATION.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                System.out.println("duration update");
                IntByReference format = new IntByReference(0);
                LongByReference duration = new LongByReference(0);
                gst.gst_message_parse_duration(msgPtr, format, duration);
                listener.durationMessage(messageSource(msgPtr), 
                        Format.valueOf(format.getValue()), duration.getValue());
            }
        });
    }
    public void disconnect(DURATION listener) {
        super.disconnect(DURATION.class, listener);
    }
    public void connect(final SEGMENT_START listener) {
        connect("sync-message::segment-start", SEGMENT_START.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                IntByReference format = new IntByReference(0);
                LongByReference position = new LongByReference(0);
                gst.gst_message_parse_segment_start(msgPtr, format, position);
                listener.segmentStart(messageSource(msgPtr), 
                        Format.valueOf(format.getValue()), position.getValue());
            }
        });
    }
    public void disconnect(SEGMENT_START listener) {
        super.disconnect(SEGMENT_START.class, listener);
    }
    public void connect(final SEGMENT_DONE listener) {
        connect("sync-message::segment-done", SEGMENT_DONE.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                IntByReference format = new IntByReference(0);
                LongByReference position = new LongByReference(0);
                gst.gst_message_parse_segment_done(msgPtr, format, position);
                listener.segmentDone(messageSource(msgPtr), 
                        Format.valueOf(format.getValue()), position.getValue());
            }
        });
    }
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
        public int callback(Pointer busPtr, Pointer msgPtr, Pointer data) {
            Bus bus = (Bus) NativeObject.instanceFor(busPtr);
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
    
    private final static GstObject messageSource(Pointer msgPtr) {
        return Element.objectFor(new MessageStruct(msgPtr).src, true);
    }
    
    private Map<BusListener, BusListenerProxy> listeners
            = Collections.synchronizedMap(new HashMap<BusListener, BusListenerProxy>());
}
class BusListenerProxy implements Bus.EOS, Bus.STATECHANGED, Bus.ERROR, Bus.WARNING, 
        Bus.INFO, Bus.TAG, Bus.BUFFERING, Bus.DURATION, Bus.SEGMENT_START, Bus.SEGMENT_DONE {
    public BusListenerProxy(Bus bus, final BusListener listener) {
        this.bus = bus;
        this.listener = listener;
        bus.connect((Bus.EOS) this);
        bus.connect((Bus.STATECHANGED) this);
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
        bus.disconnect((Bus.STATECHANGED) this);
        bus.disconnect((Bus.ERROR) this);
        bus.disconnect((Bus.WARNING) this);
        bus.disconnect((Bus.INFO) this);
        bus.disconnect((Bus.TAG) this);
        bus.disconnect((Bus.BUFFERING) this);
        bus.disconnect((Bus.SEGMENT_START) this);
        bus.disconnect((Bus.SEGMENT_DONE) this);
    }
    private Bus bus;
    private BusListener listener;

}

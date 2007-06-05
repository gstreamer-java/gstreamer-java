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

package org.gstreamer;

import com.sun.jna.Callback;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.Map;
import com.sun.jna.ptr.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gstreamer.event.BusListener;
import org.gstreamer.event.ErrorEvent;
import org.gstreamer.event.StateEvent;
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
        gst.gst_bus_set_sync_handler(this,
                NativeLibrary.getInstance("gstreamer-0.10").getFunction("gst_bus_sync_signal_handler"),
                null);
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
    public static Bus objectFor(Pointer ptr, boolean needRef) {
        return (Bus) GstObject.objectFor(ptr, Bus.class, needRef);
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
    public void connect(final EOS listener) {
        connect("sync-message::eos", EOS.class, listener,new Callback() {
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                listener.eosMessage(messageSource(msgPtr));
            }
        });
    }
    public void disconnect(EOS listener) {
        super.disconnect(EOS.class, listener);
    }
    public void connect(final ERROR listener) {
        connect("sync-message::error", ERROR.class, listener,new Callback() {
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
        connect("sync-message::warning", WARNING.class, listener,new Callback() {
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_warning(msgPtr, err, null);
                glib.g_error_free(err.getValue());
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.warningMessage(messageSource(msgPtr), error.code, error.message);
            }
        });
    }
    public void disconnect(WARNING listener) {
        super.disconnect(WARNING.class, listener);
    }
    public void connect(final INFO listener) {
        connect("sync-message::info", INFO.class, listener,new Callback() {
            public void callback(Pointer busPtr, Pointer msgPtr, Pointer user_data) {
                PointerByReference err = new PointerByReference();
                gst.gst_message_parse_info(msgPtr, err, null);
                glib.g_error_free(err.getValue());
                GErrorStruct error = new GErrorStruct(err.getValue());
                listener.infoMessage(messageSource(msgPtr), error.code, error.message);
            }
        });
    }
    public void disconnect(INFO listener) {
        super.disconnect(INFO.class, listener);
    }
    public void connect(final STATECHANGED listener) {
        connect("sync-message::state-changed", STATECHANGED.class, listener,new Callback() {
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
        connect("sync-message::tag", TAG.class, listener,new Callback() {
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
    private final static GstObject messageSource(Pointer msgPtr) {
        return Element.objectFor(new MessageStruct(msgPtr).src, true);
    }
    private Map<BusListener, BusListenerProxy> listeners
            = Collections.synchronizedMap(new HashMap<BusListener, BusListenerProxy>());
}
class BusListenerProxy implements Bus.EOS, Bus.STATECHANGED, Bus.ERROR, Bus.WARNING, Bus.INFO, Bus.TAG {
    public BusListenerProxy(Bus bus, final BusListener listener) {
        this.bus = bus;
        this.listener = listener;
        bus.connect((Bus.EOS) this);
        bus.connect((Bus.STATECHANGED) this);
        bus.connect((Bus.ERROR) this);
        bus.connect((Bus.WARNING) this);
        bus.connect((Bus.INFO) this);
        bus.connect((Bus.TAG) this);
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
    public void disconnect() {
        bus.disconnect((Bus.EOS) this);
        bus.disconnect((Bus.STATECHANGED) this);
        bus.disconnect((Bus.ERROR) this);
        bus.disconnect((Bus.WARNING) this);
        bus.disconnect((Bus.INFO) this);
        bus.disconnect((Bus.TAG) this);
        
    }
    private Bus bus;
    private BusListener listener;
}

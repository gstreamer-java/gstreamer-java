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

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import com.sun.jna.ptr.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gstreamer.event.BusListener;
import org.gstreamer.event.ErrorEvent;
import org.gstreamer.event.MessageType;
import org.gstreamer.event.StateEvent;
import org.gstreamer.lowlevel.GstAPI;
import static org.gstreamer.lowlevel.GstAPI.gst;
import org.gstreamer.lowlevel.GlibAPI;
import org.gstreamer.lowlevel.MessageStruct;


/**
 *
 */
public class Bus extends GstObject {
    static final Logger logger = Logger.getLogger(Bus.class.getName());
    static final Level LOG_DEBUG = Level.FINE;

    /**
     * Creates a new instance of Bus
     */
    public Bus(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    public Bus(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public void addBusListener(BusListener l) {
        NativeLong id = gst.gst_bus_add_watch(this, new BusListenerProxy(l), null);
        listeners.put(l, id);
    }
    public void removeBusListener(BusListener l) {
        NativeLong val = listeners.get(l);
        if (val != null) {
            
            //removeNativeListener(_handle, val);
            listeners.remove(l);
        }
    }
    
    public void setFlushing(boolean flushing) {
        gst.gst_bus_set_flushing(this, flushing ? 1 : 0);
    }
    public static Bus objectFor(Pointer ptr, boolean needRef) {
        return (Bus) GstObject.objectFor(ptr, Bus.class, needRef);
    }
    
    private Map<BusListener, NativeLong> listeners 
            = Collections.synchronizedMap(new WeakHashMap<BusListener, NativeLong>());
}
class BusListenerProxy implements GstAPI.BusCallback {
    static final Logger log = Bus.logger;
    static final Level MSG_DEBUG = Bus.LOG_DEBUG;
    private static GlibAPI glib = GlibAPI.glib;

    public BusListenerProxy(BusListener l) {
        this.listenerRef = new WeakReference<BusListener>(l);
    }
    public boolean callback(Pointer bus, Pointer msgPointer, Pointer data) {
        try {
            MessageStruct msg = new MessageStruct(msgPointer);
            log.finer("BusMessage type=" + msg.type);
            BusListener l = listenerRef.get();
            if (l == null) {
                return false;
            }
            Element src = Element.objectFor(msg.src, true);
            PointerByReference clock = new PointerByReference();
            LongByReference seg = new LongByReference();
            IntByReference fmt = new IntByReference(Format.TIME.intValue());
            IntByReference ready = new IntByReference();
            switch (MessageType.valueOf(msg.type)) {
            case GST_MESSAGE_SEGMENT_START:
                gst.gst_message_parse_segment_start(msgPointer, fmt, seg);
                log.log(MSG_DEBUG, "SEGMENT_START " + seg.getValue());
                break;
            case GST_MESSAGE_SEGMENT_DONE:
                log.log(MSG_DEBUG, "SEGMENT_DONE");
                break;
            case GST_MESSAGE_CLOCK_PROVIDE:
                log.log(MSG_DEBUG, "CLOCK PROVIDE");
                gst.gst_message_parse_clock_provide(msgPointer, clock, ready);
                if (clock.getValue() != null) {
                    //log.debug("time = " + gst_clock_get_time(clock[0]));
                }
                break;
            case GST_MESSAGE_CLOCK_LOST:
                log.log(MSG_DEBUG, "CLOCK LOST");
                break;
            case GST_MESSAGE_NEW_CLOCK:
                log.log(MSG_DEBUG, "NEW CLOCK");
                gst.gst_message_parse_new_clock(msgPointer, clock);
                //   log.log(MSG_DEBUG, "time = " + GstAPI.gst_clock_get_time(clock[0]));
                break;
            case GST_MESSAGE_TAG:
                log.log(MSG_DEBUG, "TAG");
                tagMessage(l, msgPointer, src);
                break;
            case GST_MESSAGE_EOS:
                l.eosEvent();
                break;
            case GST_MESSAGE_STATE_CHANGED:
                stateMessage(l, msgPointer, src);
                break;
            case GST_MESSAGE_ERROR:
                errorMessage(l, msgPointer, src);
                break;
            case GST_MESSAGE_WARNING:
                warningMessage(l, msgPointer, src);
                break;
            case GST_MESSAGE_BUFFERING:
                break;
            default:
                System.out.printf("Unknown GstMessage: 0x%x\n",
                        msg.type);
            }
        } catch (Exception e) {
            log.log(MSG_DEBUG, e.toString());
            // Don't propagate any exceptions up
        }
        return true;
    }
    
    private void tagMessage(BusListener l, Pointer msgPointer, GstObject src) {
        PointerByReference list = new PointerByReference();
        gst.gst_message_parse_tag(msgPointer, list);
        l.tagEvent(new TagList(list.getValue(), true, false));
    }
    private void stateMessage(BusListener l, Pointer msgPointer, GstObject src) {
        IntByReference o = new IntByReference();
        IntByReference n = new IntByReference();
        IntByReference p = new IntByReference();
        gst.gst_message_parse_state_changed(msgPointer, o, n, p);
        l.stateEvent(new StateEvent(src, o.getValue(), n.getValue(), p.getValue()));
    }
    
    private void errorMessage(BusListener l, Pointer msgPointer, GstObject src) {
        
        PointerByReference err = new PointerByReference();
        PointerByReference debug = new PointerByReference();
        gst.gst_message_parse_error(msgPointer, err, debug);
        GError error = new GError(err.getValue());
        l.errorEvent(new ErrorEvent(src, error.code, error.message));
        glib.g_free(debug.getValue());
        glib.g_error_free(err.getValue());
    }
    private void warningMessage(BusListener l, Pointer msgPointer, GstObject src) {
        PointerByReference err = new PointerByReference();
        PointerByReference debug = new PointerByReference();
        gst.gst_message_parse_warning(msgPointer, err, debug);
        GError error = new GError(err.getValue());
        l.warningEvent(new ErrorEvent(src, error.code, error.message));
        glib.g_free(debug.getValue());
        glib.g_error_free(err.getValue());
    }
    private WeakReference<BusListener> listenerRef;
    
    
}

/*
 * Copyright (c) 2007 Wayne Meissner
 *
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
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gstreamer.lowlevel.GObjectAPI;
import org.gstreamer.lowlevel.IntPtr;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;

/**
 *
 */
public abstract class GObject extends NativeObject {
    private static Logger logger = Logger.getLogger(GObject.class.getName());
    private static Level DEBUG = Level.FINE;
    private static Level LIFECYCLE = NativeObject.LIFECYCLE;
    
    public GObject(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, false, ownsHandle); // increase the refcount here
        logger.entering("GObject", "<init>", new Object[] { ptr, ownsHandle, needRef });        
        strongReferences.add(this);
        if (ownsHandle) {
            gobj.g_object_add_toggle_ref(ptr, toggle, objectID);
            if (!needRef) {                
                unref();
            }
        }
        gobj.g_object_weak_ref(this, weakNotify, objectID);
    }
    
    public void set(String property, String data) {
        logger.entering("GObject", "set", new Object[] { property, data });
        gobj.g_object_set(this, property, data, null);
    }
    public void set(String property, GObject data) {
        logger.entering("GObject", "set", new Object[] { property, data });
        gobj.g_object_set(this, property, data);
    }
    public void set(String property, Object data) {
        logger.entering("GObject", "set", new Object[] { property, data });        
        gobj.g_object_set(this, property, data, null);
    }
    public void setProperty(String property, GObject data) {
        logger.entering("GObject", "setProperty", new Object[] { property, data });
        gobj.g_object_set_property(this, property, data);
    }
    
    protected void disposeNativeHandle(Pointer ptr) {
        logger.log(LIFECYCLE, "Removing toggle ref " + getClass().getSimpleName() + " (" +  ptr + ")");
        //gobj.g_object_weak_unref(this, weakNotify, toggleID);
        gobj.g_object_remove_toggle_ref(ptr, toggle, objectID);
    }
    
    
    protected NativeLong g_signal_connect(String signal, Callback callback) {
        logger.entering("GObject", "g_signal_connect", new Object[] { signal, callback });
        return gobj.g_signal_connect_data(this, signal, callback, null, null, 0);
    }
    private class SignalCallback {
        protected SignalCallback(String signal, Callback cb) {
            this.cb  = cb;
            id = g_signal_connect(signal, cb);
        }
        synchronized protected void disconnect() {
            if (id != null) {
                gobj.g_signal_handler_disconnect(GObject.this, id);
                id = null;
            }
        }
        protected void finalize() {
            // Ensure the native callback is removed
            disconnect();
        }
        Callback cb;
        NativeLong id;
    }
    private Map<Class<?>, Map<Object, SignalCallback>> listeners =
            new HashMap<Class<?>, Map<Object, SignalCallback>>();
    private IntPtr objectID = new IntPtr(System.identityHashCode(this));
    
    public <T> void connect(String signal, Class<T> listenerClass, T listener, Callback cb) {
        Map<Object, SignalCallback> m;
        synchronized (listeners) {
            m = listeners.get(listenerClass);
            if (m == null) {
                m = Collections.synchronizedMap(new HashMap<Object, SignalCallback>());
                listeners.put(listenerClass, m);
            }
        }
        m.put(listener, new SignalCallback(signal, cb));
    }
    
    public <T> void disconnect(Class<T> listenerClass, T listener) {
        synchronized (listeners) {
            Map<Object, SignalCallback> m = listeners.get(listenerClass);
            if (m != null) {
                SignalCallback cb = m.remove(listener);
                if (cb != null) {
                    cb.disconnect();
                }
                if (m.isEmpty()) {
                    listeners.remove(listenerClass);
                }
            }
        }
    }
    public static GObject objectFor(Pointer ptr, Class<? extends GObject> defaultClass) {
        return GObject.objectFor(ptr, defaultClass, true);
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends GObject> T objectFor(Pointer ptr, Class<T> defaultClass, boolean needRef) {
        logger.entering("GObject", "objectFor", new Object[] { ptr, defaultClass, needRef });
        return NativeObject.objectFor(ptr, defaultClass, needRef);        
    }
    
    /*
     * Hooks to/from native disposal
     */
    private static final GObjectAPI.GToggleNotify toggle = new GObjectAPI.GToggleNotify() {
        public void callback(Pointer data, Pointer ptr, boolean is_last_ref) {
            
            /*
             * Manage the strong reference to this instance.  When this is the last
             * reference to the underlying object, remove the strong reference so
             * it can be garbage collected.  If it is owned by someone else, then make
             * it a strong ref, so the java GObject for the underlying C object can
             * be retained for later retrieval
             */
            GObject o = (GObject) NativeObject.instanceFor(ptr);
            if (o == null) {
                return;
            }
            logger.log(LIFECYCLE, "toggle_ref " + o.getClass().getSimpleName() +
                    " (" +  ptr + ")" + " last_ref=" + is_last_ref);
            if (is_last_ref) {
                strongReferences.remove(o);
            } else {
                strongReferences.add(o);
            }
        }
    };
    private static final GObjectAPI.GWeakNotify weakNotify = new GObjectAPI.GWeakNotify() {
        public void callback(IntPtr data, Pointer ptr) {
            //System.out.println("GObject " + ptr + " id=" + data + " destroyed");
        }        
    };
            
    private static Set<Object> strongReferences = Collections.synchronizedSet(new HashSet<Object>());
}

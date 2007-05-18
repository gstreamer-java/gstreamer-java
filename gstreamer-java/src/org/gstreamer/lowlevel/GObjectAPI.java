/*
 * GObjectAPI.java
 */

package org.gstreamer.lowlevel;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 *
 */
public interface GObjectAPI extends Library {
    GObjectAPI gobj = (GObjectAPI) Native.loadLibrary("gobject-2.0", GObjectAPI.class);
    void g_object_set_property(Pointer obj, String property, Pointer data);
    void g_object_set(Pointer obj, String propertyName, Object data, Object end);
    void g_object_set(Pointer obj, String propertyName, Object arg1, Object arg2, Object end);
    void g_object_set(Pointer obj, String propertyName, Object arg1, Object arg2, Object arg3, Object end);
    interface GClosureNotify extends Callback {
        void callback(Pointer data, Pointer closure);
    }
    int g_signal_connect_data(Pointer obj, String signal, Callback callback, Pointer data,
            GClosureNotify destroy_data, int connect_flags);
    void g_signal_handler_disconnect(Pointer ptr, int id);
    boolean g_object_is_floating(Pointer obj);
    interface GToggleNotify extends Callback {
        void callback(Pointer data, Pointer obj, boolean is_last_ref);
    }
    void g_object_add_toggle_ref(Pointer object, GToggleNotify notify, Pointer data);
    void g_object_remove_toggle_ref(Pointer object, GToggleNotify notify, Pointer data);
    
    
}

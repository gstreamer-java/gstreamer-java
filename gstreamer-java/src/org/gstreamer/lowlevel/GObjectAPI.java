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

package org.gstreamer.lowlevel;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 *
 */
public interface GObjectAPI extends Library {
    GObjectAPI gobj = (GObjectAPI) Native.loadLibrary("gobject-2.0", GObjectAPI.class);
    void g_object_set_property(Pointer obj, String property, Pointer data);
    void g_object_set(Pointer obj, String propertyName, Object... data);
    interface GClosureNotify extends Callback {
        void callback(Pointer data, Pointer closure);
    }
    NativeLong g_signal_connect_data(Pointer obj, String signal, Callback callback, Pointer data,
            GClosureNotify destroy_data, int connect_flags);
    void g_signal_handler_disconnect(Pointer ptr, NativeLong id);
    boolean g_object_is_floating(Pointer obj);
    interface GToggleNotify extends Callback {
        void callback(Pointer data, Pointer obj, boolean is_last_ref);
    }
    void g_object_add_toggle_ref(Pointer object, GToggleNotify notify, Pointer data);
    void g_object_remove_toggle_ref(Pointer object, GToggleNotify notify, Pointer data);
    
    
}

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
import com.sun.jna.*;

/**
 *
 */
public interface GlibAPI extends Library {
    GlibAPI glib = (GlibAPI) Native.loadLibrary("glib-2.0", GlibAPI.class);
    Pointer g_main_loop_new(Pointer context, boolean running);
    void g_main_loop_run(Pointer loop);
    boolean g_main_loop_is_running(Pointer loop);
    void g_main_loop_quit(Pointer loop);
    void g_main_loop_ref(Pointer ptr);
    void g_main_loop_unref(Pointer ptr);
    
    /*
     * GMainContext functions
     */
    
    Pointer g_main_context_new();
    boolean g_main_context_pending(Pointer ctx);
    boolean g_main_context_acquire(Pointer ctx);
    void g_main_context_release(Pointer ctx);
    boolean g_main_context_is_owner(Pointer ctx);
    boolean g_main_context_wait(Pointer ctx);
    
    
    interface GSourceFunc extends Callback {
        boolean callback(Pointer data);
    }
    NativeLong g_idle_add(GSourceFunc function, Pointer data);
    interface GDestroyNotify extends Callback {
        void callback(Pointer data);
    }
    
    Pointer g_timeout_source_new(int interval);
    Pointer g_timeout_source_new_seconds(int interval);
    int g_timeout_add(int interval, GSourceFunc function, Pointer data);
    int g_timeout_add_full(int priority, int interval, GSourceFunc function,
            Pointer data, GDestroyNotify notify);
    int g_timeout_add_seconds(int interval, GSourceFunc function, Pointer data);
    void g_error_free(Pointer error);
    
    void g_source_remove(int id);
    void g_free(Pointer ptr);
    
    
}

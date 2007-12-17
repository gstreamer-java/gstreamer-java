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
import com.sun.jna.ptr.PointerByReference;
import java.util.HashMap;
import org.gstreamer.GMainLoop;
import org.gstreamer.glib.GDate;

/**
 *
 */
public interface GlibAPI extends Library {
    static GlibAPI glib = (GlibAPI) GNative.loadLibrary("glib-2.0", GlibAPI.class, new HashMap<String, Object>() {{
        put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
    }});
    Pointer g_main_loop_new(GMainContext context, boolean running);
    void g_main_loop_run(GMainLoop loop);
    boolean g_main_loop_is_running(GMainLoop loop);
    void g_main_loop_quit(GMainLoop loop);
    void g_main_loop_ref(GMainLoop ptr);
    void g_main_loop_unref(GMainLoop ptr);
    void g_main_loop_unref(Pointer ptr);
    
    /*
     * GMainContext functions
     */
    
    Pointer g_main_context_new();
    Pointer g_main_context_default();
    boolean g_main_context_pending(GMainContext ctx);
    boolean g_main_context_acquire(GMainContext ctx);
    void g_main_context_release(GMainContext ctx);
    boolean g_main_context_is_owner(GMainContext ctx);
    boolean g_main_context_wait(GMainContext ctx);
    
    Pointer g_idle_source_new();
    Pointer g_timeout_source_new(int interval);
    Pointer g_timeout_source_new_seconds(int interval);
    int g_source_attach(Pointer source, GMainContext context);
    void g_source_destroy(Pointer source);
    Pointer g_source_ref(Pointer source);
    void g_source_unref(Pointer source);
    void g_source_set_callback(Pointer source, GSourceFunc callback, Pointer data, GDestroyNotify destroy);
    boolean g_source_is_destroyed(Pointer source);
    /*
     * GThread functions
     */
    interface GThreadFunc extends Callback {
        Pointer callback(Pointer data);
    }
    Pointer g_thread_create(GThreadFunc func, Pointer data, boolean joinable, PointerByReference error);
    Pointer g_thread_self();
    Pointer g_thread_join(Pointer thread);
    void g_thread_yield();
    void g_thread_set_priority(Pointer thread, int priority);
    void g_thread_exit(Pointer retval);
    
    
    
    interface GSourceFunc extends Callback {
        boolean callback(Pointer data);
    }
    NativeLong g_idle_add(GSourceFunc function, Pointer data);
    interface GDestroyNotify extends Callback {
        void callback(Pointer data);
    }
    
    int g_timeout_add(int interval, GSourceFunc function, Pointer data);
    int g_timeout_add_full(int priority, int interval, GSourceFunc function,
            Pointer data, GDestroyNotify notify);
    int g_timeout_add_seconds(int interval, GSourceFunc function, Pointer data);
    void g_error_free(Pointer error);
    
    void g_source_remove(int id);
    void g_free(Pointer ptr);
    
    Pointer g_date_new();
    Pointer g_date_new_dmy(int day, int month, int year);
    Pointer g_date_new_julian(int julian_day);
    void g_date_free(Pointer date);
    
    public final static class GList extends com.sun.jna.Structure {
        public volatile Pointer data;
        public volatile Pointer _next;
        public volatile Pointer _prev;
        public GList() {            
        }
        private GList(Pointer pointer) {
            useMemory(pointer);
            read();
        }
        private static GList valueOf(Pointer ptr) {
            return ptr != null ? new GList(ptr) : null;
        }
        public GList next() {
            return valueOf(_next);
        }
        public GList prev() {
            return valueOf(_prev);
        }
    }
}

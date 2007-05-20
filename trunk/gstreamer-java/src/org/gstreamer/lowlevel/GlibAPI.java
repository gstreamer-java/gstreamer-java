/*
 * GlibAPI.java
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
    interface GSourceFunc extends Callback {
        boolean callback(Pointer data);
    }
    NativeLong g_idle_add(GSourceFunc function, Pointer data);
    
    
    
    void g_error_free(Pointer error);
    void g_free(Pointer ptr);
    
    void g_object_set_property(Pointer obj, String property, Pointer data);
}

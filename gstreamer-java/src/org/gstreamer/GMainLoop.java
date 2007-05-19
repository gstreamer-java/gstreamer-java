/*
 * GMainLoop.java
 */

package org.gstreamer;

import com.sun.jna.Pointer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import static org.gstreamer.lowlevel.GlibAPI.glib;

/**
 *
 */
public class GMainLoop extends NativeObject implements Runnable {
    
    /** Creates a new instance of GMainLoop */
    public GMainLoop() {
        this(glib.g_main_loop_new(null, false), false, true);
    }
    GMainLoop(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public void quit() {
        glib.g_main_loop_quit(handle());
    }
    public void run() {
        glib.g_main_loop_run(handle());
    }
    
    public boolean isRunning() {
        return glib.g_main_loop_is_running(handle()) != 0;
    }
    
    public void startInBackground() {
        bgThread = new java.lang.Thread(this);
        bgThread.setDaemon(true);
        bgThread.setName("gmainloop");
        bgThread.start();
    }
    public void invokeLater(final Runnable r) {
        Gst.invokeLater(r);
    }
    public void invokeAndWait(Runnable r) {
        FutureTask<Object> task = new FutureTask<Object>(r, null);
        invokeLater(task);
        try {
            task.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getCause());
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
    
    
    //--------------------------------------------------------------------------
    // protected methods
    //
    void ref() {
        glib.g_main_loop_ref(handle());
    }
    void unref() {
        glib.g_main_loop_unref(handle());
    }
    
    void disposeNativeHandle(Pointer ptr) {
        glib.g_main_loop_unref(ptr);
    }
    
    //--------------------------------------------------------------------------
    // Instance variables
    //
    private Thread bgThread;
}

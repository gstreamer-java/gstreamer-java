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

import com.sun.jna.Pointer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.gstreamer.lowlevel.GlibAPI;

/**
 *
 */
public class GMainLoop extends NativeObject implements Runnable {
    private static GlibAPI glib = GlibAPI.glib;
    
    /** Creates a new instance of GMainLoop */
    public GMainLoop() {
        this(glib.g_main_loop_new(Gst.getMainContext().handle(), false), false, true);
    }
    
    GMainLoop(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public void quit() {
        Gst.invokeLater(new Runnable() {
            public void run() {
                glib.g_main_loop_quit(handle());
            }
        });
    }
    public void run() {
        glib.g_main_loop_run(handle());
    }
    
    public boolean isRunning() {
        return glib.g_main_loop_is_running(handle());
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

/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer;

import com.sun.jna.Pointer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import static org.gstreamer.lowlevel.GlibAPI.glib;

/**
 *
 */
public class MainLoop extends NativeObject implements Runnable {
    
    /** Creates a new instance of MainLoop */
    public MainLoop() {
        this(glib.g_main_loop_new(Gst.getMainContext(), false), false, true);
    }
    
    MainLoop(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public void quit() {
        Gst.invokeLater(new Runnable() {
            public void run() {
                glib.g_main_loop_quit(MainLoop.this);
            }
        });
    }
    public void run() {
        glib.g_main_loop_run(this);
    }
    
    public boolean isRunning() {
        return glib.g_main_loop_is_running(this);
    }
    
    /**
     * Start the main loop in a background thread.
     */
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
    protected void ref() {
        glib.g_main_loop_ref(this);
    }
    protected void unref() {
        glib.g_main_loop_unref(this);
    }
    
    protected void disposeNativeHandle(Pointer ptr) {
        glib.g_main_loop_unref(ptr);
    }
    
    //--------------------------------------------------------------------------
    // Instance variables
    //
    private Thread bgThread;
}

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
import org.gstreamer.lowlevel.GlibAPI;

/**
 *
 */
public class Timeout {
    private static GlibAPI glib = GlibAPI.glib;
    public Timeout(int milliseconds, final Runnable run) {
        this.milliseconds = milliseconds;
        callback = new GlibAPI.GSourceFunc() {
            public boolean callback(Pointer source) {
                if (glib.g_source_is_destroyed(source)) {
                    return false;
                }
                run.run();
                return true;
            }
        };
    }
    public synchronized void start() {
        if (source != null) {
            glib.g_source_destroy(source);
        }
        /*
         * If the timeout is an even number of seconds, use the more efficient
         * g_timeout_add_seconds, if it is available.
         */
        if ((milliseconds % 1000) == 0) {
            try {
                source = glib.g_timeout_source_new_seconds(milliseconds / 1000);
            } catch (UnsatisfiedLinkError e) {
                source = glib.g_timeout_source_new(milliseconds);
            }
        } else {
            source = glib.g_timeout_source_new(milliseconds);
        }
        glib.g_source_set_callback(source, callback, source, null);
        glib.g_source_attach(source, Gst.getMainContext().handle());
        
        glib.g_source_unref(source);
    }
    public synchronized void stop() {
        if (source != null) {
            glib.g_source_destroy(source);
            source = null;
        }
    }
    protected void finalize() throws Throwable {
        try {
            if (source != null) {
                glib.g_source_destroy(source);
            }
        } finally {
            super.finalize();
        }
    }
    private Pointer source;
    private GlibAPI.GSourceFunc callback;
    private int milliseconds;
}

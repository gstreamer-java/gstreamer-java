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
    private static final GlibAPI glib = GlibAPI.glib;
    
    public Timeout(int milliseconds, final Runnable run) {
        this.milliseconds = milliseconds;
        id = 0;
        callback = new GlibAPI.GSourceFunc() {
            public boolean callback(Pointer unused) {
                run.run();
                return true;
            }
        };
    }
    public synchronized void start() {
        if (id != 0) {
            stop();
        }
        /*
         * If the timeout is an even number of seconds, use the more efficient
         * g_timeout_add_seconds, if it is available.
         */      
        if ((milliseconds % 1000) == 0) {
            try {
                id = glib.g_timeout_add_seconds(milliseconds / 1000, callback, null);
            } catch (UnsatisfiedLinkError e) {
                id = glib.g_timeout_add(milliseconds, callback, null);
            }
        } else {
            id = glib.g_timeout_add(milliseconds, callback, null);
        }
    }
    public synchronized void stop() {
        glib.g_source_remove(id);
        id = 0;
    }
    protected void finalize() {
        if (id != 0) {
            glib.g_source_remove(id);
        }
    }
    private GlibAPI.GSourceFunc callback;
    private int milliseconds;
    private int id;
}

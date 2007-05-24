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

import com.sun.jna.Pointer;
import java.util.concurrent.atomic.AtomicReference;
import org.gstreamer.lowlevel.GlibAPI;

/**
 *
 */
public class GSource {
    private static GlibAPI glib = GlibAPI.glib;
    
    public GSource(final Pointer ptr, final GlibAPI.GSourceFunc callback) {
        this.handle = new AtomicReference<Pointer>(ptr);
        this.callback = new GlibAPI.GSourceFunc() {
            public boolean callback(Pointer data) {
                if (glib.g_source_is_destroyed(ptr)) {
                    return false;
                }
                boolean result = callback.callback(data);
                if (!result) {
                    destroy();
                }
                return result;
            }
        };
        
        glib.g_source_set_callback(ptr, this.callback, null, null);
    }
    public static GSource newIdle(GlibAPI.GSourceFunc callback) {
        return new GSource(glib.g_idle_source_new(), callback);
    }
    public static GSource newTimeout(int milliseconds, GlibAPI.GSourceFunc callback) {
        Pointer ptr;
        /*
         * If the timeout is an even number of seconds, use the more efficient
         * g_timeout_add_seconds, if it is available.
         */
        if ((milliseconds % 1000) == 0) {
            try {
                ptr = glib.g_timeout_source_new_seconds(milliseconds / 1000);
            } catch (UnsatisfiedLinkError e) {
                ptr = glib.g_timeout_source_new(milliseconds);
            }
        } else {
            ptr = glib.g_timeout_source_new(milliseconds);
        }
        return new GSource(ptr, callback);
    }
    public int attach(GMainContext context) {
        return glib.g_source_attach(handle(), context.handle());
    }
    public void destroy() {
        final Pointer ptr = handle.getAndSet(null);
        if (ptr != null) {
            if (!glib.g_source_is_destroyed(ptr)) {
                glib.g_source_destroy(ptr);
            }
            glib.g_source_unref(ptr);
        }
    }
    protected void finalize() throws Throwable {
        try {
            //            System.out.println("destroying context");
            destroy();
        } finally {
            super.finalize();
        }
    }
    public Pointer handle() {
        return handle.get();
    }
    private GlibAPI.GSourceFunc callback;
    private AtomicReference<Pointer> handle;
}

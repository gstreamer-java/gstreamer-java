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

package org.gstreamer.lowlevel;

import com.sun.jna.Pointer;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class GSource extends Handle {
    private static GlibAPI glib = GlibAPI.glib;
    
    public GSource(final Pointer ptr, final GlibAPI.GSourceFunc callback, final Pointer data) {
        this.handle = new AtomicReference<Pointer>(ptr);
        this.callback = new GlibAPI.GSourceFunc() {
            public boolean callback(Pointer data) {
                if (glib.g_source_is_destroyed(ptr)) {
                    return false;
                }
                return callback.callback(data);
            }
        };
        
        glib.g_source_set_callback(ptr, this.callback, data, null);
    }
   
    public int attach(GMainContext context) {
        return glib.g_source_attach(handle(), context);
    }
    public void destroy() {
        final Pointer ptr = handle.getAndSet(null);
        if (ptr != null) {
            glib.g_source_destroy(ptr);
            glib.g_source_unref(ptr);
        }
    }
    protected void finalize() throws Throwable {
        try {
//            System.out.println("destroying GSource");
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

    protected Object nativeValue() {
        return handle;
    }
}

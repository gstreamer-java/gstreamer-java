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

import org.gstreamer.*;
import com.sun.jna.Pointer;

/**
 *
 * @author wayne
 */
public class GMainContext {
    private static GlibAPI glib = GlibAPI.glib;
    
    public GMainContext() {
        this(glib.g_main_context_new());
    }
    private GMainContext(Pointer handle) {
        this.handle = handle;
    }
    public GMainContext getDefaultContext() {
        return new GMainContext(glib.g_main_context_default());
    }
    void disposeNativeHandle(Pointer ptr) {
        glib.g_main_loop_unref(ptr);
    }

    public Pointer handle() { return handle; }
    Pointer handle;
}

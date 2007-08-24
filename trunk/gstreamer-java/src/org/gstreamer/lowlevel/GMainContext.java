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

/**
 *
 */
public class GMainContext extends NativeValue {
    private static GlibAPI glib = GlibAPI.glib;
    
    public GMainContext() {
        this(glib.g_main_context_new());
    }
    private GMainContext(Pointer handle) {
        this.handle = handle;
    }
    public int attach(GSource source) {
        return glib.g_source_attach(source.handle(), this);
    }
    public static GMainContext getDefaultContext() {
        return new GMainContext(glib.g_main_context_default());
    }
    
    public Pointer handle() { return handle; }
    Pointer handle;

    protected Object nativeValue() {
        return handle;
    }
}

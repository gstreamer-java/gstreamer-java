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

/**
 *
 */
public class GMainContext extends Handle {
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
    protected void invalidate() {}
    protected void ref() {}
    protected void unref() {}
}

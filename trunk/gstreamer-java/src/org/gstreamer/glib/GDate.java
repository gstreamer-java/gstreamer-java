/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.glib;

import org.gstreamer.lowlevel.GlibAPI;
import org.gstreamer.lowlevel.NativeObject;

import com.sun.jna.Pointer;


public class GDate extends NativeObject {
    public GDate(Initializer init) {
        super(init);
    }
    public GDate(Pointer ptr, boolean needRef, boolean ownsHandle) {
        this(initializer(ptr, needRef, ownsHandle));
    }
    
    @Override
    protected void disposeNativeHandle(Pointer ptr) {
        GlibAPI.GLIB_API.g_date_free(ptr);
    }
}

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

package org.gstreamer.glib;

import com.sun.jna.Pointer;
import org.gstreamer.NativeObject;
import org.gstreamer.lowlevel.GlibAPI;


public class GDate extends NativeObject {
    public GDate(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    
    @Override
    protected void disposeNativeHandle(Pointer ptr) {
        GlibAPI.glib.g_date_free(ptr);
    }

    @Override
    protected void ref() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void unref() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

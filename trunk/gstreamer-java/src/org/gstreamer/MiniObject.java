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
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class MiniObject extends NativeObject {

    /**
     * Creates a new instance of MiniObject
     */
    MiniObject(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    MiniObject(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    protected void ref() {
        gst.gst_mini_object_ref(this);
    }
    protected void unref() {
        gst.gst_mini_object_unref(this);
    }
    
    protected void disposeNativeHandle(Pointer ptr) {
        gst.gst_mini_object_unref(ptr);
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends MiniObject> T objectFor(Pointer ptr, Class<T> defaultClass, boolean needRef) {        
        return NativeObject.objectFor(ptr, defaultClass, needRef);        
    }
}

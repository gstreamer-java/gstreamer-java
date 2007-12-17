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

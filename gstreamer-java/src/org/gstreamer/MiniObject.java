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
import org.gstreamer.lowlevel.GstTypes;

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
    void ref() {
        gst.gst_mini_object_ref(this);
    }
    void unref() {
        gst.gst_mini_object_unref(this);
    }
    
    void disposeNativeHandle(Pointer ptr) {
        gst.gst_mini_object_unref(ptr);
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends MiniObject> T objectFor(Pointer ptr, Class<T> defaultClass, boolean needRef) {        
        // Ignore null pointers
        if (ptr == null || !ptr.isValid()) {
            return null;
        }
        // Try to retrieve an existing instance for the pointer
        // This is done here instead of just leaving it up to NativeObject.objectFor()
        // so the object type does not need to be read from native memory if there 
        // is already a proxy instnace
        NativeObject obj = NativeObject.instanceFor(ptr);
        if (obj != null && defaultClass.isInstance(obj)) {
            return (T) obj;
        }        
        return NativeObject.objectFor(ptr, NativeObject.classFor(ptr, defaultClass), needRef);
    }
}

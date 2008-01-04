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

import org.gstreamer.lowlevel.NativeObject;
import com.sun.jna.Pointer;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 * Lightweight base class for the GStreamer object hierarchy
 *
 * MiniObject is a baseclass like {@link GObject}, but has been stripped down of 
 * features to be fast and small.
 * It offers sub-classing and ref-counting in the same way as GObject does.
 * It has no properties and no signal-support though.
 */
public class MiniObject extends NativeObject {

    /**
     * Creates a new instance of MiniObject
     */
    MiniObject(Initializer init) {
        super(init);
    }
    
    /**
     * Checks if a mini-object is writable.  A mini-object is writable
     * if the reference count is one and the {@link MiniObjectFlags.READONLY}
     * flag is not set.  Modification of a mini-object should only be
     * done after verifying that it is writable.
     *
     * @return true if the object is writable.
     */
    public boolean isWritable() {
        return gst.gst_mini_object_is_writable(this);
    }
    
    /*
     * FIXME: this one returns a new MiniObject, so we need to replace the Pointer
     * with the new one.  Messy.
    public void makeWritable() {
        gst.gst_mini_object_make_writable(this);
    }
    */
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

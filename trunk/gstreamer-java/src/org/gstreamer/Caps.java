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
public class Caps extends NativeObject {
    
    public static Caps emptyCaps() {
        return new Caps(gst.gst_caps_new_empty());
    }
    public static Caps anyCaps() {
        return new Caps(gst.gst_caps_new_any());
    }
    
    public Caps() {
        this(gst.gst_caps_new_empty());
    }
    public Caps(String caps) {
        this(gst.gst_caps_from_string(caps));
    }
    Caps(Pointer ptr) {
        this(ptr, false);
    }
    Caps(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    Caps(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public int size() {
        return gst.gst_caps_get_size(handle());
    }
    public Caps copy() {
        return new Caps(gst.gst_caps_copy(handle()));
    }
    public Caps union(Caps other) {
        return new Caps(gst.gst_caps_union(handle(), other.handle()));
    }
    public void merge(Caps other) {
        gst.gst_caps_merge(handle(), other.handle());
    }
    public void merge(Structure struct) {
        gst.gst_caps_merge_structure(handle(), struct.handle());
        struct.disown();
    }
    public void append(Structure struct) {
        gst.gst_caps_append_structure(handle(), struct.handle());
        struct.disown();
    }
    public void setInteger(String field, Integer value) {
        gst.gst_caps_set_simple(handle(), field, value, null);
    }
    public Structure getStructure(int index) {
        return Structure.objectFor(gst.gst_caps_get_structure(handle(), index), false, false);
    }

    public static Caps objectFor(Pointer ptr, boolean needRef) {
        return (Caps) NativeObject.objectFor(ptr, Caps.class, needRef);
    }
    
    void ref() {
        gst.gst_caps_ref(handle());
    }
    void unref() {
        gst.gst_caps_unref(handle());
    }
    void disposeNativeHandle(Pointer ptr) {
        gst.gst_caps_unref(ptr);
    }

    
}

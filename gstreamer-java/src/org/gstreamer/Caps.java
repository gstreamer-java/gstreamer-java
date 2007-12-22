/*
 * Copyright (c) 2007 Wayne Meissner
 *
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
    public Caps(Caps caps) {
        this(gst.gst_caps_copy(caps));
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
        return gst.gst_caps_get_size(this);
    }
    public Caps copy() {
        return new Caps(gst.gst_caps_copy(this));
    }
    public Caps union(Caps other) {
        return new Caps(gst.gst_caps_union(this, other));
    }
    public void merge(Caps other) {
        gst.gst_caps_merge(this, other);
    }
    public void merge(Structure struct) {
        gst.gst_caps_merge_structure(this, struct);
        struct.disown();
    }
    public void append(Structure struct) {
        gst.gst_caps_append_structure(this, struct);
        struct.disown();
    }
    public void setInteger(String field, Integer value) {
        gst.gst_caps_set_simple(this, field, value, null);
    }
    public Structure getStructure(int index) {
        return Structure.objectFor(gst.gst_caps_get_structure(this, index), false, false);
    }
    
    @Override
    public String toString() {
        return gst.gst_caps_to_string(this);
    }
    
    public static Caps objectFor(Pointer ptr, boolean needRef) {
        return new Caps(ptr, needRef, true);
    }
    
    protected void ref() {
        gst.gst_caps_ref(this);
    }
    protected void unref() {
        gst.gst_caps_unref(this);
    }
    protected void disposeNativeHandle(Pointer ptr) {
        gst.gst_caps_unref(ptr);
    }

    
}

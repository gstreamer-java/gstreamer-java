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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Structure extends NativeObject {
    
    /**
     * Creates a new instance of Structure
     */
    Structure(Pointer ptr) {
        this(ptr, false, true);
    }
    protected Structure(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    protected Structure(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public Structure(String data) {
        this(gst.gst_structure_from_string(data, new PointerByReference()), true, false);
    }
    public Structure copy() {
        return new Structure(gst.gst_structure_copy(this));
    }
    public int getInteger(String field) {
        IntByReference intRef = new IntByReference();
        gst.gst_structure_get_int(this, field, intRef);
        return intRef.getValue();
    }
    public boolean setInteger(String field, Integer value) {
        return gst.gst_structure_fixate_field_nearest_int(this, field, value);
    }
    public boolean fixateFieldNearestInteger(String field, Integer target) {
        return gst.gst_structure_fixate_field_nearest_int(this, field, target);
    } 
    String getName() {
        return gst.gst_structure_get_name(this);
    }
    
    void setName(String name) {
        gst.gst_structure_set_name(this, name);
    }
    
    public boolean hasName(String name) {
        return gst.gst_structure_has_name(this, name);
    }
    
    public static Structure objectFor(Pointer ptr, boolean needRef, boolean ownsHandle) {
        return NativeObject.objectFor(ptr, Structure.class, needRef, ownsHandle);
    }
    //--------------------------------------------------------------------------
    void ref() {}
    void unref() {}
    void disposeNativeHandle(Pointer ptr) {
        gst.gst_structure_free(ptr);
    }
    
}

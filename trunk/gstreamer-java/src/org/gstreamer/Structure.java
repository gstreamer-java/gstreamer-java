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
import com.sun.jna.ptr.PointerByReference;
import org.gstreamer.lowlevel.GType;
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
        return gst.gst_structure_copy(this);
    }
    public class InvalidFieldException extends RuntimeException {
        public InvalidFieldException(String type, String fieldName) {
            super(String.format("Structure does not contain %s field '%s'", type, fieldName));
        }
    }
    public int getInteger(String fieldName) {
        int[] val = { 0 };
        if (!gst.gst_structure_get_int(this, fieldName, val)) {
            throw new InvalidFieldException("integer", fieldName);
        }
        return val[0];
    }
    public boolean setInteger(String field, Integer value) {
        return gst.gst_structure_fixate_field_nearest_int(this, field, value);
    }
    /**
     * 
     * @param fieldName
     * @return
     */
    public boolean getBoolean(String fieldName) {
        int[] val = { 0 };
        if (!gst.gst_structure_get_boolean(this, fieldName, val)) {
            throw new InvalidFieldException("boolean", fieldName);
        }
        return val[0] != 0;
    }
    public Fraction getFraction(String fieldName) {
        int[] numerator = { 0 };
        int[] denominator = { 0 };
        if (!gst.gst_structure_get_fraction(this, fieldName, numerator, denominator)) {
            throw new InvalidFieldException("boolean", fieldName);
        }
        return new Fraction(numerator[0], denominator[0]);
    }
    public boolean fixateFieldNearestInteger(String field, Integer target) {
        return gst.gst_structure_fixate_field_nearest_int(this, field, target);
    } 
    public String getName() {
        return gst.gst_structure_get_name(this);
    }
    
    public void setName(String name) {
        gst.gst_structure_set_name(this, name);
    }
    
    public boolean hasName(String name) {
        return gst.gst_structure_has_name(this, name);
    }
    /**
     * Check if the {@link Structure} contains a field named fieldName.
     *
     * @param fieldName The name of the field to check.
     * @return true if the structure contains a field with the given name.
     */
    public boolean hasField(String fieldName) {
        return gst.gst_structure_has_field(this, fieldName);
    }
    
    /**
     * Check if the {@link Structure} contains a field named fieldName.
     *
     * @param fieldName The name of the field to check.
     * @param fieldType The type of the field.
     * @return true if the structure contains a field named fieldName and of type fieldType
     */
    public boolean hasField(String fieldName, GType fieldType) {
        return gst.gst_structure_has_field_typed(this, fieldName, fieldType);
    }
    /**
     * Check if the {@link Structure} contains an integer field named fieldName.
     *
     * @param fieldName The name of the field to check.
     * @return true if the structure contains an integer field named fieldName
     */
    public boolean hasIntField(String fieldName) {
        return hasField(fieldName, GType.INT);
    }
    
    /**
     * Check if the {@link Structure} contains a double field named fieldName.
     *
     * @param fieldName The name of the field to check.
     * @return true if the structure contains a double field named fieldName
     */
    public boolean hasDoubleField(String fieldName) {
        return hasField(fieldName, GType.DOUBLE);
    }
    
    @Override
    public String toString() {
        return gst.gst_structure_to_string(this);
    }
    public static Structure objectFor(Pointer ptr, boolean needRef, boolean ownsHandle) {
        return NativeObject.objectFor(ptr, Structure.class, needRef, ownsHandle);
    }
    //--------------------------------------------------------------------------
    protected void ref() {}
    protected void unref() {}
    protected void disposeNativeHandle(Pointer ptr) {
        gst.gst_structure_free(ptr);
    }
    
}

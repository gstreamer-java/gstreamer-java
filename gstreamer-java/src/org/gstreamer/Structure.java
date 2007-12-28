/* 
 * Copyright (C) 2007 Wayne Meissner
 * Copyright (C) 2003 David A. Schleef <ds@schleef.org>
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
import com.sun.jna.ptr.PointerByReference;
import org.gstreamer.lowlevel.GType;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 * Generic structure containing fields of names and values.
 * <p>
 * A Structure is a collection of key/value pairs. The keys are expressed
 * as GQuarks and the values can be of any GType.
 * <p>
 * In addition to the key/value pairs, a Structure also has a name. The name
 * starts with a letter and can be followed by letters, numbers and any of "/-_.:".
 * <p>
 * Structure is used by various GStreamer subsystems to store information
 * in a flexible and extensible way. 
 * <p>
 * A Structure can be created with new {@link #Structure(String)} or 
 * {@link #Structure(String, String, Object...)}, which both take a name and an
 * optional set of key/value pairs along with the types of the values.
 * <p>
 * Field values can be changed with {@link #setValue} or {@link #set}.
 * <p>
 * Field values can be retrieved with {@link #getValue} or the more
 * specific get{Integer,String}() etc functions.
 * <p>
 * Fields can be removed with {@link #removeField} or {@link #removeFields}
 * @see Caps
 * @see Event
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
    /**
     * Creates a new, empty #GstStructure with the given name.
     *
     * @param name The name of new structure.
     */
    public Structure(String name) {
        this(gst.gst_structure_empty_new(name));
    }
    /**
     * Creates a new Structure with the given name.  Parses the
     * list of variable arguments and sets fields to the values listed.
     * Variable arguments should be passed as field name, field type,
     * and value.
     *
     * @param name The name of new structure.
     * @param firstFieldName The name of first field to set
     * @param data Additional arguments.
     */
    public Structure(String name, String firstFieldName, Object... data) {
        this(gst.gst_structure_new(name, firstFieldName, data));
    }
    /**
     * Creates a Structure from a string representation.
     *
     * @param data A string representation of a Structure.
     * @return A new Structure or null when the string could not be parsed.
     */
    public static Structure fromString(String data) {
        return new Structure(gst.gst_structure_from_string(data, new PointerByReference()));
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
    
    /**
     * Get the name of @structure as a string.
     *
     * @return The name of the structure.
     */
    public String getName() {
        return gst.gst_structure_get_name(this);
    }
    
    /**
     * Sets the name of the structure to the given name.
     * 
     * The name must not be empty, must start with a letter and can be followed 
     * by letters, numbers and any of "/-_.:".
     * 
     * @param name The new name of the structure.
     */
    public void setName(String name) {
        gst.gst_structure_set_name(this, name);
    }
    
    /**
     * Checks if the structure has the given name.
     * 
     * @param name structure name to check for
     * @return true if @name matches the name of the structure.
     */
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
     * Check if the {@link Structure} contains a field named fieldName.
     *
     * @param fieldName The name of the field to check.
     * @param fieldType The type of the field.
     * @return true if the structure contains a field named fieldName and of type fieldType
     */
    public boolean hasField(String fieldName, Class<?> fieldType) {
        return gst.gst_structure_has_field_typed(this, fieldName, GType.valueOf(fieldType));
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
    
    /**
     * Removes the field with the given name from the structure.
     * If the field with the given name does not exist, the structure is unchanged.
     * @param fieldName The name of the field to remove.
     */
    public void removeField(String fieldName) {
        gst.gst_structure_remove_field(this, fieldName);
    }
    
    /**
     * Removes the fields with the given names. 
     * If a field does not exist, the argument is ignored.
     * 
     * @param fieldNames A list of field names to remove.
     */
    public void removeFields(String... fieldNames) {
        gst.gst_structure_remove_fields(this, fieldNames);
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

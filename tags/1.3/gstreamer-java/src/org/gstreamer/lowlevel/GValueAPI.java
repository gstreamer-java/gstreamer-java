/*
 * Copyright (c) 2009 Levente Farkas
 * Copyright (c) 2008 Andres Colubri
 * Copyright (c) 2008 Wayne Meissner
 *
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.lowlevel;

import java.util.HashMap;

import org.gstreamer.GObject;
import org.gstreamer.lowlevel.annotations.CallerOwnsReturn;
import org.gstreamer.lowlevel.annotations.Invalidate;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 *
 * @author wayne
 */
@SuppressWarnings("serial")
public interface GValueAPI extends Library {
    GValueAPI GVALUE_API = GNative.loadLibrary("gobject-2.0", GValueAPI.class,
    		new HashMap<String, Object>() {{
    			put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
    		}});

    static class GValue extends com.sun.jna.Structure {
        /*< private >*/
        public volatile GType g_type;

        /* public for GTypeValueTable methods */
        public static class GValueData extends com.sun.jna.Union {
            public volatile int v_int;
            public volatile long v_long;
            public volatile long v_int64;
            public volatile float v_float;
            public volatile double v_double;
            public volatile Pointer v_pointer;
        }
        public volatile GValueData data[] = new GValueData[2];

        public GValue()
        {
            super();
        }
        public GValue(Pointer ptr)
        {
            useMemory(ptr);
            read();
        }
    }

    static class GValueArray extends com.sun.jna.Structure {
        public volatile int n_values;
        public volatile GValue[] values;
        //public volatile Pointer values;
        //< private >
        public volatile int n_prealloced;

        public GValueArray() {
            clear();
        }
        public GValueArray(Pointer pointer) {
            // pointer points to a structure with three integer fields: n_values (an integer),
            // values (an array of GValues, therefore also a pointer, i.e.: an integer
            // memory address) and n_prealloced (an integer). Then this structure
            // occupies 12 bytes (4 for n_values, 4 for the array pointer and 4 for
            // n_prealloced.

            // Viewing the data pointed by the pointer as an array of three integers.
            int[] intArray = pointer.getIntArray(0, 3);
            n_values = intArray[0];     // the first element of the array is n_values.
            n_prealloced = intArray[2]; // the third element of the array n_prealloced.

            // By constructing a new pointer taking the original and offsetting it by
            // 4 bytes, we get the pointer to the GValues array.
            Pointer pointerToArray = pointer.getPointer(4);

            // This is how to construct an array of structures from a given pointer.
            // First, a single instance of GValue is created from the pointer data.
            GValue val = new GValue(pointerToArray);
            // The structure is converted into an array with the appropriate number
            // of elements.
            values = (GValue[])val.toArray(n_values);
        }
        @SuppressWarnings("unused")
        private static GValueArray valueOf(Pointer ptr) {
            return ptr != null ? new GValueArray(ptr) : null;
        }

        public int getNValues() {
            return n_values;
        }
        
        public Object getValue(int i) {
           GType valType = values[i].g_type;
           if (valType.equals(GType.INT)) { return new Integer(GVALUE_API.g_value_get_int(values[i]));
           } else if (valType.equals(GType.UINT)) { return new Integer(GVALUE_API.g_value_get_uint(values[i]));
           } else if (valType.equals(GType.CHAR)) { return new Byte(GVALUE_API.g_value_get_char(values[i]));
           } else if (valType.equals(GType.UCHAR)) { return new Byte(GVALUE_API.g_value_get_uchar(values[i]));
           } else if (valType.equals(GType.LONG)) { return new Long(GVALUE_API.g_value_get_long(values[i]).longValue());
           } else if (valType.equals(GType.ULONG)) { return new Long(GVALUE_API.g_value_get_ulong(values[i]).longValue());
           } else if (valType.equals(GType.INT64)) { return new Long(GVALUE_API.g_value_get_int64(values[i]));
           } else if (valType.equals(GType.UINT64)) { return new Long(GVALUE_API.g_value_get_uint64(values[i]));
           } else if (valType.equals(GType.BOOLEAN)) { return new Boolean(GVALUE_API.g_value_get_boolean(values[i]));
           } else if (valType.equals(GType.FLOAT)) { return new Float(GVALUE_API.g_value_get_float(values[i]));
           } else if (valType.equals(GType.DOUBLE)) { return new Double(GVALUE_API.g_value_get_double(values[i]));
           } else if (valType.equals(GType.STRING)) { return GVALUE_API.g_value_get_string(values[i]);
           } else if (valType.equals(GType.OBJECT)) { return GVALUE_API.g_value_get_object(values[i]);
           }
           return null;
        }
    }
    
    GValue g_value_init(GValue value, GType g_type);
    GValue g_value_reset(GValue value);
    void g_value_unset(GValue value);
    void g_value_set_char(GValue value, byte v_char);
    byte g_value_get_char(GValue value);
    void g_value_set_uchar(GValue value, byte v_uchar);
    byte g_value_get_uchar(GValue value);
    void g_value_set_boolean(GValue value, boolean v_boolean);
    boolean g_value_get_boolean(GValue value);
    void g_value_set_int(GValue value, int v_int);
    int g_value_get_int(GValue value);
    void g_value_set_uint(GValue value, int v_int);
    int g_value_get_uint(GValue value);
    void g_value_set_long(GValue value, NativeLong v_long);
    NativeLong g_value_get_long(GValue value);
    void g_value_set_ulong(GValue value, NativeLong v_long);
    NativeLong g_value_get_ulong(GValue value);
    void g_value_set_int64(GValue value, long v_int64);
    long g_value_get_int64(GValue value);
    void g_value_set_uint64(GValue value, long v_uint64);
    long g_value_get_uint64(GValue value);
    void g_value_set_float(GValue value, float v_float);
    float g_value_get_float(GValue value);
    void g_value_set_double(GValue value, double v_double);
    double g_value_get_double(GValue value);
    void g_value_set_enum(GValue value, int v_enum);
    int g_value_get_enum(GValue value);
    void g_value_set_string(GValue value, String v_string);
    void g_value_set_static_string (GValue value, String v_string);
    String g_value_get_string(GValue value);
    boolean g_value_type_compatible(GType src_type, GType dest_type);
    boolean g_value_type_transformable(GType src_type, GType dest_type);
    boolean g_value_transform(GValue src_value, GValue dest_value);
    
    void g_value_set_object(GValue value, GObject v_object);
    void g_value_take_object(GValue value, @Invalidate GObject v_object);
    GObject g_value_get_object(GValue value);
    @CallerOwnsReturn GObject g_value_dup_object(GValue value);
   
    GValue g_value_array_get_nth(GValueArray value_array, int index);
    Pointer g_value_array_new(int n_prealloced);
    void g_value_array_free (GValueArray value_array);

    Pointer g_value_array_copy(GValueArray value_array);
    Pointer g_value_array_prepend(GValueArray value_array, GValue value);
    Pointer g_value_array_append(GValueArray value_array, GValue value);
    Pointer g_value_array_insert(GValueArray value_array, int index_, GValue value);
    Pointer g_value_array_remove(GValueArray value_array, int index);
/*
 * GCompareDataFunc needs to be implemented.
Pointer g_value_array_sort(GValueArray value_array, GCompareFunc compare_func);
Pointer g_value_array_sort_with_data (GValueArray value_array,
        GCompareDataFunc compare_func, Pointer user_data);
 */
}

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

    public interface NoMapperAPI extends Library {

        Pointer g_value_get_object(GValue value);

        Pointer g_value_dup_object(GValue value);
    }

    NoMapperAPI GVALUE_NOMAPPER_API = GNative.loadLibrary("gobject-2.0", NoMapperAPI.class,
                    new HashMap<String, Object>() {});

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
        
        public boolean checkHolds(GType type) {
        	return GVALUE_API.g_type_check_value_holds(this, type);
        }
        
        public GType getType() {
        	return g_type;
        }
        
        public Object getValue() {
            if (g_type.equals(GType.INT)) { return toInt();
            } else if (g_type.equals(GType.UINT)) { return toUInt();
            } else if (g_type.equals(GType.CHAR)) { return toChar();
            } else if (g_type.equals(GType.UCHAR)) { return toUChar();
            } else if (g_type.equals(GType.LONG)) { return toLong();
            } else if (g_type.equals(GType.ULONG)) { return toULong();
            } else if (g_type.equals(GType.INT64)) { return toInt64();
            } else if (g_type.equals(GType.UINT64)) { return toUInt64();
            } else if (g_type.equals(GType.BOOLEAN)) { return toBoolean();
            } else if (g_type.equals(GType.FLOAT)) { return toFloat();
            } else if (g_type.equals(GType.DOUBLE)) { return toDouble();
            } else if (g_type.equals(GType.STRING)) { return toJavaString();
            } else if (g_type.equals(GType.OBJECT)) { return toObject();
            } else if (g_type.equals(GType.POINTER)) { return toPointer();
            } else {
            	// object type not supported!
            }
            return null;
        }
        
        public Integer toInt() {
        	return g_type.equals(GType.INT) ? new Integer(GVALUE_API.g_value_get_int(this)) : null; 
        }
        
        public Integer toUInt() {
        	return g_type.equals(GType.UINT) ? new Integer(GVALUE_API.g_value_get_uint(this)) : null; 
        }
        
        public Byte toChar() {
        	return g_type.equals(GType.CHAR) ? new Byte(GVALUE_API.g_value_get_char(this)) : null; 
        }
        
        public Byte toUChar() {
        	return g_type.equals(GType.UCHAR) ? new Byte(GVALUE_API.g_value_get_uchar(this)) : null;
        }
        
        public Long toLong() {
        	return g_type.equals(GType.LONG) ? new Long(GVALUE_API.g_value_get_long(this).longValue()) : null;
        }
        
        public Long toULong() {
        	return g_type.equals(GType.ULONG) ? new Long(GVALUE_API.g_value_get_ulong(this).longValue()) : null; 
        }
        
        public Long toInt64() {
        	return g_type.equals(GType.INT64)? new Long(GVALUE_API.g_value_get_int64(this)) : null; 
        }
        
        public Long toUInt64() {
        	return g_type.equals(GType.UINT64) ? new Long(GVALUE_API.g_value_get_uint64(this)) : null;
        }
        
        public Boolean toBoolean() {
        	return g_type.equals(GType.BOOLEAN) ? new Boolean(GVALUE_API.g_value_get_boolean(this)) : null;
        }
        
        public Float toFloat() {
        	return g_type.equals(GType.FLOAT) ? new Float(GVALUE_API.g_value_get_float(this)) : null;
        }
        
        public Double toDouble() {
        	return g_type.equals(GType.DOUBLE) ? new Double(GVALUE_API.g_value_get_double(this)) : null;
        }
        
        public String toJavaString() {
        	return g_type.equals(GType.STRING) ? GVALUE_API.g_value_get_string(this) : null;
        }
        
        public Object toObject() {
        	return g_type.equals(GType.OBJECT) ? GVALUE_API.g_value_get_object(this) : null;
        }
        
        public Pointer toPointer() {
        	return g_type.equals(GType.POINTER) ? GVALUE_API.g_value_get_pointer(this) : null;
        }
        
        public String toString() {
        	return GVALUE_API.g_strdup_value_contents(this);
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
            n_values = pointer.getInt(0);

            if (n_values > 0) {
                Pointer pointerToArray = pointer.getPointer(GType.SIZE);
                GValue val = new GValue(pointerToArray);
                values = (GValue[]) val.toArray(n_values);
            } else {
                values = new GValue[0];
            }
        }
        @SuppressWarnings("unused")
        private static GValueArray valueOf(Pointer ptr) {
            return ptr != null ? new GValueArray(ptr) : null;
        }

        public int getNValues() {
            return n_values;
        }

        public Object getValue(int i) {
            return values[i].getValue();
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
    Pointer g_value_get_pointer(GValue value);
    boolean g_value_type_compatible(GType src_type, GType dest_type);
    boolean g_value_type_transformable(GType src_type, GType dest_type);
    boolean g_value_transform(GValue src_value, GValue dest_value);
    
    @CallerOwnsReturn String g_strdup_value_contents(GValue value);
    
    void g_value_set_object(GValue value, GObject v_object);
    void g_value_take_object(GValue value, @Invalidate GObject v_object);
    GObject g_value_get_object(GValue value);
    @CallerOwnsReturn GObject g_value_dup_object(GValue value);
   
    Pointer g_value_get_boxed(GValue value);

    GValue g_value_array_get_nth(GValueArray value_array, int index);
    Pointer g_value_array_new(int n_prealloced);
    void g_value_array_free (GValueArray value_array);

    Pointer g_value_array_copy(GValueArray value_array);
    Pointer g_value_array_prepend(GValueArray value_array, GValue value);
    Pointer g_value_array_append(GValueArray value_array, GValue value);
    Pointer g_value_array_insert(GValueArray value_array, int index_, GValue value);
    Pointer g_value_array_remove(GValueArray value_array, int index);
    
    boolean g_type_check_value_holds(GValue value, GType type);

/*
 * GCompareDataFunc needs to be implemented.
Pointer g_value_array_sort(GValueArray value_array, GCompareFunc compare_func);
Pointer g_value_array_sort_with_data (GValueArray value_array,
        GCompareDataFunc compare_func, Pointer user_data);
 */
}

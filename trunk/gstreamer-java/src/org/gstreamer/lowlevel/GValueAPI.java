/*
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
    static GValueAPI gvalue = GNative.loadLibrary("gobject-2.0", GValueAPI.class, new HashMap<String, Object>() {{
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
}

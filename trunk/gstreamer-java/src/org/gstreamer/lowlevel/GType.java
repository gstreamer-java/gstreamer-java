/* 
 * Copyright (c) 2007 Wayne Meissner
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

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeLong;

/**
 *
 */
@SuppressWarnings("serial")
public class GType extends NativeLong {
    private static final GType[] cache;
    static {
        cache = new GType[21];
        for (int i = 0; i < cache.length; ++i) {
            cache[i] = new GType(i << 2);
        }        
    };
    public static final GType INVALID = init(0);
    public static final GType NONE = init(1);
    public static final GType INTERFACE = init(2);
    public static final GType CHAR = init(3);
    public static final GType UCHAR = init(4);
    public static final GType BOOLEAN = init(5);
    public static final GType INT = init(6);
    public static final GType UINT = init(7);
    public static final GType LONG = init(8);
    public static final GType ULONG = init(9);
    public static final GType INT64 = init(10);
    public static final GType UINT64 = init(11);
    public static final GType ENUM = init(12);
    public static final GType FLAGS = init(13);
    public static final GType FLOAT = init(14);
    public static final GType DOUBLE = init(15);
    public static final GType STRING = init(16);
    public static final GType POINTER = init(17);
    public static final GType BOXED = init(18);
    public static final GType PARAM = init(19);
    public static final GType OBJECT = init(20);

    private static GType init(int v) {
        return valueOf(v << 2);
    }
    public GType(long t) {
        super(t);
    }
    public GType() {
        super(0);
    }
    public static GType valueOf(long value) {
        if (value >= 0 && (value >> 2) < cache.length) {
            return cache[(int)value >> 2];
        }
        return new GType(value);
    }
    public static GType valueOf(Class<?> javaType) {
        if (Integer.class == javaType || int.class == javaType) {
            return INT;
        } else if (Long.class == javaType || long.class == javaType) {
            return INT64;
        } else if (Float.class == javaType || float.class == javaType) {
            return FLOAT;
        } else if (Double.class == javaType || double.class == javaType) {
            return DOUBLE;
        } else if (String.class == javaType) {
            return STRING;
        } else {
            throw new IllegalArgumentException("No GType for " + javaType);
        }
    }
    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return valueOf(((Number) nativeValue).longValue());
    }    
}

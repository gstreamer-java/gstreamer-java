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

package org.gstreamer.lowlevel;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeLong;

/**
 *
 */
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
    GType(long t) {
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
    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return valueOf(((Number) nativeValue).longValue());
    }    
}

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

/**
 *
 */
public enum SeekFlags {
    NONE(0),
    FLUSH(1 << 0),
    ACCURATE(1 << 1),
    KEY_UNIT(1 << 2),
    SEGMENT(1 << 3);
    
    private SeekFlags(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
    public static SeekFlags valueOf(int value) {
        for (SeekFlags f : values()) {
            if (f.value == value) {
                return f;
            }
        }
        throw new IllegalArgumentException("Invalid SeekFlags value: " + value);
    }
    private int value;
}

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
public enum PadLinkReturn {
    OK(0),
    WRONG_HIERARCHY(-1),
    WAS_LINKED(-2),
    WRONG_DIRECTION(-3),
    NOFORMAT(-4),
    NOSCHED(-5),
    REFUSED(-6);
    PadLinkReturn(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
    public static PadLinkReturn valueOf(int value) {
        for (PadLinkReturn r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid PadLinkReturn value: " + value);
    }
    private final int value;
    
}

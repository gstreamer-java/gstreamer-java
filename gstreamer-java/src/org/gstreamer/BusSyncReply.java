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

public enum BusSyncReply {
    DROP(0),
    PASS(1),
    ASYNC(2);
    
    BusSyncReply(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
    public static BusSyncReply valueOf(int value) {
        for (BusSyncReply r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid BusSyncReply value: " + value);
    }
    private final int value;
}

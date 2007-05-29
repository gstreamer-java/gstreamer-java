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

/**
 *
 */
public enum StateChangeReturn {
    FAILURE,
    SUCCESS,
    ASYNC,
    NO_PREROLL;
    public static final StateChangeReturn valueOf(int val) {
        if (val < 0 || val >= values().length) {
            throw new IllegalArgumentException("Invalid StateChangeReturn");
        }
        return values()[val];
    }
}

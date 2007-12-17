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
public enum TagMergeMode {
    UNDEFINED,
    REPLACE_ALL,
    REPLACE,
    APPEND,
    PREPEND,
    KEEP,
    KEEP_ALL,
    /* add more */
    COUNT;
    public int intValue() {
        return ordinal();
    }
    //
    // Static functions
    //
    public static final TagMergeMode valueOf(int mode) {
        for (TagMergeMode m : values()) {
            if (m.intValue() == mode) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid GstTagMergeMode(" + mode + ")");
    }
}

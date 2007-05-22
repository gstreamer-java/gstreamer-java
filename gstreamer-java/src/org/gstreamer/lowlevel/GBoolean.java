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

/**
 *
 */
public class GBoolean {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static int valueOf(boolean value) {
        return value ? 1 : 0;
    }
    public static int valueOf(Boolean value) {
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }
}

/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer;

/**
 *
 */
public enum Format {
    UNDEFINED(0),
    DEFAULT(1),
    BYTES(2),
    TIME(3),
    BUFFERS(4),
    PERCENT(5),
    UNKNOWN(~0);
    Format(int value) {
        this.value = value;
    }
    public final int intValue() {
        return value;
    }
    public final static Format valueOf(int format) {
        for (Format f : values()) {
            if (f.value == format) {
                return f;
            }
        }
        return UNKNOWN;
    }
    public final int value;
}

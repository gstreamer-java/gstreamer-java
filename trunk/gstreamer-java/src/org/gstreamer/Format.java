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

import org.gstreamer.lowlevel.EnumMapper;
import org.gstreamer.lowlevel.IntegerEnum;

/**
 * Standard predefined formats.
 */
public enum Format implements IntegerEnum {
    /** Undefined format */
    UNDEFINED(0),
    /**
     * The default format of the pad/element. This can be samples for raw audio,
     * frames/fields for raw video.
     */
    DEFAULT(1),
    /** bytes */
    BYTES(2),
    /** Time in nanoseconds */
    TIME(3),
    
    /** {@link Buffer}s */
    BUFFERS(4),
    /** Percentage of stream */
    PERCENT(5),
    
    /** Unknown Format value - used by EnumMapper.valueOf(), so don't change the name */
    __UNKNOWN_NATIVE_VALUE(~0);
    Format(int value) {
        this.value = value;
    }
    /**
     * Gets the integer value of the enum.
     * @return The integer value for this enum.
     */
    public final int intValue() {
        return value;
    }
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param format integer value.
     * @return Enum constant.
     */
    public final static Format valueOf(int format) {
        return EnumMapper.getInstance().valueOf(format, Format.class);
    }
    public final int value;
}

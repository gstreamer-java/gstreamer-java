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
 * Extra tag flags used when registering tags.
 */
public enum TagFlag {
    /** Undefined flag. */
    UNDEFINED,
    /** Tag is meta data. */
    META,
    /** Tag is encoded. */
    ENCODED,
    /** Tag is decoded. */
    DECODED,
    /** Number of tag flags. */
    COUNT;
    
    /**
     * Get the integer value of the enum.
     * @return The integer value for this enum.
     */
    public int intValue() {
        return ordinal();
    }
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param flag integer value.
     * @return The enum constant with the specified value.
     * @throws java.lang.IllegalArgumentException if the enum type has no constant with the specified value.
     */
    public static final TagFlag valueOf(int flag) {
        for (TagFlag f : values()) {
            if (f.intValue() == flag) {
                return f;
            }
        }
        throw new IllegalArgumentException("Invalid GstTagFlag(" + flag + ")");
    }
}

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
 * The direction of a {@link Pad}.
 */
public enum PadDirection {
    /** The direction is unknown. */
    UNKNOWN,
    /** The {@link Pad} is a source pad. */
    SRC,
    /** The {@link Pad} is a sink pad. */
    SINK;
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param dir integer value.
     * @return Enum constant.
     */
    public static final PadDirection valueOf(int dir) {
        for (PadDirection d : values()) {
            if (d.ordinal() == dir) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid PadDirection: " + dir);
    }
}

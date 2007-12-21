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


public enum PadPresence {
    /**
     * The Pad is always available.
     */
    ALWAYS,
    /**
     * The pad will become available depending on the media stream
     */
    SOMETIMES,
    /**
     * The pad is only available on request.
     */
    REQUEST;
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param value Integer value that corresponds to one of the constants.
     * @return A {@link PadPresence} value that maps to the integer.
     */
    public static final PadPresence valueOf(int value) {
        for (PadPresence p : values()) {
            if (p.ordinal() == value) {
                return p;
            }
        }
        throw new IllegalArgumentException("Invalid PadPresence: " + value);
    }
}

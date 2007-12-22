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
 * A set of buffer flags used to describe properties of a {@link Buffer}.
 */
public enum BufferFlag {
    /** 
     * The {@link Buffer} is read-only.
     * This means the data of the buffer should not be modified. The metadata 
     * might still be modified.
     */
    READONLY(MiniObjectFlags.READONLY.intValue()),
    
    /**  
     * The {@link Buffer} is part of a preroll and should not be displayed.
     */
    PREROLL(MiniObjectFlags.LAST.intValue() << 0),
    /**
     * The {@link Buffer} marks a discontinuity in the stream.
     * This typically occurs after a seek or a dropped buffer from a live or
     * network source.
     */
    DISCONT(MiniObjectFlags.LAST.intValue() << 1),
    
    /** The {@link Buffer} has been added as a field in a {@link Caps}. */
    IN_CAPS(MiniObjectFlags.LAST.intValue() << 2),
    
    /**
     * The {@link Buffer} has been created to fill a gap in the
     * stream and contains media neutral data (elements can switch to optimized code
     * path that ignores the buffer content).
     */
    GAP(MiniObjectFlags.LAST.intValue() << 3),
    
    /** This unit cannot be decoded independently. */
    DELTA_UNIT(MiniObjectFlags.LAST.intValue() << 4),
    
    /* padding */
    LAST(MiniObjectFlags.LAST.intValue() << 8),
    UNKNOWN(~0);
    
    private BufferFlag(int value) {
        this.value = value;
    }
    /**
     * Get the integer value of the enum.
     * @return The integer value for this enum.
     */
    public final int intValue() {
        return value;
    }
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param type integer value.
     * @return Enum constant.
     */
    public final static BufferFlag valueOf(int type) {
        for (BufferFlag t : values()) {
            if (t.intValue() == type) {
                return t;
            }
        }
        return UNKNOWN;
    }
    private final int value;
    
}

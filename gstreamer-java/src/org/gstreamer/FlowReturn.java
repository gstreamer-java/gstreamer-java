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
 * The result of passing data to a pad.
 */
public enum FlowReturn {
    /** Resend buffer, possibly with new caps (not send yet). */
    RESEND(1),
    /** Data passing was ok. */
    OK(0),

    /** {@link Pad} is not linked. */
    NOT_LINKED(-1),
    /** {@link Pad} is in wrong state. */
    WRONG_STATE(-2),
    /** Did not expect anything, like after EOS. */
    UNEXPECTED(-3),
    /** {@link Pad} is in not negotiated. */
    NOT_NEGOTIATED(-4),
    
    /**
     * Some (fatal) error occured. Element generating this error should post 
     * an error message with more details.
     */
    ERROR(-5),
    
    /** This operation is not supported. */
    NOT_SUPPORTED(-6);

    FlowReturn(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param value integer value.
     * @return Enum constant.
     */
    public static FlowReturn valueOf(int value) {
        for (FlowReturn r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid FlowReturn value: " + value);
    }
    private int value;
}

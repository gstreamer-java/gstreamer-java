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
 * The posible states an element can be in.
 */
public enum State implements IntegerEnum {
    /** No pending state. */
    VOID_PENDING(0),
    /** The initial state of an {@link Element}. */
    NULL(1),
    /** The {@link Element} is ready to go to PAUSED. */
    READY(2),
    /** The {@link Element} is PAUSED */
    PAUSED(3),
    /** The {@link Element} is PLAYING */
    PLAYING(4);
    
    State(int value) {
        this.value = value;
    }
    
    /**
     * Gets the integer value of the enum.
     * @return The integer value for this enum.
     */
    public int intValue() {
        return value;
    }
    private final int value;
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param state integer value.
     * @return Enum constant.
     * @throws java.lang.IllegalArgumentException if the enum type has no constant with the specified value.
     */
    public static final State valueOf(int state) {
        return EnumMapper.getInstance().valueOf(state, State.class);
    }
}

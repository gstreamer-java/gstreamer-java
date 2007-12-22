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
 * The possible return values from a state change function. 
 * <p>
 * Only {@link StateChangeReturn#FAILURE} is a real failure.
 */
public enum StateChangeReturn {
    /** The state change failed. */
    FAILURE,
    /** The state change succeeded. */
    SUCCESS,
    /** The state change will happen asynchronously. */
    ASYNC,
    /**
     * The state change succeeded but the {@link Element} cannot produce data in 
     * {@link State#PAUSED}. This typically happens with live sources.
     */
    NO_PREROLL;
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param val integer value.
     * @return The enum constant with the specified value.
     * @throws java.lang.IllegalArgumentException if the enum type has no constant with the specified value.
     */
    public static final StateChangeReturn valueOf(int val) {
        if (val < 0 || val >= values().length) {
            throw new IllegalArgumentException("Invalid StateChangeReturn");
        }
        return values()[val];
    }
}

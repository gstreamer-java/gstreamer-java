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

import org.gstreamer.lowlevel.IntegerEnum;

/**
 * The result values for a GstBusSyncHandler.
 */
public enum BusSyncReply implements IntegerEnum {
    /** Drop the {@link Message} */
    DROP(0),
    /** Pass the {@link Message} to the async queue */
    PASS(1),
    /** Pass {@link Message} to async queue, continue if message is handled */
    ASYNC(2);
    
    BusSyncReply(int value) {
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
}

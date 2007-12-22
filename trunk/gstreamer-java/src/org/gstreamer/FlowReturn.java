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


public enum FlowReturn {
    /* core predefined */ 
    RESEND(1),
    OK(0),
    /* expected failures */
    NOT_LINKED(-1),
    WRONG_STATE(-2),
    /* error cases */
    UNEXPECTED(-3),
    NOT_NEGOTIATED(-4),
    ERROR(-5),
    NOT_SUPPORTED(-6);

    FlowReturn(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
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

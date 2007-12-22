/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
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

/**
 * The status of a {@link Pad}. After activating a pad, which usually happens when 
 * the parent element goes from {@link State#READY} to {@link State#PAUSED}, the 
 * ActivateMode defines if the {@link Pad} operates in push or pull mode.
 */
public enum ActivateMode {
    /** 
     * Pad will not handle dataflow
     */
    NONE,
    /**
     * Pad handles dataflow in downstream push mode
     */
    PUSH,
    /**
     * Pad handles dataflow in upstream pull mode
     */
    PULL;

    public final int intValue() {
        return ordinal();
    }
    /**
     * Returns the enum constant of this type with the specified ordinal value.
     * @param type integer value.
     * @return Enum constant.
     */
    public final static ActivateMode valueOf(int type) {
        for (ActivateMode m : values()) {
            if (m.intValue() == type) {
                return m;
            }
        }
        return NONE;
    }
}

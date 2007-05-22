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
import static org.gstreamer.MiniObjectFlags.*;

/**
 *
 */
public enum BufferFlag {
    READONLY(MiniObjectFlags.READONLY.intValue()),
    PREROLL(MiniObjectFlags.LAST.intValue() << 0),
    DISCONT(MiniObjectFlags.LAST.intValue() << 1),
    IN_CAPS(MiniObjectFlags.LAST.intValue() << 2),
    GAP(MiniObjectFlags.LAST.intValue() << 3),
    DELTA_UNIT(MiniObjectFlags.LAST.intValue() << 4),
    /* padding */
    LAST(MiniObjectFlags.LAST.intValue() << 8),
    UNKNOWN(~0);
    private BufferFlag(int value) {
        this.value = value;
    }
    public final int intValue() {
        return value;
    }
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

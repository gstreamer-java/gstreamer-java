/*
 * BufferFlag.java
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

/*
 * Format.java
 */

package org.gstreamer;

/**
 *
 */
public enum Format {
    UNDEFINED(0),
    DEFAULT(1),
    BYTES(2),
    TIME(3),
    BUFFERS(4),
    PERCENT(5),
    UNKNOWN(~0);
    Format(int value) {
        this.value = value;
    }
    public final int intValue() {
        return value;
    }
    public final static Format valueOf(int format) {
        for (Format f : values()) {
            if (f.value == format) {
                return f;
            }
        }
        return UNKNOWN;
    }
    public final int value;
}

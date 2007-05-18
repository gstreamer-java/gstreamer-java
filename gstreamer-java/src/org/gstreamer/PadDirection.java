/*
 * PadDirection.java
 */

package org.gstreamer;

/**
 *
 */
public enum PadDirection {
    UNKNOWN,
    SRC,
    SINK;
    public static final PadDirection valueOf(int dir) {
        for (PadDirection d : values()) {
            if (d.ordinal() == dir) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid PadDirection: " + dir);
    }
}

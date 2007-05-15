/*
 * GBoolean.java
 */

package org.gstreamer.lowlevel;

/**
 *
 */
public class GBoolean {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static int valueOf(boolean value) {
        return value ? 1 : 0;
    }
    public static int valueOf(Boolean value) {
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }
}

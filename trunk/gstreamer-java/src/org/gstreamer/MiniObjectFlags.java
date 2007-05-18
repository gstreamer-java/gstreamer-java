/*
 * MiniObjectFlags.java
 */

package org.gstreamer;

/**
 *
 */
public enum MiniObjectFlags {
    READONLY(1 << 0),
    /* padding */
    LAST(1 << 4);
    
    
    private MiniObjectFlags(int value) {
        this.value = value;
    }
    private final int value;
    public int intValue() {
        return value;
    }
}

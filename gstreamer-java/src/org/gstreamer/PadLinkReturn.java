/*
 * PadLinkReturn.java
 */

package org.gstreamer;

/**
 *
 */
public enum PadLinkReturn {
    OK(0),
    WRONG_HIERARCHY(-1),
    WAS_LINKED(-2),
    WRONG_DIRECTION(-3),
    NOFORMAT(-4),
    NOSCHED(-5),
    REFUSED(-6);
    PadLinkReturn(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
    public static PadLinkReturn valueOf(int value) {
        for (PadLinkReturn r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid PadLinkReturn value: " + value);
    }
    private final int value;
    
}

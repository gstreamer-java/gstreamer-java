/*
 * State.java
 */

package org.gstreamer;

public enum State {

    VOID_PENDING(0),
    NULL(1),
    READY(2),
    PAUSED(3),
    PLAYING(4);
    
    State(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
    public final int value;
    
    //
    // Static functions
    //
    public static final State valueOf(int state) {
        for (State s : values()) {
            if (s.value == state) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid GstState(" + state + ")");
    }
    
}

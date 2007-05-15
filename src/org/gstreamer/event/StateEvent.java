/*
 * State.java
 */

package org.gstreamer.event;

import org.gstreamer.GstObject;
import org.gstreamer.State;


/**
 *
 */
public class StateEvent extends java.util.EventObject {
    
    /**
     * Creates a new instance of State
     */
    public StateEvent(GstObject src, int o, int n, int p) {
        super(src);
        oldState = State.valueOf(o);
        newState = State.valueOf(n);
        pendingState = State.valueOf(p);
    }
    public final State oldState, newState, pendingState;
}

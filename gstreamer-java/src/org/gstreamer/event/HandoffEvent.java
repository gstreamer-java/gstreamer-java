/*
 * HandoffEvent.java
 */

package org.gstreamer.event;

import org.gstreamer.Buffer;
import org.gstreamer.Element;
import org.gstreamer.Pad;

/**
 *
 */
public class HandoffEvent extends java.util.EventObject {
    
    /**
     * Creates a new instance of HandoffEvent
     */
    public HandoffEvent(Element src, Buffer buffer, Pad pad) {
        super(src);
        this.buffer = buffer;
    }
    public Buffer getBuffer() {
        return buffer;
    }
    private final Buffer buffer;
}

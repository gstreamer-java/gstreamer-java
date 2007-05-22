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

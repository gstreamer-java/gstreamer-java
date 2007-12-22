/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
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

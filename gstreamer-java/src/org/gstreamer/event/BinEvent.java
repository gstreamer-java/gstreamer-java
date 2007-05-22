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

import org.gstreamer.Bin;
import org.gstreamer.Element;

/**
 *
 */
public class BinEvent extends java.util.EventObject {
    
    /**
     * Creates a new instance of BinEvent
     */
    public BinEvent(Bin bin, Element elem) {
        super(bin);
        this.bin = bin;
        this.elem = elem;
    }
    public Element getElement() {
        return elem;
    }
    public Bin getBin() {
        return bin;
    }
    private Bin bin;
    private Element elem;
}

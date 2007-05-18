/*
 * BinEvent.java
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

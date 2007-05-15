/*
 * ElementEvent.java
 */

package org.gstreamer.event;

import org.gstreamer.Element;
import org.gstreamer.Pad;

/**
 *
 */
public class ElementEvent extends java.util.EventObject {
    
    /**
     * Creates a new instance of ElementEvent
     */
    public ElementEvent(Element elem, Pad pad) {
        super(elem);
        this.pad = pad;
    }
    public Element getElement() {
        return (Element) getSource();
    }
    public Pad getPad() {
        return pad;
    }
    private Pad pad;
}

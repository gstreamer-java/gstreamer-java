/*
 * BusAdapter.java
 */

package org.gstreamer.event;

import org.gstreamer.TagList;

/**
 *
 */
public class BusAdapter implements BusListener {
    
    /**
     * Creates a new instance of BusAdapter
     */
    public BusAdapter() {
    }
    public void errorEvent(ErrorEvent e) {}
    public void warningEvent(ErrorEvent e) {}
    public void infoEvent(ErrorEvent e) {}
    public void stateEvent(StateEvent e) {}
    public void eosEvent() {}
    public void tagEvent(TagList l) { }
}

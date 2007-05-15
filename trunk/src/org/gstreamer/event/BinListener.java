/*
 * BinListener.java
 */

package org.gstreamer.event;

/**
 *
 */
public interface BinListener {
    public void elementAdded(BinEvent evt);
    public void elementRemoved(BinEvent evt);
    
}

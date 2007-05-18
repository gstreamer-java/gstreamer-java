/*
 * ElementListener.java
 */

package org.gstreamer.event;

/**
 *
 */
public interface ElementListener {
    public void padAdded(ElementEvent evt);
    public void padRemoved(ElementEvent evt);
    public void noMorePads(ElementEvent evt);
}

/*
 * Message.java
 */

package org.gstreamer;

import com.sun.jna.Pointer;

/**
 *
 */
public class Message extends MiniObject {
    
    /**
     * Creates a new instance of Message
     */
    protected Message(Pointer ptr, boolean needRef) {
        this(ptr, true, needRef);
    }
    public Message(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
    }
}

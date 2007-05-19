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
        super(ptr, needRef);
    }
    protected Message(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
}

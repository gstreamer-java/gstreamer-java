/*
 * MessageStruct.java
 */

package org.gstreamer.lowlevel;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 *
 */
public final class MessageStruct extends Structure {
    public MiniObjectStruct mini_object;
    public Pointer lock;
    public Pointer cond;
    public int type;
    public long timestamp;
    public Pointer src;
    public Pointer structure;
    
    /**
     * Creates a new instance of MessageStruct
     */
    public MessageStruct() {
    }
    public MessageStruct(Pointer ptr) {
        useMemory(ptr);
        read();
    }
    public void write() { }
}

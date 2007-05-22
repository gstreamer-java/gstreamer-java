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

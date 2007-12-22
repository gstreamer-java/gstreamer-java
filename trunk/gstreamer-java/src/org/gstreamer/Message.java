/* 
 * Copyright (c) 2007 Wayne Meissner
 *
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

package org.gstreamer;

import com.sun.jna.Pointer;
import org.gstreamer.lowlevel.GstAPI;

/**
 *
 */
public class Message extends MiniObject {
    GstAPI.MessageStruct messageStruct;
    
    /**
     * Creates a new instance of Message
     */
    protected Message(Pointer ptr, boolean needRef) {
        super(ptr, needRef);        
        messageStruct = new GstAPI.MessageStruct(ptr);
    }
    protected Message(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
        messageStruct = new GstAPI.MessageStruct(ptr);
    }
    public GstObject getSource() {
        return Element.objectFor(messageStruct.src, true);
    }
    public Structure getStructure() {
        return Structure.objectFor(messageStruct.structure, false, false);
    }
    public int getType() {
        return messageStruct.type;
    }
}

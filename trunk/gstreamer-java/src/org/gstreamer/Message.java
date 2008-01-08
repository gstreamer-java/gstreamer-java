/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
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
    public Message(Initializer init) {
        super(init);
        messageStruct = new GstAPI.MessageStruct(handle());
    }
    
    protected Message(Pointer ptr, boolean needRef, boolean ownsHandle) {
        this(initializer(ptr, needRef, ownsHandle));
    }
    public GstObject getSource() {
        return messageStruct.src;
    }
    public Structure getStructure() {
        return Structure.objectFor(messageStruct.structure, false, false);
    }
    public int getType() {
        return messageStruct.type;
    }
}

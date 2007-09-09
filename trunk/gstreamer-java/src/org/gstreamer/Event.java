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


package org.gstreamer;

import com.sun.jna.Pointer;
import org.gstreamer.lowlevel.GstAPI;

public class Event extends MiniObject {
    
    /**
     * Creates a new instance of Buffer
     */    
    Event(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    Event(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
        //struct = new BufferStruct(ptr);
    }
    public static Event eosEvent() {
        return GstAPI.gst.gst_event_new_eos();
    }
}

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
import com.sun.jna.*;
import com.sun.jna.ptr.*;

/**
 *
 */
public class GErrorStruct extends Structure {
    private int domain; /* GQuark */
    public int code;
    public String message;
    
    /** Creates a new instance of GError */
    public GErrorStruct(Pointer ptr) {
        useMemory(ptr, 0);
    }
}

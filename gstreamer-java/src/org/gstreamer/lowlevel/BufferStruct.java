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
public final class BufferStruct extends Structure {
    public MiniObjectStruct mini_object;
    public Pointer data;
    public int size;
    public long timestamp;
    public long duration;
    public Pointer caps;
    public long offset;
    public long offset_end;
    public Pointer malloc_data;
    public BufferStruct(Pointer ptr) {
        useMemory(ptr);
        read();
    }
    public void write() {};
}

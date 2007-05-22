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
import java.nio.ByteBuffer;
import org.gstreamer.lowlevel.BufferStruct;

/**
 *
 */
public class Buffer extends MiniObject {
    
    /**
     * Creates a new instance of Buffer
     */
    Buffer(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    Buffer(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
        struct = new BufferStruct(ptr);
    }
    public int getSize() {
        return struct.size;
    }
    public long getDuration() {
        return struct.duration;
    }
    public long getTimestamp() {
        return struct.timestamp;
    }
    public Caps getCaps() {
        return Caps.objectFor(struct.caps, true);
    }
    public void write(int bufferOffset, byte[] data, int offset, int length) {
        struct.data.write(bufferOffset, data, offset, length);
    }
    public void write(int bufferOffset, int[] data, int offset, int length) {
        struct.data.write(bufferOffset, data, offset, length);
    }
    public void read(int bufferOffset, byte[] data, int offset, int length) {
        struct.data.read(bufferOffset, data, offset, length);
    }
    public void read(int bufferOffset, int[] data, int offset, int length) {
        struct.data.read(bufferOffset, data, offset, length);
    }    
    public ByteBuffer getByteBuffer() {
        return struct.data.getByteBuffer(0, struct.size);
    }
    public static Buffer objectFor(Pointer ptr, boolean needRef) {
        return (Buffer) MiniObject.objectFor(ptr, Buffer.class, needRef);
    }
    private BufferStruct struct;
}

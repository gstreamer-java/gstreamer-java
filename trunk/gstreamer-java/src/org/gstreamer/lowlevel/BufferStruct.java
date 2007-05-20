/*
 * BufferStruct.java
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

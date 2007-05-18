/*
 * Buffer.java
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
        this(ptr, true, needRef);
    }
    Buffer(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
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
        return Caps.instanceFor(struct.caps, true);
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
    public static Buffer instanceFor(Pointer ptr, boolean needRef) {
        return (Buffer) MiniObject.instanceFor(ptr, Buffer.class, needRef);
    }
    private BufferStruct struct;
}

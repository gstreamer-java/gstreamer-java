/*
 * GstMiniObjectStructure.java
 */

package org.gstreamer.lowlevel;
import com.sun.jna.*;

/**
 *
 */
public class MiniObjectStruct extends Structure {
    public GTypeInstance instance;
    public int refcount;
    public int flags;
    public Pointer _gst_reserved;
            
    /** Creates a new instance of GstMiniObjectStructure */
    public MiniObjectStruct() {}
    public MiniObjectStruct(Pointer ptr) {
        useMemory(ptr, 0);
        read();
    }
    public void write() { }
}

/*
 * GError.java
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

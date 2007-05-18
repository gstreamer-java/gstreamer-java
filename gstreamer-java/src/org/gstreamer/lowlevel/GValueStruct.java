/*
 * GValueStruct.java
 */

package org.gstreamer.lowlevel;

import com.sun.jna.Structure;

/**
 *
 */
public class GValueStruct extends Structure {
    int g_type;
    /** Creates a new instance of GValueStruct */
    public GValueStruct() {
    }
    public void write() { }
}

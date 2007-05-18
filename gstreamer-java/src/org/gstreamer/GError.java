/*
 * GError.java
 */

package org.gstreamer;

import org.gstreamer.lowlevel.GErrorStruct;

/**
 *
 */
public class GError extends RuntimeException {
    
    /** Creates a new instance of GError */
    GError(com.sun.jna.Pointer ptr) {
        GErrorStruct err = new GErrorStruct(ptr);
        code = err.code;
        message = err.message;
    }
    @Override
    public String getMessage() {
        return message;
    }
    public final int getErrorCode() {
        return code;
    }
    public final int code;
    public final String message;
}

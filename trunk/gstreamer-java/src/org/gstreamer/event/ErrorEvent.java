/*
 * GstError.java
 */

package org.gstreamer.event;

import org.gstreamer.GstObject;

/**
 *
 */
public class ErrorEvent extends java.util.EventObject {
    
    /** Creates a new instance of GstError */
    public ErrorEvent(GstObject src, int code, String msg) {
        super(src);
        this.code = code;
        this.message = msg;
    }
    public final int code;
    public final String message;
}

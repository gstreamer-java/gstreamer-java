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

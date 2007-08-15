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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.util.Map;

/**
 *
 */
public class GNative {

    public GNative() {
    }

    public static Library loadLibrary(String name, Class<? extends Library> interfaceClass, Map options) {
        if (!Platform.isWindows()) {
            return Native.loadLibrary(name, interfaceClass, options);
        }
        
        //
        // gstreamer on win32 names the dll files one of foo.dll, libfoo.dll and libfoo-0.dll
        //
        String[] nameFormats = { "%s", "lib%s", "lib%s-0" };
        for (int i = 0; i < nameFormats.length; ++i) {
            try {
                return Native.loadLibrary(String.format(nameFormats[i], name), interfaceClass, options);
            } catch (Throwable ex) {                
                if (i == (nameFormats.length - 1)) {
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException)ex;
                    }
                    throw new RuntimeException(ex);
                }
            }
        }
        throw new RuntimeException("Could not load library " + name);
    }
}

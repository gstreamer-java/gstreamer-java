/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.lowlevel;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import java.util.Map;

/**
 *
 */
public class GNative {

    public GNative() {
    }

    public static Library loadLibrary(String name, Class<? extends Library> interfaceClass, Map<String, ?> options) {
        if (Platform.isWindows()) {
            return loadWin32Library(name, interfaceClass, options);
        }
        return (Library)Native.loadLibrary(name, interfaceClass, options);
    }
    private static Library loadWin32Library(String name, Class<? extends Library> interfaceClass, Map<String, ?> options) {        
        //
        // gstreamer on win32 names the dll files one of foo.dll, libfoo.dll and libfoo-0.dll
        //
        String[] nameFormats = { 
            "%s", "lib%s", "lib%s-0",                   
        };
        for (int i = 0; i < nameFormats.length; ++i) {
            try {
                return (Library)Native.loadLibrary(String.format(nameFormats[i], name), interfaceClass, options);
            } catch (UnsatisfiedLinkError ex) {                
                continue;
            }
        }
        throw new UnsatisfiedLinkError("Could not load library " + name);
    }
    private static NativeLibrary getWin32NativeLibrary(String name) {
        //
        // gstreamer on win32 names the dll files one of foo.dll, libfoo.dll and libfoo-0.dll
        //
        String[] nameFormats = { 
            "%s", "lib%s", "lib%s-0",                   
        };
        for (int i = 0; i < nameFormats.length; ++i) {
            try {
                return NativeLibrary.getInstance(String.format(nameFormats[i], name));
            } catch (UnsatisfiedLinkError ex) {                
                continue;
            }
        }
        throw new UnsatisfiedLinkError("Could not load library " + name);
    }
    public static NativeLibrary getNativeLibrary(String name) {
        if (Platform.isWindows()) {
            return getWin32NativeLibrary(name);
        }
        return NativeLibrary.getInstance(name);
    }
}

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

import com.sun.jna.Pointer;

public class IntPtr extends Number {
    public final Number value;
    public IntPtr(int value) {
        this.value = Pointer.SIZE == 8 ? new Long(value) : new Integer(value);
    }
    
    public String toString() {        
        return Integer.toHexString(intValue());
    }

    public int intValue() {
        return value.intValue();
    }
    
    public long longValue() {
        return value.longValue();
    }

    public float floatValue() {
        return value.floatValue();        
    }

    public double doubleValue() {
        return value.doubleValue();        
    }
}

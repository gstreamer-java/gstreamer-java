/*
 * Copyright (c) 2007 Wayne Meissner
 *
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

package org.gstreamer.elements;

import org.gstreamer.*;
import com.sun.jna.Pointer;

public class BaseSrc extends Element {
    /**
     *
     * @param ptr C Pointer to the underlying GstBaseSrc
     * @param needRef
     * @param ownsHandle Whether this instance should destroy the underlying object when finalized
     * 
     */
    protected BaseSrc(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    
    /**
     *
     * @param ptr
     */
    protected BaseSrc(Pointer ptr) {
        super(ptr);
    }
}

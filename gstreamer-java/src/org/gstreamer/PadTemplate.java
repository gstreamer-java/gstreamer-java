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

package org.gstreamer;

import com.sun.jna.Pointer;
import java.util.logging.Logger;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import static org.gstreamer.lowlevel.GstAPI.gst;


public class PadTemplate extends GstObject {
    static Logger logger = Logger.getLogger(PadTemplate.class.getName());
    
    /** Creates a new instance of PadTemplate */
    public PadTemplate(String nameTemplate, PadDirection direction, Caps caps) {
        super(gst.gst_pad_template_new(nameTemplate, direction, 0, caps));            
    }
    protected PadTemplate(Pointer ptr) {
        super(ptr);
    }
    protected PadTemplate(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    protected PadTemplate(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }    
}

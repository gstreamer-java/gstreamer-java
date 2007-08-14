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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Parse {
    
    /** Creates a new instance of Parse */
    private Parse() {
    }
    
    public static Element launch(String pipeline) throws GError {
        PointerByReference err = new PointerByReference();
        Pointer p = gst.gst_parse_launch(pipeline, err);
        if (p == null || !p.isValid()) {
            throw new GError(err.getValue());
        }
        return Element.objectFor(p, true);
    }
    public static Element launch(String... pipeline) throws GError {
        PointerByReference err = new PointerByReference();
        Pointer p = gst.gst_parse_launchv(pipeline, err);
        if (p == null || !p.isValid()) {
            throw new GError(err.getValue());
        }
        return Element.objectFor(p, true);
    }
}

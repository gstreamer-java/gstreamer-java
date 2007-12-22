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
        if (p == null) {
            throw new GError(err.getValue());
        }
        return Element.objectFor(p, true);
    }
    public static Element launch(String... pipeline) throws GError {
        PointerByReference err = new PointerByReference();
        Pointer p = gst.gst_parse_launchv(pipeline, err);
        if (p == null) {
            throw new GError(err.getValue());
        }
        return Element.objectFor(p, true);
    }
}

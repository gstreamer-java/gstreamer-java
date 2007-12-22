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
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import java.util.HashMap;
import org.gstreamer.Element;

public interface GstInterfacesAPI extends Library {

    GstInterfacesAPI INSTANCE = (GstInterfacesAPI) GNative.loadLibrary("gstinterfaces-0.10", GstInterfacesAPI.class, 
            new HashMap<String, Object>() {{
                put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
            }}
    );

    
    void gst_x_overlay_set_xwindow_id(Element overlay, NativeLong xwindow_id);
    void gst_x_overlay_set_xwindow_id(Element overlay, Pointer xwindow_id);
    NativeLong gst_x_overlay_get_type();
}

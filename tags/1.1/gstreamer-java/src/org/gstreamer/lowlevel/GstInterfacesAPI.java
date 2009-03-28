/*
 * Copyright (c) 2008 Andres Colubri
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.lowlevel;

import org.gstreamer.Element;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

@SuppressWarnings("serial")
public interface GstInterfacesAPI extends Library {
    /*
    GstInterfacesAPI INSTANCE = GNative.loadLibrary("gstinterfaces-0.10", GstInterfacesAPI.class, 
            new HashMap<String, Object>() {{
                put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
            }}
    );
    */
    GstInterfacesAPI INSTANCE = GstNative.load(GstInterfacesAPI.class);

    GType  gst_implements_interface_get_type();
    boolean gst_element_implements_interface(Element element, GType iface_type);
    Pointer gst_implements_interface_cast(NativeObject from, GType type);
    Pointer gst_implements_interface_check(NativeObject from, GType type);

    /*
    void gst_x_overlay_set_xwindow_id(Element overlay, NativeLong xwindow_id);
    void gst_x_overlay_set_xwindow_id(Element overlay, Pointer xwindow_id);
    void  gst_x_overlay_expose(Element overlay);

    GType gst_x_overlay_get_type();
    GType gst_mixer_track_get_type();
    GType gst_mixer_options_get_type();
    GType gst_tuner_channel_get_type();
    GType gst_tuner_norm_get_type();
     * */
}

/* 
 * Copyright (c) 2009 Levente Farkas
 * Copyright (c) 2007, 2008 Wayne Meissner
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

import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.Event;
import org.gstreamer.FlowReturn;
import org.gstreamer.Pad;
import org.gstreamer.PadDirection;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.PadTemplate;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import org.gstreamer.lowlevel.annotations.CallerOwnsReturn;
import org.gstreamer.lowlevel.annotations.FreeReturnValue;
import org.gstreamer.lowlevel.annotations.IncRef;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * GstPad functions
 */
public interface GstPadAPI extends com.sun.jna.Library {
    static GstPadAPI GSTPAD_API = GstNative.load(GstPadAPI.class);
    
    GType gst_pad_get_type();
    GType gst_ghost_pad_get_type();
    @CallerOwnsReturn Pad gst_pad_new(String name, PadDirection direction);
    @CallerOwnsReturn Pad gst_pad_new_from_template(PadTemplate templ, String name);
    @FreeReturnValue String gst_pad_get_name(Pad pad);
    PadLinkReturn gst_pad_link(Pad src, Pad sink);
    boolean gst_pad_unlink(Pad src, Pad sink);
    boolean gst_pad_is_linked(Pad pad);
    @CallerOwnsReturn Pad gst_pad_get_peer(Pad pad);
    PadDirection gst_pad_get_direction(Pad pad);
    /* pad functions from gstutils.h */
    boolean gst_pad_can_link(Pad srcpad, Pad sinkpad);

    void gst_pad_use_fixed_caps(Pad pad);
    @CallerOwnsReturn Caps gst_pad_get_fixed_caps_func(Pad pad);
    @CallerOwnsReturn Caps gst_pad_proxy_getcaps(Pad pad);
    boolean gst_pad_proxy_setcaps(Pad pad, Caps caps);
    @CallerOwnsReturn Element gst_pad_get_parent_element(Pad pad);
    

    boolean gst_pad_set_active(Pad pad, boolean active);
    boolean gst_pad_is_active(Pad pad);
    boolean gst_pad_activate_pull(Pad pad, boolean active);
    boolean gst_pad_activate_push(Pad pad, boolean active);
    boolean gst_pad_set_blocked(Pad pad, boolean blocked);
    boolean gst_pad_is_blocked(Pad pad);
    boolean gst_pad_is_blocking(Pad pad);
    /* get_pad_template returns a non-refcounted PadTemplate */
    PadTemplate gst_pad_get_pad_template(Pad pad);
    boolean gst_pad_set_blocked_async(Pad pad, boolean blocked, PadBlockCallback callback, Pointer userData);
    
    /* capsnego function for connected/unconnected pads */
    @CallerOwnsReturn Caps gst_pad_get_caps(Pad  pad);
    void gst_pad_fixate_caps(Pad pad, Caps caps);
    boolean gst_pad_accept_caps(Pad pad, Caps caps);
    boolean gst_pad_set_caps(Pad pad, Caps caps);
    @CallerOwnsReturn Caps gst_pad_peer_get_caps(Pad pad);
    boolean gst_pad_peer_accept_caps(Pad pad, Caps caps);
    
    /* capsnego for connected pads */
    @CallerOwnsReturn Caps gst_pad_get_allowed_caps(Pad pad);
    @CallerOwnsReturn Caps gst_pad_get_negotiated_caps(Pad pad);

    /* data passing functions to peer */
    FlowReturn gst_pad_push(Pad pad, @IncRef Buffer buffer);
    boolean gst_pad_check_pull_range(Pad pad);
    FlowReturn gst_pad_pull_range(Pad pad, /* guint64 */ long offset, /* guint */ int size,
            Buffer[] buffer);
    boolean gst_pad_push_event(Pad pad, @IncRef Event event);
    boolean gst_pad_event_default(Pad pad, Event event);
    /* data passing functions on pad */
    FlowReturn gst_pad_chain(Pad pad, @IncRef Buffer buffer);
    FlowReturn gst_pad_get_range(Pad pad, /* guint64 */ long offset, /* guint */ int size,
        Buffer[] buffer);
    boolean gst_pad_send_event(Pad pad, @IncRef Event event);
    public static interface PadFixateCaps extends GstCallback {
        void callback(Pad pad, Caps caps);
    }
    void gst_pad_set_fixatecaps_function(Pad pad, PadFixateCaps fixate);
    
    public static interface PadBlockCallback extends GstCallback {
        void callback(Pad pad, boolean blocked, Pointer user_data);
    }

    /* probes */
    public static interface PadDataProbe extends GstCallback {
        void callback(Pad pad, Buffer buffer, Pointer user_data);
    }
    public static interface PadEventProbe extends GstCallback {
        boolean callback(Pad pad, Event ev, Pointer user_data);
    }

    NativeLong /* gulong */ gst_pad_add_data_probe(Pad pad, PadDataProbe handler, Pointer data);

    void gst_pad_remove_data_probe(Pad pad, NativeLong handler_id);

    NativeLong /* gulong */ gst_pad_add_event_probe(Pad pad, PadEventProbe handler, Pointer data);

    void gst_pad_remove_event_probe(Pad pad, NativeLong handler_id);

    NativeLong /* gulong */ gst_pad_add_buffer_probe(Pad pad, GstCallback handler, Pointer data);

    void gst_pad_remove_buffer_probe(Pad pad, NativeLong handler_id);
}

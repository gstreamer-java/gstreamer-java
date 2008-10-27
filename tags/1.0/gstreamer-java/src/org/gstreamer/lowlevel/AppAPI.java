/*
 * Copyright (c) 2008 Wayne Meissner
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

import java.util.HashMap;

import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.FlowReturn;
import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.AppSrc;
import org.gstreamer.lowlevel.annotations.CallerOwnsReturn;
import org.gstreamer.lowlevel.annotations.Invalidate;

import com.sun.jna.Library;

/**
 *
 * @author wayne
 */
@SuppressWarnings("serial")
public interface AppAPI extends com.sun.jna.Library {
    static AppAPI INSTANCE = GNative.loadLibrary("gstapp-0.10", 
            AppAPI.class, new HashMap<String, Object>() {{
        put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
    }});
    GType gst_app_src_get_type();
    GType gst_app_sink_get_type();
    FlowReturn gst_app_src_push_buffer(AppSrc appsrc, @Invalidate Buffer buffer);
    @CallerOwnsReturn Caps gst_app_src_get_caps(AppSrc appsrc);
    void gst_app_src_set_caps(AppSrc appsrc, Caps caps);
    void gst_app_src_set_size(AppSrc appsrc, /*gint64*/ long size);
    /*gint64*/ long gst_app_src_get_size(AppSrc appsrc);
    void gst_app_src_set_stream_type(AppSrc appsrc, AppSrc.Type type);
    AppSrc.Type gst_app_src_get_stream_type(AppSrc appsrc);
    
    void gst_app_src_flush(AppSrc appsrc);
    void gst_app_src_end_of_stream(AppSrc appsrc);

    void gst_app_sink_set_caps(AppSink appsink, Caps caps);
    @CallerOwnsReturn Caps gst_app_sink_get_caps(AppSink appsink);

    boolean gst_app_sink_is_eos(AppSink appsink);

    @CallerOwnsReturn Buffer gst_app_sink_pull_preroll(AppSink appsink);
    @CallerOwnsReturn Buffer gst_app_sink_pull_buffer(AppSink appsink);
}

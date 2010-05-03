/* 
 * Copyright (c) 2009 Levente Farkas
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Clock;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Event;
import org.gstreamer.GhostPad;
import org.gstreamer.Message;
import org.gstreamer.Pad;
import org.gstreamer.PadTemplate;
import org.gstreamer.Pipeline;
import org.gstreamer.Plugin;
import org.gstreamer.PluginFeature;
import org.gstreamer.Query;
import org.gstreamer.Registry;
import org.gstreamer.glib.GDate;
import org.gstreamer.elements.TypeFind;
import org.gstreamer.elements.BaseSink;
import org.gstreamer.elements.BaseSrc;
import org.gstreamer.interfaces.ColorBalanceChannel;
import org.gstreamer.interfaces.MixerTrack;
import org.gstreamer.interfaces.TunerChannel;
import org.gstreamer.interfaces.TunerNorm;

import com.sun.jna.Pointer;

import static org.gstreamer.lowlevel.GstColorBalanceAPI.GSTCOLORBALANCE_API;
import static org.gstreamer.lowlevel.GstMixerAPI.GSTMIXER_API;
import static org.gstreamer.lowlevel.GstTunerAPI.GSTTUNER_API;
import static org.gstreamer.lowlevel.GObjectAPI.GOBJECT_API;
import static org.gstreamer.lowlevel.BaseAPI.BASE_API;

/**
 *
 */
@SuppressWarnings("serial")
public class GstTypes {
    private static final Logger logger = Logger.getLogger(GstTypes.class.getName());
    
    
    private GstTypes() {
    }
    public static final boolean isGType(Pointer p, long type) {
        return getGType(p).longValue() == type;
    }
    public static final GType getGType(Pointer ptr) {        
        // Retrieve ptr->g_class
        Pointer g_class = ptr.getPointer(0);
        // Now return g_class->gtype
        return GType.valueOf(g_class.getNativeLong(0).longValue());
    }
    public static final Class<? extends NativeObject> classFor(Pointer ptr) {
        Pointer g_class = ptr.getPointer(0);
        Class<? extends NativeObject> cls;
        cls = gTypeInstanceMap.get(g_class);
        if (cls != null) {
            return cls;
        }

        GType type = GType.valueOf(g_class.getNativeLong(0).longValue());
        logger.finer("Type of " + ptr + " = " + type);
        while (cls == null && !type.equals(GType.OBJECT) && !type.equals(GType.INVALID)) {
            cls = getTypeMap().get(type);
            if (cls != null) {
                logger.finer("Found type of " + ptr + " = " + cls);
                gTypeInstanceMap.put(g_class, cls);
                break;
            }
            type = GOBJECT_API.g_type_parent(type);
        }
        return cls;
    }
    public static final Class<? extends NativeObject> classFor(GType type) {
        return getTypeMap().get(type);
    }
    public static final GType typeFor(Class<? extends NativeObject> cls) {
        for (Map.Entry<GType, Class<? extends NativeObject>> e : getTypeMap().entrySet()) {
            if (e.getValue().equals(cls)) {
                return e.getKey();
            }
        }
        return GType.INVALID;
    }
    private static final Map<GType, Class<? extends NativeObject>> getTypeMap() {
        return StaticData.typeMap;
    }
    private static final Map<Pointer, Class<? extends NativeObject>> gTypeInstanceMap
            = new ConcurrentHashMap<Pointer, Class<? extends NativeObject>>();
    
    public static interface API extends com.sun.jna.Library {
        GType gst_base_src_get_type();
        GType gst_base_sink_get_type();

        GType gst_bin_get_type();
        GType gst_buffer_get_type();
        GType gst_bus_get_type();
        GType gst_caps_get_type();
        GType gst_child_proxy_get_type();
        GType gst_clock_get_type();
        GType gst_date_get_type();
        GType gst_element_get_type();
        GType gst_element_factory_get_type();
        GType gst_event_get_type();
        GType gst_g_error_get_type();
        GType gst_ghost_pad_get_type();
        GType gst_index_get_type();
        GType gst_index_entry_get_type();
        GType gst_index_factory_get_type();
        GType gst_message_get_type();
        GType gst_mini_object_get_type();
        GType gst_object_get_type();
        GType gst_pad_get_type();
        GType gst_pad_template_get_type();
        GType gst_pipeline_get_type();
        GType gst_plugin_get_type();
        GType gst_plugin_feature_get_type();
        GType gst_query_get_type();
        GType gst_registry_get_type();
        GType gst_segment_get_type();
        GType gst_static_pad_template_get_type();        
        GType gst_static_caps_get_type();
        GType gst_system_clock_get_type();
        GType gst_structure_get_type();
        GType gst_tag_get_type(String tag);
        GType gst_tag_list_get_type();
        GType gst_tag_setter_get_type();
        GType gst_task_get_type();
        GType gst_type_find_get_type();
        GType gst_type_find_factory_get_type();
        GType gst_uri_handler_get_type();
    }
    public static final API GST_API = GstNative.load(API.class);
    
    private static class StaticData {
		private static final Map<GType, Class<? extends NativeObject>> typeMap =
			new HashMap<GType, Class<? extends NativeObject>>() {
			{
				// GObject types
				put(GSTCOLORBALANCE_API.gst_color_balance_channel_get_type(), ColorBalanceChannel.class);
				put(GSTMIXER_API.gst_mixer_track_get_type(), MixerTrack.class);
				put(GSTTUNER_API.gst_tuner_channel_get_type(), TunerChannel.class);
				put(GSTTUNER_API.gst_tuner_norm_get_type(), TunerNorm.class);
				// GstObject types
				put(GST_API.gst_element_get_type(), Element.class);
				put(GST_API.gst_clock_get_type(), Clock.class);
				put(GST_API.gst_date_get_type(), GDate.class);
				put(GST_API.gst_pipeline_get_type(), Pipeline.class);
				put(GST_API.gst_bus_get_type(), Bus.class);
				put(GST_API.gst_pad_get_type(), Pad.class);
				put(GST_API.gst_pad_template_get_type(), PadTemplate.class);
				put(GST_API.gst_ghost_pad_get_type(), GhostPad.class);
				put(GST_API.gst_plugin_get_type(), Plugin.class);
				put(GST_API.gst_plugin_feature_get_type(), PluginFeature.class);
				put(GST_API.gst_registry_get_type(), Registry.class);
				// GstMiniObject types
				put(GST_API.gst_buffer_get_type(), Buffer.class);
				put(GST_API.gst_event_get_type(), Event.class);
				put(GST_API.gst_message_get_type(), Message.class);
				put(GST_API.gst_query_get_type(), Query.class);
				// Element types
				put(BASE_API.gst_base_sink_get_type(), BaseSink.class);
				put(BASE_API.gst_base_src_get_type(), BaseSrc.class);
				put(GST_API.gst_type_find_get_type(), TypeFind.class);
				put(GST_API.gst_element_factory_get_type(), ElementFactory.class);
				put(GST_API.gst_bin_get_type(), Bin.class);
			}
		};
    }
}

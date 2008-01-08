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

import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
import org.gstreamer.lowlevel.NativeObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Plugin;
import org.gstreamer.PluginFeature;
import org.gstreamer.Query;
import org.gstreamer.Registry;
import org.gstreamer.StaticPadTemplate;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
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
        cls = typeMap.get(type);
        if (cls != null) {
            logger.finer("Found type of " + ptr + " = " + cls);
            gTypeInstanceMap.put(g_class, cls);
        }
        return cls;
    }
    public static final GType typeFor(Class<? extends NativeObject> cls) {
        for (Map.Entry<GType, Class<? extends NativeObject>> e : typeMap.entrySet()) {
            if (e.getValue().equals(cls)) {
                return e.getKey();
            }
        }
        return GType.INVALID;
    }
    private static final void registerGType(GType type, Class<? extends NativeObject> cls) {
        logger.fine("Registering gtype " + type + " = " + cls);
        typeMap.put(type, cls);
    }
    private static Map<GType, Class<? extends NativeObject>> typeMap = new HashMap<GType, Class<? extends NativeObject>>();
    private static Map<Pointer, Class<? extends NativeObject>> gTypeInstanceMap = Collections.synchronizedMap(new HashMap<Pointer, Class<? extends NativeObject>>());
    static {
        // GstObject types
        registerGType(gst.gst_element_get_type(), Element.class);
        registerGType(gst.gst_element_factory_get_type(), ElementFactory.class);
        registerGType(gst.gst_bin_get_type(), Bin.class);
        registerGType(gst.gst_clock_get_type(), Clock.class);
        registerGType(gst.gst_pipeline_get_type(), Pipeline.class);
        registerGType(gst.gst_bus_get_type(), Bus.class);
        registerGType(gst.gst_pad_get_type(), Pad.class);
        registerGType(gst.gst_ghost_pad_get_type(), GhostPad.class);
        registerGType(gst.gst_plugin_get_type(), Plugin.class);
        registerGType(gst.gst_plugin_feature_get_type(), PluginFeature.class);
        registerGType(gst.gst_registry_get_type(), Registry.class);
        // GstMiniObject types
        registerGType(gst.gst_buffer_get_type(), Buffer.class);
        registerGType(gst.gst_event_get_type(), Event.class);
        registerGType(gst.gst_message_get_type(), Message.class);
        registerGType(gst.gst_query_get_type(), Query.class);
    }
    
}

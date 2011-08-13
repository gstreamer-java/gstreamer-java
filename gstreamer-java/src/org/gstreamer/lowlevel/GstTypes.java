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

import static org.gstreamer.lowlevel.GObjectAPI.GOBJECT_API;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
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
import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.AppSrc;
import org.gstreamer.elements.BaseSink;
import org.gstreamer.elements.BaseSrc;
import org.gstreamer.elements.BaseTransform;
import org.gstreamer.elements.TypeFind;
import org.gstreamer.elements.good.RTPBin;
import org.gstreamer.elements.good.RTSPSrc;
import org.gstreamer.glib.GDate;
import org.gstreamer.interfaces.ColorBalanceChannel;
import org.gstreamer.interfaces.MixerTrack;
import org.gstreamer.interfaces.TunerChannel;
import org.gstreamer.interfaces.TunerNorm;

import com.sun.jna.Pointer;

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
        Class<? extends NativeObject> cls = gTypeInstanceMap.get(g_class);
        if (cls != null) {
            return cls;
        }

        GType type = GType.valueOf(g_class.getNativeLong(0).longValue());
        logger.finer("Type of " + ptr + " = " + type);
        while (cls == null && !type.equals(GType.OBJECT) && !type.equals(GType.INVALID)) {
            cls = getTypeMap().get(GOBJECT_API.g_type_name(type));
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
        return getTypeMap().get(GOBJECT_API.g_type_name(type));
    }
    public static final GType typeFor(Class<? extends NativeObject> cls) {
        for (Map.Entry<String, Class<? extends NativeObject>> e : getTypeMap().entrySet()) {
            if (e.getValue().equals(cls)) {
                return GOBJECT_API.g_type_from_name(e.getKey());
            }
        }
        return GType.INVALID;
    }
    private static final Map<String, Class<? extends NativeObject>> getTypeMap() {
        return StaticData.typeMap;
    }
    private static final Map<Pointer, Class<? extends NativeObject>> gTypeInstanceMap
            = new ConcurrentHashMap<Pointer, Class<? extends NativeObject>>();
    
    private static class StaticData {
		private static final Map<String, Class<? extends NativeObject>> typeMap =
			new HashMap<String, Class<? extends NativeObject>>() {
			{
				// GObject types
				put("GstColorBalanceChannel", ColorBalanceChannel.class);
				put("GstMixerTrack",          MixerTrack.class);
				put("GstTunerChannel",        TunerChannel.class);
				put("GstTunerNorm",           TunerNorm.class);
				// GstObject types
				put("GstBus",                 Bus.class);
				put("GstCaps",                Caps.class);
				put("GstClock",               Clock.class);
				put("GstElement",             Element.class);
				put("GstElementFactory",      ElementFactory.class);
				put("GstDate",                GDate.class);
				put("GstGhostPad",            GhostPad.class);
				put("GstPad",                 Pad.class);
				put("GstPadTemplate",         PadTemplate.class);
				put("GstPlugin",              Plugin.class);
				put("GstPluginFeature",       PluginFeature.class);
				put("GstRegistry",            Registry.class);
				// GstMiniObject types
				put("GstBuffer",              Buffer.class);
				put("GstEvent",               Event.class);
				put("GstMessage",             Message.class);
				put("GstQuery",               Query.class);
				// Element types
				put("GstAppSink",             AppSink.class);
				put("GstAppSrc",              AppSrc.class);
				put("GstBaseSrc",             BaseSrc.class);
				put("GstBaseSink",            BaseSink.class);
				put("GstBaseTransform",       BaseTransform.class);
				put("GstBin",                 Bin.class);
				//put("CapsFilter",             CapsFilter.class);
				//put("GstDecodeBin",           DecodeBin.class);
				//put("GstDecodeBin2",          DecodeBin2.class);
				//put("GstFakeSink",            FakeSink.class);
				//put("GstFakeSrc",             FakeSrc.class);
				//put("GstFdSink",              FdSink.class);
				//put("GstFdSrc",               FdSrc.class);
				//put("GstFileSink",            FileSink.class);
				//put("GstFileSrc",             FileSrc.class);
				//put("GstFunnel",              Funnel.class);
				//put("GstIdentity",            Identity.class);
				//put("GstInputSelector",       InputSelector.class);
				//put("GstMultiQueue",          MultiQueue.class);
				//put("_GstOSXVideoSink",       OSXVideoSink.class);
				//put("GstOutputSelector",      OutputSelector.class);
				put("GstPipeline",            Pipeline.class);
				//put("GstPlayBin",             PlayBin.class);
				//put("GstPlayBin2",            PlayBin2.class);
				//put("GstQueue",               Queue.class);
				//put("GstQueue2",              Queue2.class);
				//put("GstTee",                 Tee.class);
				put("GstTypeFind",            TypeFind.class);
				put("GstRtpBin",              RTPBin.class);
				put("GstRTSPSrc",             RTSPSrc.class);
			}
		};
    }
}

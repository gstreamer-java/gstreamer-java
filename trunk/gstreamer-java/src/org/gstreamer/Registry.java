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

import static org.gstreamer.lowlevel.GstAPI.gst;

import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.gstreamer.lowlevel.GType;
import org.gstreamer.lowlevel.GlibAPI.GList;

public class Registry extends GstObject {
    private static Logger logger = Logger.getLogger(Registry.class.getName());
    
    public static Registry getDefault() {
        // Need to handle the return value here, as it is a persistent object
        // i.e. the java proxy should not dispose of the underlying object when finalized
        return GstObject.objectFor(gst.gst_registry_get_default(), Registry.class,
                false, false);
    }
    /** Creates a new instance of GstElement */
    protected Registry(Pointer ptr) {
        super(ptr);
    }
    protected Registry(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    protected Registry(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public Plugin findPlugin(String name) {
        return gst.gst_registry_find_plugin(this, name);
    }
    public PluginFeature findPluginFeature(String name, GType type) {
        return gst.gst_registry_find_feature(this, name, type);
    }
    public PluginFeature findPluginFeature(String name) {
        return gst.gst_registry_lookup_feature(this, name);
    }
    public List<Plugin> getPluginList() {
        List<Plugin> list = new ArrayList<Plugin>();
        GList glist = gst.gst_registry_get_plugin_list(this);      
        GList next = glist;
        while (next != null) {
            if (next.data != null) {
                list.add(GstObject.objectFor(next.data, Plugin.class));
            }
            next = next.next();   
        }
        gst.gst_plugin_list_free(glist);
        return list;
    }
    public List<PluginFeature> getPluginFeatureListByPlugin(String name) {
        List<PluginFeature> list = new ArrayList<PluginFeature>();
        GList glist = gst.gst_registry_get_feature_list_by_plugin(this, name);
        GList next = glist;
        while (next != null) {
            if (next.data != null) {
                list.add(GstObject.objectFor(next.data, PluginFeature.class));
            }
            next = next.next();   
        }
        gst.gst_plugin_feature_list_free(glist);
        return list;
    }
}

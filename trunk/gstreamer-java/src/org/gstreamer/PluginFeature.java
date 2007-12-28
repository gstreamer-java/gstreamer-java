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

import java.util.logging.Logger;
import static org.gstreamer.lowlevel.GstAPI.gst;

public class PluginFeature extends GstObject {
    private static Logger logger = Logger.getLogger(PluginFeature.class.getName());
    
    /** Creates a new instance of PluginFeature */
    PluginFeature(Initializer init) { 
        super(init); 
    }
    public String toString() {
        return getName();
    }
    public String getName() {
        return gst.gst_plugin_feature_get_name(this);
    }
    public int getRank() {
        return gst.gst_plugin_feature_get_rank(this);
    }
    public boolean checkVersion(int major, int minor, int micro) {
        return gst.gst_plugin_feature_check_version(this, minor, minor, micro);
    }
}

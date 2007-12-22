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
import java.util.logging.Logger;
import static org.gstreamer.lowlevel.GstAPI.gst;

public class Plugin extends GstObject {
    private static Logger logger = Logger.getLogger(Plugin.class.getName());
    
    /** Creates a new instance of GstElement */
    protected Plugin(Pointer ptr) {
        super(ptr);
    }
    protected Plugin(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    protected Plugin(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    @Override
    public String getName() {
        return gst.gst_plugin_get_name(this);
    }
    public String getDescription() {
        return gst.gst_plugin_get_description(this);
    }
    public String getFilename() {
        return gst.gst_plugin_get_filename(this);
    }
    public String getVersion() {
    return gst.gst_plugin_get_version(this);
    }
    public String getLicense() {
        return gst.gst_plugin_get_license(this);
    }
    public String getSource() {
        return gst.gst_plugin_get_source(this);
    }
    public String getPackage() {
        return gst.gst_plugin_get_package(this);
    }
    public String getOrigin() {
        return gst.gst_plugin_get_origin(this);
    }
    public boolean isLoaded() {
        return gst.gst_plugin_is_loaded(this);
    }
}

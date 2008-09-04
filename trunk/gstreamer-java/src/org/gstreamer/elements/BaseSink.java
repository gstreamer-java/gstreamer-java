/* 
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

package org.gstreamer.elements;

import java.util.concurrent.TimeUnit;

import org.gstreamer.Element;
import org.gstreamer.lowlevel.BaseAPI;

public class BaseSink extends Element {
    public BaseSink(Initializer init) {
        super(init);
    }
    private static class API {
        static BaseAPI base = BaseAPI.INSTANCE;
    }
    public void setSync(boolean sync) {
        API.base.gst_base_sink_set_sync(this, sync);
    }
    public boolean isSync() {
        return API.base.gst_base_sink_get_sync(this);
    }
    public void setMaximumLateness(long lateness, TimeUnit units) {
        API.base.gst_base_sink_set_max_lateness(this, units.toMillis(lateness));
    }
    public long getMaximumLateness(TimeUnit units) {
        return units.convert(API.base.gst_base_sink_get_max_lateness(this), TimeUnit.MILLISECONDS);
    }
    public void setQOSEnabled(boolean qos) {
        API.base.gst_base_sink_set_qos_enabled(this, qos);
    }
    public boolean isQOSEnabled() {
        return API.base.gst_base_sink_is_qos_enabled(this);
    }
}

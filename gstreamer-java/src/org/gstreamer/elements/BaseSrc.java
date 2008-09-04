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

import java.util.HashMap;

import org.gstreamer.ClockTime;
import org.gstreamer.Element;
import org.gstreamer.FlowReturn;
import org.gstreamer.Format;
import org.gstreamer.lowlevel.BaseAPI;
import org.gstreamer.lowlevel.GNative;
import org.gstreamer.lowlevel.GTypeMapper;

import com.sun.jna.Library;

public class BaseSrc extends Element {

    private static interface API extends BaseAPI {
        
        FlowReturn gst_base_src_wait_playing(BaseSrc src);

        void gst_base_src_set_live(BaseSrc src, boolean live);

        boolean gst_base_src_is_live(BaseSrc src);

        void gst_base_src_set_format(BaseSrc src, Format format);

        boolean gst_base_src_query_latency(BaseSrc src, boolean[] live,
                ClockTime[] min_latency,
                ClockTime[] max_latency);

        void gst_base_src_set_do_timestamp(BaseSrc src, boolean live);

        boolean gst_base_src_get_do_timestamp(BaseSrc src);
    }

    @SuppressWarnings("serial")
    private static final API gst = GNative.loadLibrary("gstbase-0.10", 
            API.class, new HashMap<String, Object>() {{
        put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
    }});

    public BaseSrc(Initializer init) {
        super(init);
    }

    public void setFormat(Format format) {
        gst.gst_base_src_set_format(this, format);
    }
    public void setLive(boolean live) {
        gst.gst_base_src_set_live(this, live);
    }
    public boolean isLive() {
        return gst.gst_base_src_is_live(this);
    }
}

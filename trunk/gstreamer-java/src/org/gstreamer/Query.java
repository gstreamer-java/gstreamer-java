/* 
 * Copyright (C) 2008 Wayne Meissner
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


public class Query extends MiniObject {
    public Query(Initializer init) {
        super(init);
    }
    public static Query newPosition(Format format) {
        return gst.gst_query_new_position(format);
    }
    public static Query newDuration(Format format) {
        return gst.gst_query_new_duration(format);
    }
    public static Query newLatency() {
        return gst.gst_query_new_latency();
    }
}

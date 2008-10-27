/* 
 * Copyright (c) 2007, 2008 Wayne Meissner
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

/**
 * GstStructure functions
 */
public interface GstValueAPI extends com.sun.jna.Library {
    static GstValueAPI INSTANCE = GstNative.load(GstValueAPI.class);
    
    GType gst_fourcc_get_type();
    GType gst_int_range_get_type();
    GType gst_double_range_get_type();
    GType gst_fraction_range_get_type();
    GType gst_value_list_get_type();
    GType gst_fraction_get_type();
    GType gst_date_get_type();
}

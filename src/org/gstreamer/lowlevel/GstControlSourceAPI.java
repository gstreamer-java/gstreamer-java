/* 
 * Copyright (c) 2009 Levente Farkas
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

import org.gstreamer.ClockTime;
import org.gstreamer.GObject;
import org.gstreamer.controller.ControlSource;
import org.gstreamer.lowlevel.GObjectAPI.GObjectClass;
import org.gstreamer.lowlevel.GObjectAPI.GParamSpec;
import org.gstreamer.lowlevel.GValueAPI.GValue;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface GstControlSourceAPI extends Library {
	GstControlSourceAPI GSTCONTROLSOURCE_API = GstNative.load("gstcontroller", GstControlSourceAPI.class);
    int GST_PADDING = GstAPI.GST_PADDING;
	
	public static final class GstTimedValue extends com.sun.jna.Structure {
		public volatile ClockTime timestamp;
		public volatile GValue value;
	}
	public static final class GstValueArray extends com.sun.jna.Structure {
		public volatile String property_name;
		public volatile int nbsamples;
		public volatile ClockTime sample_interval;
		public volatile Pointer values;
	}
	
	public static interface GstControlSourceGetValue extends Callback {
        public boolean callback(ControlSource self, ClockTime timestamp, GValue value);
    }
	public static interface GstControlSourceGetValueArray extends Callback {
        public boolean callback(ControlSource self, ClockTime timestamp, GstValueArray value_array);
    }
	public static interface GstControlSourceBind extends Callback {
        public boolean callback(ControlSource self, GParamSpec pspec);
    }
	
	public static final class GstControlSourceStruct extends com.sun.jna.Structure {
		public volatile GObject parent;

		/*< public >*/
		public volatile GstControlSourceGetValue get_value;             /* Returns the value for a property at a given timestamp */
		public volatile GstControlSourceGetValueArray get_value_array;  /* Returns values for a property in a given timespan */

		/*< private >*/
		public volatile boolean bound;
		public volatile Pointer[] _gst_reserved = new Pointer[GST_PADDING];
	}
	
	public static final class GstControlSourceClass extends com.sun.jna.Structure {
		public volatile GObjectClass parent_class;
		  
		public volatile GstControlSourceBind bind;  /* Binds the GstControlSource to a specific GParamSpec */

		  /*< private >*/
		public volatile Pointer[] _gst_reserved = new Pointer[GST_PADDING];
	}
	
	GType gst_control_source_get_type();

	/* Functions */
	boolean gst_control_source_get_value(ControlSource self, ClockTime timestamp, GValue value);
	boolean gst_control_source_get_value_array(ControlSource self, ClockTime timestamp, GstValueArray value_array);
	boolean gst_control_source_bind(ControlSource self, GParamSpec pspec);
}

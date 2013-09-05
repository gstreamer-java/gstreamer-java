/*
 * Copyright (c) 2009 Levente Farkas
 * 
 * This file is part of gstreamer-java.
 * 
 * This code is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License version 3 only, as published by the Free Software Foundation.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License version 3
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3 along with this work. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.gstreamer.elements;

import org.gstreamer.Caps;
import org.gstreamer.ClockTime;
import org.gstreamer.Element;
import org.gstreamer.lowlevel.BaseTransformAPI;

public class BaseTransform extends Element {
	public static final String GTYPE_NAME = "GstBaseTransform";

	private static final BaseTransformAPI gst() { return BaseTransformAPI.BASETRANSFORM_API; }

	public BaseTransform(Initializer init) {
		super(init);
	}

	public void setPassthrough(boolean passthrough) {
		gst().gst_base_transform_set_passthrough(this, passthrough);
	}

	public boolean isPassthrough() {
		return gst().gst_base_transform_is_passthrough(this);
	}

	public void setInPlace(boolean inPlace) {
		gst().gst_base_transform_set_in_place(this, inPlace);
	}

	public boolean isInPlace() {
		return gst().gst_base_transform_is_in_place(this);
	}

	public void updateQoS(double proportion, long diff, ClockTime timestamp) {
		gst().gst_base_transform_update_qos(this, proportion, diff, timestamp);
	}

	public void setQoSEnabled(boolean enabled) {
		gst().gst_base_transform_set_qos_enabled(this, enabled);
	}

	public boolean isQoSEnabled() {
		return gst().gst_base_transform_is_qos_enabled(this);
	}

	public void setGapAware(boolean gapAware) {
		gst().gst_base_transform_set_gap_aware(this, gapAware);
	}

	public void suggest(Caps caps, int size) {
		gst().gst_base_transform_suggest(this, caps, size);
	}

	public void reconfigure() {
		gst().gst_base_transform_reconfigure(this);
	}
}

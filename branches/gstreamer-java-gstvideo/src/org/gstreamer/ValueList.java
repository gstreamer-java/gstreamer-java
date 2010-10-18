/* 
 * Copyright (c) 2010 Raimo Järvi
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

package org.gstreamer;

import org.gstreamer.lowlevel.GValueAPI;
import org.gstreamer.lowlevel.GstValueAPI;

public class ValueList {
    private GValueAPI.GValue value;

    ValueList(GValueAPI.GValue value) {
        this.value = value;
    }

    public int getSize() {
        return GstValueAPI.GSTVALUE_API.gst_value_list_get_size(value);
    }

    public double getDouble(int index) {
        return GValueAPI.GVALUE_API.g_value_get_double(get(index));
    }

    public float getFloat(int index) {
    	return GValueAPI.GVALUE_API.g_value_get_float(get(index));
    }
    
    public int getInteger(int index) {
        return GValueAPI.GVALUE_API.g_value_get_int(get(index));
    }

    public String getString(int index) {
        return GValueAPI.GVALUE_API.g_value_get_string(get(index));
    }

    public boolean getBoolean(int index) {
        return GValueAPI.GVALUE_API.g_value_get_boolean(get(index));
    }

    public Fraction getFraction(int index) {
        GValueAPI.GValue value = get(index);
        return new Fraction(GstValueAPI.GSTVALUE_API.gst_value_get_fraction_numerator(value),
                GstValueAPI.GSTVALUE_API.gst_value_get_fraction_denominator(value));
    }

    public Object getValue(int index) {
    	GValueAPI.GValue v = get(index);
    	
    	return v.getValue();
    }
    
    private GValueAPI.GValue get(int index) {
        GValueAPI.GValue val = GstValueAPI.GSTVALUE_API.gst_value_list_get_value(value, index);
        if (val == null) {
            throw new RuntimeException(String.format("List does not contain value %d", index));
        }
        return val;
    }
}

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

package org.gstreamer.glib;

import org.gstreamer.lowlevel.GObjectAPI;

public class GQuark {
    private final int value;
    public GQuark(int value) {
        this.value = value;
    }
    public int intValue() {
        return value;
    }
    
    public GQuark valueOf(String quark) {
        return GObjectAPI.gobj.g_quark_from_string(quark);
    }
    
    @Override
    public String toString() {
        return GObjectAPI.gobj.g_quark_to_string(this);
    }
}

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
import static org.gstreamer.lowlevel.GstAPI.gst;
/**
 *
 */
public class GhostPad extends Pad {

    /**
     * Creates a new instance of GhostPad
     */
    GhostPad(Initializer init) { 
        super(init); 
    }
    public GhostPad(String name, Pad target) {
        this(initializer(gst.gst_ghost_pad_new(name, target), true, true));
    }
    public GhostPad(String name, PadDirection direction) {
        this(initializer(gst.gst_ghost_pad_new_no_target(name, direction.ordinal()), true, true));
    }
    
    
}

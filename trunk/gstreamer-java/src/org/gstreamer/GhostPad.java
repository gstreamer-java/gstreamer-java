/* 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.gstreamer;
import org.gstreamer.lowlevel.GstAPI;
/**
 *
 */
public class GhostPad extends Pad {
    private static GstAPI gst = GstAPI.gst;

    /**
     * Creates a new instance of GhostPad
     */
    public GhostPad(String name, Pad target) {
        super(gst.gst_ghost_pad_new(name, target.handle()), true, true);
    }
    public GhostPad(String name, PadDirection direction) {
        super(gst.gst_ghost_pad_new_no_target(name, direction.ordinal()), true, true);
    }
    
    
}

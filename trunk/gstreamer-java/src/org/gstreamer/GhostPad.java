/*
 * GhostPad.java
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

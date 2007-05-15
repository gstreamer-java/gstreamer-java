/*
 * GhostPad.java
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
    public GhostPad(String name, Pad target) {
        super(gst.gst_ghost_pad_new(name, target.handle()), true, true);
    }
    public GhostPad(String name, PadDirection direction) {
        super(gst.gst_ghost_pad_new_no_target(name, direction.ordinal()), true, true);
    }
    
    
}

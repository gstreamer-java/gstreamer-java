/*
 * Parse.java
 */

package org.gstreamer;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Parse {
    
    /** Creates a new instance of Parse */
    private Parse() {
    }
    
    public static Element launch(String pipeline) throws GError {
        PointerByReference err = new PointerByReference();
        Pointer p = gst.gst_parse_launch(pipeline, err);
        if (!p.isValid()) {
            throw new GError(err.getValue());
        }
        return Element.instanceFor(p, true, true);
    }
    public static Element launch(String... pipeline) throws GError {
        PointerByReference err = new PointerByReference();
        Pointer p = gst.gst_parse_launchv(pipeline, err);
        if (!p.isValid()) {
            throw new GError(err.getValue());
        }
        return Element.instanceFor(p, true, true);
    }
}

/*
 * Pipeline.java
 */

package org.gstreamer;
import com.sun.jna.Pointer;
import org.gstreamer.lowlevel.GstAPI;

/**
 *
 */
public class Pipeline extends Bin {
    private static GstAPI gst = GstAPI.gst;

    /**
     * Creates a new instance of Pipeline
     */
    public Pipeline(String name) {
        super(gst.gst_pipeline_new(name));
    }
    protected Pipeline(Pointer ptr) {
        super(ptr);
    }
    protected Pipeline(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    protected Pipeline(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    
    @Override
    public Bus getBus() {
        return Bus.objectFor(gst.gst_pipeline_get_bus(handle()), false);
    }
}

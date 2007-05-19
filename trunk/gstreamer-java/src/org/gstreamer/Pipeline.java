/*
 * Pipeline.java
 */

package org.gstreamer;
import com.sun.jna.Pointer;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Pipeline extends Bin {
    
    /**
     * Creates a new instance of Pipeline
     */
    public Pipeline(String name) {
        this(gst.gst_pipeline_new(name), true, true);
    }
    protected Pipeline(Pointer ptr, boolean needRef) {
        this(ptr, true, needRef);
    }
    protected Pipeline(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
    }
    
    @Override
    public Bus getBus() {
        return Bus.instanceFor(gst.gst_pipeline_get_bus(handle()), false);
    }
}

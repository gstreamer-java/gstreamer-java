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
        return Bus.instanceFor(gst.gst_pipeline_get_bus(handle()), false);
    }
}

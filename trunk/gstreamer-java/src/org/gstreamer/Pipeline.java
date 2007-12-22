/* 
 * Copyright (c) 2007 Wayne Meissner
 *
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
import com.sun.jna.Pointer;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Pipeline extends Bin {

    /**
     * Creates a new instance of Pipeline with the given name.
     * 
     * @param name The name used to identify this pipeline.
     */
    public Pipeline(String name) {
        super(gst.gst_pipeline_new(name));
    }
    
    /**
     * Creates a new Pipeline proxy.
     * 
     * This constructor assumes ownership of the underlying native GstPipeline
     * and increments the reference count.
     * 
     * @param pipeline The native GstPipeline object to wrap.
     */
    protected Pipeline(Pointer pipeline) {
        super(pipeline);
    }
    
    /**
     * Creates a new Pipeline proxy.
     * 
     * This constructor assumes ownership of the underlying native GstPipeline
     * 
     * @param pipeline The native GstPipeline object to wrap.
     * @param needRef true if the reference count needs to be incremented.
     */
    protected Pipeline(Pointer pipeline, boolean needRef) {
        super(pipeline, needRef);
    }
    
    /**
     * Creates a new Pipeline proxy.
     * 
     * @param pipeline The native GstPipeline object to wrap.
     * 
     * @param needRef true if the reference count needs to be incremented.
     * 
     * @param ownsHandle Whether this proxy should take ownership of the 
     *          native handle or not.  If true, then the underlying pipeline will be
     *          unreffed when the java object is garbage collected.
     */
    protected Pipeline(Pointer pipeline, boolean needRef, boolean ownsHandle) {
        super(pipeline, needRef, ownsHandle);
    }
    
    /**
     * Sets this pipeline to automatically flush {@link Bus} messages or not.
     *
     * @param flush true if automatic flushing is desired, else false.
     */
    public void setAutoFlushBus(boolean flush) {
        gst.gst_pipeline_set_auto_flush_bus(this, flush);
    }
    
    /**
     * Checks if the pipeline will automatically flush messages when going to the NULL state.
     * 
     * @return true if the pipeline automatically flushes messages.
     */     
    public boolean getAutoFlushBus() {
        return gst.gst_pipeline_get_auto_flush_bus(this);
    }
    
    /**
     * Set the clock for pipeline. 
     * The clock will be distributed to all the elements managed by the pipeline.
     * <p>MT safe
     * 
     * @param clock The {@link Clock} to use
     * @return true if the clock could be set on the pipeline, false if some 
     * element did not accept the clock. 
     *
     */
    public boolean setClock(Clock clock) {
        return gst.gst_pipeline_set_clock(this, clock);
    }
    
    /**
     * Return the current {@link Clock} used by the pipeline.
     * 
     * @return The {@link Clock} currently in use.
     */
    public Clock getClock() {
        return gst.gst_pipeline_get_clock(this);
    }
    
    /**
     * Force the Pipeline to use the a specific clock.
     * The pipeline will always use the given clock even if new clock 
     * providers are added to this pipeline.
     * <p>MT safe
     * 
     * @param clock The {@link Clock} to use.  If clock is null, all clocking is
     * disabled, and the pipeline will run as fast as possible.
     *      
     */
    public void useClock(Clock clock) {
        gst.gst_pipeline_use_clock(this, clock);
    }
    @Override
    public Bus getBus() {
        return gst.gst_pipeline_get_bus(this);
    }
}

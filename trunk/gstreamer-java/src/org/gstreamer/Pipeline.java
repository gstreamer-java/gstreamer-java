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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Pipeline extends Bin {

    protected Pipeline(Initializer init) { 
        super(init); 
    }
    
    /**
     * Creates a new instance of Pipeline with the given name.
     * 
     * @param name The name used to identify this pipeline.
     */
    public Pipeline(String name) {
        this(initializer(gst.gst_pipeline_new(name)));
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
    
    /**
     * Sets the position in the media stream to time.
     * 
     * @param time The time to change the position to.
     */
    public void setPosition(Time time) {
        setPosition(time.longValue(), Format.TIME);
    }
    public void setPosition(long pos, Format format) {
        gst.gst_element_seek(this, 1.0, format,
                SeekFlags.FLUSH.intValue() | SeekFlags.KEY_UNIT.intValue(), 
                SeekType.SET, pos, SeekType.NONE, -1);
    }
    public long getPosition(Format format) {
        IntByReference fmt = new IntByReference(format.intValue());
        LongByReference pos = new LongByReference();
        gst.gst_element_query_position(this, fmt, pos);
        return pos.getValue();
    }
    public Time getPosition() {
        return new Time(getPosition(Format.TIME));
    }
    public Time getDuration() {
        IntByReference fmt = new IntByReference(Format.TIME.intValue());
        LongByReference duration= new LongByReference();
        gst.gst_element_query_duration(this, fmt, duration);
        return new Time(duration.getValue());
    }
    
    /**
     * Test if the Pipeline is currently playing.
     * @return true if the Pipeline is currently playing
     */
    public boolean isPlaying() {
        return getState() == State.PLAYING;
    }
    
    /**
     * Tell the Pipeline to start playing the media stream.
     */
    public void play() {
        setState(State.PLAYING);
    }
    
    /**
     * Tell the Pipeline to pause playing the media stream.
     */
    public void pause() {
        setState(State.PAUSED);
    }
    
    /**
     * Tell the Pipeline to pause playing the media stream.
     */
    public void stop() {
        setState(State.NULL);
    }
}

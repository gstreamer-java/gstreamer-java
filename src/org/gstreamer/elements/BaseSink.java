/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.elements;

import java.util.concurrent.TimeUnit;

import org.gstreamer.Buffer;
import org.gstreamer.ClockReturn;
import org.gstreamer.ClockTime;
import org.gstreamer.Element;
import org.gstreamer.FlowReturn;
import org.gstreamer.MiniObject;
import org.gstreamer.lowlevel.BaseAPI;
import org.gstreamer.lowlevel.GstNative;
import org.gstreamer.lowlevel.annotations.CallerOwnsReturn;

import com.sun.jna.Pointer;

public class BaseSink extends Element {
	public static interface BaseSinkAPI extends BaseAPI {
	    FlowReturn gst_base_sink_do_preroll(BaseSink sink, MiniObject obj);
	    FlowReturn gst_base_sink_wait_preroll(BaseSink sink);
	    
	    /* synchronizing against the clock */
	    void gst_base_sink_set_sync(BaseSink sink, boolean sync);
	    boolean gst_base_sink_get_sync(BaseSink sink);

	    /* dropping late buffers */
	    void gst_base_sink_set_max_lateness (BaseSink sink, long max_lateness);
	    long gst_base_sink_get_max_lateness(BaseSink sink);
	    
	    /* performing QoS */
	    void gst_base_sink_set_qos_enabled(BaseSink sink, boolean enabled);
	    boolean gst_base_sink_is_qos_enabled(BaseSink sink);
	    
	    /* doing async state changes */
	    void gst_base_sink_set_async_enabled(BaseSink sink, boolean enabled);
	    boolean gst_base_sink_is_async_enabled(BaseSink sink);
	    
	    /* tuning synchronisation */
	    void gst_base_sink_set_ts_offset(BaseSink sink, long offset);
	    long gst_base_sink_get_ts_offset(BaseSink sink);

	    /* last buffer */
	    @CallerOwnsReturn Buffer gst_base_sink_get_last_buffer(BaseSink sink);
	    void gst_base_sink_set_last_buffer_enabled(BaseSink sink, boolean enable);
	    boolean gst_base_sink_is_last_buffer_enabled(BaseSink sink);

	    /* latency */
	    boolean gst_base_sink_query_latency(BaseSink sink, boolean live, boolean upstream_live, ClockTime min_latency, ClockTime max_latency);
	    ClockTime gst_base_sink_get_latency(BaseSink sink);
	        
	    /* render delay */
	    void gst_base_sink_set_render_delay(BaseSink sink, ClockTime delay);
	    ClockTime gst_base_sink_get_render_delay(BaseSink sink);
	    
	    /* blocksize */
	    void gst_base_sink_set_blocksize(BaseSink sink, int blocksize);
	    int gst_base_sink_get_blocksize(BaseSink sink);
	    
	    ClockReturn gst_base_sink_wait_clock(BaseSink sink, ClockTime time, /* GstClockTimeDiff */ Pointer jitter);
	    FlowReturn gst_base_sink_wait_eos(BaseSink sink, ClockTime time, /* GstClockTimeDiff */ Pointer jitter);
	}
	
	public static final BaseSinkAPI BASESINK_API = GstNative.load("gstbase", BaseSinkAPI.class);
	
    public BaseSink(Initializer init) {
        super(init);
    }
    
    public FlowReturn preroll(MiniObject obj) {
    	return BASESINK_API.gst_base_sink_do_preroll(this, obj);
    }
    public FlowReturn waitPreroll() {
    	return BASESINK_API.gst_base_sink_wait_preroll(this);
    }
    public void setSync(boolean sync) {
    	BASESINK_API.gst_base_sink_set_sync(this, sync);
    }
    public boolean isSync() {
        return BASESINK_API.gst_base_sink_get_sync(this);
    }
    public void setMaximumLateness(long lateness, TimeUnit units) {
    	BASESINK_API.gst_base_sink_set_max_lateness(this, units.toNanos(lateness));
    }
    public long getMaximumLateness(TimeUnit units) {
        return units.convert(BASESINK_API.gst_base_sink_get_max_lateness(this),TimeUnit.NANOSECONDS);
    }
    public void setQOSEnabled(boolean qos) {
    	BASESINK_API.gst_base_sink_set_qos_enabled(this, qos);
    }
    public boolean isQOSEnabled() {
        return BASESINK_API.gst_base_sink_is_qos_enabled(this);
    }
    public void enableAsync(boolean enabled) {
    	BASESINK_API.gst_base_sink_set_async_enabled(this, enabled);
    }
    public boolean isAsync() {
    	return BASESINK_API.gst_base_sink_is_async_enabled(this);
    }
    public void setTsOffset(long offset) {
    	BASESINK_API.gst_base_sink_set_ts_offset(this, offset);
    }
    public long getTsOffset() {
    	return BASESINK_API.gst_base_sink_get_ts_offset(this);
    }
    public Buffer getLastBuffer() {
    	return BASESINK_API.gst_base_sink_get_last_buffer(this);
    }
    public void enableLastBuffer(boolean enable) {
    	BASESINK_API.gst_base_sink_set_last_buffer_enabled(this, enable);
    }
    public boolean isLastBufferEnabled() {
    	return BASESINK_API.gst_base_sink_is_last_buffer_enabled(this);
    }
    public boolean queryLatency(boolean live, boolean upstream_live, ClockTime min_latency, ClockTime max_latency) {
    	return BASESINK_API.gst_base_sink_query_latency(this, live, upstream_live, min_latency, max_latency);
    }
    public ClockTime getLatency() {
    	return BASESINK_API.gst_base_sink_get_latency(this);
    }
    public void setRenderDelay(ClockTime delay) {
    	BASESINK_API.gst_base_sink_set_render_delay(this, delay);
    }
    public ClockTime getRenderDelay() {
    	return BASESINK_API.gst_base_sink_get_render_delay(this);
    }
    public void setBlocksize(int blocksize) {
    	BASESINK_API.gst_base_sink_set_blocksize(this, blocksize);
    }
    public int getBlocksize() {
    	return BASESINK_API.gst_base_sink_get_blocksize(this);
    }
    public ClockReturn waitClock(ClockTime time, /* GstClockTimeDiff */ Pointer jitter) {
    	return BASESINK_API.gst_base_sink_wait_clock(this, time, jitter);
    }
    public FlowReturn waitEOS(ClockTime time, /* GstClockTimeDiff */ Pointer jitter) {
    	return BASESINK_API.gst_base_sink_wait_eos(this, time, jitter);
    }
}

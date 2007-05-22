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

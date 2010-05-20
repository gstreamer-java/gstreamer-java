/* 
 * Copyright (C) 2009 Levente Farkas <lfarkas@lfarkas.org>
 * Copyright (C) 2009 Tamas Korodi <kotyo@zamba.fm>
 * Copyright (C) 2008 Wayne Meissner
 * Copyright (C) 2003 Ronald Bultje <rbultje@ronald.bitfreak.net>
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

package org.gstreamer.interfaces;

import org.gstreamer.Element;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;

import static org.gstreamer.lowlevel.GstXOverlayAPI.GSTXOVERLAY_API;

/**
 * Interface for elements providing tuner operations
 */
public class XOverlay extends GstInterface {
    /**
     * Wraps the {@link Element} in a <tt>XOverlay</tt> interface
     * 
     * @param element the element to use as a <tt>XOverlay</tt>
     * @return a <tt>XOverlay</tt> for the element
     */
    public static XOverlay wrap(Element element) {
        return new XOverlay(element);
    }
    
    /**
     * Creates a new <tt>XOverlay</tt> instance
     * 
     * @param element the element that implements the tuner interface
     */
    protected XOverlay(Element element) {
        super(element, GSTXOVERLAY_API.gst_x_overlay_get_type());
    }
    
    /**
     * Sets the native window for the {@link Element} to use to display video.
     *
     * @param handle A native handle to use to display video.
     */
    public void setWindowID(long handle) {
    	GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, new NativeLong(handle));
    }

    /**
     * Sets the native window for the {@link Element} to use to display video.
     *
     * @param window A native window to use to display video, or <tt>null</tt> to
     * stop using the previously set window.
     */
    public void setWindowID(java.awt.Component window) {
        if (window == null) {
            GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, new NativeLong(0));
            return;
        }
        if (window.isLightweight())
            throw new IllegalArgumentException("Component must be a native window");
        if (Platform.isWindows())
            GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, Native.getComponentPointer(window));
        else
            GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, new NativeLong(Native.getComponentID(window)));
    }
       
    /**
     * Tell an overlay that it has been exposed. This will redraw the current frame
     * in the drawable even if the pipeline is PAUSED.
     */
    public void expose() {
        GSTXOVERLAY_API.gst_x_overlay_expose(this);
    }
    
    /**
     * Tell an overlay that it should handle events from the window system. 
     * These events are forwared upstream as navigation events. In some window 
     * system, events are not propagated in the window hierarchy if a client is 
     * listening for them. This method allows you to disable events handling 
     * completely from the XOverlay.
     */
    public void handleEvent(boolean handle_events) {
    	GSTXOVERLAY_API.gst_x_overlay_handle_events(this, handle_events);
    }
}

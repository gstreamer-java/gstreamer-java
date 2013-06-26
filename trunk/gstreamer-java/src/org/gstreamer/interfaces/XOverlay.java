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

import static org.gstreamer.lowlevel.GstXOverlayAPI.GSTXOVERLAY_API;

import org.gstreamer.Element;

import com.sun.jna.Native;
import com.sun.jna.Platform;

public class XOverlay extends XOverlayBase {

	public XOverlay(Element element) {
		super(element);
	}
	
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
	 * Sets the native window for the {@link Element} to use to display video.
	 *
	 * @param window A native window to use to display video, or <tt>null</tt> to
	 * stop using the previously set window.
	 * @deprecated use {@link org.gstreamer.swing.XOverlaySwing#setWindowHandle(java.awt.Component)} instead
	 */
	@Deprecated
	public void setWindowHandle(java.awt.Component window) {
	    if (window == null) {
	        setWindowHandle(0);
	        return;
	    }
	    if (window.isLightweight())
	        throw new IllegalArgumentException("Component must be a native window");
	    if (Platform.isWindows())
	        GSTXOVERLAY_API.gst_x_overlay_set_window_handle(this, Native.getComponentPointer(window));
	    else
	        setWindowHandle(Native.getComponentID(window));
	}

	/**
	 * Sets the native window for the {@link Element} to use to display video.
	 *
	 * @param window A native window to use to display video, or <tt>null</tt> to
	 * stop using the previously set window.
	 * @deprecated use {@link #setWindowHandle(java.awt.Component)} instead
	 */
	@Deprecated
	public void setWindowID(java.awt.Component window) {
		setWindowHandle(window);
	}
}

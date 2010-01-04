/* 
 * Copyright (c) 2009 Levente Farkas
 * Copyright (c) 2009 Tamas Korodi <kotyo@zamba.fm>
 * Copyright (c) 2008 Wayne Meissner
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

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
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
    public static final XOverlay wrap(Element element) {
        return new XOverlay(element);
    }
    
    /**
     * Creates a new <tt>XOverlay</tt> instance
     * 
     * @param element the element that implements the tuner interface
     */
    private XOverlay(Element element) {
        super(element, GSTXOVERLAY_API.gst_x_overlay_get_type());
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
        if (window.isLightweight()) {
            throw new IllegalArgumentException("Component must be be a native window");
        }
        if (Platform.isWindows()) {
            GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, Native.getComponentPointer(window));
        } else {
            GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, new NativeLong(Native.getComponentID(window)));
        }
    }
    
    /**
     * Sets the native window for the {@link Element} to use to display video.
     *
     * @param window A native window to use to display video, or <tt>null</tt> to
     * stop using the previously set window.
     */
	public void setWindowID(org.eclipse.swt.widgets.Composite comp) {
		// Composite style must be embedded
		if (!Platform.isLinux() || comp == null || (comp.getStyle() | SWT.EMBEDDED) == 0) {
			GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, new NativeLong(0));
			return;
		}
		// TODO: Test on windows and mac
		int handle;
		try {
			Class<? extends org.eclipse.swt.widgets.Composite> compClass = comp.getClass();
			Field embedHandleField = compClass.getField("embeddedHandle");
			handle = embedHandleField.getInt(comp);
			GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, new NativeLong(handle));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Tell an overlay that it has been exposed. This will redraw the current frame
     * in the drawable even if the pipeline is PAUSED.
     */
    public void expose() {
        GSTXOVERLAY_API.gst_x_overlay_expose(this);
    }
}

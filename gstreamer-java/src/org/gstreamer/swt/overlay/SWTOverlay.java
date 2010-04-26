/* 
 * Copyright (C) 2009 Levente Farkas <lfarkas@lfarkas.org>
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

package org.gstreamer.swt.overlay;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.gstreamer.Element;
import org.gstreamer.GstException;
import org.gstreamer.interfaces.XOverlay;

import com.sun.jna.NativeLong;
import com.sun.jna.Platform;

import static org.gstreamer.lowlevel.GstXOverlayAPI.GSTXOVERLAY_API;

/**
 * Interface for elements providing tuner operations
 */
public class SWTOverlay extends XOverlay {
    /**
     * Wraps the {@link Element} in a <tt>SWTOverlay</tt> interface
     * 
     * @param element the element to use as a <tt>SWTOverlay</tt>
     * @return a <tt>SWTOverlay</tt> for the element
     */
    public static SWTOverlay wrap(Element element) {
        return new SWTOverlay(element);
    }
    
    /**
     * Creates a new <tt>SWTOverlay</tt> instance
     * 
     * @param element the element that implements the tuner interface
     */
    private SWTOverlay(Element element) {
        super(element);
    }

    /**
     * Sets the native window for the {@link Element} to use to display video.
     *
     * @param window A native window to use to display video, or <tt>null</tt> to
     * stop using the previously set window.
     */
    public void setWindowID(Composite comp) {
        long handle;
        // Composite style must be embedded
        if (comp == null || ((comp.getStyle() | SWT.EMBEDDED) == 0))
            throw new GstException("Cannot set window ID, in XOverlay interface, composite is null or not SWT.EMBEDDED");
        if (Platform.isWindows()) {
            handle = comp.handle;
            GSTXOVERLAY_API.gst_x_overlay_set_xwindow_id(this, new NativeLong(handle));
        } else if (Platform.isLinux()) {
            try {
                Class<? extends Composite> compClass = comp.getClass();
                Field embedHandleField = compClass.getField("embeddedHandle");
                Class<?> t = embedHandleField.getType();
                if (t.equals(long.class))
                	handle = embedHandleField.getLong(comp);
                else if (t.equals(int.class))
                	handle = embedHandleField.getInt(comp);
                else
                	throw new RuntimeException("Field embeddedHandle of " + compClass.getCanonicalName() + 
                			" was not of type int or long; it is expected that operating system handles be of type int" +
                			" or long. This error is likely because " + getClass().getCanonicalName() + 
                			" was not designed for your platform, or your platform doesn't support the X window system.");
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
        } else {
            throw new GstException("Cannot set window ID, in XOverlay interface: not supported sink element on platform");
        }
    }
}

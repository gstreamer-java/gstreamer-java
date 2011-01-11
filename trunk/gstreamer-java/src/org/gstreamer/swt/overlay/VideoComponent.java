/*
 * Copyright (c) 2009 Tamas Korodi <kotyo@zamba.fm>
 * Copyright (c) 2010 Levente Farkas <lfarkas@lfarkas.org>
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.gstreamer.BusSyncReply;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GstException;
import org.gstreamer.Message;
import org.gstreamer.MessageType;
import org.gstreamer.Structure;
import org.gstreamer.event.BusSyncHandler;

import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.Window;
import com.sun.jna.platform.unix.X11.XCrossingEvent;
import com.sun.jna.platform.unix.X11.XEvent;
//import com.sun.jna.platform.unix.X11.XMotionEvent;

/**
 * VideoComponent which use OS's overlay video component 
 * @author lfarkas
 *
 */
public class VideoComponent extends Canvas implements BusSyncHandler, DisposeListener {
	private static int counter = 0;
	private final Element videosink;
	private final SWTOverlay overlay;
	private boolean watcherRunning = false;
	
	/**
	 * Overlay VideoComponent
	 * @param parent
	 * @param style
	 * @param enableX11Events true if X11 event should have to be grabbed (mouse move, enter and leave event on Linux).
	 * 
	 * On Linux by default the handling of mouse move, enter and leave event are not propagated.
	 * Unfortunately the "handle-events" properties hide some important expose events too, 
	 * sowe've to do some lowlevel trick to be able to get these events.
	 */
	public VideoComponent(final Composite parent, int style, boolean enableX11Events) {
		super(parent, style | SWT.EMBEDDED);
		String name = Platform.isLinux() ? "xvimagesink" : 
					  Platform.isWindows() ? "d3dvideosink" : 
					  Platform.isMac() ? "osxvideosink" : null;
		if (name == null)
			throw new GstException("Platform not supported");
		videosink = ElementFactory.make(name, "OverlayVideoComponent" + counter++);
		overlay = SWTOverlay.wrap(videosink);
		overlay.setWindowHandle(this);
		enableX11Events(enableX11Events);
		expose();
	}
	
	/**
	 * In this case we (gstreamer-linux) must handle redraw too!
	 *
	 * @param enableX11Events true if X11 event should have to be grabbed (mouse move, enter and leave event on Linux).
	 */
    public synchronized void enableX11Events(boolean enable) {
		if (enable && Platform.isLinux()) {
			videosink.set("handle-events", !enable);
			overlay.handleEvent(!enable);
			watcherRunning = true;
			new Thread() {
				@Override
				public void run() {
					try {
						final X11 x11 = X11.INSTANCE;
						final Display display = x11.XOpenDisplay(null);
						Window window = new Window(SWTOverlay.getLinuxHandle(VideoComponent.this));
						x11.XSelectInput(display, window,
								new NativeLong(X11.ExposureMask |
										X11.VisibilityChangeMask |
										X11.StructureNotifyMask |
										X11.FocusChangeMask |
										//X11.PointerMotionMask |
										X11.EnterWindowMask |
										X11.LeaveWindowMask));
						while (watcherRunning) {
							final XEvent xEvent = new XEvent();
							x11.XNextEvent(display, xEvent);
							if (watcherRunning && !isDisposed()) {
								getDisplay().asyncExec(new Runnable() {
									public void run() {
										if (watcherRunning && !isDisposed()) {
											final Event swtEvent = new Event();
											XCrossingEvent ce;
											switch (xEvent.type) {
//												case X11.MotionNotify:
//													XMotionEvent e = (XMotionEvent)xEvent.readField("xmotion");
//													swtEvent.x = e.x;
//													swtEvent.y = e.y;
//													notifyListeners(SWT.MouseMove, swtEvent);
//													break;
												case X11.EnterNotify:
													ce = (XCrossingEvent)xEvent.readField("xcrossing");
													swtEvent.x = ce.x;
													swtEvent.y = ce.y;
													notifyListeners(SWT.MouseEnter, swtEvent);
													break;
												case X11.LeaveNotify:
													ce = (XCrossingEvent)xEvent.readField("xcrossing");
													swtEvent.x = ce.x;
													swtEvent.y = ce.y;
													notifyListeners(SWT.MouseExit, swtEvent);
													break;
												default:
													overlay.expose();														
											}
										}
									}
								});
							}
						}
						x11.XCloseDisplay(display);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			addDisposeListener(this);
		}
	}
	
	/**
	 * Overlay VideoComponent
	 * @param parent
	 * @param style
	 */
	public VideoComponent(final Composite parent, int style) {
		this(parent, style, false);
	}
	
	public void widgetDisposed(DisposeEvent arg0) {
		watcherRunning = false;
		removeDisposeListener(this);
	}	
	
	/**
     * Tell an overlay that it has been exposed. This will redraw the current frame
     * in the drawable even if the pipeline is PAUSED.
     */
	public void expose() {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if(!isDisposed())
					overlay.expose();
			}
		});
	}
	
	/**
	 * Set to keep aspect ratio
	 * 
	 * @param keepAspect
	 */
	public void setKeepAspect(boolean keepAspect) {
		videosink.set("force-aspect-ratio", keepAspect);
	}

	/**
	 * Retrieves the Gstreamer element, representing the video component
	 * 
	 * @return element
	 */
	public Element getElement() {
		return videosink;
	}
	
	/**
	 * Get the native handle of this video component
	 * 
	 * @return long the native window handle
	 */
	public long handle() {
		return SWTOverlay.handle(this);
	}

	/**
	 * Implements the BusSyncHandler interface
	 * @param message
	 * @return
	 */
	public BusSyncReply syncMessage(Message message) {
		if (message.getType() != MessageType.ELEMENT)
			return BusSyncReply.PASS;
		Structure s = message.getStructure();
		if (s == null || !s.hasName("prepare-xwindow-id"))
			return BusSyncReply.PASS;
		overlay.setWindowHandle(this);
		return BusSyncReply.DROP;
	}
}

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
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.gstreamer.BusSyncReply;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Message;
import org.gstreamer.Pad;
import org.gstreamer.Structure;
import org.gstreamer.Pad.LINKED;
import org.gstreamer.Pad.UNLINKED;
import org.gstreamer.Pad.EVENT_PROBE;
import org.gstreamer.event.BusSyncHandler;
import org.gstreamer.event.NavigationEvent;

import com.sun.jna.Platform;

public class VideoComponent extends Canvas implements BusSyncHandler {
	private static int counter = 0;
	private final Element videosink;
	private final EVENT_PROBE probe;
	private Pad srcPad;
	
	public VideoComponent(final Composite parent, int style) {
		super(parent, style | SWT.EMBEDDED);

		// TODO: replace directdrawsink with dshowvideosink if dshowvideosink become more stable:
		// http://forja.rediris.es/forum/forum.php?thread_id=5255&forum_id=1624
		videosink = ElementFactory.make(Platform.isLinux() ? "xvimagesink" : "directdrawsink", "OverlayVideoComponent" + counter++);
		videosink.set("sync", false);
		videosink.set("async", false);
		SWTOverlay.wrap(videosink).setWindowID(this);
		
		probe = new EVENT_PROBE() {
			public boolean eventReceived(Pad arg0, org.gstreamer.Event event) {
				if (event instanceof NavigationEvent) {
					Structure s = event.getStructure();
					System.out.println(s);
					if ("mouse-move".equals(s.getString("event"))) {
						final double x = s.getDouble("pointer_x");
						final double y = s.getDouble("pointer_y");
						parent.getDisplay().asyncExec(new Runnable() {
							public void run() {
								if(!parent.isDisposed() && getListeners(SWT.MouseMove).length != 0) {
									Event m = new Event();
									m.x = (int)(Math.round(x));
									m.y = (int)(Math.round(y));
									notifyListeners(SWT.MouseMove, m);
								}
							}
						});
					}
				}
				return false;
			}
		};
		
		Pad sinkPad = videosink.getSinkPads().get(0);
		sinkPad.connect(new LINKED() {
			public void linked(Pad pad, Pad peer) {
				srcPad = peer;
				srcPad.addEventProbe(probe);
			}
		});
		sinkPad.connect(new UNLINKED() {
			public void unlinked(Pad pad, Pad peer) {
				if (srcPad != null) {
					srcPad.removeEventProbe(probe);
					srcPad = null;
				}
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
	 * Implements the BusSyncHandler interface
	 * @param message
	 * @return
	 */
	public BusSyncReply syncMessage(Message message) {
		Structure s = message.getStructure();
		if (s == null || !s.hasName("prepare-xwindow-id"))
			return BusSyncReply.PASS;
		SWTOverlay.wrap(videosink).setWindowID(this);
		return BusSyncReply.DROP;
	}
}

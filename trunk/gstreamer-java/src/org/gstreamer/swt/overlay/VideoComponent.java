/*
 * Copyright (c) 2009 Tamas Korodi <kotyo@zamba.fm>
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
import org.eclipse.swt.widgets.Listener;
import org.gstreamer.BusSyncReply;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Message;
import org.gstreamer.Structure;
import org.gstreamer.event.BusSyncHandler;

import com.sun.jna.Platform;

public class VideoComponent extends Canvas implements BusSyncHandler {
	private double aspectY = 0.0;
	private double aspectX = 0.0;

	private final Element videosink;
	
	public VideoComponent(final Composite parent, int style) {
		super(parent, style | SWT.EMBEDDED);
		
		// TODO: replace directdrawsink with dshowvideosink if dshowvideosink become more stable:
		// http://forja.rediris.es/forum/forum.php?thread_id=5255&forum_id=1624
		videosink = ElementFactory.make(Platform.isLinux() ? "xvimagesink" : "directdrawsink", "OverlayVideoComponent");
		SWTOverlay.wrap(videosink).setWindowID(this);
		
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (aspectX == 0.0 || aspectY == 0.0)
					return;
				int newX = getSize().x;
				int newY = getSize().y;
				double coX = ((double)newX) / aspectY;
				double coY = ((double)newY) / aspectX;
				if (coY <= coX)
					newX = (int)(coY * aspectY);
				else
					newY = (int)(coX * aspectX);
				setSize(newX, newY);
				setLocation(getSize().x / 2 - getSize().x / 2, getSize().y / 2 - getSize().y / 2);
				layout(true);
			}
		});
		videosink.set("sync", false);
		videosink.set("async", false);
		if (Platform.isLinux())
			videosink.set("handle-events", false);
	}
	
	/**
	 * Sets the given aspect ratio
	 * @param x
	 * @param y
	 */
	public void setAspectRatio(double x, double y) {
		aspectX = x;
		aspectY = y;
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
	 * Set to keep aspect ratio
	 * 
	 * @param keepAspect
	 */
	public void setKeepAspect(boolean keepAspect) {
		videosink.set("force-aspect-ratio", keepAspect);
	}

	public BusSyncReply syncMessage(Message message) {
		Structure s = message.getStructure();
        if (s == null || !s.hasName("prepare-xwindow-id"))
            return BusSyncReply.PASS;
		SWTOverlay.wrap(videosink).setWindowID(this);
        return BusSyncReply.DROP; 
	}
}

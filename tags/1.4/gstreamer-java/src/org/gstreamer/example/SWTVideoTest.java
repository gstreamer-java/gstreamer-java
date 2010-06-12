/*
 * Copyright (C) 2009 Tamas Korodi <kotyo@zamba.fm> 
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

package org.gstreamer.example;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swt.overlay.VideoComponent;


public class SWTVideoTest {
	public static void main(String[] args) {
		args = Gst.init("SWTVideoTest", args);

		Pipeline pipe = new Pipeline("SWT Overlay Test");
		Element src = ElementFactory.make("videotestsrc", "videotest");
		Element id = ElementFactory.make("identity", "id");
		
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setSize(640, 480);
			shell.setLayout(new GridLayout(1, false));

			shell.setText("SWT Video Test");
			final VideoComponent component = new VideoComponent(shell, SWT.NONE);
			component.getElement().setName("video");
			component.setKeepAspect(true);
			component.setLayoutData(new GridData(GridData.FILL_BOTH));

			Element sink = component.getElement();

			shell.open();

			pipe.addMany(src, id, sink);
			Element.linkMany(src, id, sink);
			pipe.setState(State.PLAYING);
			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();

		} catch (Exception e) {
		}
	}
}

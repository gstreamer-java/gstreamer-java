/* 
 * Copyright (c) 2009 Tamas Korodi <kotyo@zamba.fm>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.gstreamer.example;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.gstreamer.Gst;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.swt.VideoComponent;

public class SWTOverlayPlayer {
	public static void main(String[] args) {
		args = Gst.init("SWT Player", args);

		args = Gst.init("SWTOverlayPlayer", args);
		if (args.length < 1) {
			System.err.println("Usage: SwingPlayer <filename>");
			System.exit(1);
		}

		PlayBin play = new PlayBin("swt player");
		play.setInputFile(new File(args[0]));

		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setSize(640, 480);
			shell.setLayout(new GridLayout(1, false));

			shell.setText("SWT Video Test");
			final VideoComponent component = new VideoComponent(shell, SWT.NONE, true);
			component.getElement().setName("video");
			component.setKeepAspect(true);
			component.setLayoutData(new GridData(GridData.FILL_BOTH));

			play.setVideoSink(component.getElement());
			play.play();

			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();

		} catch (Exception e) {
		}
	}
}

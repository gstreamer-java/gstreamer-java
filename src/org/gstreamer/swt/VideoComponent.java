/*
 * Copyright (c) 2008 Peter Bocz
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

package org.gstreamer.swt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.gstreamer.elements.RGBDataSink;

public class VideoComponent extends Canvas {

	private BufferedImage currentImage = null;
	private final RGBDataSink videosink;
	private final Lock bufferLock = new ReentrantLock();
	private boolean updatePending = false;
	private int alpha = 255;
	private String ovText;
	private long start = System.currentTimeMillis();
	private boolean keepAspect = true;
	private boolean showOverlay = false;
	private boolean showFPS = false;
	private Color bgColor;
	private int sizeX = 0, sizeY = 0;
	private int newX = 0, newY = 0;

	public VideoComponent(final Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		videosink = new RGBDataSink("GstVideoComponent", new RGBListener());
		videosink.setPassDirectBuffer(true);

		final Font font = new Font(getDisplay(), "Arial", 13, SWT.NORMAL);

		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent event) {
				Point cSize = getSize();
				if (currentImage != null) {
					event.gc.setFont(font);
					newX = 0;
					newY = 0;
					int fps = 0;

					int[] Frame = ((DataBufferInt) currentImage.getRaster().getDataBuffer()).getData();
					ImageData imgdata
						= new ImageData(currentImage.getWidth(), currentImage.getHeight(), 24,
										new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
					imgdata.setPixels(0, 0, currentImage.getWidth() * currentImage.getHeight(), Frame, 0);

					if ((currentImage.getWidth() != cSize.x) || (currentImage.getHeight() != cSize.y)) {
						sizeX = cSize.x;
						sizeY = cSize.y;
						event.gc.setInterpolation(SWT.HIGH);
						if (keepAspect) {
							if (((float) currentImage.getWidth() / (float) cSize.x)
								> ((float) currentImage.getHeight() / (float) cSize.y)) {
								sizeY = cSize.x * currentImage.getHeight() / currentImage.getWidth();
								newY = (cSize.y - sizeY) / 2;
							} else {
								sizeX = cSize.y * currentImage.getWidth() / currentImage.getHeight();
								newX = (cSize.x - sizeX) / 2;
							}
						}
						imgdata = imgdata.scaledTo(sizeX, sizeY);
					}

					if (alpha != event.gc.getAlpha()) {
						event.gc.setAlpha(alpha);
					}
					Image image = new Image(parent.getDisplay(), imgdata);
					event.gc.drawImage(image, newX, newY);

					if (showFPS) {
						fps = (int) (1000 / (System.currentTimeMillis() - start));
					}

					if (showOverlay) {
						event.gc.drawText(ovText, newX + 5, newY + 5, false);
						newY += 20;
					}
					if (showFPS) {
						event.gc.drawText(" FPS:" + fps, newX + 5, newY + 5, false);
					}
					image.dispose();
					if (showFPS) {
						start = System.currentTimeMillis();
					}
				} else {
					if (bgColor != null) {
						event.gc.setBackground(bgColor);
						event.gc.fillRectangle(0, 0, cSize.x, cSize.y);
					}
				}
			}
		});
	}

	public int getSizeX() { return sizeX; }
	public int getSizeY() { return sizeY; }
	public int getNewX() { return newX; }
	public int getNewY() { return newY; }

	/**
	 * Retrieves the Gstreamer element, representing the video component
	 * 
	 * @return element
	 */
	public RGBDataSink getElement() {
		return videosink;
	}

	/**
	 * Set to keep aspect ratio
	 * 
	 * @param keepAspect
	 */
	public void setKeepAspect(boolean keepAspect) {
			this.keepAspect = keepAspect;
	}

	/**
	 * Set the aplpha value of the video component. It works fine when overlay
	 * is turned off.
	 * 
	 * @param alpha
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	/**
	 * Set the overlay text of the video component. It works fine when overlay
	 * is turned off.
	 * 
	 * @param text
	 */
	public void setOverlay(String text) {
		this.ovText = text;
	}

	/**
	 * Set show FPS of the video component. It works fine when overlay is turned
	 * off.
	 * 
	 * @param bn
	 */
	public void showFPS(boolean bn) {
		this.showFPS = bn;
	}

	/**
	 * Set show overlay text. It works fine when overlay is turned off.
	 * 
	 * @param bn
	 */
	public void showOverlay(boolean bn) {
		this.showOverlay = bn;
	}

	/**
	 * Retrieves the alpha value of the video component
	 * 
	 * @return alpha value of the video component
	 */
	public int getAlpha() {
		return alpha;
	}

	private final Runnable update = new Runnable() {

		public void run() {
			bufferLock.lock();
			try {
				if (!isDisposed()) {
					redraw();
				}
				updatePending = false;
			} finally {
				bufferLock.unlock();
			}
		}
	};

	private BufferedImage getBufferedImage(int width, int height) {
		if (currentImage != null && currentImage.getWidth() == width
				&& currentImage.getHeight() == height) {
			return currentImage;
		}
		if (currentImage != null) {
			currentImage.flush();
		}
		currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		currentImage.setAccelerationPriority(0.0f);
		return currentImage;
	}

	/**
	 * Sets the background color
	 * 
	 * @param bgColor
	 */
	public void setBackGroundColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * Gets the background color
	 * 
	 * @param bgColor
	 */
	public Color getBackGroundColorColor() {
		return bgColor;
	}

	private class RGBListener implements RGBDataSink.Listener {

		public void rgbFrame(boolean isPrerollFrame, int width, int height, IntBuffer rgb) {
			if (!bufferLock.tryLock()) {
				return;
			}
			if (updatePending && !isPrerollFrame) {
				bufferLock.unlock();
				return;
			}
			try {
				final BufferedImage renderImage = getBufferedImage(width, height);
				int[] pixels = ((DataBufferInt) renderImage.getRaster().getDataBuffer()).getData();
				rgb.get(pixels, 0, width * height);
				updatePending = true;
			} finally {
				bufferLock.unlock();
			}
			// Tell Canvas to use the new buffer
			if (!isDisposed()) {
				getDisplay().asyncExec(update);
			}
		}
	}
}

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

import java.nio.IntBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.gstreamer.elements.RGBDataSink;

public class VideoComponent extends Canvas {
	
	private ImageData currentImageData = null;
	private Object currentImageDataLock = new Object();
	private Image currentImage;
	private final RGBDataSink videosink;
	private int alpha = 255;
	private String ovText;
	private long start = System.currentTimeMillis();
	private boolean keepAspect = true;
	private boolean showOverlay = false;
	private boolean showFPS = false;
	private Color bgColor;
	private int sizeX = 0, sizeY = 0;
	private int newX = 0, newY = 0;
	private Font gcFont;
	private int redrawFps;
	private int redrawInterval;
	
	public VideoComponent(final Composite parent, int style) {
		this(parent, style, 25);
	}
	
	public VideoComponent(final Composite parent, int style, int redrawFps) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		setRedrawFps(redrawFps);
		videosink = new RGBDataSink("GstVideoComponent", new RGBListener());
		videosink.setPassDirectBuffer(true);
		gcFont = new Font(getDisplay(), "Arial", 13, SWT.NORMAL);
		addPaintListener();
		addDisposeListener();
		startRedrawCycle();
	}
	
	public int getRedrawFps() {
		return redrawFps;
	}
	
	public void setRedrawFps(int redrawFps) {
		if (redrawFps < 1000 && redrawFps >= 1) {
			this.redrawFps = redrawFps;
			redrawInterval = 1000 / redrawFps;
		}
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public int getNewX() {
		return newX;
	}
	
	public int getNewY() {
		return newY;
	}
	
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
	
	public Image getCurrentImage() {
		synchronized (currentImageDataLock) {
			if (currentImageData != null)
				return new Image(getDisplay(), currentImageData);
		}
		return null;
	}
	
	public ImageData getCurrentImageData() {
		synchronized (currentImageDataLock) {
			if (currentImageData != null)
				return (ImageData) currentImageData.clone();
		}
		return null;
	}
	
	private void addPaintListener() {
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				if (!isDisposed()) {
					GC gc = event.gc;
					Point size = getSize();
					if (size.x != 0 && size.y != 0) {
						if (gcFont != null && !gcFont.isDisposed())
							gc.setFont(gcFont);
						if (bgColor != null) {
							gc.setBackground(bgColor);
							gc.fillRectangle(0, 0, size.x, size.y);
						}
						synchronized (currentImageDataLock) {
							if (currentImageData != null) {
								if (currentImage != null && !currentImage.isDisposed())
									currentImage.dispose();
								newX = 0;
								newY = 0;
								int fps = 0;
								int currentWidth = currentImageData.width;
								int currentHeight = currentImageData.height;
								if ((currentWidth != size.x) || (currentHeight != size.y)) {
									sizeX = size.x;
									sizeY = size.y;
									gc.setInterpolation(SWT.HIGH);
									if (keepAspect) {
										if (((float) currentWidth / (float) size.x) > ((float) currentHeight
												/ (float) size.y)) {
											sizeY = (int) ((float) size.x * (float) currentHeight
													/ (float) currentWidth);
											newY = (int) (((float) size.y - (float) sizeY) / 2f);
										} else {
											sizeX = (int) ((float) size.y * (float) currentWidth
													/ (float) currentHeight);
											newX = (int) (((float) size.x - (float) sizeX) / 2f);
										}
									}
									currentImageData = currentImageData.scaledTo(sizeX, sizeY);
								}
								
								if (alpha != gc.getAlpha())
									gc.setAlpha(alpha);
									
								currentImage = new Image(getDisplay(), currentImageData);
								gc.drawImage(currentImage, newX, newY);
								
								if (showFPS)
									fps = (int) (1000 / (System.currentTimeMillis() - start));
									
								if (showOverlay) {
									gc.drawText(ovText, newX + 5, newY + 5, false);
									newY += 20;
								}
								if (showFPS) {
									gc.drawText(" FPS:" + fps, newX + 5, newY + 5, false);
									start = System.currentTimeMillis();
								}
							}
						}
					}
				}
			}
		});
	}
	
	private void addDisposeListener() {
		if (gcFont != null && !gcFont.isDisposed())
			gcFont.dispose();
		if (currentImage != null && !currentImage.isDisposed())
			currentImage.dispose();
	}
	
	private void startRedrawCycle() {
		getDisplay().timerExec(redrawInterval, new Runnable() {
			public void run() {
				if (!isDisposed()) {
					redraw();
					getDisplay().timerExec(redrawInterval, this);
				}
			}
		});
	}
	
	private class RGBListener implements RGBDataSink.Listener {
		
		public void rgbFrame(boolean isPrerollFrame, int width, int height, IntBuffer rgb) {
			synchronized (currentImageDataLock) {
				int[] pixels = new int[width * height];
				rgb.get(pixels);
				currentImageData = new ImageData(width, height, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
				currentImageData.setPixels(0, 0, width * height, pixels, 0);
			}
		}
	}
}

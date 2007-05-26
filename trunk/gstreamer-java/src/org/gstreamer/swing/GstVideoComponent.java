/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.gstreamer.swing;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.gstreamer.*;
import org.gstreamer.event.*;

/**
 *
 */
public class GstVideoComponent extends javax.swing.JComponent {
    
    AtomicReference<BufferedImage> nextRef = new AtomicReference<BufferedImage>(null);
    Element fakesink, videofilter;
    Object interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
    Object quality = RenderingHints.VALUE_RENDER_SPEED;
    private static boolean openglEnabled;
    private static boolean useVolatile;
    private static boolean quartzEnabled;
    private Bin bin;
    private boolean keepAspect = true;
    private float alpha = 1.0f;
    
    static {
        try {
            String openglProperty = System.getProperty("sun.java2d.opengl");
            openglEnabled = openglProperty != null && Boolean.parseBoolean(openglProperty);
        } finally { }
        try {
            String quartzProperty = System.getProperty("apple.awt.graphics.UseQuartz");
            quartzEnabled = Boolean.parseBoolean(quartzProperty);
        } finally { }
        
    }
    /** Creates a new instance of GstVideoComponent */
    public GstVideoComponent() {
        fakesink = ElementFactory.make("fakesink", "GstVideoComponent");
        fakesink.set("signal-handoffs", true);
        fakesink.set("sync", true);
        fakesink.addHandoffListener(new VideoHandoffListener());
        bin = new Bin("GstVideoComponent");
        setDoubleBuffered(false);
        setBackground(new Color(0, 0, 0, 0));
        
        //
        // Convert the input into 32bit RGB so it can be fed directly to a BufferedImage
        //
        Element conv = ElementFactory.make("ffmpegcolorspace", "conv");
        videofilter = ElementFactory.make("capsfilter", "videoflt");
        videofilter.setCaps(new Caps("video/x-raw-rgb, bpp=32, depth=24"));
        bin.addMany(conv, videofilter, fakesink);
        conv.link(videofilter, fakesink);
        
        if (openglEnabled) {
            // Bilinear interpolation can be accelerated by the OpenGL pipeline
            interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            quality = RenderingHints.VALUE_RENDER_QUALITY;
            useVolatile = true;
        }
        if (quartzEnabled) {
            //interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            //quality = RenderingHints.VALUE_RENDER_QUALITY;
            useVolatile = true; // a bit faster on MacOSX ?
        }
        //
        // Link the ghost pads on the bin to the sink pad on the convertor
        //
        Pad pad = conv.getPad("sink");
        bin.addPad(new GhostPad("sink", pad));
    }
    
    public Element getElement() {
        return bin;
    }
    
    public void setKeepAspect(boolean keepAspect) {
        this.keepAspect = keepAspect;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        int width = getWidth(), height = getHeight();
        Graphics2D g2d = (Graphics2D) g.create();
        if (alpha < 1.0f) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }
        g2d.setColor(Color.BLACK);
        
        if (currentImage != null) {
            int imgWidth = currentImage.getWidth(null), imgHeight = currentImage.getHeight(null);
            // Figure out the aspect ratio
            double aspect = (double) imgWidth / (double) imgHeight;
            // Draw & scale on the fly
            int scaledHeight = (int)((double) width / aspect);
            if (!keepAspect) {
                render(g2d, 0, 0, width, height);
            } else if (scaledHeight < height) {
                // Component is higher than the aspect says it is - fill the top/bottom
                // with black bars, and scale the height of the video wrt the width
                int y = (height - scaledHeight) / 2;
                //
                // Create the black bars at the top/bottom
                //
                if (alpha == 1.0f) {
                    int fillHeight = y + 1; // 1 pixel overlap for rounding
                    g2d.fillRect(0, 0, width, fillHeight);
                    g2d.fillRect(0, height - fillHeight, width, fillHeight);
                }
                // Now draw the image itself
                render(g2d, 0, y, width, scaledHeight);
            } else {
                int scaledWidth = (int)((double) height * aspect);
                int x = (width - scaledWidth) / 2;
                //
                // Create black bars at left/right
                //
                if (alpha == 1.0f) {
                    int fillWidth = x + 1; // 1 pixel overlap for rounding
                    g2d.fillRect(0, 0, fillWidth, height);
                    g2d.fillRect(width - fillWidth, 0, fillWidth,  height);
                }
                // Now draw the image itself
                render(g2d, x, 0, scaledWidth, height);
            }
        } else {
            g2d.fillRect(0, 0, width, height);
        }
        g2d.dispose();
        
    }
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
    public float getAlpha() {
        return alpha;
    }
    private void render(Graphics g, int x, int y, int w, int h) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, quality);
        if (useVolatile) {
            do {
                if (volatileImage == null ||
                        volatileImage.validate(getGraphicsConfiguration()) != VolatileImage.IMAGE_OK) {
                    renderOffscreenVolatileImage();
                }
                g.drawImage(volatileImage, x, y, w, h, null);
            } while (volatileImage.contentsLost());
        } else {
            g.drawImage(currentImage, x, y, w, h, null);
        }
    }
    private void renderOffscreenVolatileImage() {
        if (currentImage == null) {
            return;
        }
        do {
            int w = currentImage.getWidth(), h = currentImage.getHeight();
            if (volatileImage == null || volatileImage.getWidth() != w ||
                    volatileImage.getHeight() != h ||
                    volatileImage.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                if (volatileImage != null) {
                    volatileImage.flush();
                }
                GraphicsConfiguration gc = getGraphicsConfiguration();
                volatileImage = gc.createCompatibleVolatileImage(w, h);
            }
            Graphics2D g = volatileImage.createGraphics();
            g.drawImage(currentImage, 0, 0, null);
            g.dispose();
        } while (volatileImage.contentsLost());
    }
    
    private volatile VolatileImage volatileImage = null;
    BufferedImage currentImage = null;
    private void switchBuffer(BufferedImage bImage) {
        // Atomically set the next buffer to be rendered
        BufferedImage oldbuf = nextRef.getAndSet(bImage);
        if (oldbuf != null) {
            freeBufferedImage(oldbuf);
        } else {
            // Only signal swing to switch buffers if the previous buffer was sent for display
            SwingUtilities.invokeLater(update);
        }
    }
    Runnable update = new Runnable() {
        public void run() {
            BufferedImage nextBuffer = nextRef.getAndSet(null);
            if (nextBuffer != null) {
                if (currentImage != null) {
                    freeBufferedImage(currentImage);
                }
                currentImage = nextBuffer;
                if (useVolatile) {
                    renderOffscreenVolatileImage();
                }
                paintImmediately(0, 0, getWidth(), getHeight());
            }
            
        }
    };
    
    private List<BufferedImage> buffers = Collections.synchronizedList(new LinkedList<BufferedImage>());
    BufferedImage getBufferedImage(int width, int height) {
        BufferedImage buf;
        
        //
        // If the swing thread has not had time to render the current Image, just re-use it
        //
        if ((buf = nextRef.getAndSet(null)) != null) {
            if (buf.getWidth() == width && buf.getHeight() == height) {
                return buf;
            }
            buf.flush();
        }
        if (!buffers.isEmpty()) {
            buf = buffers.remove(0);
            // If the size matches, return the cached buffer
            if (buf.getWidth() == width && buf.getHeight() == height) {
                return buf;
            }
            // Invalidate all the pooled buffers
            buffers.clear();
        }
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    void freeBufferedImage(BufferedImage buf) {
        buffers.add(buf);
    }
    class VideoHandoffListener implements HandoffListener {
        public void handoff(HandoffEvent ev) {
            
            Buffer buffer = ev.getBuffer();
            Caps caps = buffer.getCaps();
            Structure struct = caps.getStructure(0);
            
            int width = struct.getInteger("width");
            int height = struct.getInteger("height");
            if (width < 1 || height < 1) {
                return;
            }
            final BufferedImage bImage = getBufferedImage(width, height);
            int[] pixels = ((DataBufferInt) bImage.getRaster().getDataBuffer()).getData();
            buffer.read(0, pixels, 0, width * height);
            
            // Tell swing to use the new buffer
            switchBuffer(bImage);
            
            // Dispose of the gstreamer buffer immediately
            buffer.dispose();
        }
        
    }
}

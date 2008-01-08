/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.swing;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.gstreamer.*;
import org.gstreamer.event.*;
import org.gstreamer.elements.RGBDataSink;

/**
 *
 */
public class GstVideoComponent extends javax.swing.JComponent {
    private VideoFrame currentFrame = null;
    private RGBDataSink videosink;
    private RenderingHints renderingHints;
    private static boolean openglEnabled = false;
    private static boolean quartzEnabled = false;
    private static boolean ddscaleEnabled = false;
    private boolean keepAspect = true;
    private float alpha = 1.0f;
    private Timer resourceTimer;
    private VolatileImage volatileImage;
    private boolean frameRendered = false;
    
    static {
        try {
            String openglProperty = System.getProperty("sun.java2d.opengl");
            openglEnabled = openglProperty != null && Boolean.parseBoolean(openglProperty);
        } catch (Exception ex) { }
        try {
            String quartzProperty = System.getProperty("apple.awt.graphics.UseQuartz");
            quartzEnabled = Boolean.parseBoolean(quartzProperty);
        } catch (Exception ex) { }
        try {
            String ddscaleProperty = System.getProperty("sun.java2d.ddscale");
            String d3dProperty = System.getProperty("sun.java2d.d3d");
            ddscaleEnabled = Boolean.parseBoolean(ddscaleProperty) && Boolean.parseBoolean(d3dProperty);
        } catch (Exception ex) { }
    }
    /** Creates a new instance of GstVideoComponent */
    public GstVideoComponent() {
        videosink = new RGBDataSink("GstVideoComponent", new RGBListener());
        videosink.setPassDirectBuffer(true);
        Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();
        setOpaque(true);
        setBackground(Color.BLACK);
        
        if (openglEnabled) {
            // Bilinear interpolation can be accelerated by the OpenGL pipeline
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
        } else if (quartzEnabled) {
            //hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
        } else if (ddscaleEnabled) {
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        renderingHints = new RenderingHints(hints);
        //
        // Kick off a timer to free up the volatile image if there have been no recent updates
        // (e.g. the player is paused)
        //
        resourceTimer = new Timer(250, resourceReaper);
    }

    private ActionListener resourceReaper = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (!frameRendered) {
                    if (volatileImage != null) {
                        volatileImage.flush();
                        volatileImage = null;
                    }
                    //
                    // Flush any cached VideoFrames
                    //
                    VideoFrame f;
                    while ((f = freeQueue.poll()) != null) {
                        f.flush();
                    }
                    frameRendered = false;
                    
                    // Stop the timer so we don't wakeup needlessly
                    resourceTimer.stop();
                }
            }
    };
    public Element getElement() {
        return videosink;
    }
    
    public void setKeepAspect(boolean keepAspect) {
        this.keepAspect = keepAspect;
    }

    @Override
    public boolean isLightweight() {
        return true;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        int width = getWidth(), height = getHeight();
        Graphics2D g2d = (Graphics2D) g.create();
        if (alpha < 1.0f) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }
        g2d.setColor(getBackground());
        
        if (currentFrame != null) {
            int imgWidth = currentFrame.getWidth(), imgHeight = currentFrame.getHeight();
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
                if (isOpaque()) {
                    int fillHeight = height - scaledHeight;
                    g2d.fillRect(0, 0, width, y);
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
                if (isOpaque()) {
                    int fillWidth = width - scaledWidth;
                    g2d.fillRect(0, 0, x, height);
                    g2d.fillRect(width - fillWidth, 0, fillWidth,  height);
                }
                // Now draw the image itself
                render(g2d, x, 0, scaledWidth, height);
            }
        } else if (alpha >= 1.0f) {
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
    
    public void renderVolatileImage(BufferedImage bufferedImage) {
        do {
            int w = bufferedImage.getWidth(), h = bufferedImage.getHeight();
            GraphicsConfiguration gc = getGraphicsConfiguration();
            if (volatileImage == null || volatileImage.getWidth() != w 
                    || volatileImage.getHeight() != h 
                    || volatileImage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
                if (volatileImage != null) {
                    volatileImage.flush();
                }
                volatileImage = gc.createCompatibleVolatileImage(w, h);
                volatileImage.setAccelerationPriority(1.0f);
            }
            // 
            // Now paint the BufferedImage into the accelerated image
            //
            Graphics2D g = volatileImage.createGraphics();
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();
        } while (volatileImage.contentsLost());
    }
    private void render(Graphics g, int x, int y, int w, int h) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHints(renderingHints);
        do {
            if (volatileImage == null 
                || volatileImage.validate(getGraphicsConfiguration()) != VolatileImage.IMAGE_OK) {
                renderVolatileImage(currentFrame.bufferedImage);
            }
            g2d.drawImage(volatileImage, x, y, w, h, null);
        } while (volatileImage.contentsLost());
        
        g2d.dispose();
        
        //
        // Restart the resource reaper timer if neccessary
        //
        if (!frameRendered) {
            frameRendered = true;
            if (!resourceTimer.isRunning()) {
                resourceTimer.restart();
            }
        }
    }
    
    int oldWidth = 0, oldHeight = 0;
    Runnable update = new Runnable() {
        public void run() {
            VideoFrame nextImage = renderQueue.poll();
            if (nextImage != null) {
                if (currentFrame != null) {
                    freeVideoFrame(currentFrame);
                }                
                currentFrame = nextImage;
                renderVolatileImage(currentFrame.bufferedImage);
                
                final int imgWidth = currentFrame.getWidth(), imgHeight = currentFrame.getHeight();
                if (imgWidth != oldWidth || imgHeight != oldHeight || !keepAspect) {
                    paintImmediately(0, 0, getWidth(), getHeight());
                } else {
                    // Scale the area and just request that be painted
                    double aspect = (double) imgWidth / (double) imgHeight;
                    int width = getWidth(), height = getHeight();
                    int scaledHeight = (int)((double) width / aspect);
                    if (scaledHeight < height) {
                        // Component is higher than the aspect says it is - fill the top/bottom
                        // with black bars, and scale the height of the video wrt the width
                        paintImmediately(0, (height - scaledHeight) / 2, width, scaledHeight);
                    } else {
                        int scaledWidth = (int)((double) height * aspect);
                        paintImmediately((width - scaledWidth) / 2, 0, scaledWidth, height);
                    }
                }
                oldWidth = imgWidth;
                oldHeight = imgHeight;
            }
            
        }
    };
    
    
    private class VideoFrame {
        BufferedImage bufferedImage;
        public VideoFrame(int w, int h) {
            bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        }
        public final int getWidth() { return bufferedImage.getWidth(null); }
        public final int getHeight() { return bufferedImage.getHeight(null); }
        public void flush() {
            bufferedImage.flush();
        }
    }
    private Queue<VideoFrame> renderQueue = new ArrayBlockingQueue<VideoFrame>(1);
    private Queue<VideoFrame> freeQueue = new ArrayBlockingQueue<VideoFrame>(1);
    VideoFrame allocVideoFrame(int width, int height) {
        VideoFrame buf;
        
        if ((buf = freeQueue.poll()) != null) {
            if (buf.getWidth() == width && buf.getHeight() == height) {
                return buf;
            }
            // The buffer was not the correct type - flush it and move on
            buf.flush();
        }
        return new VideoFrame(width, height);
    }
    void freeVideoFrame(VideoFrame buf) {
        if (!freeQueue.offer(buf)) {
            buf.flush();
        }
    }
    class RGBListener implements RGBDataSink.Listener {
        public void rgbFrame(int width, int height, IntBuffer rgb) {
            //
            // If the current frame has not been rendered, just ignore this one
            //
            if (!renderQueue.isEmpty()) {
                return;
            }
            final VideoFrame renderImage = allocVideoFrame(width, height);
            int[] pixels = ((DataBufferInt) renderImage.bufferedImage.getRaster().getDataBuffer()).getData();
            rgb.get(pixels, 0, width * height);
            
            if (!renderQueue.offer(renderImage)) {
                freeVideoFrame(renderImage);
            }
            // Tell swing to use the new buffer
            SwingUtilities.invokeLater(update);
        }
        
    }
}

/*
 * Copyright (c) 2009 Andres Colubri
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

package org.gstreamer.elements;

import com.sun.jna.Pointer;
import java.nio.IntBuffer;
import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.lowlevel.GstBinAPI;
import org.gstreamer.lowlevel.GstNative;

public class RGBDataAppSink extends Bin {
    private static final GstBinAPI gst = GstNative.load(GstBinAPI.class);
    private boolean passDirectBuffer = false;
    private final Listener listener;
    private final AppSink sink;

    public static interface Listener {
        void rgbFrame(int width, int height, IntBuffer rgb);
    }

    public RGBDataAppSink(String name, Listener listener) {
        super(initializer(gst.ptr_gst_bin_new(name)));
        this.listener = listener;

        sink = (AppSink) ElementFactory.make("appsink", "VideoSink");
        sink.set("emit-signals", true);
        sink.set("sync", true);
        sink.connect(new AppSinkNewBufferListener());
        
        //
        // Convert the input into 32bit RGB so it can be fed directly to a BufferedImage
        //
        Element conv = ElementFactory.make("ffmpegcolorspace", "ColorConverter");
        Element videofilter = ElementFactory.make("capsfilter", "ColorFilter");
        videofilter.setCaps(new Caps("video/x-raw-rgb, bpp=32, depth=24"));
        addMany(conv, videofilter, sink);
        Element.linkMany(conv, videofilter, sink);

        //
        // Link the ghost pads on the bin to the sink pad on the convertor
        //
        addPad(new GhostPad("sink", conv.getStaticPad("sink")));
    }

    public RGBDataAppSink(String name, Pipeline pipeline, Listener listener) {
        super(initializer(gst.ptr_gst_bin_new(name)));
        this.listener = listener;

        // Doesn't work. getElementByName returns a BaseSink which cannot be casted
        // to AppSink.
        sink = (AppSink) pipeline.getElementByName("VideoSink");
        sink.set("emit-signals", true);
        sink.set("sync", true);
        sink.connect(new AppSinkNewBufferListener());
    }

    /**
     * Indicate whether the {@link RGBDataAppSink} should pass the native {@link java.nio.IntBuffer}
     * to the listener, or should copy it to a heap buffer.  The default is to pass
     * a heap {@link java.nio.IntBuffer} copy of the data
     * @param passThru If true, pass through the native IntBuffer instead of
     * copying it to a heap IntBuffer.
     */
    public void setPassDirectBuffer(boolean passThru) {
        this.passDirectBuffer = passThru;
    }

    /**
     * Gets the actual gstreamer sink element.
     *
     * @return a AppSink
     */
    public BaseSink getSinkElement() {
        return sink;
    }

    /**
     * Gets the <tt>Caps</tt> configured on this <tt>data sink</tt>
     *
     * @return The caps configured on this <tt>sink</tt>
     */
    public Caps getCaps() {
        return sink.getCaps();
    }

    /**
     * A listener class that handles the new-buffer signal from the AppSink element.
     *
     */
    class AppSinkNewBufferListener implements AppSink.NEW_BUFFER {
        public void newBuffer(Element elem, Pointer userData)
        {
            Buffer buffer = sink.pullBuffer();

            Caps caps = buffer.getCaps();
            Structure struct = caps.getStructure(0);

            int width = struct.getInteger("width");
            int height = struct.getInteger("height");
            if (width < 1 || height < 1) {
                return;
            }
            IntBuffer rgb;
            if (passDirectBuffer) {
                rgb = buffer.getByteBuffer().asIntBuffer();
            } else {
                rgb = IntBuffer.allocate(width * height);
                rgb.put(buffer.getByteBuffer().asIntBuffer()).flip();
            }
            listener.rgbFrame(width, height, rgb);

            //
            // Dispose of the gstreamer buffer immediately to avoid more being
            // allocated before the java GC kicks in
            //
            buffer.dispose();
        }
    }
}

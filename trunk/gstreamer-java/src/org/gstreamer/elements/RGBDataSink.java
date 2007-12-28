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

package org.gstreamer.elements;

import org.gstreamer.Element;
import java.nio.IntBuffer;
import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Pad;
import org.gstreamer.Structure;
import static org.gstreamer.lowlevel.GstAPI.gst;


public class RGBDataSink extends Bin {
    private boolean passDirectBuffer = false;
    private final Listener listener;
    
    public static interface Listener {
        void rgbFrame(int width, int height, IntBuffer rgb);
    }
    
    /**
     * Creates a new instance of RGBDataSink with the given name.
     * 
     * @param name The name used to identify this pipeline.
     */
    public RGBDataSink(String name, Listener listener) {
        super(initializer(gst.gst_bin_new(name)));
        this.listener = listener;
        Element videosink = ElementFactory.make("fakesink", "VideoSink");
        videosink.set("signal-handoffs", true);
        videosink.set("sync", true);
        videosink.connect(new VideoHandoffListener());
        
        //
        // Convert the input into 32bit RGB so it can be fed directly to a BufferedImage
        //
        Element conv = ElementFactory.make("ffmpegcolorspace", "ColorConverter");
        Element videofilter = ElementFactory.make("capsfilter", "ColorFilter");
        videofilter.setCaps(new Caps("video/x-raw-rgb, bpp=32, depth=24"));
        addMany(conv, videofilter, videosink);
        Element.linkMany(conv, videofilter, videosink);
        
        //
        // Link the ghost pads on the bin to the sink pad on the convertor
        //
        Pad pad = conv.getStaticPad("sink");
        addPad(new GhostPad("sink", pad));
    }
    /**
     * Indicate whether the {@link RGBDataSink} should pass the native {@link java.nio.IntBuffer}
     * to the listener, or should copy it to a heap buffer.  The default is to pass
     * a heap {@link java.nio.IntBuffer} copy of the data
     * @param passThru If true, pass through the native IntBuffer instead of 
     * copying it to a heap IntBuffer.
     */
    public void setPassDirectBuffer(boolean passThru) {
        this.passDirectBuffer = passThru;
    }
    class VideoHandoffListener implements Element.HANDOFF {
        public void handoff(Element element, Buffer buffer, Pad pad) {
            
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
            // Dispose of the gstreamer buffer immediately to aoid more being 
            // allocated before the java GC kicks in
            //
            buffer.dispose();
        }
    }
}

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

import java.io.File;
import java.util.LinkedList;

import org.gstreamer.Buffer;
import org.gstreamer.State;
import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.lowlevel.GlibAPI;
import org.gstreamer.lowlevel.GlibAPI.GSourceFunc;
import org.gstreamer.lowlevel.GstBinAPI;
import org.gstreamer.lowlevel.GstNative;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.gstreamer.ClockTime;

public class RGBDataFileSink extends Bin {
    private static final GstBinAPI gst = GstNative.load(GstBinAPI.class);
    private static final GlibAPI glib = GlibAPI.glib;
    
    private final LinkedList<Buffer> bufferList;
    private final AppSrc source;
    private final Caps videoCaps;

    private final AppSrcNeedDataListener startFeed;
    private final AppSrcEnoughDataListener stopFeed;
    private final PushBuffer pushBuffer;
    private final int FPS;
    private final int NANOS_PER_FRAME;
    private final int sourceWidth;
    private final int sourceHeight;

    private int sourceID;
    private int frameCount;
    private int QUEUED_FRAMES;

    public RGBDataFileSink(String name, int width, int height, int fps, String encoderStr, String[] encoderPropertyNames, Object[] encoderPropertyData, String muxerStr, File file) {
        super(initializer(gst.ptr_gst_bin_new(name)));

        bufferList = new LinkedList();

        QUEUED_FRAMES = 30;

        sourceWidth = width;
        sourceHeight = height;
        FPS = fps;
        NANOS_PER_FRAME = (int)(1e9 / FPS);

        videoCaps = Caps.fromString("video/x-raw-rgb,width=" + width + ",height=" + height + "," +
                                    "bpp=32,endianness=4321,depth=24,red_mask=65280,green_mask=16711680,blue_mask=-16777216," +
                                    "framerate=" + fps + "/1");

        pushBuffer = new PushBuffer();
        startFeed = new AppSrcNeedDataListener();
        stopFeed = new AppSrcEnoughDataListener();

        // Building pipeline.
        source = (AppSrc)ElementFactory.make("appsrc", "source");
        source.set("is-live", true);
        source.setCaps(videoCaps);
        source.setMaxBytes(QUEUED_FRAMES * sourceWidth * sourceHeight * 4);

        source.connect(startFeed);
        source.connect(stopFeed);

        Element formatConverter = ElementFactory.make("ffmpegcolorspace", "formatConverter");
        Element formatFilter = ElementFactory.make("capsfilter", "formatFilter");
        Caps capsFormat = Caps.fromString("video/x-raw-yuv,format=(fourcc)I420,width=" + width + ",height=" + height);
        formatFilter.setCaps(capsFormat);

        Element queue0 = ElementFactory.make("queue", "queue0");
        Element fpsAdjuster = ElementFactory.make("videorate", "fpsAdjuster");
        Element fpsFilter = ElementFactory.make("capsfilter", "fpsFilter");
        Caps capsFPS = Caps.fromString("video/x-raw-yuv,framerate=" + fps + "/1");
        fpsFilter.setCaps(capsFPS);

        Element encoder = ElementFactory.make(encoderStr, "encoder");
        if (encoderPropertyNames != null && encoderPropertyData != null)
        {
            // Setting encoder properties.
            int n0 = encoderPropertyNames.length;
            int n1 = encoderPropertyData.length;
            int n = n0 < n1 ? n0 : n1;
            for (int i = 0; i < n; i++) encoder.set(encoderPropertyNames[i], encoderPropertyData[i]);
        }
        
        Element queue1 = ElementFactory.make("queue", "queue1");
        Element muxer = ElementFactory.make(muxerStr, "muxer");
        Element queue2 = ElementFactory.make("queue", "queue2");

        Element sink = ElementFactory.make("filesink", "sink");
        sink.set("location", file.toString());

        addMany(source, formatConverter, formatFilter, queue0, fpsAdjuster, fpsFilter, encoder, queue1, muxer, queue2, sink);
        Element.linkMany(source, formatConverter, formatFilter, queue0, fpsAdjuster, fpsFilter, encoder, queue1, muxer, queue2, sink);

        sourceID = 0;
        frameCount = 0;
    }

    public void pushRGBFrame(Buffer buf)
    {
        bufferList.add(buf);
    }

    public void start()
    {
        frameCount = 0;
        setState(State.PLAYING);
    }

    public void stop()
    {
        setState(State.PAUSED);
        source.endOfStream();
    }

    public void setQueueSize(int nFrames)
    {
        QUEUED_FRAMES = nFrames;
        source.setMaxBytes(QUEUED_FRAMES * sourceWidth * sourceHeight * 4);
    }

    public int getQueueSize()
    {
        return QUEUED_FRAMES;
    }

    class AppSrcNeedDataListener implements AppSrc.NEED_DATA {
        public void startFeed(Element elem, int size, Pointer userData)
        {
            System.out.println("AppSrc needs data");
            if (sourceID == 0)
            {
                NativeLong val = glib.g_idle_add(pushBuffer, userData);
                sourceID = val.intValue();
            }
        }
    }

    class AppSrcEnoughDataListener implements AppSrc.ENOUGH_DATA {
        public void stopFeed(Element elem, Pointer userData)
        {
            System.out.println("AppSrc has enough data");
            if (sourceID != 0)
            {
                glib.g_source_remove(sourceID);
                sourceID = 0;
            }
        }
    }

    class PushBuffer implements GSourceFunc {
        public boolean callback(Pointer data)
        {
            if (0 < bufferList.size())
            {
                // There are buffers available in the fifo list to be sent to the
                // appsrc queue.
                Buffer buf = bufferList.remove(0);
                frameCount++;

                long f = frameCount * NANOS_PER_FRAME;
                buf.setCaps(videoCaps);
                buf.setTimestamp(ClockTime.fromNanos(f));
                source.pushBuffer(buf);
                buf.dispose();
            }
            return true;
        }
    }
}

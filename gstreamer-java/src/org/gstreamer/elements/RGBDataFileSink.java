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
import java.util.concurrent.ExecutorService;

import org.gstreamer.Gst;
import org.gstreamer.ClockTime;
import org.gstreamer.Buffer;
import org.gstreamer.State;
import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.lowlevel.GlibAPI;
import org.gstreamer.lowlevel.GstBinAPI;
import org.gstreamer.lowlevel.GstNative;

import com.sun.jna.Pointer;

public class RGBDataFileSink extends Bin {
    private static final GstBinAPI gst = GstNative.load(GstBinAPI.class);
    private static final GlibAPI glib = GlibAPI.glib;

    private final LinkedList<Buffer> bufferList;
    private final AppSrc source;
    private final Caps videoCaps;

    private final AppSrcNeedDataListener needDataListener;
    private final AppSrcEnoughDataListener enoughDataListener;
    private final BufferDispatcher bufferDispatcher;
    private final int FPS;
    private final int NANOS_PER_FRAME;
    private final int sourceWidth;
    private final int sourceHeight;

    private boolean sendingData;
    private int frameCount;
    private int QUEUED_FRAMES;

    public RGBDataFileSink(String name, int width, int height, int fps, String encoderStr, String[] encoderPropertyNames, Object[] encoderPropertyData, String muxerStr, File file) {
        super(initializer(gst.ptr_gst_bin_new(name)));

        bufferList = new LinkedList<Buffer>();

        QUEUED_FRAMES = 30;

        sourceWidth = width;
        sourceHeight = height;
        FPS = fps;
        NANOS_PER_FRAME = (int)(1e9 / FPS);

        videoCaps = Caps.fromString("video/x-raw-rgb,width=" + width + ",height=" + height + "," +
                                    "bpp=32,endianness=4321,depth=24,red_mask=65280,green_mask=16711680,blue_mask=-16777216," +
                                    "framerate=" + fps + "/1");

        // Building pipeline.
        source = (AppSrc)ElementFactory.make("appsrc", "source");
        source.set("is-live", true);
        source.set("format", 3); // GST_FORMAT_TIME = 3
        source.setCaps(videoCaps);
        source.setMaxBytes(QUEUED_FRAMES * sourceWidth * sourceHeight * 4);

        needDataListener = new AppSrcNeedDataListener();
        enoughDataListener = new AppSrcEnoughDataListener();
        source.connect(needDataListener);
        source.connect(enoughDataListener);

        bufferDispatcher = new BufferDispatcher();
        ExecutorService exec = Gst.getExecutorService();
        exec.submit(bufferDispatcher);

        Element formatConverter = ElementFactory.make("ffmpegcolorspace", "formatConverter");
        Element formatFilter = ElementFactory.make("capsfilter", "formatFilter");
        Caps capsFormat = Caps.fromString("video/x-raw-yuv,format=(fourcc)I420,width=" + width + ",height=" + height);
        formatFilter.setCaps(capsFormat);

        Element encoder = ElementFactory.make(encoderStr, "encoder");
        if (encoderPropertyNames != null && encoderPropertyData != null)
        {
            // Setting encoder properties.
            int n0 = encoderPropertyNames.length;
            int n1 = encoderPropertyData.length;
            int n = n0 < n1 ? n0 : n1;
            for (int i = 0; i < n; i++) encoder.set(encoderPropertyNames[i], encoderPropertyData[i]);
        }

        Element muxer = ElementFactory.make(muxerStr, "muxer");

        Element sink = ElementFactory.make("filesink", "sink");
        sink.set("location", file.toString());

        addMany(source, formatConverter, formatFilter, encoder, muxer, sink);
        Element.linkMany(source, formatConverter, formatFilter, encoder, muxer, sink);

        sendingData = false;
    }

    public void pushRGBFrame(Buffer buf)
    {
        addBuffer(buf);
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

    public int getNumQueuedFrames()
    {
        return bufferList.size();
    }

    class AppSrcNeedDataListener implements AppSrc.NEED_DATA {
        public void startSendingData(Element elem, int size, Pointer userData)
        {
            if (!sendingData)
            {
                sendingData = true;

                // If needDataListener is not disconnected, then it keeps triggering
                // "need-data" signals... This shouldn't happen. Bug in the signal
                // handling in Gstreamer-java?
                // Same thing in stopSendingData below.
                source.disconnect(needDataListener);
                source.connect(enoughDataListener);
            }
        }
    }

    class AppSrcEnoughDataListener implements AppSrc.ENOUGH_DATA {
        public void stopSendingData(Element elem, Pointer userData)
        {
            if (sendingData)
            {
                sendingData = false;
                source.connect(needDataListener);
                source.disconnect(enoughDataListener);
            }
        }
    }

    private void addBuffer(Buffer buf)
    {
        bufferList.add(buf);
    }

    private void pushBuffer()
    {
        if (sendingData)
            if (0 < bufferList.size())
            {
                // There are buffers available in the fifo list to be sent to the
                // appsrc queue.
                Buffer buf = bufferList.remove(0);
                frameCount++;

                long f = frameCount * NANOS_PER_FRAME;
                buf.setCaps(videoCaps);
                buf.setTimestamp(ClockTime.fromNanos(f));
                buf.setDuration(ClockTime.fromNanos(NANOS_PER_FRAME));
                source.pushBuffer(buf);
                buf.dispose();
            }
    }

    class BufferDispatcher implements Runnable {
        public void run()
        {
            while (true) {
                pushBuffer();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

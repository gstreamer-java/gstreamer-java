/*
 * Copyright (c) 2009 Levente Farkas
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
import java.util.concurrent.ScheduledExecutorService;
import com.sun.jna.Pointer;

import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.ClockTime;
import org.gstreamer.Buffer;
import org.gstreamer.State;
import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.lowlevel.GstBinAPI;
import org.gstreamer.lowlevel.GstNative;

/**
 * This bin encapsulates a pipeline that allows to encode RGB buffers into a video
 * file. It uses the AppSrc element to inject the buffers into the gst pipeline.
 */
public class RGBDataFileSink extends Bin {
    private static final GstBinAPI gst = GstNative.load(GstBinAPI.class);

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
    private ScheduledExecutorService executor;

    /**
     * Creates a new RGBDataFileSink.
     *
     * @param name The name used to identify this RGBDataFileSink.
     * @param width The width of the buffers that will be sent.
     * @param height The height of the buffers that will be sent.
     * @param fps The framerate with which the buffers should be saved to file.
     * @param encoderStr The name of the encoder to use.
     * @param encoderPropertyNames Array of property names for the encoder.
     * @param encoderPropertyData Array of property values for the encoder.
     * @param muxerStr The name of the muxer to use
     * @param file Output file where the stream is saved to.
     */
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
        
        source.setLive(true);
        
        // Using either BUFFERS or TIME doesn't seem
        // to make a difference, but BUFFERS make more
        // sense with the buffer timestamping. See comments
        // in pushBuffer() method below.
        source.setFormat(Format.BUFFERS);
        //source.setFormat(Format.TIME);
        
        source.setLatency(-1, 0);
        source.setSize(-1);
        source.setCaps(videoCaps);
        source.setMaxBytes(QUEUED_FRAMES * sourceWidth * sourceHeight * 4);

        needDataListener = new AppSrcNeedDataListener();
        enoughDataListener = new AppSrcEnoughDataListener();
        source.connect(needDataListener);
        source.connect(enoughDataListener);

        bufferDispatcher = new BufferDispatcher();
        executor = Gst.getScheduledExecutorService();

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

    /**
     * Pushes a buffer down the pipeline.
     *
     * @param buf The buffer to push. Actually, it is not immediatelly pushed into
     * the gst pipeline, but it is added to a fifo linked list that holds the buffer
     * temporarily until the AppSrc requests more data for its internal queue.
     *
     */
    public void pushRGBFrame(Buffer buf)
    {
        addBuffer(buf);

        // Find the way to set bufferDispatcher only once but gets executed periodically
        // See the documentation of
        // public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory)
        // "Creates a single-threaded executor that can schedule commands
        // to run after a given delay, or to execute periodically..."
        executor.execute(bufferDispatcher);
    }

    /**
     * Sets the state of the pipeline to PLAYING.
     *
     */
    public void start()
    {
        frameCount = 0;
        setState(State.PLAYING);
    }

    /**
     * Sets the state of the pipeline to PAUSED.
     *
     */
    public void stop()
    {
        setState(State.PAUSED);
        source.endOfStream();
    }

    /**
     * Sets the size of the AppSrc queue.
     *
     * @param nFrames Size of the queue expressed in number of frames.
     */
    public void setQueueSize(int nFrames)
    {
        QUEUED_FRAMES = nFrames;
        source.setMaxBytes(QUEUED_FRAMES * sourceWidth * sourceHeight * 4);
    }

    /**
     * Returns the current size of the AppSrc queue.
     *
     */
    public int getQueueSize()
    {
        return QUEUED_FRAMES;
    }

    /**
     * Returns the number of buffers currently stored in the fifo linked list (still
     * not pushed down the pipeline).
     *
     */
    public int getNumQueuedFrames()
    {
        return bufferList.size();
    }

    /**
     * A listener class that handles the need-data signal from the AppSrc element.
     *
     */
    class AppSrcNeedDataListener implements AppSrc.NEED_DATA {
        public void needData(Element elem, int size, Pointer userData)
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

    /**
     * A listener class that handles the enough-data signal from the AppSrc element.
     *
     */
    class AppSrcEnoughDataListener implements AppSrc.ENOUGH_DATA {
        public void enoughData(Element elem, Pointer userData)
        {
            if (sendingData)
            {
                sendingData = false;
                source.connect(needDataListener);
                source.disconnect(enoughDataListener);
            }
        }
    }

    /**
     * Adds a buffer to the fifo linked list.
     *
     */
    private void addBuffer(Buffer buf)
    {
        bufferList.add(buf);
    }

    /**
     * It pushes a buffer into the gst pipeline.
     *
     */
    private void pushBuffer()
    {
        if (sendingData)
            if (0 < bufferList.size())
            {
                // There are buffers available in the fifo list to be sent to the
                // appsrc queue.
                Buffer buf = bufferList.remove(0);
                frameCount++;

                buf.setCaps(videoCaps);
                
                // For some reason this duration and timestamp setting works 
                // with all encoders I tried so far (theora, x264, dirac),
                // although doesn't make much sense (frame duration 1 nano?)...
                buf.setTimestamp(ClockTime.fromNanos(frameCount));
                buf.setDuration(ClockTime.fromNanos(1));
                
                // ... this other one, which is logically correc, doesn't work for
                // theora (frames are dropped for no apparent reason each 
                // two seconds):
                //long f = frameCount * NANOS_PER_FRAME;
                //buf.setTimestamp(ClockTime.fromNanos(f));
                //buf.setDuration(ClockTime.fromNanos(NANOS_PER_FRAME));
                
                source.pushBuffer(buf);
                buf.dispose();
            }
    }

    /**
     * The runnable that pushes one buffer down the gst pipeline.
     *
     */
    class BufferDispatcher implements Runnable {
        public void run()
        {
            pushBuffer();
        }
    }
}

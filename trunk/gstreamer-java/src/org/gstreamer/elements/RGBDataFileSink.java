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
import java.io.File;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.lowlevel.GlibAPI;
import org.gstreamer.lowlevel.GlibAPI.GSourceFunc;
import org.gstreamer.lowlevel.GstBinAPI;
import org.gstreamer.lowlevel.GstNative;

public class RGBDataFileSink extends Bin {
    private static final GstBinAPI gst = GstNative.load(GstBinAPI.class);
    private static final GlibAPI glib = GlibAPI.glib;
    
    private final LinkedList<IntBuffer> bufferList;
    private final AppSrc source;
    private final Caps videoCaps;

    private final AppSrcNeedDataListener startFeed;
    private final AppSrcEnoughDataListener stopFeed;
    private final PushBuffer pushBuffer;
    private final int sourceID;

    public RGBDataFileSink(String name, int width, int height, int fps, String encoderStr, String[] encoderPropertyNames, Object[] encoderPropertyData, String muxerStr, File file) {
        super(initializer(gst.ptr_gst_bin_new(name)));

        bufferList = new LinkedList();
        
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
    }

    public void pushRGBFrame(IntBuffer frame)
    {
        bufferList.add(frame);
    }

    class AppSrcNeedDataListener implements AppSrc.NEED_DATA {
        public void startFeed(Element elem, int size, Pointer userData)
        {
            System.out.println("AppSrc needs data");
/*
  if (app->source_id == 0) {
	g_print ("start feeding at frame %i\n", app->num_frame);
    app->source_id = g_idle_add ((GSourceFunc) push_buffer, app);
  }
  */
        }
    }

    class AppSrcEnoughDataListener implements AppSrc.ENOUGH_DATA {
        public void stopFeed(Element elem, Pointer userData)
        {
            System.out.println("AppSrc has enough data");
/*
  if (app->source_id != 0) {
	g_print ("stop feeding at frame %i\n", app->num_frame);
    g_source_remove (app->source_id);
    app->source_id = 0;
  }
*/
        }
    }

    class PushBuffer implements GSourceFunc {
        public boolean callback(Pointer data)
        {
/*
  gpointer raw_buffer;
  GstBuffer *app_buffer;
  GstFlowReturn ret;

  app->num_frame++;

  if (app->num_frame >= TOTAL_FRAMES) {
    // we are EOS, send end-of-stream and remove the source
    g_signal_emit_by_name (app->source, "end-of-stream", &ret);
    return FALSE;
  }

  // Allocating the memory for the buffer
  raw_buffer = g_malloc0 (BUFFER_SIZE);
  app_buffer = bufferList.remove(0);

  app_buffer = gst_app_buffer_new (raw_buffer, BUFFER_SIZE, g_free, raw_buffer);

  // newer basesrc will set caps for use automatically but it does not really
  // hurt to set it on the buffer again
  gst_buffer_set_caps (app_buffer, gst_caps_from_string (video_caps));

  // Setting the correct timestamp for the buffer is very important, otherwise the
  // resulting video file won't be created correctly
  GST_BUFFER_TIMESTAMP(app_buffer) = (GstClockTime)((app->num_frame / 30.0) * 1e9);

  // push new buffer
  g_signal_emit_by_name (app->source, "push-buffer", app_buffer, &ret);
  gst_buffer_unref (app_buffer);

  if (ret != GST_FLOW_OK) {
    // some error, stop sending data
    return FALSE;
  }

  return TRUE;

 */
            return true;
        }
    }
}

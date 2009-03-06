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

import java.nio.IntBuffer;
import java.util.LinkedList;

import org.gstreamer.*;
import org.gstreamer.elements.*;


public class RGBDataFileSink extends Pipeline {
}

//"video/x-raw-rgb,width=80,height=60,bpp=32,endianness=4321,depth=24,red_mask=65280,green_mask=16711680,blue_mask=-16777216,framerate=30/1";
/*
public class RGBDataFileSink extends Pipeline {
    private final LinkedList<IntBuffer> bufferList;

    public RGBDataFileSink(Caps caps, String encoder, String encoderParams, String muxer, String filename) {
        this(makeRawElement("playbin", "test"));


        // "appsrc is-live=true name=source caps=\"%s\" ! ffmpegcolorspace ! video/x-raw-yuv,format=(fourcc)I420,width=80,height=60 ! queue ! videorate ! video/x-raw-yuv,framerate=30/1 ! h264enc ! queue ! avimux ! queue ! filesink location=test.avi"

        bufferList = new LinkedList();
    }




    public static Pipeline launch(String pipelineDecription) {
        Pointer[] err = { null };
        Pipeline pipeline = gst.gst_parse_launch(pipelineDecription, err);
        if (pipeline == null) {
            throw new GstException(new GError(new GErrorStruct(err[0])));
        }
        pipeline.initBus();
        return pipeline;
    }

 

    public void pushRGBFrame(IntBuffer rgb)
    {
        //bufferList.add(rgb);
        
    }

    public void start()
    {
    }

    @Override public void stop()
    {
    }


static gboolean
push_buffer (AppData * app)
{
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
}

// This signal callback is called when appsrc needs data, we add an idle handler
// to the mainloop to start pushing data into the appsrc
static void
start_feed (GstElement * pipeline, guint size, AppData * app)
{
  if (app->source_id == 0) {
	g_print ("start feeding at frame %i\n", app->num_frame);
    app->source_id = g_idle_add ((GSourceFunc) push_buffer, app);
  }
}

// This callback is called when appsrc has enough data and we can stop sending.
// We remove the idle handler from the mainloop
static void
stop_feed (GstElement * pipeline, AppData * app)
{
  if (app->source_id != 0) {
	g_print ("stop feeding at frame %i\n", app->num_frame);
    g_source_remove (app->source_id);
    app->source_id = 0;
  }
}

// called when we get a GstMessage from the pipeline when we get EOS, we
// exit the mainloop and this testapp.
static gboolean
on_pipeline_message (GstBus * bus, GstMessage * message, AppData * app)
{
  GstState state, pending;

  switch (GST_MESSAGE_TYPE (message)) {
    case GST_MESSAGE_EOS:
      g_print ("Received End of Stream message\n");
      g_main_loop_quit (app->loop);
      break;
    case GST_MESSAGE_ERROR:
      g_print ("Received error\n");
      g_main_loop_quit (app->loop);
      break;
    case GST_MESSAGE_STATE_CHANGED:
	  gst_element_get_state(app->source, &state, &pending, GST_CLOCK_TIME_NONE);
      // g_print ("State changed from %i to %i\n", state, pending);
      break;
	default:
      break;
  }
  return TRUE;
}



int
main (int argc, char *argv[])
{
  AppData *app = NULL;
  gchar *string = NULL;
  GstBus *bus = NULL;
  GstElement *appsrc = NULL;

  gst_init (&argc, &argv);

  app = g_new0 (AppData, 1);

  app->loop = g_main_loop_new (NULL, FALSE);

  // setting up pipeline, we push video data into this pipeline that will
  // then be recorded to an avi file, encoded with the h.264 codec
  string =
      g_strdup_printf ("appsrc is-live=true name=source caps=\"%s\" ! ffmpegcolorspace ! video/x-raw-yuv,format=(fourcc)I420,width=80,height=60 ! queue ! videorate ! video/x-raw-yuv,framerate=30/1 ! h264enc ! queue ! avimux ! queue ! filesink location=test.avi",
      video_caps);
  app->pipeline = gst_parse_launch (string, NULL);
  g_free (string);

  if (app->pipeline == NULL) {
    g_print ("Bad pipeline\n");
    return -1;
  }

  appsrc = gst_bin_get_by_name (GST_BIN (app->pipeline), "source");
  // configure for time-based format
  g_object_set (appsrc, "format", GST_FORMAT_TIME, NULL);
  // setting maximum of bytes queued. default is 200000
  gst_app_src_set_max_bytes((GstAppSrc *)appsrc, QUEUED_FRAMES * BUFFER_SIZE);
  // uncomment the next line to block when appsrc has buffered enough
  // g_object_set (appsrc, "block", TRUE, NULL);
  app->source = appsrc;

  // add watch for messages
  bus = gst_element_get_bus (app->pipeline);
  gst_bus_add_watch (bus, (GstBusFunc) on_pipeline_message, app);
  gst_object_unref (bus);

  // configure the appsrc, we will push data into the appsrc from the
  // mainloop
  g_signal_connect (app->source, "need-data", G_CALLBACK (start_feed), app);
  g_signal_connect (app->source, "enough-data", G_CALLBACK (stop_feed), app);

  // go to playing and wait in a mainloop
  gst_element_set_state (app->pipeline, GST_STATE_PLAYING);

  // this mainloop is stopped when we receive an error or EOS
  g_print ("Creating movie...\n");
  g_main_loop_run (app->loop);
  g_print ("Done.\n");

  gst_app_src_end_of_stream (GST_APP_SRC (app->source));

  gst_element_set_state (app->pipeline, GST_STATE_NULL);

  // Cleaning up
  gst_object_unref (app->source);
  gst_object_unref (app->pipeline);
  g_main_loop_unref (app->loop);
  g_free (app);

  return 0;
}
    
     

}
*/
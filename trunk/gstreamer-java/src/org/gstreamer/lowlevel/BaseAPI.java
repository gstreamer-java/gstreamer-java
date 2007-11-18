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

package org.gstreamer.lowlevel;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;
import java.util.HashMap;
import org.gstreamer.ActivateMode;
import org.gstreamer.BaseSink;
import org.gstreamer.BaseSrc;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.Event;
import org.gstreamer.FlowReturn;
import org.gstreamer.Pad;
import org.gstreamer.lowlevel.GstAPI.*;

public interface BaseAPI extends Library {
    static BaseAPI INSTANCE = (BaseAPI) GNative.loadLibrary("gstbase-0.10", 
            BaseAPI.class, new HashMap<String, Object>() {{
        put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
    }});
    public static final int GST_PADDING = GstAPI.GST_PADDING;
    public static final int GST_PADDING_LARGE = GstAPI.GST_PADDING_LARGE;
    
    GType gst_base_src_get_type();
    GType gst_base_sink_get_type();
    public final static class GstBaseSrcStruct extends com.sun.jna.Structure {
        public GstElementStruct element;

        /*< protected >*/
        public volatile Pad srcpad;

        /* available to subclass implementations */
        /* MT-protected (with LIVE_LOCK) */
        public volatile /* GMutex */ Pointer live_lock;
        public volatile /* GCond */ Pointer live_cond;
        public volatile boolean is_live;
        public volatile boolean live_running;

        /* MT-protected (with LOCK) */
        public volatile int blocksize;	/* size of buffers when operating push based */
        public volatile boolean can_activate_push;	/* some scheduling properties */
        public volatile ActivateMode pad_mode;
        public volatile boolean seekable;
        public volatile boolean random_access;

        public volatile /* GstClockID */ Pointer clock_id;	/* for syncing */
        public volatile /* GstClockTime */ long  end_time;

        /* MT-protected (with STREAM_LOCK) */
        public volatile GstSegmentStruct segment;
        public volatile boolean	 need_newsegment;

        public volatile /* guint64 */ long offset;	/* current offset in the resource, unused */
        public volatile /* guint64 */ long size;        /* total size of the resource, unused */

        public volatile int num_buffers;
        public volatile int num_buffers_left;

        /*< private >*/        
        /*
        union {
            struct {
              // FIXME: those fields should be moved into the private struct
              boolean  typefind;
              boolean  running;
              GstEvent *pending_seek;
            } ABI;
            gpointer       _gst_reserved[GST_PADDING_LARGE-1];
        } data;
        */
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * (GST_PADDING_LARGE - 1)];
        public volatile /* GstBaseSrcPrivate */ Pointer priv;
    }
    //
    // Callbacks for BaseSrc/BaseSink classes
    //
    public static interface GetCaps extends Callback {
        public Caps callback(Element element);
    }
    public static interface SetCaps extends Callback {
        public boolean callback(Element element, Caps caps);
    }
    public static interface BooleanFunc1 extends Callback {
        public boolean callback(Element element);
    }

    public static interface GetTimes extends Callback {
        public void callback(Element src, Buffer buffer, 
                LongByReference start, LongByReference end);
    }
    public static interface GetSize extends Callback {
        boolean callback(BaseSrc element, LongByReference size);
    }
    public static interface EventNotify extends Callback {
        boolean callback(Element src, Event event);
    }
    public static interface Create extends Callback {
        public FlowReturn callback(BaseSrc src, long offset, int size,
                /* GstBuffer ** */ Pointer bufRef);
    }
    public static interface BufferAlloc extends Callback {
        public FlowReturn callback(BaseSrc sink, long offset, int size,
                Caps caps, /* GstBuffer ** */ Pointer bufRef);
    }
    public static interface Render extends Callback {
        public FlowReturn callback(BaseSink sink, Buffer buffer);
    }
    
    public static interface Seek extends Callback {
        boolean callback(BaseSrc src, GstSegmentStruct segment);
    }
    public static interface Query extends Callback {
        boolean callback(BaseSrc src, Query query);            
    }

    public static interface Fixate extends Callback {
        public void callback(Element element, Caps caps);
    }
    public final static class GstBaseSrcClass extends com.sun.jna.Structure {
        public GstBaseSrcClass() {}
        public GstBaseSrcClass(Pointer ptr) {
            useMemory(ptr);
            read();
        }
        
        //
        // Actual data members
        //
        public GstElementClass parent_class;
        /*< public >*/
        /* virtual methods for subclasses */

        /* get caps from subclass */
        public GetCaps get_caps;
        /* notify the subclass of new caps */
        public SetCaps set_caps;
        /* decide on caps */
        public BooleanFunc1 negotiate;
        /* generate and send a newsegment (UNUSED) */
        public volatile Pointer newsegment;
  
  
        /* start and stop processing, ideal for opening/closing the resource */
        public BooleanFunc1 start;
        public BooleanFunc1 stop;
  
        /* 
         * Given a buffer, return start and stop time when it should be pushed
         * out. The base class will sync on the clock using these times. 
         */
        public GetTimes get_times;
  
        /* get the total size of the resource in bytes */
        public GetSize get_size;

        /* check if the resource is seekable */
        public BooleanFunc1 is_seekable;

        /* unlock any pending access to the resource. subclasses should unlock
        * any function ASAP. */
        public BooleanFunc1 unlock;
 
        
        /* notify subclasses of an event */
        public EventNotify event;

        /* ask the subclass to create a buffer with offset and size */
        public Create create;
  
        /* additions that change padding... */
        /* notify subclasses of a seek */
        public Seek seek;
        /* notify subclasses of a query */
        public Query query;
        
        /* check whether the source would support pull-based operation if
        * it were to be opened now. This vfunc is optional, but should be
        * implemented if possible to avoid unnecessary start/stop cycles.
        * The default implementation will open and close the resource to
        * find out whether get_range is supported and that is usually
        * undesirable. */
        public BooleanFunc1 check_get_range;        

        /* called if, in negotation, caps need fixating */
        public Fixate fixate;        

        /*< private >*/
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * (GST_PADDING_LARGE - 4)];
    }
    
    public final static class GstBaseSinkStruct extends com.sun.jna.Structure {
        public GstElementStruct element;
        
        /*< protected >*/
        public volatile Pad sinkpad;
        public volatile ActivateMode pad_mode;

        /*< protected >*/ /* with LOCK */
        public volatile long offset;
        public volatile boolean can_activate_pull;
        public volatile boolean can_activate_push;

        /*< protected >*/ /* with PREROLL_LOCK */
        public volatile Pointer /*GQueue */ preroll_queue;
        public volatile int preroll_queue_max_len;
        public volatile int preroll_queued;
        public volatile int buffers_queued;
        public volatile int events_queued;
        public volatile boolean eos;
        public volatile boolean eos_queued;
        public volatile boolean need_preroll;
        public volatile boolean have_preroll;
        public volatile boolean playing_async;

        /*< protected >*/ /* with STREAM_LOCK */
        public volatile boolean have_newsegment;
        public volatile GstSegmentStruct segment;

        /*< private >*/ /* with LOCK */
        public volatile Pointer /* GstClockID */ clock_id;
        public volatile long /* GstClockTime */  end_time;
        public volatile boolean sync;
        public volatile boolean flushing;

        /*< private >*/
        /*
        union {
            struct {
              // segment used for clipping incomming buffers
              GstSegment    *clip_segment;
              // max amount of time a buffer can be late, -1 no limit. 
              int64	     max_lateness;
            } ABI;
            gpointer _gst_reserved[GST_PADDING_LARGE - 1];
        } abidata;
        */
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * (GST_PADDING_LARGE - 1)];
        public volatile Pointer /* GstBaseSinkPrivate */ priv;
    }
    
    public final static class GstBaseSinkClass extends com.sun.jna.Structure {
        public GstBaseSinkClass() {}
        public GstBaseSinkClass(Pointer ptr) {
            useMemory(ptr);
            read();
        }
        
        //
        // Actual data members
        //
        public GstElementClass parent_class;
        /* get caps from subclass */
        public GetCaps get_caps;

        /* notify subclass of new caps */
        public SetCaps set_caps;        

        /* allocate a new buffer with given caps */
        public BufferAlloc buffer_alloc;
  
        /* get the start and end times for syncing on this buffer */
        public GetTimes get_times;
  
        /* start and stop processing, ideal for opening/closing the resource */
        public BooleanFunc1 start;
        public BooleanFunc1 stop;
  
        /* 
         * unlock any pending access to the resource. subclasses should unlock
         * any function ASAP. 
         */
        public BooleanFunc1 unlock;
  

        /* notify subclass of event, preroll buffer or real buffer */
        public EventNotify event;
        
        public Render preroll;
        public Render render;
 
        /* ABI additions */

        /* when an ASYNC state change to PLAYING happens */ /* with LOCK */
        //GstStateChangeReturn (*async_play)   (GstBaseSink *sink);
        public volatile Pointer async_play;

        /* start or stop a pulling thread */
        //boolean (*activate_pull)(GstBaseSink *sink, gboolean active);
        public volatile Pointer activate_pull;

        /* fixate sink caps during pull-mode negotiation */
        public Fixate fixate;

        /*< private >*/
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * (GST_PADDING_LARGE-3)];
    }
}

/* 
 * Copyright (c) 2009 Levente Farkas
 * Copyright (c) 2007, 2008 Wayne Meissner
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

package org.gstreamer.lowlevel;

import java.util.Arrays;
import java.util.List;

import org.gstreamer.Buffer;
import org.gstreamer.BufferCopyFlags;
import org.gstreamer.Caps;
import org.gstreamer.ClockTime;
import org.gstreamer.lowlevel.GstMiniObjectAPI.MiniObjectStruct;
import org.gstreamer.lowlevel.annotations.CallerOwnsReturn;
import org.gstreamer.lowlevel.annotations.Invalidate;

import com.sun.jna.Pointer;

/**
 * GstBuffer functions
 */
public interface GstBufferAPI extends com.sun.jna.Library {
    GstBufferAPI GSTBUFFER_API = GstNative.load(GstBufferAPI.class);

    GType gst_buffer_get_type();
    @CallerOwnsReturn Buffer gst_buffer_new();
    @CallerOwnsReturn Buffer gst_buffer_new_and_alloc(int size);
    @CallerOwnsReturn Buffer gst_buffer_try_new_and_alloc(int size);
    @CallerOwnsReturn Buffer gst_buffer_copy(Buffer buf);
    void gst_buffer_copy_metadata (Buffer dest, Buffer src, BufferCopyFlags flags);
    boolean gst_buffer_is_metadata_writable(Buffer buf);
    Buffer gst_buffer_make_metadata_writable(@Invalidate Buffer buf);
    /* creating a subbuffer */
    @CallerOwnsReturn Buffer gst_buffer_create_sub(Buffer parent, int offset, int size);
    
    @CallerOwnsReturn Caps gst_buffer_get_caps(Buffer buffer);
    void gst_buffer_set_caps(Buffer buffer, Caps caps);
    /* span two buffers intelligently */
    boolean gst_buffer_is_span_fast(Buffer buf1, Buffer buf2);
    @CallerOwnsReturn Buffer gst_buffer_span(Buffer buf1, int offset, Buffer buf2, int len);
    /* buffer functions from gstutils.h */
    @CallerOwnsReturn Buffer gst_buffer_merge(Buffer buf1, Buffer buf2);
    @CallerOwnsReturn Buffer gst_buffer_join(@Invalidate Buffer buf1, @Invalidate Buffer buf2);
    
    public static final class BufferStruct extends com.sun.jna.Structure {
        volatile public MiniObjectStruct mini_object;
        public Pointer data;
        public int size;
        public ClockTime timestamp;
        public ClockTime duration;
        public Pointer caps;
        public long offset;
        public long offset_end;
        public Pointer malloc_data;
        public BufferStruct(Pointer ptr) {
            useMemory(ptr);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{
                "mini_object", "data", "size",
                "timestamp", "duration", "caps",
                "offset", "offset_end", "malloc_data"
            });
        }
    }
}

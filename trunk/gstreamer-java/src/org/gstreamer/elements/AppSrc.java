/*
 * Copyright (c) 2009 Andres Colubri
 * Copyright (c) 2008 Wayne Meissner
 * Copyright (C) 2007 David Schleef <ds@schleef.org>
 *           (C) 2008 Wim Taymans <wim.taymans@gmail.com>
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

import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.lowlevel.AppAPI;
import org.gstreamer.lowlevel.GstAPI.GstCallback;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;

/**
 * Enables an application to feed buffers into a pipeline.
 */
public class AppSrc extends BaseSrc {
    private static final AppAPI gst() { return AppAPI.APP_API; }
    
    public enum Type {
        STREAM,
        SEEKABLE,
        RANDOM_ACCESS;
    }
     
    public AppSrc(Initializer init) {
        super(init);
    }
   
    @Override
    public void setCaps(Caps caps) {
        gst().gst_app_src_set_caps(this, caps);
    }
    public Caps getCaps() {
        return gst().gst_app_src_get_caps(this);
    }

    public void setSize(long size) {
        gst().gst_app_src_set_size(this, size);
    }
    public long getSize() {
        return gst().gst_app_src_get_size(this);
    }

    public void setStreamType(AppSrc.Type type) {
        gst().gst_app_src_set_stream_type(this, type);
    }
    AppSrc.Type getStreamType(AppSrc.Type type) {
        return gst().gst_app_src_get_stream_type(this);
    }

    public void setMaxBytes(long max) {
        gst().gst_app_src_set_max_bytes(this, max);
    }
    public long getMaxBytes() {
        return gst().gst_app_src_get_max_bytes(this);
    }

    public void setLatency(long min, long max) {
        gst().gst_app_src_set_latency(this, min, max);
    }
    public void getLatency(long[] minmax) {
        LongByReference minRef = new LongByReference();
        LongByReference maxRef = new LongByReference();
        gst().gst_app_src_get_latency(this, minRef, minRef);
        if ((minmax == null) || (minmax.length != 2)) minmax = new long[2];
        minmax[0] = minRef.getValue();
        minmax[1] = maxRef.getValue();
    }

    public void pushBuffer(Buffer buffer) {
        gst().gst_app_src_push_buffer(this, buffer);
    }
    public void endOfStream() {
        gst().gst_app_src_end_of_stream(this);
    }

    /**
     * Signal emitted when this {@link AppSrc} needs data.
     */
    public static interface NEED_DATA {
        /**
         *
         * @param elem
         * @param size
         * @param userData
         */
        public void needData(Element elem, int size, Pointer userData);
    }

    /**
     * Adds a listener for the <code>need-data</code> signal
     *
     * @param listener Listener to be called when appsrc needs data.
     */
    public void connect(final NEED_DATA listener) {
        connect("need-data", NEED_DATA.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, int size, Pointer userData) {
                listener.needData(elem, size, userData);
            }
        });
    }

    /**
     * Removes a listener for the <code>need-data</code> signal
     *
     * @param listener The listener that was previously added.
     */
    public void disconnect(NEED_DATA listener) {
        disconnect(NEED_DATA.class, listener);
    }

    /**
     * Signal emitted when this {@link AppSrc} has enough data in the queue.
     */
    public static interface ENOUGH_DATA {
        /**
         *
         * @param elem
         * @param userData
         */
        public void enoughData(Element elem, Pointer userData);
    }

    /**
     * Adds a listener for the <code>enough-data</code> signal
     *
     * @param listener Listener to be called this when appsrc fills its queue.
     */
    public void connect(final ENOUGH_DATA listener) {
        connect("enough-data", ENOUGH_DATA.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, Pointer userData) {
                listener.enoughData(elem, userData);
            }
        });
    }

    /**
     * Removes a listener for the <code>enough-data</code> signal
     *
     * @param listener The listener that was previously added.
     */
    public void disconnect(ENOUGH_DATA listener) {
        disconnect(ENOUGH_DATA.class, listener);
    }

    /**
     * Signal emitted when this {@link AppSrc} when it requires the application
     * to push buffers from a specific location in the input stream.
     */
    public static interface SEEK_DATA {
        /**
         *
         * @param elem
         * @param position
         * @param userData
         */
        public void seekData(Element elem, long position, Pointer userData);
    }

    /**
     * Adds a listener for the <code>seek-data</code> signal
     *
     * @param listener Listener to be called when appsrc when its "stream-mode"
     * property is set to "seekable" or "random-access". The signal argument
     * will contain the new desired position in the stream expressed in the unit
     * set with the "format" property. After receiving the seek-data signal,
     * the application should push-buffers from the new position.
     */
    public void connect(final SEEK_DATA listener) {
        connect("seek-data", SEEK_DATA.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, long position, Pointer userData) {
                listener.seekData(elem, position, userData);
            }
        });
    }

    /**
     * Removes a listener for the <code>seek-data</code> signal
     *
     * @param listener The listener that was previously added.
     */
    public void disconnect(SEEK_DATA listener) {
        disconnect(SEEK_DATA.class, listener);
    }
}

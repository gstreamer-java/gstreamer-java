/*
 * Copyright (c) 2009 Levente Farkas
 * Copyright (c) 2009 Wayne Meissner
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

import com.sun.jna.Pointer;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.lowlevel.AppAPI;
import org.gstreamer.lowlevel.GstAPI.GstCallback;

/**
 * A sink {@link org.gstreamer.Element} that enables an application to pull data
 * from a pipeline.
 */
public class AppSink extends BaseSink {
    private static final AppAPI gst() { return AppAPI.APP_API; }

    public AppSink(Initializer init) {
        super(init);
    }

    /**
     * Sets the capabilities on the appsink element.
     * <p>
     * After calling this method, the sink will only accept caps that match <tt>caps</tt>.
     * If <tt>caps</tt> is non-fixed, you must check the caps on the buffers to 
     * get the actual used caps. 
     * 
     * @param caps The <tt>Caps</tt> to set.
     */
    @Override
    public void setCaps(Caps caps) {
        gst().gst_app_sink_set_caps(this, caps);
    }

    /**
     * Gets the <tt>Caps</tt> configured on this <tt>AppSink</tt>
     *
     * @return The caps configured on this <tt>AppSink</tt>
     */
    public Caps getCaps() {
        return gst().gst_app_sink_get_caps(this);
    }

    /**
     * Checks if this <tt>AppSink</tt> is end-of-stream.
     * <p>
     * If an EOS event has been received, no more buffers can be pulled.
     * 
     * @return <tt>true</tt> if no more buffers can be pulled and this 
     * <tt>AppSink</tt> is EOS.
     */
    public boolean isEOS() {
        return gst().gst_app_sink_is_eos(this);
    }

    /**
     * Get the last preroll buffer in this <tt>AppSink</tt>.
     * <p>
     * This was the buffer that caused the appsink to preroll in the PAUSED state.
     * This buffer can be pulled many times and remains available to the application 
     * even after EOS.
     * <p>
     * This function is typically used when dealing with a pipeline in the PAUSED
     * state. Calling this function after doing a seek will give the buffer right
     * after the seek position.
     * <p>
     * Note that the preroll buffer will also be returned as the first buffer
     * when calling {@link #pullBuffer}.
     * <p>
     * If an EOS event was received before any buffers, this function returns
     * <tt>null</tt>. Use {@link #isEOS} to check for the EOS condition.
     * <p>
     * This function blocks until a preroll buffer or EOS is received or the appsink
     * element is set to the READY/NULL state. 
     *
     * @return A {@link Buffer} or <tt>null</tt> when the appsink is stopped or EOS.
     */
    public Buffer pullPreroll() {
        return gst().gst_app_sink_pull_preroll(this);
    }

    /**
     * Pulls a {@link org.gstreamer.Buffer} from the <tt>AppSink</tt>.
     * <p>
     * This function blocks until a buffer or EOS becomes available or the appsink
     * element is set to the READY/NULL state. 
     * <p>
      * This function will only return buffers when the appsink is in the PLAYING
     * state. All rendered buffers will be put in a queue so that the application
     * can pull buffers at its own rate. Note that when the application does not
     * pull buffers fast enough, the queued buffers could consume a lot of memory,
     * especially when dealing with raw video frames.
     * <p>
     * If an EOS event was received before any buffers, this function returns
     * <tt>null</tt>. Use {@link #isEOS} to check for the EOS condition. 
     *
     * Returns: a #GstBuffer or NULL when the appsink is stopped or EOS.
     * @return A {@link org.gstreamer.Buffer} or NULL when the appsink is stopped or EOS. 
     */
    public Buffer pullBuffer() {
        return gst().gst_app_sink_pull_buffer(this);
    }

    /**
     * Signal emitted when this {@link AppSink} when a new buffer is ready.
     */
    public static interface NEW_BUFFER {
        /**
         *
         * @param elem
         * @param userData
         */
        public void newBuffer(Element elem, Pointer userData);
    }

    /**
     * Adds a listener for the <code>new-buffer</code> signal. If a blocking
     * behaviour is not desirable, setting the "emit-signals" property to TRUE
     * will make appsink emit the "new-buffer" and "new-preroll" signals when a
     * buffer can be pulled without blocking.
     *
     * @param listener
     */
    public void connect(final NEW_BUFFER listener) {
        connect("new-buffer", NEW_BUFFER.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, Pointer userData) {
                listener.newBuffer(elem, userData);
            }
        });
    }

    /**
     * Removes a listener for the <code>new-buffer</code> signal
     *
     * @param listener The listener that was previously added.
     */
    public void disconnect(NEW_BUFFER listener) {
        disconnect(NEW_BUFFER.class, listener);
    }
    
    /**
     * Signal emitted when this {@link AppSink} when a new buffer is ready.
     */
    public static interface NEW_PREROLL {
        /**
         *
         * @param elem
         * @param userData
         */
        public void newPreroll(Element elem, Pointer userData);
    }

    /**
     * Adds a listener for the <code>new-preroll</code> signal. If a blocking
     * behaviour is not desirable, setting the "emit-signals" property to TRUE
     * will make appsink emit the "new-buffer" and "new-preroll" signals when a
     * buffer can be pulled without blocking.
     *
     * @param listener
     */
    public void connect(final NEW_PREROLL listener) {
        connect("new-preroll", NEW_PREROLL.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, Pointer userData) {
                listener.newPreroll(elem, userData);
            }
        });
    }

    /**
     * Removes a listener for the <code>new-buffer</code> signal
     *
     * @param listener The listener that was previously added.
     */
    public void disconnect(NEW_PREROLL listener) {
        disconnect(NEW_PREROLL.class, listener);
    }
}

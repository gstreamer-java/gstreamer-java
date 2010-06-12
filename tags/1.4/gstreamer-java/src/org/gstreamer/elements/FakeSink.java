/*
 * Copyright (c) 2008 Wayne Meissner
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
import org.gstreamer.Pad;
import org.gstreamer.lowlevel.GstAPI;

/**
 *
 */
public class FakeSink extends BaseSink {
    public FakeSink(String name) {
        this(makeRawElement("fakesink", name));
    }

    public FakeSink(Initializer init) {
        super(init);
    }

    /**
     * Signal emitted when this {@link FakeSink} has a {@link Buffer} ready.
     *
     * @see #connect(PREROLL_HANDOFF)
     * @see #disconnect(PREROLL_HANDOFF)
     */
    public static interface PREROLL_HANDOFF {
        /**
         * Called when a {@link FakeSink} has a {@link Buffer} ready.
         *
         * @param fakesink the fakesink instance.
         * @param buffer the buffer that just has been received.
         * @param pad the pad that received it.
         * @param user_data user data set when the signal handler was connected.
         */
        public void prerollHandoff(FakeSink fakesink, Buffer buffer, Pad pad, Pointer user_data);
    }

    /**
     * Add a listener for the <code>preroll-handoff</code> signal.
     *
     * @param listener The listener to be called when a {@link Buffer} is ready.
     */
    public void connect(final PREROLL_HANDOFF listener) {
        connect("preroll-handoff", PREROLL_HANDOFF.class, listener, new GstAPI.GstCallback() {
            @SuppressWarnings("unused")
            public void callback(FakeSink fakesink, Buffer buffer, Pad pad, Pointer user_data) {
                listener.prerollHandoff(fakesink, buffer, pad, user_data);
            }
        });
    }

    /**
     * Remove a listener for the <code>preroll-handoff</code> signal.
     *
     * @param listener The listener that was previously added.
     */
    public void disconnect(PREROLL_HANDOFF listener) {
        disconnect(PREROLL_HANDOFF.class, listener);
    }
}

/* 
 * Copyright (c) 2007 Wayne Meissner
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

import org.gstreamer.Bin;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.Pad;
import org.gstreamer.lowlevel.GstAPI.GstCallback;

/**
 * Utility {@link Element} to automatically identify media stream types and hook
 * up elements.
 * 
 * @deprecated This element is deprecated and no longer supported. You should use the uridecodebin or decodebin2 element instead (or, even better: playbin2).
 */
@Deprecated
public class DecodeBin extends Bin {
    public static final String GST_NAME = "decodebin";
    public static final String GTYPE_NAME = "GstDecodeBin";

    /**
     * Creates a new DecodeBin.
     * 
     * @param name The name used to identify this DecodeBin.
     */
    public DecodeBin(String name) {
        this(makeRawElement(GST_NAME, name));
    }
    public DecodeBin(Initializer init) {
        super(init);
    }

    /**
     * Signal emitted when this {@link DecodeBin} decodes a new pad.
     */
    public static interface NEW_DECODED_PAD {
        /**
         * 
         * @param element The element which has the new Pad.
         * @param pad the new Pad.
         * @param last (unknown)
         */
        public void newDecodedPad(DecodeBin element, Pad pad, boolean last);
    }
    /**
     * Adds a listener for the <code>new-decoded-pad</code> signal
     * 
     * @param listener Listener to be called when a new {@link Pad} is encountered
     * on the {@link Element}
     */
    public void connect(final NEW_DECODED_PAD listener) {
        connect(NEW_DECODED_PAD.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(DecodeBin elem, Pad pad, boolean last) {
                listener.newDecodedPad(elem, pad, last);
            }
        });
    }
    /**
     * Removes a listener for the <code>new-decoded-pad</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(NEW_DECODED_PAD listener) {
        disconnect(NEW_DECODED_PAD.class, listener);
    }
    

    /**
     * Signal emitted when this {@link DecodeBin} decodes a removed pad.
     */
    public static interface REMOVED_DECODED_PAD {
        /**
         * 
         * @param element The element which has the new Pad.
         * @param pad the new Pad.
         */
        public void removedDecodedPad(DecodeBin element, Pad pad);
    }
    /**
     * Adds a listener for the <code>removed-decoded-pad</code> signal
     * 
     * @param listener Listener to be called when a new {@link Pad} is encountered
     * on the {@link Element}
     */
    public void connect(final REMOVED_DECODED_PAD listener) {
        connect(REMOVED_DECODED_PAD.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(DecodeBin elem, Pad pad) {
                listener.removedDecodedPad(elem, pad);
            }
        });
    }
    /**
     * Removes a listener for the <code>removed-decoded-pad</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(REMOVED_DECODED_PAD listener) {
        disconnect(REMOVED_DECODED_PAD.class, listener);
    }

    /**
     * Signal is emitted when a pad for which there is no further possible decoding is added to the {@link DecodeBin}.
     */
    public static interface UNKNOWN_TYPE {
        /**
         * 
         * @param element The element which has the new Pad.
         * @param pad the new Pad.
         * @param caps the caps of the pad that cannot be resolved.
         */
        public void unknownType(DecodeBin element, Pad pad, Caps caps);
    }
    /**
     * Adds a listener for the <code>unknown-type</code> signal
     * 
     * @param listener Listener to be called when a new {@link Pad} is encountered
     * on the {@link Element}
     */
    public void connect(final UNKNOWN_TYPE listener) {
        connect(UNKNOWN_TYPE.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(DecodeBin elem, Pad pad, Caps caps) {
                listener.unknownType(elem, pad, caps);
            }
        });
    }
    /**
     * Removes a listener for the <code>unknown-type</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(UNKNOWN_TYPE listener) {
        disconnect(UNKNOWN_TYPE.class, listener);
    }
}

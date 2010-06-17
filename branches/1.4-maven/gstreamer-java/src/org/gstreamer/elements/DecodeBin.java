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
import org.gstreamer.Element;
import org.gstreamer.Pad;
import org.gstreamer.lowlevel.GstAPI.GstCallback;

import com.sun.jna.Pointer;

/**
 * Utility {@link Element} to automatically identify media stream types and hook
 * up elements.
 */
public class DecodeBin extends Bin {
    /**
     * Creates a new DecodeBin.
     * 
     * @param name The name used to identify this DecodeBin.
     */
    public DecodeBin(String name) {
        this(makeRawElement("decodebin", name));
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
        public void newDecodedPad(Element element, Pad pad, boolean last);
    }
    
    /**
     * Adds a listener for the <code>new-decoded-pad</code> signal
     * 
     * @param listener Listener to be called when a new {@link Pad} is encountered
     * on the {@link Element}
     */
    public void connect(final NEW_DECODED_PAD listener) {
        connect("new-decoded-pad", NEW_DECODED_PAD.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, Pad pad, boolean last, Pointer user_data) {
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
    
}

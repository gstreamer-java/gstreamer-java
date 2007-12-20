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

package org.gstreamer.elements;

import com.sun.jna.Pointer;
import org.gstreamer.Bin;
import org.gstreamer.Element;
import org.gstreamer.Pad;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 * Utility {@link Element} to automatically identify media stream types and hook
 * up elements.
 */
public class DecodeBin extends Bin {
    public DecodeBin(String name) {
        super(gst.gst_element_factory_make("decodebin", name));
    }
    
    @Deprecated
    public DecodeBin(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public static interface NEW_DECODED_PAD {
        public void newDecodedPad(Element element, Pad pad, boolean last);
    }
    
    /**
     * Add a listener for the <code>new-decoded-pad</code> signal
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
     * Remove a listener for the <code>new-decoded-pad</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(NEW_DECODED_PAD listener) {
        disconnect(NEW_DECODED_PAD.class, listener);
    }
    
}

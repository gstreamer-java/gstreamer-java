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
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 * Utility {@link Element} to identify media types in the stream.
 */
public class TypeFind extends Element {
    public TypeFind(String name) {
        super("typefind", name);
    }
    
    @Deprecated /* Only used internally */
    public TypeFind(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    
    /**
     * Signal emitted when a new media type is identified at the {@link TypeFind} element.
     */
    public static interface HAVE_TYPE {
        void typeFound(Element elem, int probability, Caps caps);
    }
    @Deprecated public static interface HAVETYPE extends HAVE_TYPE {}
    
    /**
     * Add a listener for the <code>have-type</code> signal.
     * 
     * @param listener Listener to be called when a new type is discovered.
     */
    public void connect(final HAVE_TYPE listener) {
        connect("have-type", HAVE_TYPE.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, int probability, Caps caps, Pointer user_data) {
                listener.typeFound(elem, probability, caps);
            }
        });
    }
    /**
     * Remove a listener for the <code>have-type</code> signal.
     * 
     * @param listener The previously added listener for the signal.
     */
    public void disconnect(HAVE_TYPE listener) {
        disconnect(HAVE_TYPE.class, listener);
    }
    
}

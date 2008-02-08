/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
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
public final class TypeFind extends Element {
    public TypeFind(String name) {
        this(makeRawElement("typefind", name));
    }
    
    public TypeFind(Initializer init) {
        super(init);
    }
    
    /**
     * Signal emitted when a new media type is identified at the {@link TypeFind} element.
     */
    public static interface HAVE_TYPE {
        void typeFound(Element elem, int probability, Caps caps);
    }
    
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
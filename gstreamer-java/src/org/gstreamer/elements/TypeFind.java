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
import org.gstreamer.lowlevel.GstAPI.HaveTypeCallback;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 * @author wayne
 */
public class TypeFind extends Element {
    public TypeFind(String name) {
        super("typefind", name);
    }
    public static interface TYPEFIND {
        void typeFound(Element elem, int probability, Caps caps);
    }
    public void connect(final TYPEFIND listener) {
        connect("have-type", TYPEFIND.class, listener, new HaveTypeCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, int probability, Caps caps, Pointer user_data) {
                listener.typeFound(elem, probability, caps);
            }
        });
    }
    public void disconnect(TYPEFIND listener) {
        disconnect(TYPEFIND.class, listener);
    }
    
}

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

package org.gstreamer;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import static org.gstreamer.lowlevel.GstAPI.gst;
import static org.gstreamer.lowlevel.GlibAPI.glib;

/**
 *
 */
public class Pad extends GstObject {

    /**
     * Creates a new instance of Pad
     */
    
    Pad(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    
    protected Pad(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    
    public static Pad objectFor(Pointer ptr, boolean needRef) {
        return GstObject.objectFor(ptr, Pad.class, needRef);
    }
    
    public Caps getCaps() {
        return gst.gst_pad_get_caps(this);
    }
    public boolean setCaps(Caps caps) {
        return gst.gst_pad_set_caps(this, caps);
    }
    public boolean peerAcceptCaps(Caps caps) {
        return gst.gst_pad_peer_accept_caps(this, caps);
    }
    
    public PadLinkReturn link(Pad sink) {
        return gst.gst_pad_link(this, sink);
    }
    public boolean unlink(Pad sink) {
        return gst.gst_pad_unlink(this, sink);
    }
    public boolean isLinked() {
        return gst.gst_pad_is_linked(this);
    }
    public PadDirection getDirection() {
        return gst.gst_pad_get_direction(this);
    }
    public Element getParentElement() {
        return gst.gst_pad_get_parent_element(this);
    }
    public static interface HAVEDATA {
        public boolean haveData(Pad pad, Buffer buffer);
    }
    public static interface LINKED {
        public void linked(Pad pad, Pad peer);
    }
    public static interface UNLINKED {
        public void unlinked(Pad pad, Pad peer);
    }
    public static interface REQUESTLINK {
        public void requestLink(Pad pad);
    }
    public void connect(final HAVEDATA listener) {
        connect("have-data", HAVEDATA.class, listener, new Callback() {
            @SuppressWarnings("unused")
			public boolean callback(Pointer pad, Pointer buffer) {
                return listener.haveData(objectFor(pad, true), new Buffer(buffer, true));
            }
        });
    }
    public void connect(final LINKED listener) {
        connect("linked", LINKED.class, listener, new Callback() {
            @SuppressWarnings("unused")
			public void callback(Pointer pad, Pointer peer) {
                listener.linked(objectFor(pad,true),objectFor(peer, true));
            }
        });
    }
    public void disconnect(LINKED listener) {
        disconnect(LINKED.class, listener);
    }
    
    public void connect(final UNLINKED listener) {
        connect("unlinked", LINKED.class, listener, new Callback() {
            @SuppressWarnings("unused")
			public void callback(Pointer pad, Pointer peer) {
                listener.unlinked(objectFor(pad,true),objectFor(peer, true));
            }
        });
    }
    public void disconnect(UNLINKED listener) {
        disconnect(UNLINKED.class, listener);
    }
    
    
    public void connect(final REQUESTLINK listener) {
        connect("request-link", REQUESTLINK.class, listener,new Callback() {
            @SuppressWarnings("unused")
			public void callback(Pointer pad, Pointer peer) {
                listener.requestLink(objectFor(pad, true));
            }
        });
    }
    public void disconnect(REQUESTLINK listener) {
        disconnect(REQUESTLINK.class, listener);
    }
}

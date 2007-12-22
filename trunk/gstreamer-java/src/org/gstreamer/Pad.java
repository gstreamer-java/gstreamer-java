/* 
 * Copyright (c) 2007 Wayne Meissner
 *
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
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import static org.gstreamer.lowlevel.GstAPI.gst;

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
    /**
     * Signal emitted when new data is available on the {@link Pad}
     */
    public static interface HAVE_DATA {
        public boolean haveData(Pad pad, Buffer buffer);
    }
    
    /**
     * Signal emitted when new this {@link Pad} is linked to another {@link Pad}
     */
    public static interface LINKED {
        public void linked(Pad pad, Pad peer);
    }
    
    /**
     * Signal emitted when new this {@link Pad} is disconnected from a peer {@link Pad}
     */
    public static interface UNLINKED {
        public void unlinked(Pad pad, Pad peer);
    }
    
    /**
     * Signal emitted when a connection to a peer {@link Pad} is requested.
     */
    public static interface REQUEST_LINK {
        public void requestLink(Pad pad, Pad peer);
    }
    
    @Deprecated
    public static interface HAVEDATA extends HAVE_DATA {}
    @Deprecated
    public static interface REQUESTLINK extends REQUEST_LINK {}
    
    /**
     * Add a listener for the <code>have-data</code> signal on this @{link Pad}
     * 
     * @param listener The listener to be called when data is available.
     */
    public void connect(final HAVE_DATA listener) {
        connect("have-data", HAVE_DATA.class, listener, new Callback() {
            @SuppressWarnings("unused")
            public boolean callback(Pad pad, Pointer buffer) {
                return listener.haveData(pad, new Buffer(buffer, true));
            }
        });
    }
    
    /**
     * Add a listener for the <code>linked</code> signal on this @{link Pad}
     * 
     * @param listener The listener to be called when a peer {@link Pad} is linked.
     */
    public void connect(final LINKED listener) {
        connect("linked", LINKED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pad pad, Pad peer, Pointer user_data) {
                listener.linked(pad, peer);
            }
        });
    }
    
    /**
     * Remove a listener for the <code>linked</code> signal on this @{link Pad}
     * 
     * @param listener The listener previously added for this signal.
     */
    public void disconnect(LINKED listener) {
        disconnect(LINKED.class, listener);
    }
    
    /**
     * Add a listener for the <code>unlinked</code> signal on this @{link Pad}
     * 
     * @param listener The listener to be called when when a peer {@link Pad} is unlinked.
     */
    public void connect(final UNLINKED listener) {
        connect("unlinked", UNLINKED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pad pad, Pad peer, Pointer user_data) {
                listener.unlinked(pad, peer);
            }
        });
    }
    /**
     * Remove a listener for the <code>unlinked</code> signal on this @{link Pad}
     * 
     * @param listener The listener previously added for this signal.
     */
    public void disconnect(UNLINKED listener) {
        disconnect(UNLINKED.class, listener);
    }
    
    /**
     * Add a listener for the <code>request-link</code> signal on this @{link Pad}
     * 
     * @param listener The listener to be called when a peer {@link Pad} requests
     * to be linked to this one.
     */
    public void connect(final REQUEST_LINK listener) {
        connect("request-link", REQUEST_LINK.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Pad pad, Pad peer, Pointer user_daa) {
                listener.requestLink(pad, peer);
            }
        });
    }
    
    /**
     * Remove a listener for the <code>request-link</code> signal on this @{link Pad}
     * 
     * @param listener The listener previously added for this signal.
     */
    public void disconnect(REQUEST_LINK listener) {
        disconnect(REQUEST_LINK.class, listener);
    }
}

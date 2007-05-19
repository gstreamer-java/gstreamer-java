/*
 * Pad.java
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
    
    public static Pad instanceFor(Pointer ptr, boolean needRef) {
        return (Pad) GstObject.instanceFor(ptr, Pad.class, needRef);
    }
    
    public Caps getCaps() {
        return Caps.instanceFor(gst.gst_pad_get_caps(handle()), false);
    }
    public boolean setCaps(Caps caps) {
        return gst.gst_pad_set_caps(handle(), caps.handle());
    }
    public boolean peerAcceptCaps(Caps caps) {
        return gst.gst_pad_peer_accept_caps(handle(), caps.handle());
    }
    
    public PadLinkReturn link(Pad sink) {
        return PadLinkReturn.valueOf(gst.gst_pad_link(handle(), sink.handle()));
    }
    public boolean unlink(Pad sink) {
        return gst.gst_pad_unlink(handle(), sink.handle());
    }
    public boolean isLinked() {
        return gst.gst_pad_is_linked(handle());
    }
    public PadDirection getDirection() {
        return PadDirection.valueOf(gst.gst_pad_get_direction(handle()));
    }
    public Element getParentElement() {
        return Element.instanceFor(gst.gst_pad_get_parent_element(handle()), false);
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
            public boolean callback(Pointer pad, Pointer buffer) {
                return listener.haveData(instanceFor(pad, true), new Buffer(buffer, true));
            }
        });
    }
    public void connect(final LINKED listener) {
        connect("linked", LINKED.class, listener, new Callback() {
            public void callback(Pointer pad, Pointer peer) {
                listener.linked(instanceFor(pad, true), instanceFor(peer, true));
            }
        });
    }
    public void disconnect(LINKED listener) {
        disconnect(LINKED.class, listener);
    }
    
    public void connect(final UNLINKED listener) {
        connect("unlinked", LINKED.class, listener, new Callback() {
            public void callback(Pointer pad, Pointer peer) {
                listener.unlinked(instanceFor(pad, true), instanceFor(peer, true));
            }
        });
    }
    public void disconnect(UNLINKED listener) {
        disconnect(UNLINKED.class, listener);
    }
    
    
    public void connect(final REQUESTLINK listener) {
        connect("request-link", REQUESTLINK.class, listener, new Callback() {
            public void callback(Pointer pad, Pointer peer) {
                listener.requestLink(instanceFor(pad, true));
            }
        });
    }
    public void disconnect(REQUESTLINK listener) {
        disconnect(REQUESTLINK.class, listener);
    }
}

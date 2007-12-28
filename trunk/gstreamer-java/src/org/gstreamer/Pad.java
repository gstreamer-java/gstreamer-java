/* 
 * Copyright (C) 2007 Wayne Meissner
 * Copyright (C) 1999,2000 Erik Walthinsen <omega@cse.ogi.edu>
 *                    2000 Wim Taymans <wtay@chello.be>
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

package org.gstreamer;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 * Object contained by elements that allows links to other elements.
 * <p>
 * A {@link Element} is linked to other elements via "pads", which are extremely
 * light-weight generic link points.
 * After two pads are retrieved from an element with {@link Element#getPad},
 * the pads can be link with {@link #link}. (For quick links,
 * you can also use {@link Element#link}, which will make the obvious
 * link for you if it's straightforward.)
 * <p>
 * Pads are typically created from a {@link PadTemplate} with {@link #Pad(PadTemplate, String)}.
 * <p>
 * Pads have {@link Caps} attached to it to describe the media type they are
 * capable of dealing with.  {@link #getCaps} and {@link setCaps} are
 * used to manipulate the caps of the pads.
 * Pads created from a pad template cannot set capabilities that are
 * incompatible with the pad template capabilities.
 * <p>
 * Pads without pad templates can be created with gst_pad_new(),
 * which takes a direction and a name as an argument.  If the name is NULL,
 * then a guaranteed unique name will be assigned to it.
 * <p>
 * {@link #getParentElement} will retrieve the Element that owns the pad.
 * <p>
 * An Element creating a pad will typically use the various
 * gst_pad_set_*_function() calls to register callbacks for various events
 * on the pads.
 * <p>
 * GstElements will use gst_pad_push() and gst_pad_pull_range() to push out
 * or pull in a buffer.
 * <p>
 * To send an Event on a pad, use {@link #sendEvent} and {@link #pushEvent}.
 * 
 * @see PadTemplate
 * @see Element
 * @see Event
 */
public class Pad extends GstObject {

    /**
     * Creates a new instance of Pad
     */
    Pad(Initializer init) { 
        super(init); 
    }
    /**
     * Creates a new pad with the given name in the given direction.
     * If name is null, a guaranteed unique name (across all pads)
     * will be assigned.
     * 
     * @param name The name of the new pad.
     * @param direction The direction of the new pad.
     */
    public Pad(String name, PadDirection direction) {
        this(initializer(gst.gst_pad_new(name, direction)));
    }
    
    /**
     * Creates a new pad with the given name from the given template.
     * 
     * If name is null, a guaranteed unique name (across all pads)
     * will be assigned.
     *
     * @param template The pad template to use.
     * @param name The name of the new pad.
     */
    public Pad(PadTemplate template, String name) {
        this(initializer(gst.gst_pad_new_from_template(template, name)));
    }
    
    /**
     * Get the capabilities this pad can produce or consume.
     * 
     * This method returns all possible caps a pad can operate with, using
     * the pad's get_caps function; not just the Caps as set by {@link #setCaps}.
     * 
     * This returns the pad template caps if not explicitly set.
     *
     * MT safe.
     * @return a newly allocated copy of the {@link Caps} of this pad.
     */
    public Caps getCaps() {
        return gst.gst_pad_get_caps(this);
    }
    
    /**
     * Sets the capabilities of this pad. 
     * 
     * The caps must be fixed. Any previous caps on the pad will be destroyed. 
     *
     * It is possible to set null caps, which will make the pad unnegotiated
     * again.
     *
     * MT safe.
     * @param caps The {@link Caps} to set.
     * @return true if the caps could be set. false if the caps were not fixed
     * or bad parameters were provided to this function.
     */
    public boolean setCaps(Caps caps) {
        return gst.gst_pad_set_caps(this, caps);
    }
    
    /**
     * Gets the capabilities of the allowed media types that can flow through this pad and its peer.
     *
     * The allowed capabilities is calculated as the intersection of the results of
     * calling {@link #getCaps} on this pad and its peer. 
     * 
     * MT safe.
     * @return The allowed {@link Caps} of the pad link, or null if this pad has no peer.
     */
    public Caps getAllowedCaps() {
        return gst.gst_pad_get_allowed_caps(this);
    }
    
    /**
     * Get the capabilities of the media type that currently flows through this pad
     * and its peer.
     *
     * This function can be used on both src and sink pads. Note that srcpads are
     * always negotiated before sinkpads so it is possible that the negotiated caps
     * on the srcpad do not match the negotiated caps of the peer.
     *
     * MT safe.
     * @return the negotiated #GstCaps of the pad link, or null if this pad has
     * no peer, or is not negotiated yet
     * 
     */
    public Caps getNegotiatedCaps() {
        return gst.gst_pad_get_negotiated_caps(this);
    }
    
    /**
     * Get the peer of this pad.
     *
     * MT safe.
     * @return The peer Pad of this Pad.
     */
    public Pad getPeer() {
        return gst.gst_pad_get_peer(this);
    }
    /**
     * Get the capabilities of the peer connected to this pad.
     *
     * @return the {@link Caps} of the peer pad, or null if there is no peer pad.
     */
    public Caps getPeerCaps() {
        return gst.gst_pad_peer_get_caps(this);
    }
    
    /**
     * Check if the pad accepts the given caps.
     *
     * @param caps a {@link Caps} to check on the pad.
     * @return true if the pad can accept the caps.
     */
    public boolean acceptCaps(Caps caps) {
        return gst.gst_pad_accept_caps(this, caps);
    }
    
    /**
     * Check if the peer of this pad accepts the caps.
     * If this pad has no peer, this method returns true.
     *
     * @param caps {@link Caps} to check on the pad
     * @return true if the peer pad can accept the caps or this pad no peer.
     */
    public boolean peerAcceptCaps(Caps caps) {
        return gst.gst_pad_peer_accept_caps(this, caps);
    }
    
    /**
     * Links this pad and a sink pad.
     *
     * MT Safe.
     * @param pad the sink Pad to link.
     * @return A result code indicating if the connection worked or what went wrong.
     */
    public PadLinkReturn link(Pad pad) {
        return gst.gst_pad_link(this, pad);
    }
    
    /**
     *
     * Unlinks the source pad from the sink pad. 
     * Will emit the "unlinked" signal on both pads.
     *
     * MT safe.
     * 
     * @param pad the sink Pad to unlink.
     * @return true if the pads were unlinked. This function returns false if
     * the pads were not linked together.
     */
    public boolean unlink(Pad pad) {
        return gst.gst_pad_unlink(this, pad);
    }
    
    /**
     * Check if this pad is linked to another pad or not.
     *
     * @return true if the pad is linked, else false.
     */
    public boolean isLinked() {
        return gst.gst_pad_is_linked(this);
    }
    
    /**
     * Get the direction of the pad. 
     * The direction of the pad is decided at construction time so this function
     * does not take the LOCK.
     *
     * @return The {@link PadDirection} of the pad.
     */
    public PadDirection getDirection() {
        return gst.gst_pad_get_direction(this);
    }
    
    /**
     * Get the parent of this pad, cast to a {@link Element}. 
     * If this pad has no parent or its parent is not an element, returns null.
     *
     * @return The parent of the pad.
     */
    public Element getParentElement() {
        return gst.gst_pad_get_parent_element(this);
    }
    
    /**
     * Activates or deactivates the given pad.
     * Normally called from within core state change functions.
     *
     * If active is true, makes sure the pad is active. If it is already active, either in
     * push or pull mode, just return. Otherwise dispatches to the pad's activate
     * function to perform the actual activation.
     *
     * If not @active, checks the pad's current mode and calls
     * gst_pad_activate_push() or gst_pad_activate_pull(), as appropriate, with a
     * FALSE argument.
     *
     * @param active whether or not the pad should be active.
     * @return true if the operation was successful.
     */
    public boolean setActive(boolean active) {
        return gst.gst_pad_set_active(this, active);
    }
    
    /**
     *
     * Blocks or unblocks the dataflow on a pad. 
     * 
     * MT safe.
     * @param blocked boolean indicating we should block or unblock
     * @return true if the pad could be blocked. This function can fail if the
     * wrong parameters were passed or the pad was already in the requested state.
     */
    public boolean setBlocked(boolean blocked) {
        return gst.gst_pad_set_blocked(this, blocked);
    }
         
    /**
     * Checks if the pad is blocked or not.
     * This function returns the last requested state of the pad. It is not 
     * certain that the pad is actually blocking at this point (see {@link #isBlocking}).
     *
     * @return true if the pad is blocked.
     */
    public boolean isBlocked() {
        return gst.gst_pad_is_blocked(this);
    }
    
    /**
     * Checks if the pad is blocking or not. This is a guaranteed state
     * of whether the pad is actually blocking on a {@link Buffer} or a {@link Event}.
     * 
     * @return true if the pad is blocking.
     */
    public boolean isBlocking() {
        return gst.gst_pad_is_blocking(this);
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
     * Add a listener for the <code>have-data</code> signal on this {@link Pad}
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
     * Add a listener for the <code>linked</code> signal on this {@link Pad}
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
     * Remove a listener for the <code>linked</code> signal on this {@link Pad}
     * 
     * @param listener The listener previously added for this signal.
     */
    public void disconnect(LINKED listener) {
        disconnect(LINKED.class, listener);
    }
    
    /**
     * Add a listener for the <code>unlinked</code> signal on this {@link Pad}
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
     * Remove a listener for the <code>unlinked</code> signal on this {@link Pad}
     * 
     * @param listener The listener previously added for this signal.
     */
    public void disconnect(UNLINKED listener) {
        disconnect(UNLINKED.class, listener);
    }
    
    /**
     * Add a listener for the <code>request-link</code> signal on this {@link Pad}
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
     * Remove a listener for the <code>request-link</code> signal on this {@link Pad}
     * 
     * @param listener The listener previously added for this signal.
     */
    public void disconnect(REQUEST_LINK listener) {
        disconnect(REQUEST_LINK.class, listener);
    }
}

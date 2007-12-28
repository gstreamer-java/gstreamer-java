/* 
 * Copyright (C) 2007 Wayne Meissner
 * Copyright (C) 1999,2000 Erik Walthinsen <omega@cse.ogi.edu>
 *                    2004 Wim Taymans <wim@fluendo.com>
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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.gstreamer.event.ElementEvent;
import org.gstreamer.event.ElementListener;
import org.gstreamer.event.HandoffEvent;
import org.gstreamer.event.HandoffListener;
import org.gstreamer.lowlevel.GstAPI;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import static org.gstreamer.lowlevel.GstAPI.gst;


/**
 * Abstract base class for all pipeline elements.
 * <p>
 * Element is the abstract base class needed to construct an element that
 * can be used in a GStreamer pipeline. Please refer to the plugin writers
 * guide for more information on creating Element subclasses.
 * <p>
 * The name of a Element can be retrieved with {@link #getName} and set with
 * {@link #setName}.
 * <p>
 * All elements have pads (of the type {@link Pad}).  These pads link to pads on
 * other elements.  {@link Buffer}s flow between these linked pads.
 * An Element has a list of {@link Pad} structures for all their input (or sink)
 * and output (or source) pads.
 * Core and plug-in writers can add and remove pads with {@link #addPad}
 * and {@link #removePad}.
 * <p>
 * A pad of an element can be retrieved by name with {@link #getPad}.
 * An list of all pads can be retrieved with {@link #getPads}.
 * <p>
 * Elements can be linked through their pads.
 * If the link is straightforward, use the {@link #link}
 * convenience function to link two elements, or {@link #linkMany}
 * for more elements in a row.
 * <p>
 * For finer control, use {@link #linkPads} and {@link #linkPadsFiltered}
 * to specify the pads to link on each element by name.
 * <p>
 * Each element has a state (see {@link State}).  You can get and set the state
 * of an element with {@link #getState} and {@link #setState}.
 *
 */
public class Element extends GstObject {
    private static Logger logger = Logger.getLogger(Element.class.getName());
    
    /** Creates a new instance of Element */
    protected Element(Initializer init) { 
        super(init);
    }
    protected static Initializer makeRawElement(String factoryName, String elementName) {
        return initializer(ElementFactory.makeRawElement(factoryName, elementName));
    }
    
    /**
     * Links this element to another element. 
     * The link must be from source to destination; the other direction will not 
     * be tried. 
     * <p>
     * The function looks for existing pads that aren't linked yet. 
     * It will request new pads if necessary. Such pads need to be released manualy when unlinking.
     * If multiple links are possible, only one is established.
     *<p>
     * Make sure you have added your elements to a bin or pipeline with
     * {@link Bin#add} or {@link Bin#addMany} before trying to link them.
     *
     * @param dest The {@link Element} containing the destination pad.
     * @return true if the elements could be linked, false otherwise.
     */
    public boolean link(Element dest) {
        return gst.gst_element_link(this, dest);
    }
    
    /**
     * Chain together a series of elements, with this element as the first in the list. 
     * <p>
     * Make sure you have added your elements to a bin or pipeline with
     * {@link Bin#add} or {@link Bin#addMany} before trying to link them.
     *
     * @param elems The list of elements to be linked.
     * @return true if the elements could be linked, false otherwise.
     */
    public boolean link(Element... elems) {
        // Its much more efficient to copy the array and let the native code do the linking
        Element[] list = new Element[elems.length + 1];
        list[0] = this;
        System.arraycopy(elems, 0, list, 1, elems.length);
        return linkMany(list);
    }
    /**
     * Unlinks all source pads of this source element with all sink pads
     * of the sink element to which they are linked.
     *<p>
     * If the link has been made using {@link #link}, it could have created an
     * requestpad, which has to be released using gst_element_release_request_pad().
     * 
     * @param dest The sink Element to unlink.
     */
    public void unlink(Element dest) {
        gst.gst_element_unlink(this, dest);
    }
    
    public StateChangeReturn setState(State state) {
        return gst.gst_element_set_state(this, state);
    }
    public void setCaps(Caps caps) {
        gobj.g_object_set(this, "caps", caps);
    }
    /**
     * @deprecated Use {@link #getStaticPad}
     */
    @Deprecated
    public Pad getPad(String padname) {
        return gst.gst_element_get_static_pad(this, padname);
    }
    
    /**
     * Retrieves a pad from the element by name. This version only retrieves
     * already-existing (i.e. 'static') pads.
     * 
     * @param padname The name of the {@link Pad} to get.
     * @return The requested {@link Pad} if found, otherwise null.
     */
    public Pad getStaticPad(String padname) {
        return gst.gst_element_get_static_pad(this, padname);
    }
    
    /**
     *  Retrieves a list of the element's pads. 
     *
     * @return the List of {@link Pad}s.
     */
    public List<Pad> getPads() {
        return new GstIterator<Pad>(gst.gst_element_iterate_pads(this), Pad.class).asList();
    }
    
    /**
     *  Retrieves a list of the element's source pads. 
     *
     * @return the List of {@link Pad}s.
     */
    public List<Pad> getSrcPads() {
        return new GstIterator<Pad>(gst.gst_element_iterate_src_pads(this), Pad.class).asList();
    }
    
    /**
     *  Retrieves a list of the element's sink pads. 
     *
     * @return the List of {@link Pad}s.
     */
    public List<Pad> getSinkPads() {
        return new GstIterator<Pad>(gst.gst_element_iterate_sink_pads(this), Pad.class).asList();
    }
    /**
     * Adds a {@link Pad} (link point) to the Element. 
     * The Pad's parent will be set to this element.
     *<p>
     * Pads are not automatically activated so elements should perform the needed
     * steps to activate the pad in case this pad is added in the PAUSED or PLAYING
     * state. See {@link Pad#setActive} for more information about activating pads.
     *<p>
     * This function will emit the {@link PAD_ADDED} signal on the element.
     *
     * @param pad The {@link Pad} to add.
     * @return true if the pad could be added.  This function can fail when
     * a pad with the same name already existed or the pad already had another
     * parent. 
     */
    public boolean addPad(Pad pad) {
        return gst.gst_element_add_pad(this, pad);
    }
    
    /**
     * Remove a {@link Pad} from the element.
     * <p>
     * This method is used by plugin developers and should not be used
     * by applications. Pads that were dynamically requested from elements
     * with gst_element_get_request_pad() should be released with the
     * gst_element_release_request_pad() function instead.
     *<p>
     * Pads are not automatically deactivated so elements should perform the needed
     * steps to deactivate the pad in case this pad is removed in the PAUSED or
     * PLAYING state. See {@link Pad#setActive} for more information about
     * deactivating pads.
     *<p>
     * This function will emit the {@link PAD_REMOVED} signal on the element.
     *
     * @param pad The {@link Pad} to remove.
     * @return true if the pad could be removed. Can return false if the
     * pad does not belong to the provided element.
     */
    public boolean removePad(Pad pad) {
        pad.ref(); // FIXME the comment says this seems to be needed.
        return gst.gst_element_remove_pad(this, pad);
    }
    
    public State getState() {
        return getState(-1);
    }
    
    /**
     * Gets the state of the element.
     *<p>
     * For elements that performed an ASYNC state change, as reported by
     * {@link #setState}, this function will block up to the
     * specified timeout value for the state change to complete.
     * 
     * @param timeout The amount of time in nanoseconds to wait.
     * @return The {@link State} the Element is currently in.
     *
     */
    public State getState(long timeout) {
        IntByReference state = new IntByReference();
        IntByReference pending = new IntByReference();

        gst.gst_element_get_state(this, state, pending, timeout);
        return State.valueOf(state.getValue());
    }
    public void getState(long timeout, State[] states) {
        IntByReference state = new IntByReference();
        IntByReference pending = new IntByReference();
        
        gst.gst_element_get_state(this, state, pending, timeout);
        states[0] = State.valueOf(state.getValue());
        states[1] = State.valueOf(pending.getValue());
    }
    
    /**
     * Retrieves the factory that was used to create this element.
     * @return the {@link ElementFactory} used for creating this element.
     */
    public ElementFactory getFactory() {
        return gst.gst_element_get_factory(this);
    }
    
    /**
     * Get the bus of the element. Note that only a {@link Pipeline} will provide a
     * bus for the application.
     *
     * @return the element's {@link Bus}
     */
    public Bus getBus() {
        return gst.gst_element_get_bus(this);
    }
    
    /**
     * Sends an event to an element.
     * <p>
     * If the element doesn't implement an event handler, the event will be 
     * pushed on a random linked sink pad for upstream events or a random 
     * linked source pad for downstream events.
     *
     * @param ev The {@link Event} to send.
     * @return true if the event was handled.
     */
    public boolean sendEvent(Event ev) {
        ev.ref(); // send_event takes ownership, so need a ref here to keep using it
        return gst.gst_element_send_event(this, ev);
    }
    /**
     * 
     * @param listener
     */
    public void addElementListener(ElementListener listener) {
        listenerMap.put(listener, new ElementListenerProxy(listener));
    }
    /**
     * 
     * @param listener
     */
    public void removeElementListener(ElementListener listener) {
        ElementListenerProxy proxy = listenerMap.get(listener);
        if (proxy != null) {
            proxy.disconnect();
            listenerMap.remove(listener);
        }
    }
    /**
     * Signal emitted when an {@link Pad} is added to this {@link Element}
     */
    public static interface PAD_ADDED {
        public void padAdded(Element element, Pad pad);
    }
    
    /**
     * Signal emitted when an {@link Pad} is removed from this {@link Element}
     */
    public static interface PAD_REMOVED {
        public void padRemoved(Element element, Pad pad);
    }
    
    /**
     * Signal emitted when this {@link Element} ceases to generated dynamic pads.
     */
    public static interface NO_MORE_PADS {
        public void noMorePads(Element element);
    }
    /**
     * Signal emitted when this {@link Element} has a {@link Buffer} ready.
     */
    public static interface HANDOFF {
        public void handoff(Element element, Buffer buffer, Pad pad);
    }
    /**
     * Signal emitted when this {@link org.gstreamer.elements.DecodeBin} decodes a new pad.
     * @deprecated use {@link org.gstreamer.elements.DecodeBin.NEW_DECODED_PAD} instead.
     */
    @Deprecated
    public static interface NEW_DECODED_PAD {
        public void newDecodedPad(Element element, Pad pad, boolean last);
    }
    @Deprecated
    public static interface PADADDED extends PAD_ADDED {}
    @Deprecated
    public static interface PADREMOVED extends PAD_REMOVED {}
    @Deprecated
    public static interface NOMOREPADS extends NO_MORE_PADS {}
    @Deprecated
    public static interface NEWDECODEDPAD extends NEW_DECODED_PAD {}
    
    /**
     * Add a listener for the <code>pad-added</code> signal
     * 
     * @param listener Listener to be called when a {@link Pad} is added to the {@link Element}.
     */
    public void connect(final PAD_ADDED listener) {
        connect("pad-added", PAD_ADDED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, Pad pad, Pointer user_data) {
                listener.padAdded(elem, pad);
            }
        });
    }
    
    /**
     * Remove a listener for the <code>pad-added</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(PAD_ADDED listener) {
        disconnect(PAD_ADDED.class, listener);
    }
    /**
     * Add a listener for the <code>pad-added</code> signal
     * 
     * @param listener Listener to be called when a {@link Pad} is removed from the {@link Element}.
     */
    public void connect(final PAD_REMOVED listener) {
        connect("pad-removed", PAD_REMOVED.class, listener,new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, Pad pad, Pointer user_data) {
                listener.padRemoved(elem, pad);
            }
        });
    }
    
    /**
     * Remove a listener for the <code>pad-removed</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(PAD_REMOVED listener) {
        disconnect(PAD_REMOVED.class, listener);
    }
    
    /**
     * Add a listener for the <code>no-more-pads</code> signal
     * 
     * @param listener Listener to be called when the {@link Element} will has 
     * finished generating dynamic pads.
     */
    public void connect(final NO_MORE_PADS listener) {
        connect("no-more-pads", NO_MORE_PADS.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Element elem, Pointer user_data) {
                listener.noMorePads(elem);
            }
        });
    }
    
    /**
     * Remove a listener for the <code>no-more-pads</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(NO_MORE_PADS listener) {
        disconnect(NO_MORE_PADS.class, listener);
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
            public void callback(Element elem, Pad pad, boolean last) {
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
    /**
     * Add a listener for the <code>handoff</code> signal on this Bin
     * 
     * @param listener The listener to be called when a {@link Buffer} is ready.
     */
    public void connect(final HANDOFF listener) {
        connect("handoff", HANDOFF.class, listener, new GstAPI.HandoffCallback() {
            public void callback(Element src, Buffer buffer, Pad pad, Pointer user_data) {
                buffer.struct.read();
                listener.handoff(src, buffer, pad);
            }            
        });
    }
    /**
     * Remove a listener for the <code>handoff</code> signal
     * 
     * @param listener The listener that was previously added.
     */
    public void disconnect(HANDOFF listener) {
        disconnect(HANDOFF.class, listener);
    }
    
    public void addHandoffListener(final HandoffListener listener) {
        HANDOFF handoff = new HANDOFF() {
            public void handoff(Element elem, Buffer buffer, Pad pad) {
                listener.handoff(new HandoffEvent(elem, buffer, pad));
            }
        };
        handoffMap.put(listener, handoff);
        connect(handoff);
    }
    public void removeHandoffListener(HandoffListener listener) {
        disconnect(handoffMap.get(listener));
    }
    
    class ElementListenerProxy {
        public ElementListenerProxy(final ElementListener listener) {
            Element.this.connect(added = new PAD_ADDED() {
                public void padAdded(Element elem, Pad pad) {
                    listener.padAdded(new ElementEvent(elem, pad));
                }
            });
            Element.this.connect(removed = new PAD_REMOVED() {
                public void padRemoved(Element elem, Pad pad) {
                    listener.padRemoved(new ElementEvent(elem, pad));
                }
            });
            Element.this.connect(nomorepads = new NO_MORE_PADS() {
                public void noMorePads(Element elem) {
                    listener.noMorePads(new ElementEvent(elem, null));
                }
            });
        }
        public void disconnect() {
            Element.this.disconnect(nomorepads);
            Element.this.disconnect(removed);
            Element.this.disconnect(added);
        }
        private PAD_ADDED added;
        private PAD_REMOVED removed;
        private NO_MORE_PADS nomorepads;
    }
    
    /**
     * Link together a list of elements.
     * <p>
     * Make sure you have added your elements to a bin or pipeline with
     * {@link Bin#add} or {@link Bin#addMany} before trying to link them.
     
     * @param elements The list of elements to link together.
     * @return true if all elements successfully linked.
     */
    public static boolean linkMany(Element... elements) {
        return gst.gst_element_link_many(elements);
    }
    
    /**
     * Unlink a list of elements.
     * 
     * @param elements The list of elements to link together
     * 
     */
    public static void unlinkMany(Element... elements) {
        gst.gst_element_unlink_many(elements);
    }
    
    /**
     * Link together source and destination pads of two elements.
     * 
     * A side effect is that if one of the pads has no parent, it becomes a
     * child of the parent of the other element.  If they have different
     * parents, the link fails.
     * 
     * @param src The {@link Element} containing the source {@link Pad}.
     * @param srcPadName The name of the source {@link Pad}.  Can be null for any pad.
     * @param dest The {@link Element} containing the destination {@link Pad}.
     * @param destPadName The name of the destination {@link Pad}.  Can be null for any pad.
     * 
     * @return true if the pads were successfully linked.
     */
    public static boolean linkPads(Element src, String srcPadName, Element dest, String destPadName) {
        return gst.gst_element_link_pads(src, srcPadName, dest, destPadName);
    }
    
    /**
     * Link together source and destination pads of two elements.
     * A side effect is that if one of the pads has no parent, it becomes a child of the parent of
     * the other element. If they have different parents, the link fails. If caps
     * is not null, makes sure that the caps of the link is a subset of caps.
     * 
     * @param src The {@link Element} containing the source {@link Pad}.
     * @param srcPadName The name of the source {@link Pad}.  Can be null for any pad.
     * @param dest The {@link Element} containing the destination {@link Pad}.
     * @param destPadName The name of the destination {@link Pad}.  Can be null for any pad.
     * @param caps The {@link Caps} to use to filter the link.
     * 
     * @return true if the pads were successfully linked.
     */
    public static boolean linkPadsFiltered(Element src, String srcPadName, 
            Element dest, String destPadName, Caps caps) {
        return gst.gst_element_link_pads_filtered(src, srcPadName, dest, destPadName, caps);
    }
    
    /**
     * Unlink source and destination pads of two elements.
     * 
     * @param src The {@link Element} containing the source {@link Pad}.
     * @param srcPadName The name of the source {@link Pad}.
     * @param dest The {@link Element} containing the destination {@link Pad}.
     * @param destPadName The name of the destination {@link Pad}.
     * 
     */
    public static void unlinkPads(Element src, String srcPadName, Element dest, String destPadName) {
        gst.gst_element_unlink_pads(src, srcPadName, dest, destPadName);
    }
    
    static Element objectFor(Pointer ptr, boolean needRef) {
        return GstObject.objectFor(ptr, Element.class, needRef);
    }
    
    private Map<HandoffListener, HANDOFF> handoffMap =
            new ConcurrentHashMap<HandoffListener, HANDOFF>();
    private Map<ElementListener, ElementListenerProxy> listenerMap =
            new ConcurrentHashMap<ElementListener, ElementListenerProxy>();
}


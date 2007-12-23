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

package org.gstreamer;
import org.gstreamer.event.BinEvent;
import static org.gstreamer.lowlevel.GstAPI.gst;
import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gstreamer.event.BinListener;
import org.gstreamer.lowlevel.GstAPI.GstCallback;
import org.gstreamer.lowlevel.GstTypes;

/**
 * Base class and element that can contain other elements.
 * 
 * Bin is an element that can contain other {@link Element}s, allowing them to be
 * managed as a group.
 * <p>
 * Pads from the child elements can be ghosted to the bin, see {@link GhostPad}.
 * This makes the bin look like any other elements and enables creation of
 * higher-level abstraction elements.
 * <p>
 * A new {@link Bin} is created with {@link Bin#Bin(String)}. Use a {@link Pipeline} instead if you
 * want to create a toplevel bin because a normal bin doesn't have a bus or
 * handle clock distribution of its own.
 * <p>
 * After the bin has been created you will typically add elements to it with
 * {@link Bin#add(Element)}. Elements can be removed with {@link Bin#remove(Element)}
 * <p>
 * An element can be retrieved from a bin with {@link Bin#getElementByName(String)}.
 * <p>
 * A list of elements contained in a bin can be retrieved with {@link Bin#getElements}
 *
 * The {@link ELEMENT_ADDED} signal is fired whenever a new element is added
 * to the bin. Likewise the {@link ELEMENT_REMOVED} signal is fired
 * whenever an element is removed from the bin.
 *
 */
public class Bin extends Element {
    
    /**
     * Creates a new Bin with the given name.
     * @param name The Name to assign to the new Bin
     */
    public Bin(String name) {
        super(gst.gst_bin_new(name));
    }
    
    /**
     * Creates a new instance of Bin.  This constructor is for use by subclasses.
     * @param factoryName The type of Bin subclass to create.
     * @param name The Name to assign to the new Bin
     */
    protected Bin(String factoryName, String name) {
        super(factoryName, name);
    }
    
    /**
     *
     * @param ptr
     */
    protected Bin(Pointer ptr) {
        super(ptr);
    }
    
    /**
     *
     * @param ptr
     * @param needRef
     */
    protected Bin(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    /**
     *
     * @param ptr C Pointer to the underlying GstBin
     * @param needRef
     * @param ownsHandle Whether this instance should destroy the underlying object when finalized
     * 
     */
    protected Bin(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    
    /**
     * Adds an Element to this Bin.
     * <p>
     * Sets the element's parent, and thus takes ownership of the element. 
     * An element can only be added to one bin.
     * <p>
     * If the element's pads are linked to other pads, the pads will be unlinked
     * before the element is added to the bin.
     *
     * @param element The {@link Element} to add to this Bin.
     * @return true if the element was successfully added, false if the Bin 
     * will not accept the element.
     */
    public boolean add(Element element) {
        return gst.gst_bin_add(this, element);
    }
    
    /**
     * Adds an array of Element objects to this Bin
     *
     * @param elements The array of {@link Element} to add to this Bin
     * @see Bin#add(Element)
     */
    public void addMany(Element... elements) {
        gst.gst_bin_add_many(this, elements);
    }
    
    /**
     * Removes a Element from this Bin
     * <p>
     * Removes the element from the bin, unparenting it as well.
     *
     * If the element's pads are linked to other pads, the pads will be unlinked
     * before the element is removed from the bin.
     *
     * @param element The {@link Element} to remove
     * @return true if the element was successfully removed
     */
    public boolean remove(Element element) {
        return gst.gst_bin_remove(this, element);
    }
    
    /**
     * Removes an array of {@link Element} objects from this Bin
     *
     * @param elements The list {@link Element} to remove
     */
    public void removeMany(Element... elements) {
        gst.gst_bin_remove_many(this, elements);
    }
    
    private List<Element> elementList(Pointer iter) {
        return new GstIterator<Element>(iter, Element.class).asList();
    }
    /**
     * Retrieve a list of the {@link Element}s contained in the Bin.
     * 
     * @return The List of {@link Element}s.
     */
    public List<Element> getElements() {
        return elementList(gst.gst_bin_iterate_elements(this));
    }
    /**
     * Gets an a list of the elements in this bin in topologically
     * sorted order. This means that the elements are returned from
     * the most downstream elements (sinks) to the sources.
     * @return The List of {@link Element}s.
     */
    public List<Element> getElementsSorted() {
        return elementList(gst.gst_bin_iterate_sorted(this));
    }
    
    /**
     * Retrieve a list of the {@link Element}s contained in the Bin and its Bin children.
     * 
     * This differs from {@link #getElements()} as it will also return {@link Element}s 
     * that are in any Bin elements contained in this Bin, also recursing down those Bins.
     * 
     * @return The List of {@link Element}s.
     */
    public List<Element> getElementsRecursive() {
        return elementList(gst.gst_bin_iterate_recurse(this));
    }
    
    /**
     * Retrieve a list of the sink {@link Element}s contained in the Bin.
     * @return The List of sink {@link Element}s.
     */
    public List<Element> getSinks() {
        return elementList(gst.gst_bin_iterate_sinks(this));
    }
    
    /**
     * Retrieve a list of the source {@link Element}s contained in the Bin.
     * @return The List of source {@link Element}s.
     */
    public List<Element> getSources() {
        return elementList(gst.gst_bin_iterate_sources(this));
    }
    
    /**
     * Gets the {@link Element} with the given name from the bin. This
     * function recurses into child bins.
     *
     * @param name The name of the {@link Element} to find.
     * @return The {@link Element} if found, else null.
     */
    public Element getElementByName(String name) {
        return gst.gst_bin_get_by_name(this, name);
    }
    
    /**
     * Gets the element with the given name from this bin. If the
     * element is not found, a recursion is performed on the parent bin.
     * @param name The name of the {@link Element} to find.
     * @return The {@link Element} if found, else null.
     */
    public Element getElementByNameRecurseUp(String name) {
        return gst.gst_bin_get_by_name_recurse_up(this, name);
    }
    
    /**
     * Looks for an element inside the bin that implements the given
     * interface. If such an element is found, it returns the element.
     * @param iface The class of the {@link Element} to search for.
     * @return The {@link Element} that implements the interface.
     */
    public <T extends Element> T getElementByInterface(Class<T> iface) {
        return iface.cast(gst.gst_bin_get_by_interface(this, GstTypes.typeFor(iface)));
    }
    
    /**
     * Signal emitted when an {@link Element} is added to this Bin
     */
    public static interface ELEMENT_ADDED {
        public void elementAdded(Bin bin, Element elem);
    }
    
    /**
     * Signal emitted when an {@link Element} is removed from this Bin
     */
    public static interface ELEMENT_REMOVED {
        public void elementRemoved(Bin bin, Element elem);
    }
    
    /**
     * @deprecated Use {@link ELEMENT_ADDED} instead
     */
    @Deprecated
    public static interface ELEMENTADDED extends ELEMENT_ADDED { }
    /**
     * @deprecated Use {@link ELEMENT_REMOVED} instead
     */
    @Deprecated
    public static interface ELEMENTREMOVED extends ELEMENT_REMOVED { }
    
    /**
     * Add a listener for the <code>element-added</code> signal on this Bin
     * 
     * @param listener The listener to be called when an {@link Element} is added.
     */
    public void connect(final ELEMENT_ADDED listener) {
        connect("element-added", ELEMENT_ADDED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bin bin, Element elem, Pointer user_data) {
                listener.elementAdded(bin, elem);
            }
        });
    }
    
    /**
     * Add a listener for the <code>element-removed</code> signal on this Bin
     * 
     * @param listener The listener to be called when an {@link Element} is removed.
     */
    public void connect(final ELEMENT_REMOVED listener) {
        connect("element-removed", ELEMENT_REMOVED.class, listener, new GstCallback() {
            @SuppressWarnings("unused")
            public void callback(Bin bin, Element elem, Pointer user_data) {
                listener.elementRemoved(bin, elem);
            }
        });
    }
    
    /**
     * Disconnect the listener for the <code>element-added</code> signal
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ELEMENT_ADDED listener) {
        disconnect(ELEMENT_ADDED.class, listener);
    }
    
    /**
     * Disconnect the listener for the <code>element-removed</code> signal
     * 
     * @param listener The listener that was registered to receive the signal.
     */
    public void disconnect(ELEMENT_REMOVED listener) {
        disconnect(ELEMENT_REMOVED.class, listener);
    }
    
    /**
     * Add a listener for Bin events.
     * 
     * This is an alternative interface for listening for {@link ELEMENT_ADDED} and 
     * {@link ELEMENT_REMOVED} signals.
     * 
     * @param listener The listener to send events to.
     */
    public void addBinListener(BinListener listener) {
        listenerMap.put(listener, new BinListenerProxy(listener));
    }
    
    /**
     * Remove a listener for Bin events.
     * 
     * @param listener The {link BinListener} previously registered using 
     * {@link #addBinListener(BinListener)}.
     */
    public void removeBinListener(BinListener listener) {
        BinListenerProxy proxy = listenerMap.remove(listener);
        if (proxy != null) {
            disconnect(proxy.added);
            disconnect(proxy.removed);
        }
    }
    class BinListenerProxy {
        public BinListenerProxy(final BinListener listener) {
            Bin.this.connect(added = new ELEMENT_ADDED() {
                public void elementAdded(Bin bin, Element elem) {
                    listener.elementAdded(new BinEvent(bin, elem));
                }
            });
            Bin.this.connect(removed = new ELEMENT_REMOVED() {
                public void elementRemoved(Bin bin, Element elem) {
                    listener.elementRemoved(new BinEvent(bin, elem));
                }
            });
        }
        ELEMENT_ADDED added;
        ELEMENT_REMOVED removed;
    }
    private Map<BinListener, BinListenerProxy> listenerMap =
            Collections.synchronizedMap(new HashMap<BinListener, BinListenerProxy>());
}

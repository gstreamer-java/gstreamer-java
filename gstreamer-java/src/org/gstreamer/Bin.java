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
import org.gstreamer.event.BinEvent;
import static org.gstreamer.lowlevel.GstAPI.gst;
import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gstreamer.event.BinListener;
import org.gstreamer.lowlevel.GstAPI.GstCallback;

/**
 *
 */
public class Bin extends Element {
    
    /**
     * Creates a new instance of GstBin
     * @param name The Name to assign to the new Bin
     */
    public Bin(String name) {
        super(gst.gst_bin_new(name));
    }
    
    /**
     * Creates a new instance of GstBin.  This constructor is for use by subclasses.
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
     * Adds an Element to this GstBin
     *
     * @param element The array of {@link Element} to add to this Bin
     * @return true if the element was successfully removed
     */
    public boolean add(Element element) {
        return gst.gst_bin_add(this, element);
    }
    
    /**
     * Adds an array of Element objects to this GstBin
     *
     * @param elements The array of {@link Element} to add to this Bin
     */
    public void addMany(Element... elements) {
        gst.gst_bin_add_many(this, elements);
    }
    
    /**
     * Removes a Element from this GstBin
     *
     * @param e The {@link Element} to remove
     * @return true if the element was successfully removed
     */
    public boolean remove(Element e) {
        return gst.gst_bin_remove(this, e);
    }
    
    /**
     * Removes an array of {@link Element} objects from this GstBin
     *
     * @param elements The list {@link Element} to remove
     */
    public void removeMany(Element... elements) {
        gst.gst_bin_remove_many(this, elements);
    }
    private List<Element> elementList(Pointer iter) {
        return new GstIterator<Element>(iter, Element.class).asList();
    }
    public List<Element> getElements() {
        return elementList(gst.gst_bin_iterate_elements(this));
    }
    public List<Element> getElementsSorted() {
        return elementList(gst.gst_bin_iterate_sorted(this));
    }
    public List<Element> getElementsRecursive() {
        return elementList(gst.gst_bin_iterate_recurse(this));
    }
    public List<Element> getSinks() {
        return elementList(gst.gst_bin_iterate_sinks(this));
    }
    public List<Element> getSources() {
        return elementList(gst.gst_bin_iterate_sources(this));
    }
    public Element getElementByName(String name) {
        return gst.gst_bin_get_by_name(this, name);
    }
    public Element getElementByNameRecurseUp(String name) {
        return gst.gst_bin_get_by_name_recurse_up(this, name);
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
    
    @Deprecated
    public static interface ELEMENTADDED extends ELEMENT_ADDED { }
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
    public void addBinListener(BinListener listener) {
        listenerMap.put(listener, new BinListenerProxy(listener));
    }
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

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
import org.gstreamer.event.BinEvent;
import static org.gstreamer.lowlevel.GstAPI.gst;
import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gstreamer.event.BinListener;

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
//        return gst.gst_bin_add(handle(), element.handle());
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
    public static interface ELEMENTADDED {
        public void elementAdded(Bin bin, Element elem);
    }
    public static interface ELEMENTREMOVED {
        public void elementRemoved(Bin bin, Element elem);
    }
    
    
    public void connect(final ELEMENTADDED listener) {
        connect("element-added", ELEMENTADDED.class, listener,new Callback() {
            public void callback(Pointer bin, Pointer elem, Pointer user_data) {
                listener.elementAdded(Bin.this,Element.objectFor(elem, true));
            }
        });
    }
    public void connect(final ELEMENTREMOVED listener) {
        connect("element-removed", ELEMENTREMOVED.class, listener,new Callback() {
            public void callback(Pointer bin, Pointer elem, Pointer user_data) {
                listener.elementRemoved(Bin.this,Element.objectFor(elem, true));
            }
        });
    }
    public void disconnect(ELEMENTADDED listener) {
        disconnect(ELEMENTADDED.class, listener);
    }
    public void disconnect(ELEMENTREMOVED listener) {
        disconnect(ELEMENTREMOVED.class, listener);
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
            Bin.this.connect(added = new ELEMENTADDED() {
                public void elementAdded(Bin bin, Element elem) {
                    listener.elementAdded(new BinEvent(bin, elem));
                }
            });
            Bin.this.connect(removed = new ELEMENTREMOVED() {
                public void elementRemoved(Bin bin, Element elem) {
                    listener.elementRemoved(new BinEvent(bin, elem));
                }
            });
        }
        ELEMENTADDED added;
        ELEMENTREMOVED removed;
    }
    private Map<BinListener, BinListenerProxy> listenerMap =
            Collections.synchronizedMap(new HashMap<BinListener, BinListenerProxy>());
}

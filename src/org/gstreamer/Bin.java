/*
 * Bin.java
 */

package org.gstreamer;
import com.sun.jna.Callback;
import org.gstreamer.event.BinEvent;
import static org.gstreamer.lowlevel.GstAPI.gst;
import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
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
        this(gst.gst_bin_new(name), true, true);
    }
    /**
     *
     * @param ptr
     * @param needRef
     */
    protected Bin(Pointer ptr, boolean needRef) {
        super(ptr, true, needRef);
    }
    /**
     *
     * @param ptr C Pointer to the underlying GstBin
     * @param ownsHandle Whether this instance should destroy the underlying object when finalized
     * @param needRef
     */
    protected Bin(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
    }
    public static interface ELEMENTADDED {
        public void elementAdded(Bin bin, Element elem);
    }
    public static interface ELEMENTREMOVED {
        public void elementRemoved(Bin bin, Element elem);
    }
    /**
     * Adds an array of Element objects to this GstBin
     *
     * @param elements The array of {@link Element} to add to this Bin
     */
    public void add(Element... elements) {
        for (Element e : elements) {
            gst.gst_bin_add(handle(), e.handle());
        }
    }
    /**
     * Removes a Element from this GstBin
     *
     * @param e The {@link Element} to remove
     * @return true if the element was successfully removed
     */
    public boolean remove(Element e) {
        return gst.gst_bin_remove(handle(), e.handle());
    }
    public void connect(final ELEMENTADDED listener) {
        connect("element-added", ELEMENTADDED.class, listener, new Callback() {
            public void callback(Pointer bin, Pointer elem, Pointer user_data) {
                listener.elementAdded(Bin.this, Element.instanceFor(elem, true, true));
            }
        });
    }
    public void connect(final ELEMENTREMOVED listener) {
        connect("element-removed", ELEMENTREMOVED.class, listener, new Callback() {
            public void callback(Pointer bin, Pointer elem, Pointer user_data) {
                listener.elementRemoved(Bin.this, Element.instanceFor(elem, true, true));
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

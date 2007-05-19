/*
 * Element.java
 */

package org.gstreamer;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import static org.gstreamer.State.*;
import org.gstreamer.event.ElementEvent;
import org.gstreamer.event.ElementListener;
import org.gstreamer.event.HandoffEvent;
import org.gstreamer.event.HandoffListener;
import static org.gstreamer.lowlevel.GstAPI.gst;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;

/**
 *
 */
public class Element extends GstObject {
    static Logger logger = Logger.getLogger(Element.class.getName());
    
    /** Creates a new instance of GstElement */
    protected Element(Pointer ptr) {
        super(ptr);
    }
    protected Element(Pointer ptr, boolean needRef) {
        super(ptr, needRef);
    }
    protected Element(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    
    public boolean link(Element e) {
        return gst.gst_element_link(handle(), e.handle());
    }
    public void link(Element... elems) {
        Element prev = this;
        for (Element e : elems) {
            prev.link(e);
            prev = e;
        }
    }
    public void unlink(Element e) {
        gst.gst_element_unlink(handle(), e.handle());
    }
    public void play() {
        setState(State.PLAYING);
    }
    public void pause() {
        setState(State.PAUSED);
    }
    public void stop() {
        setState(State.NULL);
    }
    public void setState(State state) {
        gst.gst_element_set_state(handle(), state.intValue());
    }
    public void setCaps(Caps caps) {
        gobj.g_object_set(handle(), "caps", caps.handle(), null);
    }
    public Pad getPad(String padname) {
        return Pad.instanceFor(gst.gst_element_get_pad(handle(), padname), false);
    }
    public boolean addPad(Pad pad) {
        return gst.gst_element_add_pad(handle(), pad.handle());
    }
    public boolean removePad(Pad pad) {
        return gst.gst_element_remove_pad(handle(), pad.handle());
    }
    public boolean isPlaying() {
        IntByReference state = new IntByReference();
        IntByReference pending = new IntByReference();
        
        gst.gst_element_get_state(handle(), state, pending, -1);
        return state.getValue() == State.PLAYING.intValue();
    }
   
    public Time getPosition() {
        IntByReference fmt = new IntByReference(Format.TIME.intValue());
        LongByReference pos = new LongByReference();
        gst.gst_element_query_position(handle(), fmt, pos);
        return new Time(pos.getValue());
    }
    public Time getDuration() {
        IntByReference fmt = new IntByReference(Format.TIME.intValue());
        LongByReference duration= new LongByReference();
        gst.gst_element_query_duration(handle(), fmt, duration);
        return new Time(duration.getValue());
    }
    public ElementFactory getFactory() {
        return ElementFactory.instanceFor(gst.gst_element_get_factory(handle()), false);
    }
    public Bus getBus() {
        return Bus.instanceFor(gst.gst_element_get_bus(handle()), false);
    }
    public void addElementListener(ElementListener listener) {
        listenerMap.put(listener, new ElementListenerProxy(listener));
    }
    public void removeElementListener(ElementListener listener) {
        ElementListenerProxy proxy = listenerMap.get(listener);
        if (proxy != null) {
            proxy.disconnect();
            listenerMap.remove(listener);
        }
    }
    public static interface PADADDED {
        public void padAdded(Element element, Pad pad);
    }
    public static interface PADREMOVED {
        public void padRemoved(Element element, Pad pad);
    }
    public static interface NOMOREPADS {
        public void noMorePads(Element element);
    }
    public static interface HANDOFF {
        public void handoff(Element element, Buffer buffer, Pad pad);
    }
    public void connect(final PADADDED listener) {
        connect("pad-added", PADADDED.class, listener, new Callback() {
            public void callback(Pointer elem, Pointer pad, Pointer user_data) {
                listener.padAdded(Element.this, Pad.instanceFor(pad, true));
            }
        });
    }
    public void disconnect(PADADDED listener) {
        disconnect(PADADDED.class, listener);
    }
    
    public void connect(final PADREMOVED listener) {
        connect("pad-removed", PADREMOVED.class, listener, new Callback() {
            public void callback(Pointer elem, Pointer pad, Pointer user_data) {
                listener.padRemoved(Element.this, Pad.instanceFor(pad, true));
            }
        });
    }
    public void disconnect(PADREMOVED listener) {
        disconnect(PADREMOVED.class, listener);
    }
    
    public void connect(final NOMOREPADS listener) {
        connect("no-more-pads", NOMOREPADS.class, listener, new Callback() {
            public void callback(Pointer elem, Pointer user_data) {
                listener.noMorePads(Element.this);
            }
        });
    }
    public void disconnect(NOMOREPADS listener) {
        disconnect(NOMOREPADS.class, listener);
    }
    public void connect(final HANDOFF listener) {
        connect("handoff", HANDOFF.class, listener, new Callback() {
            public void callback(Pointer srcPtr, Pointer bufPtr, Pointer padPtr, Pointer user_data) {
                Element src = Element.instanceFor(srcPtr, true);
                Buffer buffer = new Buffer(bufPtr, true);
                Pad pad = Pad.instanceFor(padPtr, true);
                listener.handoff(src, buffer, pad);
            }
        });
    }
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
            Element.this.connect(added = new PADADDED() {
                public void padAdded(Element elem, Pad pad) {
                    listener.padAdded(new ElementEvent(elem, pad));
                }
            });
            Element.this.connect(removed = new PADREMOVED() {
                public void padRemoved(Element elem, Pad pad) {
                    listener.padRemoved(new ElementEvent(elem, pad));
                }
            });
            Element.this.connect(nomorepads = new NOMOREPADS() {
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
        private PADADDED added;
        private PADREMOVED removed;
        private NOMOREPADS nomorepads;
    }
    
    public static boolean linkMany(Element... elems) {
        Element prev = null;
        for (Element e : elems) {
            if (prev != null) {
                if (!prev.link(e)) {
                    return false;
                }
            }
            prev = e;
        }
        return true;
    }
    public static Element instanceFor(Pointer ptr, boolean needRef) {
        return (Element) GstObject.objectFor(ptr, Element.class, needRef);
    }
    static Element instanceFor(Pointer ptr) {
        return (Element) GstObject.objectFor(ptr, Element.class);
    }
    
    private Map<HandoffListener, HANDOFF> handoffMap =
            new ConcurrentHashMap<HandoffListener, HANDOFF>();
    private Map<ElementListener, ElementListenerProxy> listenerMap =
            new ConcurrentHashMap<ElementListener, ElementListenerProxy>();
}


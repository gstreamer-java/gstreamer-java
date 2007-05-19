/*
 * GstObject.java
 */

package org.gstreamer;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gstreamer.lowlevel.GBoolean;
import org.gstreamer.lowlevel.GObjectAPI;
import static org.gstreamer.lowlevel.GstAPI.gst;
import static org.gstreamer.lowlevel.GlibAPI.glib;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import org.gstreamer.lowlevel.GstTypes;

/**
 *
 */
public class GstObject extends NativeObject {
    static Logger logger = Logger.getLogger(GstObject.class.getName());
    static Level DEBUG = Level.FINE;
    static Level LIFECYCLE = NativeObject.LIFECYCLE;
    
    /** Creates a new instance of GstObject */
    protected GstObject(Pointer ptr) {
        // By default, Owns the handle and needs to ref+sink it to retain it
        this(ptr, true, true);
    }
    /**
     *
     * @param ptr
     * @param needRef
     */
    protected GstObject(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    /**
     * Wraps an underlying C GstObject with a Java object
     * @param ptr C Pointer to the underlying GstObject.
     * @param ownsHandle Whether this instance should destroy the underlying object when finalized.
     * @param needRef Whether the reference count of the underlying object needs
     *                to be incremented immediately to retain a reference.
     */
    protected GstObject(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, false, ownsHandle); // increase the refcount here
        logger.entering("GstObject", "<init>", new Object[] { ptr, ownsHandle, needRef });
        if (ownsHandle) {
            strongReferences.add(this);
            gobj.g_object_add_toggle_ref(handle(), toggle, null);
            if (!needRef) {
                unref();
            }
            // Lose the floating ref so when this object is destroyed
            // and it is the last ref, the C object gets freed
            sink();
        }
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof GstObject && ((GstObject) o).handle().equals(handle());
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + handle() + ")";
    }
    public void set(String property, String data) {
        logger.entering("GstObject", "set", new Object[] { property, data });
        gobj.g_object_set(handle(), property, data, null);
    }
    public void set(String property, GstObject data) {
        logger.entering("GstObject", "set", new Object[] { property, data });
        gobj.g_object_set(handle(), property, data.handle(), null);
    }
    public void set(String property, Object data) {
        logger.entering("GstObject", "set", new Object[] { property, data });
        if (data instanceof Boolean) {
            data = GBoolean.valueOf((Boolean) data);
        }
        gobj.g_object_set(handle(), property, data, null);
    }
    public void setProperty(String property, GstObject data) {
        logger.entering("GstObject", "setProperty", new Object[] { property, data });
        gobj.g_object_set_property(handle(), property, data.handle());
    }
    
    public void setName(String name) {
        logger.entering("GstObject", "setName", name);
        gst.gst_object_set_name(handle(), name);
    }
    public String getName() {
        logger.entering("GstObject", "getName");
        Pointer ptr = gst.gst_object_get_name(handle());
        String s = ptr.getString(0, false);
        glib.g_free(ptr);
        return s;
    }
    protected int g_signal_connect(String signal, Callback callback) {
        logger.entering("GstObject", "g_signal_connect", new Object[] { signal, callback });
        return gobj.g_signal_connect_data(handle(), signal, callback, null, null, 0);
    }
    void ref() {
        gst.gst_object_ref(handle());
    }
    void unref() {
        gst.gst_object_unref(handle());
    }
    void sink() {
        gst.gst_object_sink(handle());
    }
    void disposeNativeHandle(Pointer ptr) {
        logger.log(LIFECYCLE, "Removing toggle ref " + getClass().getSimpleName() + " (" +  handle() + ")");
        gobj.g_object_remove_toggle_ref(handle(), toggle, null);
    }
    
    public static GstObject objectFor(Pointer ptr, Class defaultClass) {
        return objectFor(ptr, defaultClass, true);
    }
    public static GstObject objectFor(Pointer ptr, Class defaultClass, boolean needRef) {
        logger.entering("GstObject", "instanceFor", new Object[] { ptr, defaultClass, needRef });
        // Ignore null pointers
        if (ptr == null || !ptr.isValid()) {
            return null;
        }
        // Try to retrieve an existing instance for the pointer
        NativeObject obj = NativeObject.instanceFor(ptr);
        if (obj != null) {
            return (GstObject) obj;
        }
        // Try to figure out what type of object it is by checking its GType
        Class cls = GstTypes.classFor(ptr);
        if (cls == null) {
            cls = defaultClass;
        }
        return (GstObject) NativeObject.objectFor(ptr, cls, needRef);
    }
    private class SignalCallback {
        protected SignalCallback(String signal, Callback cb) {
            this.cb  = cb;
            id = g_signal_connect(signal, cb);
        }
        synchronized protected void disconnect() {
            if (id != 0) {
                gobj.g_signal_handler_disconnect(handle(), id);
                id = 0;
            }
        }
        protected void finalize() {
            // Ensure the native callback is removed
            disconnect();
        }
        Callback cb;
        int id;
    }
    private Map<Class, Map<Object, SignalCallback>> listeners =
            new HashMap<Class, Map<Object, SignalCallback>>();
    
    void connect(String signal, Class listenerClass, Object listener, Callback cb) {
        Map<Object, SignalCallback> m;
        synchronized (listeners) {
            m = listeners.get(listenerClass);
            if (m == null) {
                m = Collections.synchronizedMap(new HashMap<Object,SignalCallback>());
                listeners.put(listenerClass, m);
            }
        }
        m.put(listener, new SignalCallback(signal, cb));
    }
    
    void disconnect(Class listenerClass, Object listener) {
        synchronized (listeners) {
            Map<Object, SignalCallback> m = listeners.get(listenerClass);
            if (m != null) {
                SignalCallback cb = m.remove(listener);
                if (cb != null) {
                    cb.disconnect();
                }
                if (m.isEmpty()) {
                    listeners.remove(listenerClass);
                }
            }
        }
    }
    
    /*
     * Hooks to/from native disposal
     */
    private static final GObjectAPI.GToggleNotify toggle = new GObjectAPI.GToggleNotify() {
        public void callback(Pointer data, Pointer ptr, boolean is_last_ref) {
            
            /*
             * Manage the strong reference to this instance.  When this is the last
             * reference to the underlying object, remove the strong reference so
             * it can be garbage collected.  If it is owned by someone else, then make
             * it a strong ref, so the java GstObject for the underlying C object can
             * be retained for later retrieval
             */
            GstObject o = (GstObject) NativeObject.instanceFor(ptr);
            if (o == null) {
                return;
            }
            logger.log(LIFECYCLE, "toggle_ref " + o.getClass().getSimpleName() +
                    " (" +  ptr + ")" + " last_ref=" + is_last_ref);
            if (is_last_ref) {
                strongReferences.remove(o);
            } else {
                strongReferences.add(o);
            }
        }
    };
    
    private static Set<GstObject> strongReferences = Collections.synchronizedSet(new HashSet<GstObject>());
    
}

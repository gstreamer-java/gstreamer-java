/*
 * MiniObject.java
 */

package org.gstreamer;

import com.sun.jna.Pointer;
import org.gstreamer.lowlevel.GstAPI;
import org.gstreamer.lowlevel.GstTypes;

/**
 *
 */
public class MiniObject extends NativeObject {
    private static GstAPI gst = GstAPI.gst;
    /**
     * Creates a new instance of MiniObject
     */
    MiniObject(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    MiniObject(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    void ref() {
        gst.gst_mini_object_ref(handle());
    }
    void unref() {
        gst.gst_mini_object_unref(handle());
    }
    
    void disposeNativeHandle(Pointer ptr) {
        gst.gst_mini_object_unref(ptr);
    }
    @SuppressWarnings("unchecked")
    public static MiniObject objectFor(Pointer ptr, Class<? extends NativeObject> defaultClass, boolean needRef) {
        // Try to retrieve an existing instance for the pointer
        NativeObject obj = NativeObject.instanceFor(ptr);
        if (obj != null) {
            return (MiniObject) obj;
        }
        // Try to figure out what type of object it is by checking its GType
        Class<? extends NativeObject> cls = GstTypes.classFor(ptr);
        if (cls == null) {
            cls = defaultClass;
        }
        return (MiniObject) NativeObject.objectFor(ptr, cls, needRef);
    }
}

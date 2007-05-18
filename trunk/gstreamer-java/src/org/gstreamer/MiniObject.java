/*
 * MiniObject.java
 */

package org.gstreamer;
import static org.gstreamer.lowlevel.GstAPI.gst;
import com.sun.jna.Pointer;
import org.gstreamer.lowlevel.GstTypes;

/**
 *
 */
public class MiniObject extends NativeObject {
    
    /**
     * Creates a new instance of MiniObject
     */
    MiniObject(Pointer ptr, boolean needRef) {
        this(ptr, true, needRef);
    }
    MiniObject(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
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
    public static MiniObject instanceFor(Pointer ptr, Class defaultClass, boolean needRef) {
        // Try to retrieve an existing instance for the pointer
        NativeObject obj = NativeObject.instanceFor(ptr);
        if (obj != null) {
            return (MiniObject) obj;
        }
        // Try to figure out what type of object it is by checking its GType
        Class cls = GstTypes.classFor(ptr);
        if (cls == null) {
            cls = defaultClass;
        }
        return (MiniObject) NativeObject.instanceFor(ptr, cls, true, needRef);
    }
}

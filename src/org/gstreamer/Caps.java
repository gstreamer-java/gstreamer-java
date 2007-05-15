/*
 * Caps.java
 */

package org.gstreamer;
import com.sun.jna.Pointer;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Caps extends NativeObject {
    
    public static Caps emptyCaps() {
        return new Caps(gst.gst_caps_new_empty());
    }
    public static Caps anyCaps() {
        return new Caps(gst.gst_caps_new_any());
    }
    
    public Caps() {
        this(gst.gst_caps_new_empty());
    }
    public Caps(String caps) {
        this(gst.gst_caps_from_string(caps));
    }
    Caps(Pointer ptr) {
        this(ptr, true, false);
    }
    Caps(Pointer ptr, boolean needRef) {
        this(ptr, true, needRef);
    }
    Caps(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
    }
    public int size() {
        return gst.gst_caps_get_size(handle());
    }
    public Caps copy() {
        return new Caps(gst.gst_caps_copy(handle()), true, false);
    }
    public Caps union(Caps other) {
        return new Caps(gst.gst_caps_union(handle(), other.handle()), true, false);
    }
    public void merge(Caps other) {
        gst.gst_caps_merge(handle(), other.handle());
    }
    public void merge(Structure struct) {
        gst.gst_caps_merge_structure(handle(), struct.handle());
        struct.disown();
    }
    public void append(Structure struct) {
        gst.gst_caps_append_structure(handle(), struct.handle());
        struct.disown();
    }
    public void setInteger(String field, Integer value) {
        gst.gst_caps_set_simple(handle(), field, value, null);
    }
    public Structure getStructure(int index) {
        return Structure.instanceFor(gst.gst_caps_get_structure(handle(), index), false, false);
    }

    public static Caps instanceFor(Pointer ptr, boolean ownsHandle, boolean needRef) {
        return (Caps) NativeObject.instanceFor(ptr, Caps.class, ownsHandle, needRef);
    }
    
    void ref() {
        gst.gst_caps_ref(handle());
    }
    void unref() {
        gst.gst_caps_unref(handle());
    }
    void disposeNativeHandle(Pointer ptr) {
        gst.gst_caps_unref(ptr);
    }

    
}

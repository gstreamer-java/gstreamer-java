/*
 * Structure.java
 */

package org.gstreamer;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Structure extends NativeObject {
    
    /**
     * Creates a new instance of Structure
     */
    Structure(Pointer ptr) {
        this(ptr, true, false);
    }
    protected Structure(Pointer ptr, boolean needRef) {
        this(ptr, true, needRef);
    }
    protected Structure(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
    }
    public Structure(String data) {
        this(gst.gst_structure_from_string(data, new PointerByReference()), true, false);
    }
    public Structure copy() {
        return new Structure(gst.gst_structure_copy(handle()));
    }
    public int getInteger(String field) {
        IntByReference intRef = new IntByReference();
        gst.gst_structure_get_int(handle(), field, intRef);
        return intRef.getValue();
    }
    public boolean setInteger(String field, Integer value) {
        return gst.gst_structure_fixate_field_nearest_int(handle(), field, value);
    }
    public boolean fixateFieldNearestInteger(String field, Integer target) {
        return gst.gst_structure_fixate_field_nearest_int(handle(), field, target);
    }
    public static Structure instanceFor(Pointer ptr, boolean needRef, boolean ownsHandle) {
        return (Structure) NativeObject.instanceFor(ptr, Structure.class, needRef, ownsHandle);
    }
    
    //--------------------------------------------------------------------------
    void ref() {}
    void unref() {}
    void disposeNativeHandle(Pointer ptr) {
        gst.gst_structure_free(ptr);
    }
    
}

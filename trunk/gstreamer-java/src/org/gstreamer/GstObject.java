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
import com.sun.jna.Pointer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import static org.gstreamer.lowlevel.GlibAPI.glib;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class GstObject extends GObject {
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
        super(ptr, needRef, ownsHandle);
        if (ownsHandle) {
            // Lose the floating ref so when this object is destroyed
            // and it is the last ref, the C object gets freed
            sink();
        }
    }
    
    public void setName(String name) {
        logger.entering("GstObject", "setName", name);
        gst.gst_object_set_name(this, name);
    }
    public String getName() {
        logger.entering("GstObject", "getName");
        Pointer ptr = gst.gst_object_get_name(this);
        String s = ptr.getString(0, false);
        glib.g_free(ptr);
        return s;
    }
    
    void ref() {
        gst.gst_object_ref(this);
    }
    void unref() {
        gst.gst_object_unref(this);
    }
    void sink() {
        gst.gst_object_sink(this);
    }
    
    public static GstObject objectFor(Pointer ptr, Class<? extends GstObject> defaultClass) {
        return GstObject.objectFor(ptr, defaultClass, true);
    }
    @SuppressWarnings("unchecked")
    public static GstObject objectFor(Pointer ptr, Class<? extends GstObject> defaultClass, boolean needRef) {
        logger.entering("GstObject", "objectFor", new Object[] { ptr, defaultClass, needRef });
        return (GstObject) GObject.objectFor(ptr, defaultClass, needRef);
    }
}

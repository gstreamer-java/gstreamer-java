/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer;
import com.sun.jna.Pointer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class GstObject extends GObject {
    private static Logger logger = Logger.getLogger(GstObject.class.getName());
    static Level DEBUG = Level.FINE;
    static Level LIFECYCLE = NativeObject.LIFECYCLE;
    /**
     * Wraps an underlying C GstObject with a Java proxy
     * @param init C initialization data
     */
    protected GstObject(Initializer init) {
        super(init);
        if (init.ownsHandle && init.needRef) {
            // Lose the floating ref so when this object is destroyed
            // and it is the last ref, the C object gets freed
            sink();
        }
    }
    protected static Initializer initializer(Pointer ptr) {
        return initializer(ptr, true, true);
    }
    protected static Initializer initializer(Pointer ptr, boolean needRef) {
        return initializer(ptr, needRef, true);
    }
   
    public void setName(String name) {
        logger.entering("GstObject", "setName", name);
        gst.gst_object_set_name(this, name);
    }
    public String getName() {
        logger.entering("GstObject", "getName");
        return gst.gst_object_get_name(this);
    }
    
    protected void ref() {
        gst.gst_object_ref(this);
    }
    protected void unref() {
        gst.gst_object_unref(this);
    }
    void sink() {
        gst.gst_object_sink(this);
    }
    
    public static <T extends GstObject> T objectFor(Pointer ptr, Class<T> defaultClass) {
        return GstObject.objectFor(ptr, defaultClass, true);
    }
   
    public static <T extends GstObject> T objectFor(Pointer ptr, Class<T> defaultClass, boolean needRef) {
        logger.entering("GstObject", "objectFor", new Object[] { ptr, defaultClass, needRef });
        return GObject.objectFor(ptr, defaultClass, needRef);
    }
    
    //
    // Returned objects have their refcount incremented by gstreamer.  If we
    // have an existing object for the pointer, unref() it to remove the extra ref.
    //
    @SuppressWarnings("unchecked")
    public static <T extends GstObject> T returnedObject(Pointer ptr, Class<T> defaultClass) {
        logger.entering("GstObject", "objectFor", new Object[] { ptr, defaultClass });
        // Ignore null pointers
        if (ptr == null) {
            return null;
        }
        // Try to retrieve an existing instance for the pointer
        NativeObject obj = NativeObject.instanceFor(ptr);
        if (obj != null) {
            obj.unref(); // Lose the extra ref added by gstreamer
            return (T) obj;
        }        
        return NativeObject.objectFor(ptr, NativeObject.classFor(ptr, defaultClass), false);
    }
}

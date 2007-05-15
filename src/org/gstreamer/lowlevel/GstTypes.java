/*
 * GstType.java
 */

package org.gstreamer.lowlevel;

import com.sun.jna.Pointer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Clock;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Message;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class GstTypes {
    private static final Logger logger = Logger.getLogger(GstTypes.class.getName());
    private static final int GTypeSize = Pointer.SIZE;  // Assumes sizeof pointer == sizeof long

    private GstTypes() {
    }
    public static final boolean isGType(Pointer p, long type) {
        return getGType(p) == type;
    }
    public static final long getGType(Pointer ptr) {
        // Retrieve ptr->g_class
        Pointer g_class = ptr.getPointer(0);
        
        // Now return g_class->gtype
        if (GTypeSize == 8) {
            return g_class.getLong(0);
        } else {
            return g_class.getInt(0);
        }
    }
    public static final Class classFor(Pointer ptr) {
        Pointer g_class = ptr.getPointer(0);
        Class cls;
        cls = gTypeInstanceMap.get(g_class);
        if (cls != null) {
            return cls;
        }
        long type = GTypeSize == 8 ? g_class.getLong(0) : g_class.getInt(0);
        logger.finer("Type of " + ptr + " = " + type);
        cls = typeMap.get(type);
        if (cls != null) {
            logger.finer("Found type of " + ptr + " = " + cls);
            gTypeInstanceMap.put(g_class, cls);
        }
        return cls;
    }
    private static final void registerGType(long type, Class cls) {
        if (GTypeSize == 4) {
            // On 32 bit systems, just use the bottom int of the type
            type = ((int) type & 0xffffffff);
        }
        logger.fine("Registering gtype " + type + " = " + cls);
        typeMap.put(type, cls);
    }
    private static Map<Long, Class> typeMap = new HashMap<Long, Class>();
    private static Map<Pointer, Class> gTypeInstanceMap = Collections.synchronizedMap(new HashMap<Pointer, Class>());
    static {
        // GstObject types
        registerGType(gst.gst_element_get_type(), Element.class);
        registerGType(gst.gst_element_factory_get_type(), ElementFactory.class);
        registerGType(gst.gst_bin_get_type(), Bin.class);
        registerGType(gst.gst_clock_get_type(), Clock.class);
        registerGType(gst.gst_pipeline_get_type(), Pipeline.class);
        registerGType(gst.gst_bus_get_type(), Bus.class);
        registerGType(gst.gst_pad_get_type(), Pad.class);
        
        // GstMiniObject types
        registerGType(gst.gst_buffer_get_type(), Buffer.class);
        registerGType(gst.gst_message_get_type(), Message.class);
    }
    
}

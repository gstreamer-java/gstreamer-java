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

package org.gstreamer.io;

import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.OutputStream;
import org.gstreamer.BaseSink;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.FlowReturn;
import org.gstreamer.PadDirection;
import org.gstreamer.PadTemplate;
import org.gstreamer.lowlevel.BaseAPI;
import org.gstreamer.lowlevel.GObjectAPI;
import org.gstreamer.lowlevel.GObjectAPI.GClassInitFunc;
import org.gstreamer.lowlevel.GType;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 * @author wayne
 */
public class OutputStreamSink extends BaseSink {
    public static final GType TYPE;
    
    protected OutputStream os;
    
    public OutputStreamSink(final OutputStream os, String name) {
        super(gobj.g_object_new(OutputStreamSink.TYPE, "name", name));
        this.os = os;       
    }
    private static final BaseAPI.Render render = new BaseAPI.Render() {
        
        public FlowReturn callback(BaseSink sink, Buffer buffer) {
            OutputStream os = ((OutputStreamSink)sink).os;
            //System.out.println("render size=" + buffer.getSize());
            try {                
                byte[] tmp = new byte[buffer.getSize()];
                buffer.getByteBuffer().get(tmp);
                os.write(tmp);

                return FlowReturn.OK;
            } catch (IOException ex) {               
                return FlowReturn.ERROR;
            }            
        }
        
    };
    private static final GClassInitFunc classInit = new GClassInitFunc() {
        public void callback(Pointer g_class, Pointer class_data) {
            BaseAPI.GstBaseSinkClass base = new BaseAPI.GstBaseSinkClass(g_class);
            base.render = render;            
            base.write();            
        }
    };
    private static final GObjectAPI.GBaseInitFunc baseInit = new GObjectAPI.GBaseInitFunc() {

        public void callback(Pointer g_class) {
            PadTemplate template = new PadTemplate("sink", PadDirection.SINK, 
                Caps.anyCaps());
            gst.gst_element_class_add_pad_template(g_class, template);
        }
    };
    static {
        GObjectAPI.GTypeInfo info = new GObjectAPI.GTypeInfo();
        info.class_init = classInit;
        info.instance_init = null;
        info.class_size = (short)new BaseAPI.GstBaseSinkClass().size();
        info.instance_size = (short)new BaseAPI.GstBaseSinkStruct().size();        
        info.base_init = baseInit;        
        
        TYPE = gobj.g_type_register_static(BaseAPI.INSTANCE.gst_base_sink_get_type(), 
                "OutputStreamSink", info, 0);
    }
}

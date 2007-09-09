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

import org.gstreamer.*;
import com.sun.jna.Pointer;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gstreamer.lowlevel.BaseAPI;

import org.gstreamer.lowlevel.GObjectAPI;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import org.gstreamer.lowlevel.GObjectAPI.GClassInitFunc;
import org.gstreamer.lowlevel.GType;
import org.gstreamer.lowlevel.GstAPI.BufferStruct;
import org.gstreamer.lowlevel.GstAPI.GstSegmentStruct;

import static org.gstreamer.lowlevel.GstAPI.gst;

public class InputStreamSrc extends BaseSrc {
    
    public InputStreamSrc(final InputStream is, String name) {
        super(gobj.g_object_new(InputStreamSrc.type, "name", name));
        this.is = is;       
    }
    
    private static void readFully(InputStream is, BufferStruct dst) throws IOException {
        if (is instanceof FileInputStream && ((FileInputStream) is).getChannel() != null) {

            FileChannel channel = ((FileInputStream) is).getChannel();            
            ByteBuffer buffer = dst.data.getByteBuffer(0, dst.size);
            int total = 0;
            
            long position = dst.offset;            
            while (buffer.hasRemaining()) {                
                int n = channel.read(buffer, position);
                if (n < 0) {
                    if (total < 1) { 
                        throw new EOFException();
                    }
                    
                    break;
                }                
                position += n;
                total += n;                
            }
            // Adjust the endpoint in the case of EOF
            dst.offset_end = position;            
        } else {
            byte[] tmp = new byte[dst.size];
            ByteBuffer buffer = dst.data.getByteBuffer(0, dst.size);
            int total = 0;            
            
            while (buffer.hasRemaining()) {                
                int n = is.read(tmp, 0, Math.min(tmp.length, buffer.remaining()));
                if (n < 0) {
                    if (total < 1) { 
                        throw new EOFException();
                    }
                    
                    break;
                }
                buffer.put(tmp, 0, n);
                total += n;                
            }
            // Adjust the endpoint in the case of EOF
            dst.offset_end = dst.offset + total;
        }
    }
    
    private FlowReturn srcCreate(long offset, int size, Pointer bufRef) {        
        Buffer buffer = new Buffer(size);  
        //System.out.println("read(" + offset + ", " + size + ")");
        buffer.setOffset(offset);        
        try {            
            readFully(is, buffer.struct);
            buffer.struct.timestamp = System.currentTimeMillis();
            //System.out.println("Sending buf=" + buf);
            buffer.struct.write();            
            bufRef.setPointer(0, buffer.struct.getPointer());
            buffer.disown();
            return FlowReturn.OK;
        } catch (IOException ex) {
            return FlowReturn.UNEXPECTED;
        }        
    }
    public boolean isSeekable() {
        return is instanceof FileInputStream && ((FileInputStream) is).getChannel() != null; 
    }
    private InputStream is;
    private static final BaseAPI.Create srcCreate = new BaseAPI.Create() {

        public FlowReturn callback(BaseSrc src, long offset, int size, Pointer bufRef) {
            return ((InputStreamSrc)src).srcCreate(offset, size, bufRef);            
        }
        
    };
    private static final BaseAPI.BooleanFunc1 isSeekable = new BaseAPI.BooleanFunc1() {

        public boolean callback(Element element) {
            return ((InputStreamSrc) element).isSeekable();
        }
    };
    private static final BaseAPI.Seek doSeek = new BaseAPI.Seek() {
       
        public boolean callback(BaseSrc src, GstSegmentStruct segment) {
            InputStream is = ((InputStreamSrc)src).is;
//            System.out.println("Seeking to " + segment);
            if (is instanceof FileInputStream && ((FileInputStream) is).getChannel() != null) {
                try {
                    FileChannel channel = ((FileInputStream) is).getChannel();
                    channel.position(segment.start);
                    return true;
                } catch (IOException ex) {
                    Logger.getLogger(InputStreamSrc.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
            // Always return true for non-seekable channels, otherwise the sink 
            // doesn't receive any segments
            return true;
        }        
    };
    private static final GClassInitFunc classInit = new GClassInitFunc() {
        public void callback(Pointer g_class, Pointer class_data) {
            BaseAPI.GstBaseSrcClass base = new BaseAPI.GstBaseSrcClass(g_class);
            base.create = srcCreate;
            base.is_seekable = isSeekable;
            base.seek = doSeek;
            base.write();            
        }
    };
    private static final GObjectAPI.GBaseInitFunc baseInit = new GObjectAPI.GBaseInitFunc() {

        public void callback(Pointer g_class) {
            PadTemplate template = new PadTemplate("src", PadDirection.SRC, 
                Caps.anyCaps());
            gst.gst_element_class_add_pad_template(g_class, template);
        }
    };
    public static final GType type;
    static {
        GObjectAPI.GTypeInfo info = new GObjectAPI.GTypeInfo();
        info.class_init = classInit;
        info.instance_init = null;
        info.class_size = (short)new BaseAPI.GstBaseSrcClass().size();
        info.instance_size = (short)new BaseAPI.GstBaseSrcStruct().size();
        //info.class_size = 1024;
        info.base_init = baseInit;
        //info.instance_size = 1024;        
        
        type = gobj.g_type_register_static(BaseAPI.INSTANCE.gst_base_src_get_type(), 
                "InputStreamSrc", info, 0);
    }
}
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
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import org.gstreamer.lowlevel.GstAPI.BufferStruct;
import org.gstreamer.lowlevel.GstAPI.GstSegmentStruct;

import static org.gstreamer.lowlevel.GstAPI.gst;

public class InputStreamSrc extends CustomSrc {
    private InputStream is;
    public InputStreamSrc(final InputStream is, String name) {
        super(InputStreamSrc.class, name);  
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
    
    @Override
    protected FlowReturn srcFillBuffer(long offset, int size, Buffer buffer) {        
//        System.out.println("InputStreamSrc.srcFillBuffer(offset=" + offset + ", size=" + size + ")");
        try {            
            readFully(is, buffer.struct);
            return FlowReturn.OK;
        } catch (IOException ex) {
            return FlowReturn.UNEXPECTED;
        }        
    }
    @Override
    public boolean srcIsSeekable() {
//        System.out.println("InputStreamSrc.isSeekable");
        // This doesn't work (yet)
//        return is instanceof FileInputStream && ((FileInputStream) is).getChannel() != null; 
        return false;
    }
    
    @Override
    protected boolean srcSeek(GstSegmentStruct segment) {            
//        System.out.println("Seeking to " + segment);
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
}
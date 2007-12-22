/*
 * Copyright (c) 2007 Wayne Meissner
 *
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

import org.gstreamer.elements.CustomSrc;
import org.gstreamer.*;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gstreamer.lowlevel.GstAPI.GstSegmentStruct;

import static org.gstreamer.lowlevel.GstAPI.gst;

public class ReadableByteChannelSrc extends CustomSrc {
    private final ReadableByteChannel channel;
    private FileChannel fileChannel;
    
    public ReadableByteChannelSrc(ReadableByteChannel src, String name) {
        super(ReadableByteChannelSrc.class, name);  
        this.channel = src;
        if (channel instanceof FileChannel) {
            this.fileChannel = (FileChannel) channel;
        }
    }
    
    private void readFully(Buffer buffer) throws IOException {
        ByteBuffer dstBuffer = buffer.getByteBuffer();
        int total = 0;
        long position = buffer.getOffset();

        while (dstBuffer.hasRemaining()) {
            int n = 0;
            if (fileChannel != null) {
                n = fileChannel.read(dstBuffer, position);
            } else {
                n = channel.read(dstBuffer);
            }
            System.out.println("Read in " + n + " bytes");
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
        buffer.setLastOffset(position);       

    }
    
    @Override
    protected FlowReturn srcFillBuffer(long offset, int size, Buffer buffer) {        
//        System.out.println("InputStreamSrc.srcFillBuffer(offset=" + offset + ", size=" + size + ")");
        try {            
            readFully(buffer);
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
        if (fileChannel != null) {
            try {
                fileChannel.position(segment.start);
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
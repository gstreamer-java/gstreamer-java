/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gstreamer.Buffer;
import org.gstreamer.ClockTime;
import org.gstreamer.FlowReturn;
import org.gstreamer.Format;
import org.gstreamer.elements.CustomSrc;
import org.gstreamer.lowlevel.GstAPI.GstSegmentStruct;


public class ReadableByteChannelSrc extends CustomSrc {
    private final ReadableByteChannel channel;
    private FileChannel fileChannel;
    private long channelPosition = 0;
    public ReadableByteChannelSrc(ReadableByteChannel src, String name) {
        super(ReadableByteChannelSrc.class, name);  
        this.channel = src;
        if (channel instanceof FileChannel) {
            this.fileChannel = (FileChannel) channel;
        }
        setFormat(Format.BYTES);
    }
    
    private void readFully(long offset, long size, Buffer buffer) throws IOException {
        ByteBuffer dstBuffer = buffer.getByteBuffer();
        int total = 0;
        long position = fileChannel != null ? offset : channelPosition;
        buffer.setOffset(position);
        while (dstBuffer.hasRemaining()) {
            int n = 0;
            if (fileChannel != null) {
                n = fileChannel.read(dstBuffer, position);
            } else {
                n = channel.read(dstBuffer);
            }
//            System.out.println("Read in " + n + " bytes");
            if (n < 0) {
                if (total < 1) { 
                    throw new EOFException();
                }
                break;
            }                
            position += n;
            total += n;                
        }
        channelPosition = position;
        
        // Adjust the endpoint in the case of EOF
        buffer.setLastOffset(position);
        buffer.setTimestamp(ClockTime.NONE);
    }
    
    @Override
    protected FlowReturn srcFillBuffer(long offset, int size, Buffer buffer) {        
//        System.out.println("InputStreamSrc.srcFillBuffer(offset=" + offset + ", size=" + size + ")");
        try {            
            readFully(offset, size, buffer);
            return FlowReturn.OK;
        } catch (IOException ex) {
//            System.out.println(ex);
            return FlowReturn.UNEXPECTED;
        }        
    }
    @Override
    public boolean srcIsSeekable() {
//        System.out.println("InputStreamSrc.isSeekable");
        return fileChannel != null;
    }
    
    @Override
    protected boolean srcSeek(GstSegmentStruct segment) {            
//        System.out.println("Seeking to " + segment);
        if (fileChannel != null) {
            try {
                fileChannel.position(segment.start);
                segment.last_stop = segment.start;
                segment.time = segment.start;
                segment.write();
                return true;
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        // Always return true for non-seekable channels, otherwise the sink 
        // doesn't receive any segments
        return true;
    }

    @Override
    protected long srcGetSize() {
        if (fileChannel != null) {
            try {
                return fileChannel.size();
            } catch (IOException ex) {
                Logger.getLogger(ReadableByteChannelSrc.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
        // We can't figure out the size of non-filechannel files
        return -1;
    }
}
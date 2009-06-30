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

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import org.gstreamer.Buffer;
import org.gstreamer.FlowReturn;
import org.gstreamer.elements.CustomSink;

/**
 *
 * @author wayne
 */
public class WriteableByteChannelSink extends CustomSink {
    
    private WritableByteChannel channel;
    
    public WriteableByteChannelSink(final WritableByteChannel channel, String name) {
        super(WriteableByteChannelSink.class, name);
        this.channel = channel;
    }
    
    @Override
    protected final FlowReturn sinkRender(Buffer buffer) throws IOException {
        channel.write(buffer.getByteBuffer());
        return FlowReturn.OK;
    }   
}

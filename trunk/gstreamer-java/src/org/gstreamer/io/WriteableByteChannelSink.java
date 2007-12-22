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

package org.gstreamer.io;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import org.gstreamer.Buffer;
import org.gstreamer.elements.CustomSink;
import org.gstreamer.FlowReturn;

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
        System.out.println(getClass().getSimpleName() + ".sinkRender");
        channel.write(buffer.getByteBuffer());
        return FlowReturn.OK;
    }   
}

/* 
 * Copyright (c) 2013 Levente Farkas <lfarkas@lfarkas.org>
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

package org.gstreamer;

/**
 * A set of flags that can be provided to the gst_buffer_copy_into() function to specify which items should be copied.
 */
public final class BufferCopyFlags {
    /** copy nothing */
    public final static int NONE = 0;
    
    /** flag indicating that buffer flags should be copied */
    public final static int FLAGS = 1 << 0;
    
    /** flag indicating that buffer pts, dts, duration, offset and offset_end should be copied */
    public final static int TIMESTAMPS = 1 << 1;
    
    /** flag indicating that buffer meta should be copied */
    public final static int META = 1 << 2;
    
    /** flag indicating that buffer memory should be copied and appended to already existing memory */
    public final static int MEMORY = 1 << 3;
    
    /** flag indicating that buffer memory should be merged */
    public final static int MERGE = 1 << 4;
}

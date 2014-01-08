/* 
 * Copyright (c) 2009 Levente Farkas
 * Copyright (c) 2007, 2008 Wayne Meissner
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

package org.gstreamer.lowlevel;

import java.util.Arrays;
import java.util.List;

import org.gstreamer.MiniObject;
import org.gstreamer.lowlevel.GObjectAPI.GTypeInstance;
import org.gstreamer.lowlevel.annotations.CallerOwnsReturn;
import org.gstreamer.lowlevel.annotations.Invalidate;

import com.sun.jna.Pointer;

/**
 * GstMiniObject functions
 */
public interface GstMiniObjectAPI extends com.sun.jna.Library {
    GstMiniObjectAPI GSTMINIOBJECT_API = GstNative.load(GstMiniObjectAPI.class);

    void gst_mini_object_ref(MiniObject ptr);
    void gst_mini_object_unref(MiniObject ptr);
    void gst_mini_object_unref(Pointer ptr);
    @CallerOwnsReturn Pointer ptr_gst_mini_object_copy(MiniObject mini_object);
    @CallerOwnsReturn MiniObject gst_mini_object_copy(MiniObject mini_object);
    boolean gst_mini_object_is_writable(MiniObject mini_object);
    /* FIXME - invalidate the argument, and return a MiniObject */
    @CallerOwnsReturn Pointer ptr_gst_mini_object_make_writable(@Invalidate MiniObject mini_object);
    @CallerOwnsReturn MiniObject gst_mini_object_make_writable(@Invalidate MiniObject mini_object);
    
    public static final class MiniObjectStruct extends com.sun.jna.Structure {
        public volatile GTypeInstance instance;
        public volatile int refcount;
        public volatile int flags;
        public volatile Pointer _gst_reserved;
        
        /** Creates a new instance of GstMiniObjectStructure */
        public MiniObjectStruct() {}
        public MiniObjectStruct(Pointer ptr) {
            useMemory(ptr);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{
                "instance", "refcount", "flags",
                "_gst_reserved"
            });
        }
        
        
    }
}

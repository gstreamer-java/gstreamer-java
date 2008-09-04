/*
 * Copyright (c) 2008 Wayne Meissner
 * Copyright (C) 2007 David Schleef <ds@schleef.org>
 *           (C) 2008 Wim Taymans <wim.taymans@gmail.com>
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

package org.gstreamer.elements;

import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.lowlevel.AppAPI;

/**
 * Enables an application to feed buffers into a pipeline.
 */
public class AppSrc extends BaseSrc {
    public enum Type {
        STREAM,
        SEEKABLE,
        RANDOM_ACCESS;
    }
    public AppSrc(Initializer init) {
        super(init);
    }
    public void pushBuffer(Buffer buffer) {
        AppAPI.INSTANCE.gst_app_src_push_buffer(this, buffer);
    }
    @Override
    public void setCaps(Caps caps) {
        AppAPI.INSTANCE.gst_app_src_set_caps(this, caps);
    }
    public void flush() {
        AppAPI.INSTANCE.gst_app_src_flush(this);
    }
    public void endOfStream() {
        AppAPI.INSTANCE.gst_app_src_end_of_stream(this);
    }
}

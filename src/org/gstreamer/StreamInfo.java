/*
 * Copyright (c) 2009 Andres Colubri
 * Copyright (c) 2008 Wayne Meissner
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

import com.sun.jna.Pointer;
//import org.gstreamer.lowlevel.GObjectAPI;
//import org.gstreamer.lowlevel.GType;

/**
 * Object containing specific meta information such as width/height/framerate of
 * video streams or samplerate/number of channels of audio. Each stream info object
 * has the following properties:
 * "object" (GstObject) (the decoder source pad usually)
 * "type" (enum) (if this is an audio/video/subtitle stream)
 * "decoder" (string) (name of decoder used to decode this stream)
 * "mute" (boolean) (to mute or unmute this stream)
 * "caps" (GstCaps) (caps of the decoded stream)
 * "language-code" (string) (ISO-639 language code for this stream, mostly used for audio/subtitle streams)
 * "codec" (string) (format this stream was encoded in)
 */
public class StreamInfo extends GObject {
    //private static final GObjectAPI gst = GObjectAPI.INSTANCE;

    /**
     * For internal gstreamer-java use only
     *
     * @param init initialization data
     */
    public StreamInfo(Initializer init) {
        super(init);
        throw new IllegalArgumentException("Cannot instantiate this class");
    }

    public StreamInfo(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(initializer(ptr, needRef, ownsHandle));
    }

    public Object getObject() {
        return get("object");
    }

    // TODO: finish the StreamInfo API.
    /*
    public GType getType() {
        return get("type");
    }

    public String getDecoder() {
        return get("decoder");
    }

    public boolean getMute() {
        return get("mute");
    }

    public Caps getCaps() {
        return get("caps");
    }

    public String getLanguageCode() {
        return get("language-code");
    }

    public String getCodec() {
        return get("codec");
    }
     */

}

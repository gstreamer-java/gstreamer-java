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

package org.gstreamer;

/**
 * Flags to be used with gst_element_seek() or gst_event_new_seek(). 
 * <p>
 * All flags can be used together.
 *<p>
 * A non flushing seek might take some time to perform as the currently
 * playing data in the pipeline will not be cleared.
 *<p>
 * An accurate seek might be slower for formats that don't have any indexes
 * or timestamp markers in the stream. Specifying this flag might require a
 * complete scan of the file in those cases.
 *<p>
 * When performing a segment seek: after the playback of the segment completes,
 * no EOS will be emitted by the element that performed the seek, but a
 * #GST_MESSAGE_SEGMENT_DONE message will be posted on the bus by the element.
 * When this message is posted, it is possible to send a new seek event to
 * continue playback. With this seek method it is possible to perform seemless
 * looping or simple linear editing.
 */
public enum SeekFlags {
    /** No flag. */
    
    NONE(0),
    /** Flush pipeline. */
    FLUSH(1 << 0),
    /** 
     * Accurate position is requested, this might be considerably slower for some formats. 
     */
    ACCURATE(1 << 1),
    
    /**
     * Seek to the nearest keyframe. This might be faster but less accurate.
     */
    KEY_UNIT(1 << 2),
    /** Perform a segment seek. */
    SEGMENT(1 << 3);
    
    private SeekFlags(int value) {
        this.value = value;
    }
    
    /**
     * Get the integer value of the enum.
     * @return The integer value for this enum.
     */
    public int intValue() {
        return value;
    }
    
    /**
     * Returns the enum constant of this type with the specified integer value.
     * @param value Integer value that corresponds to one of the constants.
     * @return Enum constant that maps to the integer.
     */
    public static SeekFlags valueOf(int value) {
        for (SeekFlags f : values()) {
            if (f.value == value) {
                return f;
            }
        }
        throw new IllegalArgumentException("Invalid SeekFlags value: " + value);
    }
    private int value;
}

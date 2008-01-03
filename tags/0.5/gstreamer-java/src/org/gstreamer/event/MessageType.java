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

package org.gstreamer.event;

public enum MessageType {
    GST_MESSAGE_UNKNOWN(0),
    GST_MESSAGE_EOS(1 << 0),
    GST_MESSAGE_ERROR(1 << 1),
    GST_MESSAGE_WARNING(1 << 2),
    GST_MESSAGE_INFO(1 << 3),
    GST_MESSAGE_TAG(1 << 4),
    GST_MESSAGE_BUFFERING(1 << 5),
    GST_MESSAGE_STATE_CHANGED(1 << 6),
    GST_MESSAGE_STATE_DIRTY(1 << 7),
    GST_MESSAGE_STEP_DONE(1 << 8),
    GST_MESSAGE_CLOCK_PROVIDE(1 << 9),
    GST_MESSAGE_CLOCK_LOST(1 << 10),
    GST_MESSAGE_NEW_CLOCK(1 << 11),
    GST_MESSAGE_STRUCTURE_CHANGE(1 << 12),
    GST_MESSAGE_STREAM_STATUS(1 << 13),
    GST_MESSAGE_APPLICATION(1 << 14),
    GST_MESSAGE_ELEMENT(1 << 15),
    GST_MESSAGE_SEGMENT_START(1 << 16),
    GST_MESSAGE_SEGMENT_DONE(1 << 17),
    GST_MESSAGE_DURATION(1 << 18),
    GST_MESSAGE_LATENCY(1 << 19),
    GST_MESSAGE_ANY(~0);
    
    MessageType(int value) {
        this.value = value; 
    }
    public static MessageType valueOf(int messageType) {
        for (MessageType m : values()) {
            if (m.value == messageType) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid GstMessageType(" + messageType + ")");
    }
    public final int value;
}

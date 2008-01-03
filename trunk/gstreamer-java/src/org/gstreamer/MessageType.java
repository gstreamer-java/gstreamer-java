/* 
 * Copyright (C) 2008 Wayne Meissner
 * Copyright (C) 2004 Wim Taymans <wim@fluendo.com>
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

import org.gstreamer.lowlevel.IntegerEnum;

/**
 * The different message types that are available.
 */
public enum MessageType implements IntegerEnum {
    /** An undefined message */
    UNKNOWN(0),
    /** 
     * end-of-stream reached in a pipeline. The application will
     * only receive this message in the PLAYING state and every time it sets a
     * pipeline to PLAYING that is in the EOS state. The application can perform a
     * flushing seek in the pipeline, which will undo the EOS state again. 
     */
    EOS(1 << 0),
    /** 
     * An error occured. Whe the application receives an error
     * message it should stop playback of the pipeline and not assume that more
     * data will be played.
     */
    ERROR(1 << 1),
    
    /** A warning occured. */
    WARNING(1 << 2),
    /** An info message occured. */
    INFO(1 << 3),
    /** A tag was found. */
    TAG(1 << 4),
    /**
     * The pipeline is buffering. When the application
     * receives a buffering message in the PLAYING state for a non-live pipeline it
     * must PAUSE the pipeline until the buffering completes, when the percentage
     * field in the message is 100%. For live pipelines, no action must be
     * performed and the buffering percentage can be used to inform the user about
     * the progress.
     */
    BUFFERING(1 << 5),
    /** A state change happened */
    STATE_CHANGED(1 << 6),
    /** an element changed state in a streaming thread. This message is deprecated.*/
    STATE_DIRTY(1 << 7),
    /** a framestep finished. This message is not yet implemented. */
    STEP_DONE(1 << 8),
    /**
     * an element notifies its capability of providing a clock. This message is 
     * used internally and never forwarded to the application.
     */
    CLOCK_PROVIDE(1 << 9),
    /** 
     * The current clock as selected by the pipeline became unusable. The pipeline 
     * will select a new clock on the next PLAYING state change.
     */
    CLOCK_LOST(1 << 10),
    /** A new clock was selected in the pipeline. */
    NEW_CLOCK(1 << 11),
    /** The structure of the pipeline changed. Not implemented yet. */
    STRUCTURE_CHANGE(1 << 12),
    /**
     * Status about a stream, emitted when it starts, stops, errors, etc.. Not implemented yet.
     */
    STREAM_STATUS(1 << 13),
    /** Message posted by the application, possibly via an application-specific element. */
    APPLICATION(1 << 14),
    /** Element specific message, see the specific element's documentation */
    ELEMENT(1 << 15),
    /**
     * Pipeline started playback of a segment. This message is used internally and 
     * never forwarded to the application.
     */
    SEGMENT_START(1 << 16),
    /**
     * Pipeline completed playback of a segment. This message is forwarded to the 
     * application after all elements that posted {@link SEGMENT_START}
     * have posted a GST_MESSAGE_SEGMENT_DONE message.
     */
    SEGMENT_DONE(1 << 17),
    /**
     * The duration of a pipeline changed. The application can get the new duration 
     * with a duration query.
     */
    DURATION(1 << 18),
    /**
     * Posted by elements when their latency changes. The pipeline will calculate 
     * and distribute a new latency. Since: 0.10.12
     */
    LATENCY(1 << 19),
    /**
     * Posted by elements when they start an ASYNC state
     * change. This message is not forwarded to the application but is used
     * internally. Since: 0.10.13. 
     */
    ASYNC_START(1 << 20),
    /**
     * Posted by elements when they complete an ASYNC state change. The application 
     * will only receive this message from the toplevel pipeline. Since: 0.10.13
     */
    ASYNC_DONE(1 << 21),
    ANY(~0);
    MessageType(int type) {
        this.type = type;
    }
    public int intValue() {
        return type;
    }
    private final int type;
}

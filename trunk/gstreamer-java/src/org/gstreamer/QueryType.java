/* 
 * Copyright (C) 2008 Wayne Meissner
 * Copyright (C) 1999,2000 Erik Walthinsen <omega@cse.ogi.edu>
 *                    2000 Wim Taymans <wim.taymans@chello.be>
 *                    2005 Wim Taymans <wim@fluendo.com>
 * 
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
 * Standard predefined Query types
 */
public enum QueryType {
    /** invalid query type */
    NONE,
    /** current position in stream */
    POSITION,
    /** total duration of the stream */
    DURATION,
    /** latency of stream */
    LATENCY,
    /** current jitter of stream */
    JITTER,
    /** current rate of the stream */
    RATE,
    /** seeking capabilities */
    SEEKING,
    /** segment start/stop positions */
    SEGMENT,
    /** convert values between formats */
    CONVERT,
    /** query supported formats for convert */
    FORMATS
}

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.gstreamer;

/**
 *
 */
public class Time {
    
    /**
     * Creates a new instance of Time
     */
    public Time(long nano) {
        nanoseconds = nano;
        seconds = nano / 1000000000;
        
    }
    public long getHours() {
        return (seconds / 3600) % 24;
    }
    public long getMinutes() {
        return (seconds / 60) % 60;
    }
    public long getSeconds() {
        return seconds % 60;
    }
    public long nanoseconds() {
        return nanoseconds;
    }

    public String toString() {
        return String.format("%02d:%02d:%02d", getHours(), getMinutes(), getSeconds());
    }
    public static final long NANOSECONDS = 1000000000L;
    private long seconds;
    private long nanoseconds;
}

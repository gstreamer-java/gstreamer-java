/*
 * Copyright (c) 2007 Wayne Meissner
 *
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
public class Time extends org.gstreamer.lowlevel.NativeValue {
    public final static Time NONE = new Time(-1);
    public final static Time ZERO = new Time(0);

    /**
     * Creates a new instance of Time
     * 
     * @param nanoseconds The length of time this object represents, in nanoseconds.
     */
    public Time(long nanoseconds) {
        this.nanoseconds = nanoseconds;
    }
    
    /**
     * Get the hours component of the total time.
     * 
     * @return The hours component of the total time.
     */
    public long getHours() {
        return (longValue() / NANOSECONDS / 3600) % 24;
    }
    
    /**
     * Get the minutes component of the total time.
     * 
     * @return The minutes component of the total time.
     */
    public long getMinutes() {
        return (longValue() / NANOSECONDS / 60) % 60;
    }
    
    /**
     * Get the seconds component of the total time.
     * 
     * @return The seconds component of the total time.
     */
    public long getSeconds() {
        return (longValue() / NANOSECONDS) % 60;
    }
    
    /**
     * Get the nanosecond component of the total time.
     * 
     * @return The nanoseconds component of the total time.
     */
    public long getNanoSeconds() {
        return nanoseconds % NANOSECONDS;
    }
    
    /**
     * Get the native GstTime represented by this object.
     * 
     * @return The length of time this object represents, in nanoseconds.
     */
    public long longValue() {
        return nanoseconds;
    }
    protected Object nativeValue() {
        return new Long(nanoseconds);
    }
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", getHours(), getMinutes(), getSeconds());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Time)) {
            return false;
        }
        return ((Time) obj).nanoseconds == nanoseconds;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.nanoseconds ^ (this.nanoseconds >>> 32));
        return hash;
    }
    
    public static final long NANOSECONDS = 1000000000L;
    private long nanoseconds;
}

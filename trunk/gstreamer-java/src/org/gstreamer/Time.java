/*
 * Time.java
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
        return (seconds / 3600) % (24);
    }
    public long getMinutes() {
        return (seconds / 60) % 60;
    }
    public long getSeconds() {
        return seconds % 60;
    }
    public long getNanoSeconds() {
        return nanoseconds;
    }
    public String toString() {
        return String.format("%02d:%02d:%02d", getHours(), getMinutes(), getSeconds());       
    }
    private long seconds;
    private long nanoseconds;
}

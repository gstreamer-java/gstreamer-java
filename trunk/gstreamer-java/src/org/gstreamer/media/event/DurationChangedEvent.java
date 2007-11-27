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
package org.gstreamer.media.event;

import org.gstreamer.Time;
import org.gstreamer.media.MediaPlayer;

/**
 * Based on code from FMJ by Ken Larson
 */
public class DurationChangedEvent extends MediaEvent {

    final Time duration;

    public DurationChangedEvent(MediaPlayer from, Time newDuration) {
        super(from);
        this.duration = newDuration;
    }

    public Time getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + getSource() + ",duration=" + duration + "]";

    }
}

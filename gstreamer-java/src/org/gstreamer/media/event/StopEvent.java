package org.gstreamer.media.event;

import org.gstreamer.State;
import org.gstreamer.Time;
import org.gstreamer.media.MediaPlayer;

/**
 * Based on code from FMJ by Ken Larson
 */
public class StopEvent extends TransitionEvent {

    private Time mediaTime;

    public StopEvent(MediaPlayer from, State previous, State current, State target, Time mediaTime) {
        super(from, previous, current, target);
        this.mediaTime = mediaTime;
    }

    public Time getMediaTime() {
        return mediaTime;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + getSource() + ",previousState=" + getPreviousState() +
                ",currentState=" + getCurrentState() + ",targetState=" + getPendingState() +
                ",mediaTime=" + mediaTime + "]";
    }
}

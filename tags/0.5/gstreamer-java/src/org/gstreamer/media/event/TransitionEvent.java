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

package org.gstreamer.media.event;

import org.gstreamer.State;
import org.gstreamer.media.MediaPlayer;

/**
 * Based on code from FMJ by Ken Larson
 */
public class TransitionEvent extends MediaEvent {

    public final State previousState;
    public final State currentState;
    public final State pendingState;

    public TransitionEvent(MediaPlayer player, State previousState, State currentState, 
            State pendingState) {
        super(player);
        this.previousState = previousState;
        this.currentState = currentState;
        this.pendingState = pendingState;
    }

    public State getPreviousState() {
        return previousState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public State getPendingState() {
        return pendingState;
    }
    public State getTargetState() {
        return pendingState;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + getSource() + ",previousState=" + previousState +
                ",currentState=" + currentState + ",targetState=" + pendingState + "]";
    }
}

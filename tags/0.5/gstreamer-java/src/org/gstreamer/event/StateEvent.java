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

import org.gstreamer.GstObject;
import org.gstreamer.State;


/**
 *
 */
public class StateEvent extends java.util.EventObject {
    
    /**
     * Creates a new instance of State
     */
    public StateEvent(GstObject src, State o, State n, State p) {
        super(src);
        oldState = o;
        newState = n;
        pendingState = p;
    }
    public final State oldState, newState, pendingState;
}

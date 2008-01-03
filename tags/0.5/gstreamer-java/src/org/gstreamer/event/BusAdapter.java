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

import org.gstreamer.Format;
import org.gstreamer.TagList;

/**
 *
 */
public class BusAdapter implements BusListener {
    
    /**
     * Creates a new instance of BusAdapter
     */
    public BusAdapter() {
    }
    public void errorEvent(ErrorEvent e) {}
    public void warningEvent(ErrorEvent e) {}
    public void infoEvent(ErrorEvent e) {}
    public void stateEvent(StateEvent e) {}
    public void eosEvent() {}
    public void tagEvent(TagList l) { }
    public void bufferingEvent(int percent) {}
    public void durationEvent(Format format, long percent) {}
    public void segmentStart(Format format, long position) {}
    public void segmentDone(Format format, long position) {}
}

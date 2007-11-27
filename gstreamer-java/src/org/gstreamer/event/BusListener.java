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

package org.gstreamer.event;

import org.gstreamer.Format;
import org.gstreamer.TagList;

/**
 *
 */
public interface BusListener {
    public void errorEvent(ErrorEvent e);
    public void warningEvent(ErrorEvent e);
    public void infoEvent(ErrorEvent e);
    public void stateEvent(StateEvent e);
    public void eosEvent();
    public void tagEvent(TagList l);
    public void bufferingEvent(int percent);
    public void durationEvent(Format format, long percent);
    public void segmentStart(Format format, long position);
    public void segmentDone(Format format, long position);
}

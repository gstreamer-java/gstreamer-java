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


public class MediaAdapter implements MediaListener, java.util.EventListener {

    public void pause(StopEvent evt) { }
    public void start(StartEvent evt) { }
    public void stop(StopEvent evt) { }
    public void endOfMedia(EndOfMediaEvent evt) { }
    public void positionChanged(PositionChangedEvent evt) { }
    public void durationChanged(DurationChangedEvent evt) { }

}

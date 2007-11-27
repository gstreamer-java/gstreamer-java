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

package org.gstreamer.media;

import java.net.URI;
import org.gstreamer.Pipeline;
import org.gstreamer.media.event.MediaListener;

public interface MediaPlayer {
    Pipeline getPipeline();
    void setURI(URI uri);
    void play();
    void pause();
    void stop();
    void addMediaListener(MediaListener listener);
    void removeMediaListener(MediaListener listener);
}

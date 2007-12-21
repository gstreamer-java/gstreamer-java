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

package org.gstreamer;

public class StaticPadTemplate {

    private final String templateName;
    private final PadDirection direction;
    private final PadPresence presence;
    private final Caps caps;

    StaticPadTemplate(String templateName, PadDirection direction, PadPresence presence,
            Caps caps) {
        this.templateName = templateName;
        this.direction = direction;
        this.presence = presence;
        this.caps = caps;
    }

    /**
     * Get the name of the template.
     * @return The name of the template.
     */
    public String getName() {
        return templateName;
    }

    /**
     * Get the direction (SINK, SRC) of the template.
     * @return The {@link PadDirection} of the template.
     */
    public PadDirection getDirection() {
        return direction;
    }

    /**
     * Get the presence (ALWAYS, SOMETIMES, REQUEST) of the template.
     * @return The {@link PadPresence} of this template.
     */
    public PadPresence getPresence() {
        return presence;
    }
    /**
     * Get the {@link Caps} of the template.
     * @return The {@link Caps} for this template.
     */
    public Caps getCaps() {
        return caps;
    }
}

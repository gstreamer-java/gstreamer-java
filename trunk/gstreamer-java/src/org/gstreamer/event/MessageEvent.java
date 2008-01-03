/* 
 * Copyright (c) 2007, 2008 Wayne Meissner
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

import org.gstreamer.Element;
import org.gstreamer.GstObject;

/**
 *
 */
public class MessageEvent extends java.util.EventObject {
    
    /** Creates a new instance of GstError */
    public MessageEvent(GstObject src, int code, String msg) {
        super(src);
        this.code = code;
        this.message = msg;
    }
    public Element getElement() {
        return (Element) getSource();
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    
    public final int code;
    public final String message;
}

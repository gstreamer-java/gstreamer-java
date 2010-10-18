/* 
 * Copyright (c) 2010 DHoyt <david.g.hoyt@gmail.com>
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.elements;

/**
 * Utility {@link org.gstreamer.Element} to automatically identify media stream types and hook
 * up elements.
 */
public class DecodeBin2 extends DecodeBin {
    /**
     * Creates a new DecodeBin2.
     * 
     * @param name The name used to identify this DecodeBin.
     */
    public DecodeBin2(String name) {
        super(makeRawElement("decodebin2", name));
    }
    
    public DecodeBin2(Initializer init) {
        super(init);
    }
}

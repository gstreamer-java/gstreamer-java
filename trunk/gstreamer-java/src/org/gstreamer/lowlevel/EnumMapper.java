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

package org.gstreamer.lowlevel;

import java.util.EnumSet;

/**
 * Maps to and from native int and an Enum value.
 * @author wayne
 */
public class EnumMapper {
    private static final EnumMapper mapper = new EnumMapper();
    public static EnumMapper getInstance() {
        return mapper;
    }
    
    public int intValue(Enum value) {
        return value instanceof IntegerEnum ? ((IntegerEnum) value).intValue() : value.ordinal();
    }
    public <E extends Enum<E>> E valueOf(int value, Class<E> enumClass) {
        //
        // Just loop around all the enum values and find one that matches.
        // Storing the values in a Map might be faster, but by the time you deal
        // with locking overhead, its hardly worth it for small enums.
        // 
        if (IntegerEnum.class.isAssignableFrom(enumClass)) {
            for (E e : EnumSet.allOf(enumClass)) {
                if (((IntegerEnum) e).intValue() == value) {
                    return e;
                }
            }
        } else {
            for (E e : EnumSet.allOf(enumClass)) {
                if (e.ordinal() == value) {
                    return e;
                }
            }
        }
        //
        // No value found - try to find the default value for unknown values.
        // This is useful for enums that aren't fixed in stone and/or where you
        // don't want to throw an Exception for an unknown value.
        //
        try {
            return Enum.valueOf(enumClass, "__UNKNOWN_NATIVE_VALUE");
        } catch (IllegalArgumentException ex) {      
            //
            // No default, so just give up and throw an exception
            //
            throw new IllegalArgumentException("No known Enum mapping for " + enumClass.getName());
        }
    }
}

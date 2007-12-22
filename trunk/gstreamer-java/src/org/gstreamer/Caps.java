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

package org.gstreamer;
import com.sun.jna.Pointer;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class Caps extends NativeObject {
    
    public static Caps emptyCaps() {
        return new Caps(gst.gst_caps_new_empty());
    }
    public static Caps anyCaps() {
        return new Caps(gst.gst_caps_new_any());
    }
    
    public Caps() {
        this(gst.gst_caps_new_empty());
    }
    public Caps(String caps) {
        this(gst.gst_caps_from_string(caps));
    }
    public Caps(Caps caps) {
        this(gst.gst_caps_copy(caps));
    }
    Caps(Pointer ptr) {
        this(ptr, false);
    }
    Caps(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    Caps(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    public int size() {
        return gst.gst_caps_get_size(this);
    }
    public Caps copy() {
        return new Caps(gst.gst_caps_copy(this));
    }
    
     /**
     * Creates a new {@link Caps} that contains all the formats that are in
     * either this Caps or the other Caps.
     * @param other The {@link Caps} to union with this one.
     * @return The new {@link Caps}
     */
    public Caps union(Caps other) {
        return gst.gst_caps_union(this, other);
    }
    
    /**
     * Creates a new {@link Caps} that contains all the formats that are common
     * to both this Caps and the other Caps.
     * 
     * @param other The {@link Caps} to intersect with this one.
     *
     * @return The new {@link Caps}
     */
    public Caps intersect(Caps other) {
        return gst.gst_caps_intersect(this, other);
    }
    
    /**
     * Subtracts the subtrahend Caps from this Caps.
     * 
     * <note>This function does not work reliably if optional properties for caps
     * are included on one caps and omitted on the other.</note>
     * @param subtrahend The {@link Caps} to subtract.
     * @return
     */
    public Caps subtract(Caps subtrahend) {
        return gst.gst_caps_subtract(this, subtrahend);
    }

    /**
     * Normalize the Caps.
     * 
     * Creates a new {@link Caps} that represents the same set of formats as
     * this Caps, but contains no lists.  Each list is expanded into separate
     * {@link Structure}s
     * 
     * @return The new {@link Caps}
     * @see Structure
     */
    public Caps normalize() {
        return gst.gst_caps_normalize(this);
    }
    
    /**
     * Merge another {@link Caps} with this one.
     * Appends the structures contained in the other Caps to this one, if they 
     * are not yet expressed by this Caps. The structures in other are not copied,
     * they are transferred to this Caps, and then other is freed.
     * If either caps is ANY, the resulting caps will be ANY.
     * @param other The other {@link Caps} to merge.
     */
    public void merge(Caps other) {
        gst.gst_caps_merge(this, other);
        other.disown();
    }
    
    public void merge(Structure struct) {
        gst.gst_caps_merge_structure(this, struct);
        struct.disown();
    }
    
    public void append(Structure struct) {
        gst.gst_caps_append_structure(this, struct);
        struct.disown();
    }
    public void setInteger(String field, Integer value) {
        gst.gst_caps_set_simple(this, field, value, null);
    }
    public Structure getStructure(int index) {
        return Structure.objectFor(gst.gst_caps_get_structure(this, index), false, false);
    }
    
    @Override
    public String toString() {
        return gst.gst_caps_to_string(this);
    }
    
    public static Caps objectFor(Pointer ptr, boolean needRef) {
        return new Caps(ptr, needRef, true);
    }
    
    protected void ref() {
        gst.gst_caps_ref(this);
    }
    protected void unref() {
        gst.gst_caps_unref(this);
    }
    protected void disposeNativeHandle(Pointer ptr) {
        gst.gst_caps_unref(ptr);
    }

    
}

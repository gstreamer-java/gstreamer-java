/*
 * Copyright (c) 2007 Wayne Meissner
 *
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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.gstreamer.lowlevel.GType;
import org.gstreamer.lowlevel.GstAPI;
import static org.gstreamer.lowlevel.GstAPI.gst;
import static org.gstreamer.lowlevel.GlibAPI.glib;


/**
 *
 */
public class TagList extends Structure {
    
    /**
     * Creates a new instance of TagList
     */
    TagList(Pointer ptr, boolean needRef) {
        this(ptr, needRef, true);
    }
    TagList(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
    }
    TagList(Pointer ptr) {
        this(ptr, false, true);
    }
    public String getString(Tag tag) {
        return getString(tag.getId(), 0);
    }
    public String getString(String tag) {
        return getString(tag, 0);
    }
    
    public String getString(String tag, int index) {
        ensureType(tag, GType.STRING);
        
        PointerByReference value = new PointerByReference();
        gst.gst_tag_list_get_string_index(this, tag, index, value);
        if (value.getValue() == null) {
            return null;
        }
        String ret = value.getValue().getString(0, false);
        glib.g_free(value.getValue());
        return ret;
    }
    public Number getNumber(String tag, int index) {
        GType type = getTagType(tag);
        if (GType.INT.equals(type)) {        
            return getInt(tag, index);
        } else if (GType.UINT.equals(type)) {        
            return getUInt(tag, index);
        } else if (GType.INT64.equals(type)) {        
            return getInt64(tag, index);
        } else {
            throw new IllegalArgumentException("Tag [" + tag + "] is not a number");
        }
    }
    public Integer getInt(String tag, int index) {
        ensureType(tag, GType.INT);
        IntByReference value = new IntByReference();
        gst.gst_tag_list_get_int_index(this, tag, index, value);
        return value.getValue();
    }
    public Integer getUInt(String tag, int index) {
        ensureType(tag, GType.UINT);
        IntByReference value = new IntByReference();
        gst.gst_tag_list_get_uint_index(this, tag, index, value);
        return value.getValue();
    }
    public Long getInt64(String tag, int index) {
        if (!isTagType(tag, GType.INT64)) {
            throw new IllegalArgumentException("Tag [" + tag + "] is not of type INT");
        }
        LongByReference value = new LongByReference();
        gst.gst_tag_list_get_int64_index(this, tag, index, value);
        return value.getValue();
    }
    private boolean isTagType(String tag, GType type) {
        return getTagType(tag).equals(type);
    }
    private void ensureType(String tag, GType type) {
        if (!isTagType(tag, type)) {
            throw new IllegalArgumentException("Tag [" + tag + "] is not of type " + type);
        }
    }
    public List<String> getTagNames() {
        final List<String> list = new LinkedList<String>();
        gst.gst_tag_list_foreach(this, new GstAPI.TagForeachFunc() {
            public void callback(Pointer ptr, Pointer tagPointer, Pointer user_data) {
                list.add(tagPointer.getString(0, false));
            }
        }, null);
        return list;
    }
    public Map<String, Object> getTags() {
        final Map<String, Object> m = new HashMap<String, Object>();
        for (String tag : getTagNames()) {  
            GType type = getTagType(tag);
            if (GType.STRING.equals(type)) {            
                m.put(tag, getString(tag));
            } else if (GType.INT.equals(type)) {
                m.put(tag, getInt(tag, 0));
            } else if (GType.UINT.equals(type)) {
                m.put(tag, getUInt(tag, 0));
            } else if (GType.INT64.equals(type)) {            
                m.put(tag, getInt64(tag, 0));
            } else {            
                System.out.println("Unknown type for tag " + tag);                
            }
        }
        return m;
    }
    public TagList merge(TagList list2, TagMergeMode mode) {
        return new TagList(gst.gst_tag_list_merge(this, list2, mode));
    }
    public static GType getTagType(String tag) {        

        GType type = tagTypeMap.get(tag);
        if (type != null) {
            return type;
        }
        tagTypeMap.put(tag, type = gst.gst_tag_get_type(tag));
        return type;
    }
    
    @Override
    protected void disposeNativeHandle(Pointer ptr) {
        gst.gst_tag_list_free(ptr);
    }
    static final Map<String, GType> tagTypeMap = Collections.synchronizedMap(new HashMap<String, GType>());
}

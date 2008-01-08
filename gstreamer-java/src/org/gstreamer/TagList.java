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
    public TagList(Initializer init) {
        super(init);
    }
    public TagList() {
        super(initializer(gst.gst_tag_list_new()));
    }
    public String getString(Tag tag) {
        return getTag(tag).toString();
    }
    public String getString(String tag) {
        return getTag(tag, 0).toString();
    }
    public Object getTag(String tag) {
        return getTag(tag, 0);
    }
    public Object getTag(Tag tag) {
        return getTag(tag, 0);
    }
    public Object getTag(String tag, int index) {
        GType type = getTagType(tag);
        if (GType.STRING.equals(type)) {
            return getString(tag, index);
        } else if (GType.INT.equals(type)) {
            return getInt(tag, index);
        } else if (GType.UINT.equals(type)) {
            return getUInt(tag, index);
        } else if (GType.INT64.equals(type)) {
            return getInt64(tag, index);
        } else if (GType.DOUBLE.equals(type)) {
            return getDouble(tag, index);
        }
        return null;
    }
    public Object getTag(Tag tag, int index) {
        return getTag(tag.getId(), index);
    }
        
    public String getString(String tag, int index) {
        if (!isTagType(tag, GType.STRING)) {
            return getTag(tag, index).toString();
        }
        
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
        } else if (GType.DOUBLE.equals(type)) {
            return getDouble(tag, index);
        } else {
            throw new IllegalArgumentException("Tag [" + tag + "] is not a number");
        }
    }
    public Integer getInt(String tag, int index) {
        ensureType(tag, GType.INT);
        int[] value = new int[1];
        gst.gst_tag_list_get_int_index(this, tag, index, value);
        return value[0];
    }
    public Integer getUInt(String tag, int index) {
        ensureType(tag, GType.UINT);
        int[] value = new int[1];
        gst.gst_tag_list_get_uint_index(this, tag, index, value);
        return value[0];
    }
    public Long getInt64(String tag, int index) {
        ensureType(tag, GType.INT64);
        long[] value = new long[1];
        gst.gst_tag_list_get_int64_index(this, tag, index, value);
        return value[0];
    }
    
    public Double getDouble(String tag, int index) {
        ensureType(tag, GType.DOUBLE);
        double[] value = new double[1];
        gst.gst_tag_list_get_double_index(this, tag, index, value);
        return value[0];
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
            public void callback(Pointer ptr, String tag, Pointer user_data) {
                list.add(tag);
            }
        }, null);
        return list;
    }
    public Map<String, Object> getTags() {
        final Map<String, Object> m = new HashMap<String, Object>();
        gst.gst_tag_list_foreach(this, new GstAPI.TagForeachFunc() {
            public void callback(Pointer ptr, String tag, Pointer user_data) {
                Object data = getTag(tag, 0);
                if (data != null) {
                    m.put(tag, data);
                }
            }
        }, null);
        return m;
    }
    public TagList merge(TagList list2, TagMergeMode mode) {
        return new TagList(initializer(gst.gst_tag_list_merge(this, list2, mode), false, true));
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

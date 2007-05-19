/*
 * TagList.java
 */

package org.gstreamer;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
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
public class TagList extends NativeObject {
    
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
        this(ptr, true, true);
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
        gst.gst_tag_list_get_string_index(handle(), tag, index, value);
        if (value.getValue() == null) {
            return null;
        }
        String ret = value.getValue().getString(0, false);
        glib.g_free(value.getValue());
        return ret;
    }
    public Number getNumber(String tag, int index) {
        switch (tagTypeMap.get(tag)) {
            case INT:
                return getInt(tag, index);
            case UINT:
                return getUInt(tag, index);
            case INT64:
                return getInt64(tag, index);
            default:
                throw new IllegalArgumentException("Tag [" + tag + "] is not a number");
        }
    }
    public Integer getInt(String tag, int index) {
        ensureType(tag, GType.INT);
        IntByReference value = new IntByReference();
        gst.gst_tag_list_get_int_index(handle(), tag, index, value);
        return value.getValue();
    }
    public Integer getUInt(String tag, int index) {
        ensureType(tag, GType.UINT);
        IntByReference value = new IntByReference();
        gst.gst_tag_list_get_uint_index(handle(), tag, index, value);
        return value.getValue();
    }
    public Long getInt64(String tag, int index) {
        if (!isTagType(tag, GType.INT64)) {
            throw new IllegalArgumentException("Tag [" + tag + "] is not of type INT");
        }
        LongByReference value = new LongByReference();
        gst.gst_tag_list_get_int64_index(handle(), tag, index, value);
        return value.getValue();
    }
    private boolean isTagType(String tag, GType type) {
        return tagTypeMap.get(tag) == type;
    }
    private void ensureType(String tag, GType type) {
        if (!isTagType(tag, type)) {
            throw new IllegalArgumentException("Tag [" + tag + "] is not of type " + type);
        }
    }
    public List<String> getTagNames() {
        final List<String> list = new LinkedList<String>();
        gst.gst_tag_list_foreach(handle(), new GstAPI.TagForeachFunc() {
            public void callback(Pointer ptr, Pointer tagPointer, Pointer user_data) {
                list.add(tagPointer.getString(0, false));
            }
        }, null);
        return list;
    }
    public Map<String, Object> getTags() {
        final Map<String, Object> m = new HashMap<String, Object>();
        for (String tag : getTagNames()) {
            switch (tagTypeMap.get(tag)) {
                case STRING:
                    m.put(tag, getString(tag));
                    break;
                case INT:
                    m.put(tag, getInt(tag, 0));
                    break;
                case UINT:
                    m.put(tag, getUInt(tag, 0));
                    break;
                case INT64:
                    m.put(tag, getInt64(tag, 0));
                    break;
                default:
                    System.out.println("Unknown type for tag " + tag);
                    break;
            }
        }
        return m;
    }
    public TagList merge(TagList list2, TagMergeMode mode) {
        return new TagList(gst.gst_tag_list_merge(this.handle(), list2.handle(), mode.intValue()));
    }
    void ref() {}
    void unref() {}
    void disposeNativeHandle(Pointer ptr) {
        gst.gst_tag_list_free(ptr);
    }
    
    //--------------------------------------------------------------------------
    // Pre-cache all the tag types (i.e. are they string, int, etc)
    //
    static final Map<String, GType> tagTypeMap = new HashMap<String, GType>();
    static {
        for (Tag tag : Tag.values()) {
            tagTypeMap.put(tag.getId(), GType.valueOf(gst.gst_tag_get_type(tag.getId())));
        }
    }
}

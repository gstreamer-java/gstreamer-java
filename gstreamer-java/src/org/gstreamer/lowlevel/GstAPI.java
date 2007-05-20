/*
 * GstAPI.java
 */

package org.gstreamer.lowlevel;
import com.sun.jna.*;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public interface GstAPI extends Library {
    GstAPI gst = (GstAPI) Native.loadLibrary("gstreamer-0.10", GstAPI.class);
    String gst_version_string();
    void gst_version(LongByReference major, LongByReference minor, LongByReference micro, LongByReference nano);
    void gst_init(IntByReference argc, PointerByReference argv);
    boolean gst_init_check(IntByReference argc, PointerByReference argv, PointerByReference err);
    void gst_deinit();
    
    
    /*
     * GstElementFactory methods
     */
    NativeLong gst_element_factory_get_type();
    Pointer gst_element_factory_find(String factoryName);
    Pointer gst_element_factory_make(String factoryName, String elementName);
    
    Pointer gst_element_factory_create(Pointer factory, String elementName);
    NativeLong gst_element_factory_get_element_type(Pointer factory);
    String gst_element_factory_get_longname(Pointer factory);
    String gst_element_factory_get_klass(Pointer factory);
    String gst_element_factory_get_description(Pointer factory);
    String gst_element_factory_get_author(Pointer factory);
    int gst_element_factory_get_num_pad_templates(Pointer factory);
    int gst_element_factory_get_uri_type(Pointer factory);
    
    /*
     * GstElement methods
     */
    NativeLong gst_element_get_type();
    int gst_element_set_state(Pointer elem, int state);
    int gst_element_get_state(Pointer elem, IntByReference state, IntByReference pending, long timeout);
    boolean gst_element_query_position(Pointer elem, IntByReference fmt, LongByReference pos);
    boolean gst_element_query_duration(Pointer elem, IntByReference fmt, LongByReference pos);
    boolean gst_element_link(Pointer elem1, Pointer elem2);
    void gst_element_unlink(Pointer elem1, Pointer elem2);
    Pointer gst_element_get_pad(Pointer elem, String name);
    boolean gst_element_add_pad(Pointer elem, Pointer pad);
    boolean gst_element_remove_pad(Pointer elem, Pointer pad);
    
    Pointer gst_element_iterate_pads(Pointer element);
    Pointer gst_element_iterate_src_pads(Pointer element);
    Pointer gst_element_iterate_sink_pads(Pointer element);
    /* factory management */
    Pointer gst_element_get_factory(Pointer element);
    Pointer gst_element_get_bus(Pointer element);
    
    /*
     * GstGhostPad functions
     */
    Pointer gst_ghost_pad_new(String name, Pointer target);
    Pointer gst_ghost_pad_new_no_target(String name, int direction);
    
    
    Pointer gst_pipeline_new(String name);
    NativeLong gst_pipeline_get_type();
    Pointer gst_pipeline_get_bus(Pointer ptr);
    
    void gst_object_ref(Pointer ptr);
    void gst_object_unref(Pointer ptr);
    void gst_object_sink(Pointer ptr);
    
    void gst_object_set_name(Pointer ptr, String name);
    Pointer gst_object_get_name(Pointer ptr); // returns a string - needs to be freed
    
    /*
     * GstBin functions
     */
    Pointer gst_bin_new(String name);
    NativeLong gst_bin_get_type();
    boolean gst_bin_add(Pointer bin, Pointer element);
    boolean gst_bin_remove(Pointer bin, Pointer element);
    Pointer gst_bin_get_by_name(Pointer bin, String name);
    Pointer gst_bin_get_by_name_recurse_up(Pointer bin, String name);
    Pointer gst_bin_iterate_elements(Pointer bin);
    Pointer gst_bin_iterate_sorted(Pointer bin);
    Pointer gst_bin_iterate_recurse(Pointer bin);
    Pointer gst_bin_iterate_sinks(Pointer bin);
    Pointer gst_bin_iterate_sources(Pointer bin);
    
    /*
     * GstMiniObject functions
     */
    void gst_mini_object_ref(Pointer ptr);
    void gst_mini_object_unref(Pointer ptr);
    
    /*
     * GstIterator functions
     */
    void gst_iterator_free(Pointer iter);
    int gst_iterator_next(Pointer iter, PointerByReference next);
    void gst_iterator_resync(Pointer iter);
    
    /*
     * GstBus functions
     * */
    NativeLong gst_bus_get_type();
    void gst_bus_set_flushing(Pointer ptr, int flushing);
    interface BusCallback extends Callback {
        boolean callback(Pointer bus, Pointer msg, Pointer data);
    }
    NativeLong gst_bus_add_watch(Pointer bus, BusCallback function, Pointer data);
    
    
    /*
     * GstMessage functions
     */
    NativeLong gst_message_get_type();
    int gst_message_type(Pointer msg);
    void gst_message_parse_segment_start(Pointer msg, IntByReference format, LongByReference segment);
    void gst_message_parse_state_changed(Pointer msg, IntByReference o, IntByReference n, IntByReference p);
    void gst_message_parse_tag(Pointer msg, PointerByReference tagList);
    void gst_message_parse_clock_provide(Pointer msg, PointerByReference clock, IntByReference reader);
    void gst_message_parse_new_clock(Pointer msg, PointerByReference clock);
    void gst_message_parse_error(Pointer msg, PointerByReference err, PointerByReference debug);
    void gst_message_parse_warning(Pointer msg, PointerByReference err, PointerByReference debug);
    
    /*
     * gstparse functions
     */
    Pointer gst_parse_launch(String pipeline_description, PointerByReference error);
    Pointer gst_parse_launchv(String[] pipeline_description, PointerByReference error);
    
    /*
     * GstCaps functions
     */
    NativeLong gst_caps_get_type();
    Pointer gst_caps_new_empty();
    Pointer gst_caps_new_any();
    Pointer gst_caps_ref(Pointer caps);
    Pointer gst_caps_unref(Pointer caps);
    Pointer gst_caps_copy(Pointer caps);
    Pointer gst_caps_from_string(String string);
    
    /* manipulation */
    void gst_caps_append(Pointer caps1, Pointer caps2);
    void gst_caps_merge(Pointer caps1, Pointer caps2);
    void gst_caps_append_structure(Pointer caps, Pointer structure);
    void gst_caps_remove_structure(Pointer caps, int idx);
    void gst_caps_merge_structure(Pointer caps, Pointer structure);
    int gst_caps_get_size(Pointer caps);
    Pointer gst_caps_get_structure(Pointer caps, int index);
    Pointer gst_caps_copy_nth(Pointer caps, int nth);
    void gst_caps_truncate(Pointer caps);
    void gst_caps_set_simple(Pointer caps, String field, Object val, Pointer end);
    void gst_caps_set_simple(Pointer caps, String field, Object val1, Object val2, Pointer end);
    Pointer gst_caps_union(Pointer caps, Pointer other);
    /*
     * GstStructure functions
     */
    void gst_structure_free(Pointer ptr);
    boolean gst_structure_get_int(Pointer ptr, String fieldname, IntByReference value);
    boolean gst_structure_fixate_field_nearest_int(Pointer pointer, String field, Integer target);
    Pointer gst_structure_from_string(String data, PointerByReference end);
    Pointer gst_structure_copy(Pointer pointer);
    /*
     * GstTagList functions
     */
    interface TagForeachFunc extends Callback {
        void callback(Pointer list, Pointer tag, Pointer user_data);
    }
    interface TagMergeFunc extends Callback {
        void callback(Pointer dest, Pointer src);
    }
    Pointer gst_tag_list_new();
    void gst_tag_list_free(Pointer list);
    boolean gst_is_tag_list(Pointer p);
    Pointer gst_tag_list_copy(Pointer list);
    boolean gst_tag_list_is_empty(Pointer list);
    void gst_tag_list_insert(Pointer into, Pointer from, int mode);
    Pointer gst_tag_list_merge(Pointer list1, Pointer list2, int mode);
    int gst_tag_list_get_tag_size(Pointer list, String tag);
    void gst_tag_list_remove_tag(Pointer list, String tag);
    void gst_tag_list_foreach(Pointer list, TagForeachFunc func, Pointer user_data);
    
    boolean gst_tag_list_get_char(Pointer list, String tag, ByteByReference value);
    boolean gst_tag_list_get_char_index(Pointer list, String tag, int index, ByteByReference value);
    boolean gst_tag_list_get_uchar(Pointer list, String tag, ByteByReference value);
    boolean gst_tag_list_get_uchar_index(Pointer list, String tag, int index, ByteByReference value);
    boolean gst_tag_list_get_boolean(Pointer list, String tag, IntByReference value);
    boolean gst_tag_list_get_boolean_index(Pointer list, String tag, int index, IntByReference value);
    boolean gst_tag_list_get_int(Pointer list, String tag, IntByReference value);
    boolean gst_tag_list_get_int_index(Pointer list, String tag, int index, IntByReference value);
    boolean gst_tag_list_get_uint(Pointer list, String tag, IntByReference value);
    boolean gst_tag_list_get_uint_index(Pointer list, String tag, int index, IntByReference value);
    boolean gst_tag_list_get_int64(Pointer list, String tag, LongByReference value);
    boolean gst_tag_list_get_int64_index(Pointer list, String tag, int index, LongByReference value);
    boolean gst_tag_list_get_string(Pointer list, String tag, PointerByReference value);
    boolean gst_tag_list_get_string_index(Pointer list, String tag, int index, PointerByReference value);
    
    boolean gst_tag_exists(String tag);
    NativeLong gst_tag_get_type(String tag);
    String gst_tag_get_nick(String tag);
    String gst_tag_get_description(String tag);
    int gst_tag_get_flag(String tag);
    boolean gst_tag_is_fixed(String tag);
    
    /*
     * GstClock functions
     */
    NativeLong gst_clock_get_type();
    long gst_clock_set_resolution(Pointer clock, long resolution);
    long gst_clock_get_resolution(Pointer clock);
    long gst_clock_get_time(Pointer clock);
    void gst_clock_set_calibration(Pointer clock, long internal, long external, long rate_num, long rate_denom);
    void gst_clock_get_calibration(Pointer clock, LongByReference internal, LongByReference external,
            LongByReference rate_num, LongByReference rate_denom);
    /* master/slave clocks */
    boolean gst_clock_set_master(Pointer clock, Pointer master);
    Pointer gst_clock_get_master(Pointer clock);
    boolean gst_clock_add_observation(Pointer clock, long slave, long Master, DoubleByReference r_squared);
    
    /* getting and adjusting internal time */
    long gst_clock_get_internal_time(Pointer clock);
    long gst_clock_adjust_unlocked(Pointer clock, long internal);
    
    /*
     * GstPad functions
     */
    NativeLong gst_pad_get_type();
    boolean gst_pad_peer_accept_caps(Pointer pad, Pointer caps);
    boolean gst_pad_set_caps(Pointer pad, Pointer caps);
    Pointer gst_pad_get_caps(Pointer pad);
    Pointer gst_pad_get_name(Pointer pad); // Returns a string that needs to be freed
    int gst_pad_link(Pointer src, Pointer sink);
    boolean gst_pad_unlink(Pointer src, Pointer sink);
    boolean gst_pad_is_linked(Pointer pad);
    boolean gst_pad_can_link(Pointer srcpad, Pointer sinkpad);
    int gst_pad_get_direction(Pointer pad);
    Pointer gst_pad_get_parent_element(Pointer pad);
    NativeLong gst_buffer_get_type();
    
    
}
class GstAPIMapper {
    private GstAPIMapper() {}
    static Map<String, String> getFunctionMap() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("gst_caps_set_1", "gst_caps_set");
        m.put("gst_caps_set_2", "gst_caps_set");
        return m;
    }
}

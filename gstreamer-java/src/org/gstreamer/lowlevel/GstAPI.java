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

package org.gstreamer.lowlevel;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Clock;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Format;
import org.gstreamer.GstObject;
import org.gstreamer.MiniObject;
import org.gstreamer.Pad;
import org.gstreamer.PadDirection;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.Pipeline;
import org.gstreamer.SeekType;
import org.gstreamer.State;
import org.gstreamer.StateChangeReturn;
import org.gstreamer.Structure;
import org.gstreamer.TagList;
import org.gstreamer.TagMergeMode;
import org.gstreamer.Time;

/**
 *
 */
public interface GstAPI extends Library {
    GstAPI gst = (GstAPI) GNative.loadLibrary("gstreamer-0.10", GstAPI.class);
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
    
    Pointer gst_element_factory_create(ElementFactory factory, String elementName);
    NativeLong gst_element_factory_get_element_type(ElementFactory factory);
    String gst_element_factory_get_longname(ElementFactory factory);
    String gst_element_factory_get_klass(ElementFactory factory);
    String gst_element_factory_get_description(ElementFactory factory);
    String gst_element_factory_get_author(ElementFactory factory);
    int gst_element_factory_get_num_pad_templates(ElementFactory factory);
    int gst_element_factory_get_uri_type(ElementFactory factory);
    
    /*
     * GstElement methods
     */
    NativeLong gst_element_get_type();
    StateChangeReturn gst_element_set_state(Element elem, State state);
    StateChangeReturn gst_element_get_state(Element elem, IntByReference state, IntByReference pending, long timeout);
    boolean gst_element_query_position(Element elem, IntByReference fmt, LongByReference pos);
    boolean gst_element_query_duration(Element elem, IntByReference fmt, LongByReference pos);
    boolean gst_element_seek(Element elem, double rate, Format format, int flags,
            SeekType cur_type, long cur, SeekType stop_type, long stop);
    boolean gst_element_seek_simple(Element elem, Format format, int flags, long pos);
    boolean gst_element_link(Element elem1, Element elem2);
    boolean gst_element_link_many(Element... elements);
    void gst_element_unlink_many(Element... elements);
    void gst_element_unlink(Element elem1, Element elem2);
    Pad gst_element_get_pad(Element elem, String name);
    boolean gst_element_add_pad(Element elem, Pad pad);
    boolean gst_element_remove_pad(Element elem, Pad pad);
    boolean gst_element_link_pads(Element src, String srcpadname, Element dest, String destpadname);
    void gst_element_unlink_pads(Element src, String srcpadname, Element dest, String destpadname);
    boolean gst_element_link_pads_filtered(Element src, String srcpadname, Element dest, String destpadname,
            Caps filter);
    
    Pointer gst_element_iterate_pads(Element element);
    Pointer gst_element_iterate_src_pads(Element element);
    Pointer gst_element_iterate_sink_pads(Element element);
    /* factory management */
    ElementFactory gst_element_get_factory(Element element);
    Bus gst_element_get_bus(Element element);
    
    /*
     * GstGhostPad functions
     */
    Pointer gst_ghost_pad_new(String name, Pad target);
    Pointer gst_ghost_pad_new_no_target(String name, int direction);
    
    /*
     * GstPipeline
     */
    Pointer gst_pipeline_new(String name);
    NativeLong gst_pipeline_get_type();
    Bus gst_pipeline_get_bus(Pipeline pipeline);
    void gst_pipeline_set_auto_flush_bus(Pipeline pipeline, boolean flush);
    boolean gst_pipeline_get_auto_flush_bus(Pipeline pipeline);
    void gst_pipeline_set_new_stream_time(Pipeline pipeline, Time time);
    long gst_pipeline_get_last_stream_time(Pipeline pipeline);
    void gst_pipeline_use_clock(Pipeline pipeline, Clock clock);
    boolean gst_pipeline_set_clock(Pipeline pipeline, Clock clock);
    Clock gst_pipeline_get_clock(Pipeline pipeline);
    void gst_pipeline_auto_clock(Pipeline pipeline);
    void gst_pipeline_set_delay(Pipeline pipeline, Time delay);
    long gst_pipeline_get_delay(Pipeline pipeline);
    
    
    /*
     * GstObject
     */
    void gst_object_ref(GstObject ptr);
    void gst_object_unref(GstObject ptr);
    void gst_object_sink(GstObject ptr);
    
    void gst_object_set_name(GstObject obj, String name);
    Pointer gst_object_get_name(GstObject obj); // returns a string - needs to be freed
    
    /*
     * GstBin functions
     */
    Pointer gst_bin_new(String name);
    NativeLong gst_bin_get_type();
    
    boolean gst_bin_add(Bin bin, Element element);
    void gst_bin_add_many(Bin bin, Element... elements);
    boolean gst_bin_remove(Bin bin, Element element);
    void gst_bin_remove_many(Bin bin, Element... elements);
    Element gst_bin_get_by_name(Bin bin, String name);
    Element gst_bin_get_by_name_recurse_up(Bin bin, String name);
    Pointer gst_bin_iterate_elements(Bin bin);
    Pointer gst_bin_iterate_sorted(Bin bin);
    Pointer gst_bin_iterate_recurse(Bin bin);
    Pointer gst_bin_iterate_sinks(Bin bin);
    Pointer gst_bin_iterate_sources(Bin bin);
    
    /*
     * GstMiniObject functions
     */
    void gst_mini_object_ref(MiniObject ptr);
    void gst_mini_object_unref(MiniObject ptr);
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
    void gst_bus_set_flushing(Bus ptr, int flushing);
    interface BusCallback extends Callback {
        boolean callback(Pointer bus, Pointer msg, Pointer data);
    }
    NativeLong gst_bus_add_watch(Bus bus, BusCallback function, Pointer data);
    void gst_bus_set_sync_handler(Bus bus, Pointer function, Pointer data);
    void gst_bus_set_sync_handler(Bus bus, Callback function, Pointer data);
    
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
    void gst_message_parse_info(Pointer msg, PointerByReference err, PointerByReference debug);
    
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
    Pointer gst_caps_ref(Caps caps);
    Pointer gst_caps_unref(Caps caps);
    Pointer gst_caps_unref(Pointer caps);
    Pointer gst_caps_copy(Caps caps);
    Pointer gst_caps_from_string(String string);
    
    /* manipulation */
    void gst_caps_append(Caps caps1, Caps caps2);
    void gst_caps_merge(Caps caps1, Caps caps2);
    void gst_caps_append_structure(Caps caps, Structure structure);
    void gst_caps_remove_structure(Caps caps, int idx);
    void gst_caps_merge_structure(Caps caps, Structure structure);
    int gst_caps_get_size(Caps caps);
    Pointer gst_caps_get_structure(Caps caps, int index);
    Pointer gst_caps_copy_nth(Caps caps, int nth);
    void gst_caps_truncate(Caps caps);
    void gst_caps_set_simple(Caps caps, String field, Object... values);
    Pointer gst_caps_union(Caps caps, Caps other);
    /*
     * GstStructure functions
     */
    void gst_structure_free(Pointer ptr);
    boolean gst_structure_get_int(Structure structure, String fieldname, IntByReference value);
    boolean gst_structure_fixate_field_nearest_int(Structure structure, String field, int target);
    Pointer gst_structure_from_string(String data, PointerByReference end);
    Pointer gst_structure_copy(Structure src);
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
    boolean gst_is_tag_list(TagList p);
    Pointer gst_tag_list_copy(TagList list);
    boolean gst_tag_list_is_empty(TagList list);
    void gst_tag_list_insert(TagList into, TagList from, TagMergeMode mode);
    Pointer gst_tag_list_merge(TagList list1, TagList list2, TagMergeMode mode);
    int gst_tag_list_get_tag_size(TagList list, TagList tag);
    void gst_tag_list_remove_tag(TagList list, TagList tag);
    void gst_tag_list_foreach(TagList list, TagForeachFunc func, Pointer user_data);
    
    boolean gst_tag_list_get_char(TagList list, String tag, ByteByReference value);
    boolean gst_tag_list_get_char_index(TagList list, String tag, int index, ByteByReference value);
    boolean gst_tag_list_get_uchar(TagList list, String tag, ByteByReference value);
    boolean gst_tag_list_get_uchar_index(TagList list, String tag, int index, ByteByReference value);
    boolean gst_tag_list_get_boolean(TagList list, String tag, IntByReference value);
    boolean gst_tag_list_get_boolean_index(TagList list, String tag, int index, IntByReference value);
    boolean gst_tag_list_get_int(TagList list, String tag, IntByReference value);
    boolean gst_tag_list_get_int_index(TagList list, String tag, int index, IntByReference value);
    boolean gst_tag_list_get_uint(TagList list, String tag, IntByReference value);
    boolean gst_tag_list_get_uint_index(TagList list, String tag, int index, IntByReference value);
    boolean gst_tag_list_get_int64(TagList list, String tag, LongByReference value);
    boolean gst_tag_list_get_int64_index(TagList list, String tag, int index, LongByReference value);
    boolean gst_tag_list_get_string(TagList list, String tag, PointerByReference value);
    boolean gst_tag_list_get_string_index(TagList list, String tag, int index, PointerByReference value);
    
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
    long gst_clock_set_resolution(Clock clock, long resolution);
    long gst_clock_get_resolution(Clock clock);
    long gst_clock_get_time(Clock clock);
    void gst_clock_set_calibration(Clock clock, long internal, long external, long rate_num, long rate_denom);
    void gst_clock_get_calibration(Clock clock, LongByReference internal, LongByReference external,
            LongByReference rate_num, LongByReference rate_denom);
    /* master/slave clocks */
    boolean gst_clock_set_master(Clock clock, Clock master);
    Clock gst_clock_get_master(Clock clock);
    boolean gst_clock_add_observation(Clock clock, long slave, long Master, DoubleByReference r_squared);
    
    /* getting and adjusting internal time */
    long gst_clock_get_internal_time(Clock clock);
    long gst_clock_adjust_unlocked(Clock clock, long internal);
    
    /*
     * GstPad functions
     */
    NativeLong gst_pad_get_type();
    boolean gst_pad_peer_accept_caps(Pad pad, Caps caps);
    boolean gst_pad_set_caps(Pad pad, Caps caps);
    Caps gst_pad_get_caps(Pad pad);
    Pointer gst_pad_get_name(Pad pad); // Returns a string that needs to be freed
    PadLinkReturn gst_pad_link(Pad src, Pad sink);
    boolean gst_pad_unlink(Pad src, Pad sink);
    boolean gst_pad_is_linked(Pad pad);
    boolean gst_pad_can_link(Pad srcpad, Pad sinkpad);
    PadDirection gst_pad_get_direction(Pad pad);
    Element gst_pad_get_parent_element(Pad pad);
    NativeLong gst_buffer_get_type();
    
    public final class BufferStruct extends com.sun.jna.Structure {
        public MiniObjectStruct mini_object;
        public Pointer data;
        public int size;
        public long timestamp;
        public long duration;
        public Pointer caps;
        public long offset;
        public long offset_end;
        public Pointer malloc_data;
        public BufferStruct(Pointer ptr) {
            useMemory(ptr);
            read();
        }
        public void write() {};
    }
    public class MiniObjectStruct extends com.sun.jna.Structure {
        public GTypeInstance instance;
        public int refcount;
        public int flags;
        public Pointer _gst_reserved;
        
        /** Creates a new instance of GstMiniObjectStructure */
        public MiniObjectStruct() {}
        public MiniObjectStruct(Pointer ptr) {
            useMemory(ptr, 0);
            read();
        }
        public void write() { }
    }
    public final class MessageStruct extends com.sun.jna.Structure {
        public MiniObjectStruct mini_object;
        public Pointer lock;
        public Pointer cond;
        public int type;
        public long timestamp;
        public Pointer src;
        public Pointer structure;
        
        /**
         * Creates a new instance of MessageStruct
         */
        public MessageStruct() {
        }
        public MessageStruct(Pointer ptr) {
            useMemory(ptr);
            read();
        }
        public void write() { }
    }
    
    public class GErrorStruct extends com.sun.jna.Structure {
        public int domain; /* GQuark */
        public int code;
        public String message;
        
        /** Creates a new instance of GError */
        public GErrorStruct(Pointer ptr) {
            useMemory(ptr, 0);
            read();
        }
    }
    
}

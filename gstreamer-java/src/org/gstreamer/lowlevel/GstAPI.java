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
import java.util.HashMap;
import org.gstreamer.Bin;
import org.gstreamer.Buffer;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Clock;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Event;
import org.gstreamer.Format;
import org.gstreamer.GstObject;
import org.gstreamer.MiniObject;
import org.gstreamer.Pad;
import org.gstreamer.PadDirection;
import org.gstreamer.PadLinkReturn;
import org.gstreamer.PadTemplate;
import org.gstreamer.Pipeline;
import org.gstreamer.Plugin;
import org.gstreamer.Registry;
import org.gstreamer.SeekFlags;
import org.gstreamer.SeekType;
import org.gstreamer.State;
import org.gstreamer.StateChangeReturn;
import org.gstreamer.Structure;
import org.gstreamer.TagList;
import org.gstreamer.TagMergeMode;
import org.gstreamer.Time;
import org.gstreamer.annotations.FreeReturnValue;
import org.gstreamer.lowlevel.GlibAPI.GList;

/**
 *
 */
public interface GstAPI extends Library {
    static GstAPI gst = (GstAPI) GNative.loadLibrary("gstreamer-0.10", GstAPI.class, new HashMap<String, Object>() {{
        put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
    }});
    String gst_version_string();
    void gst_version(LongByReference major, LongByReference minor, LongByReference micro, LongByReference nano);
    void gst_init(IntByReference argc, PointerByReference argv);
    boolean gst_init_check(IntByReference argc, PointerByReference argv, PointerByReference err);
    void gst_deinit();
    
    
    /*
     * GstElementFactory methods
     */
    GType gst_element_factory_get_type();
    ElementFactory gst_element_factory_find(String factoryName);
    Pointer gst_element_factory_make(String factoryName, String elementName);
    
    Pointer gst_element_factory_create(ElementFactory factory, String elementName);
    GType gst_element_factory_get_element_type(ElementFactory factory);
    String gst_element_factory_get_longname(ElementFactory factory);
    String gst_element_factory_get_klass(ElementFactory factory);
    String gst_element_factory_get_description(ElementFactory factory);
    String gst_element_factory_get_author(ElementFactory factory);
    int gst_element_factory_get_num_pad_templates(ElementFactory factory);
    int gst_element_factory_get_uri_type(ElementFactory factory);
    
    /*
     * GstElement methods
     */
    GType gst_element_get_type();
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
    boolean gst_element_send_event(Element element, Event event);


    /* element class pad templates */
    void gst_element_class_add_pad_template(Pointer klass, PadTemplate templ);
    PadTemplate gst_element_class_get_pad_template(Pointer /*GstElementClass*/ element_class, String name);
    
    /* templates and factories */
    GType gst_pad_template_get_type();
    GType gst_static_pad_template_get_type();

    Pointer gst_pad_template_new(String name_template, PadDirection direction, 
            /* GstPadPresence */ int presence, Caps caps);

    /* flush events */
    Event gst_event_new_flush_start();
    Event gst_event_new_flush_stop();
    /* EOS event */
    Event gst_event_new_eos();

    /*
     * GstGhostPad functions
     */
    Pointer gst_ghost_pad_new(String name, Pad target);
    Pointer gst_ghost_pad_new_no_target(String name, int direction);
    
    /*
     * GstPipeline
     */
    Pointer gst_pipeline_new(String name);
    GType gst_pipeline_get_type();
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
    GType gst_object_get_type();
    void gst_object_ref(GstObject ptr);
    void gst_object_unref(GstObject ptr);
    void gst_object_sink(GstObject ptr);
    
    void gst_object_set_name(GstObject obj, String name);
    @FreeReturnValue
    String gst_object_get_name(GstObject obj); // returns a string - needs to be freed
    
    /*
     * GstBin functions
     */
    Pointer gst_bin_new(String name);
    GType gst_bin_get_type();
    
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
    GType gst_bus_get_type();
    void gst_bus_set_flushing(Bus ptr, int flushing);
    interface BusCallback extends Callback {
        boolean callback(Pointer bus, Pointer msg, Pointer data);
    }
    NativeLong gst_bus_add_watch(Bus bus, BusCallback function, Pointer data);
    void gst_bus_set_sync_handler(Bus bus, Pointer function, Pointer data);
    void gst_bus_set_sync_handler(Bus bus, Callback function, Pointer data);
    void gst_bus_enable_sync_message_emission(Bus bus);

    /*
     * GstMessage functions
     */
    GType gst_message_get_type();
    int gst_message_type(Pointer msg);
    void gst_message_parse_state_changed(Pointer msg, IntByReference o, IntByReference n, IntByReference p);
    void gst_message_parse_tag(Pointer msg, PointerByReference tagList);
    void gst_message_parse_clock_provide(Pointer msg, PointerByReference clock, IntByReference reader);
    void gst_message_parse_new_clock(Pointer msg, PointerByReference clock);
    void gst_message_parse_error(Pointer msg, PointerByReference err, PointerByReference debug);
    void gst_message_parse_warning(Pointer msg, PointerByReference err, PointerByReference debug);
    void gst_message_parse_info(Pointer msg, PointerByReference err, PointerByReference debug);
    void gst_message_parse_buffering(Pointer msg, IntByReference percent);
    void gst_message_parse_segment_start(Pointer message, IntByReference format, LongByReference position); 
    void gst_message_parse_segment_done(Pointer message, IntByReference format, LongByReference position);
    void gst_message_parse_duration(Pointer message, IntByReference format, LongByReference position);
    
    /*
     * gstparse functions
     */
    Pointer gst_parse_launch(String pipeline_description, PointerByReference error);
    Pointer gst_parse_launchv(String[] pipeline_description, PointerByReference error);
    
    /*
     * GstCaps functions
     */
    GType gst_caps_get_type();
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
    
    String gst_structure_get_name(Structure structure);
    void gst_structure_set_name(Structure structure, String name);
    boolean gst_structure_has_name(Structure structure, String name); 

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
    GType gst_tag_get_type(String tag);
    String gst_tag_get_nick(String tag);
    String gst_tag_get_description(String tag);
    int gst_tag_get_flag(String tag);
    boolean gst_tag_is_fixed(String tag);
    
    /*
     * GstClock functions
     */
    GType gst_clock_get_type();
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
    
    boolean gst_element_implements_interface(Element element, NativeLong iface_type);    
    Pointer gst_implements_interface_cast(GstObject obj, NativeLong gtype);    
    boolean gst_implements_interface_check(GstObject from, NativeLong type);
    /*
     * GstPad functions
     */
    GType gst_pad_get_type();
    boolean gst_pad_peer_accept_caps(Pad pad, Caps caps);
    boolean gst_pad_set_caps(Pad pad, Caps caps);
    Caps gst_pad_get_caps(Pad pad);
    @FreeReturnValue
    String gst_pad_get_name(Pad pad); // Returns a string that needs to be freed
    PadLinkReturn gst_pad_link(Pad src, Pad sink);
    boolean gst_pad_unlink(Pad src, Pad sink);
    boolean gst_pad_is_linked(Pad pad);
    boolean gst_pad_can_link(Pad srcpad, Pad sinkpad);
    PadDirection gst_pad_get_direction(Pad pad);
    Element gst_pad_get_parent_element(Pad pad);
    GType gst_buffer_get_type();    
    Pointer gst_buffer_new_and_alloc(int size);
    
    /*
     * GstPlugin functions
     */
    /* function for filters */
    /**
     * GstPluginFilter:
     * @plugin: the plugin to check
     * @user_data: the user_data that has been passed on e.g. gst_registry_plugin_filter()
     *
     * A function that can be used with e.g. gst_registry_plugin_filter()
     * to get a list of plugins that match certain criteria.
     *
     * Returns: TRUE for a positive match, FALSE otherwise
     */
    interface PluginFilter extends Callback {
        boolean callback(Plugin plugin);
    }

    GType gst_plugin_get_type();

    String gst_plugin_get_name(Plugin plugin);
    String gst_plugin_get_description(Plugin plugin);
    String gst_plugin_get_filename(Plugin plugin);
    String gst_plugin_get_version(Plugin plugin);
    String gst_plugin_get_license(Plugin plugin);
    String gst_plugin_get_source(Plugin plugin);
    String gst_plugin_get_package(Plugin plugin);
    String gst_plugin_get_origin(Plugin plugin);
    //GModule *		gst_plugin_get_module		(Plugin plugin);
    boolean gst_plugin_is_loaded(Plugin plugin);
    boolean gst_plugin_name_filter(Plugin plugin, String name);

    //Plugin 		gst_plugin_load_file		(String filename, GError** error);

    Plugin gst_plugin_load(Plugin plugin);
    Plugin gst_plugin_load_by_name(String name);
    void gst_plugin_list_free(GList list);
    
    /*
     * GstRegistry functions
     */
    /* normal GObject stuff */
    GType gst_registry_get_type();

    Pointer gst_registry_get_default();
    boolean gst_registry_scan_path(Registry registry, String path);
    GList gst_registry_get_path_list(Registry registry);

    boolean gst_registry_add_plugin(Registry registry, Plugin plugin);
    void gst_registry_remove_plugin	(Registry registry, Plugin plugin);
    //boolean gst_registry_add_feature(Registry  registry, GstPluginFeature feature);
    //void gst_registry_remove_feature(Registry  registry, GstPluginFeature * feature);
    GList gst_registry_get_plugin_list(Registry registry);
    GList gst_registry_plugin_filter(Registry registry, PluginFilter filter, boolean first, Pointer user_data);
    //GList gst_registry_feature_filter(Registry registry, GstPluginFeatureFilter filter,
    //							 boolean first,
    //							 gpointer user_data);
    GList gst_registry_get_feature_list(Registry registry, GType type);
    GList gst_registry_get_feature_list_by_plugin(Registry registry, String name);

    Plugin gst_registry_find_plugin(Registry registry, String name);
    //GstPluginFeature gst_registry_find_feature(Registry registry, String name, GType type);

    Plugin gst_registry_lookup(Registry registry, String filename);
    //GstPluginFeature * 	gst_registry_lookup_feature 	(Registry registry, const char *name);


    boolean gst_registry_binary_read_cache(Registry registry, String location);
    boolean	gst_registry_binary_write_cache(Registry registry, String location);

    boolean	gst_registry_xml_read_cache(Registry registry, String location);
    boolean	gst_registry_xml_write_cache(Registry registry, String location);

    
    static final int GST_PADDING = 4;
    static final int GST_PADDING_LARGE = 20;
    
    public final static class GstObjectStruct extends com.sun.jna.Structure {        
        public GObjectAPI.GObjectStruct object;
        public volatile int refcount;
        public volatile Pointer lock;
        public volatile String name;
        public volatile String name_prefix;
        public volatile Pointer parent;
        public volatile int flags;
        public volatile Pointer _gst_reserved;
    }
    public final static class GstObjectClass extends com.sun.jna.Structure {
        public GObjectAPI.GObjectClass parent_class;
        public volatile Pointer path_string_separator;
        public volatile Pointer signal_object;
        public volatile Pointer lock;
        // These are really Callbacks, but we don't need them yet
        public volatile Pointer parent_set;
        public volatile Pointer parent_unset;
        public volatile Pointer object_saved;
        public volatile Pointer deep_notify;
        public volatile Pointer save_thyself;
        public volatile Pointer restore_thyself;
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * GST_PADDING];
    }
    final static class GstElementDetails extends com.sun.jna.Structure {
         /*< public > */
        public volatile String longname;
        public volatile String klass;
        public volatile String description;
        public volatile String author;
        /*< private > */
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * GST_PADDING];        
    }
    public final static class GstElementStruct extends com.sun.jna.Structure {
        public GstObjectStruct object;
        public volatile Pointer state_lock;
        public volatile Pointer state_cond;
        public volatile int state_cookie;
        public volatile State current_state;
        public volatile State next_state; 
        public volatile State pending_state;         
        public volatile StateChangeReturn last_return;
        public volatile Pointer bus;
        public volatile Pointer clock;
        public volatile long base_time;
        public volatile short numpads;
        public volatile Pointer pads;
        public volatile short numsrcpads;
        public volatile Pointer srcpads;
        public volatile short numsinkpads;
        public volatile Pointer sinkpads;
        public volatile int pads_cookie;
        // Use an array of byte as arrays of Pointer don't work
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * GST_PADDING];
    }
    public final static class GstElementClass extends com.sun.jna.Structure {
        //
        // Callbacks for this class
        //
        public static interface RequestNewPad extends Callback {
            public Pad callback(Element element, /* PadTemplate */ Pointer templ, String name);
        }
        public static interface ReleasePad extends Callback {
            public void callback(Element element, Pad pad);
        }
        public static interface GetState extends Callback {
            public StateChangeReturn callback(Element element, Pointer p_state, 
                    Pointer p_pending, long timeout);
        }
        public static interface SetState extends Callback {
            public StateChangeReturn callback(Element element, State state);
        }
        public static interface ChangeState extends Callback {
            public StateChangeReturn callback(Element element, int transition);
        }
        //
        // Actual data members
        //
        public GstObjectClass parent_class;
        public volatile GstElementDetails details;
        public volatile ElementFactory elementfactory;
        public volatile Pointer padtemplates;
        public volatile int numpadtemplates;
        public volatile int pad_templ_cookie;
        /*< private >*/
        /* signal callbacks */
        public volatile Pointer pad_added;
        public volatile Pointer pad_removed;
        public volatile Pointer no_more_pads;
        /* request/release pads */
        public RequestNewPad request_new_pad;
        public ReleasePad release_pad;
        /* state changes */
        public GetState get_state;
        public SetState set_state;
        public ChangeState change_state;
        /* bus */
        public volatile Pointer set_bus;
        /* set/get clocks */
        public volatile Pointer provide_clock;
        public volatile Pointer set_clock;
        
        /* index */
        public volatile Pointer get_index;
        public volatile Pointer set_index;
        public volatile Pointer send_event;
        /* query functions */
        public volatile Pointer get_query_types;
        public volatile Pointer query;
      
        /*< private >*/  
        // Use an array of byte if arrays of Pointer don't work
        public volatile byte[] _gst_reserved = new byte[Pointer.SIZE * GST_PADDING];
    }
    public static final class GstSegmentStruct extends com.sun.jna.Structure {
        /*< public >*/
        public double rate;
        public double abs_rate;
        public Format format;
        public SeekFlags   flags;
        public long start;
        public long stop;
        public long time;
        public long accum;

        public long last_stop;
        public long duration;

        /* API added 0.10.6 */
        public double applied_rate;

        /*< private >*/
        public volatile byte[] _gst_reserved = new byte[(Pointer.SIZE * GST_PADDING) - (Double.SIZE / 8)];
    };

    public final static class Segment {}
    public final static class Query {}
    
    public final class GstStaticPadTemplate extends com.sun.jna.Structure {    
        public Pointer name_template;
        public PadDirection  direction;
        public /* PadPresence */ int presence;
        public Caps caps;
};

    public final class BufferStruct extends com.sun.jna.Structure {
        volatile public MiniObjectStruct mini_object;
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
    }
    public class MiniObjectStruct extends com.sun.jna.Structure {
        public volatile GTypeInstance instance;
        public volatile int refcount;
        public volatile int flags;
        public volatile Pointer _gst_reserved;
        
        /** Creates a new instance of GstMiniObjectStructure */
        public MiniObjectStruct() {}
        public MiniObjectStruct(Pointer ptr) {
            useMemory(ptr);
            read();
        }
    }
    public final class MessageStruct extends com.sun.jna.Structure {
        public volatile MiniObjectStruct mini_object;
        public volatile Pointer lock;
        public volatile Pointer cond;
        public volatile int type;
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
        public volatile int domain; /* GQuark */
        public volatile int code;
        public volatile String message;
        
        /** Creates a new instance of GError */
        public GErrorStruct() { clear(); }
        public GErrorStruct(Pointer ptr) {
            useMemory(ptr);
            read();
        }
    }
    public static interface HandoffCallback extends Callback {
        public void callback(Element src, Buffer buffer, Pad pad, Pointer user_data);                
    }
    public static interface HaveTypeCallback extends Callback {
        void callback(Element elem, int probability, Caps caps, Pointer user_data);
    }
    public static interface ElementAddedCallback extends Callback {
        public void callback(Bin bin, Element elem, Pointer user_data);
    }
    public static interface ElementRemovedCallback extends Callback {
        public void callback(Bin bin, Element elem, Pointer user_data);
    }
}

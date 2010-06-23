/* 
 * Copyright (c) 2007 Wayne Meissner
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

package org.gstreamer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gstreamer.elements.DecodeBin;
import org.gstreamer.elements.DecodeBin2;
import org.gstreamer.elements.FakeSink;
import org.gstreamer.elements.FakeSrc;
import org.gstreamer.elements.FileSrc;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.elements.TypeFind;
import org.gstreamer.lowlevel.GstCapsAPI;
import org.gstreamer.lowlevel.GstElementFactoryAPI;
import org.gstreamer.lowlevel.GstNative;
import org.gstreamer.lowlevel.GstPadTemplateAPI;
import org.gstreamer.lowlevel.GstTypes;
import org.gstreamer.lowlevel.GlibAPI.GList;
import org.gstreamer.lowlevel.GstPadTemplateAPI.GstStaticPadTemplate;

import com.sun.jna.Pointer;
import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.AppSrc;

/**
 * ElementFactory is used to create instances of elements.
 * 
 * Use the {@link #find} and {@link #create} methods to create element instances 
 * or use {@link #make} as a convenient shortcut.
 *
 */
public class ElementFactory extends PluginFeature {
    private static Logger logger = Logger.getLogger(ElementFactory.class.getName());
    private static interface API extends GstElementFactoryAPI, GstCapsAPI, GstPadTemplateAPI {}
    private static final API gst = GstNative.load(API.class);
    static Level DEBUG = Level.FINE;
    
    /**
     * Creates a new instance of ElementFactory
     * @param init internal initialization data.
     */
    public ElementFactory(Initializer init) {
        super(init); 
        logger.entering("ElementFactory", "<init>", new Object[] { init });
    }
    
    /**
     * Creates a new element from the factory.
     *
     * @param name the name to assign to the created Element
     * @return A new {@link Element}
     */
    public Element create(String name) {
        logger.entering("ElementFactory", "create", name);
        Pointer elem = gst.ptr_gst_element_factory_create(this, name);
        logger.log(DEBUG, "gst_element_factory_create returned: " + elem);
        if (elem == null) {
            throw new IllegalArgumentException("Cannot create GstElement");
        }
        return elementFor(elem, getName());
    }
    /**
     * Returns the name of the person who wrote the factory.
     * 
     * @return The name of the author
     */
    public String getAuthor() {
        logger.entering("ElementFactory", "getAuthor");
        return gst.gst_element_factory_get_author(this);
    }
    /**
     * Returns a description of the factory.
     * 
     * @return A brief description of the factory.
     */
    public String getDescription() {
        logger.entering("ElementFactory", "getDescription");
        return gst.gst_element_factory_get_description(this);
    }
    /**
     * Returns the long, English name for the factory.
     * 
     * @return The long, English name for the factory.
     */
    public String getLongName() {
        logger.entering("ElementFactory", "getLongName");
        return gst.gst_element_factory_get_longname(this);
    }
    
    /**
     * Returns a string describing the type of factory.
     * This is an unordered list separated with slashes ('/').
     * 
     * @return The description of the type of factory.
     */
    public String getKlass() {
        logger.entering("ElementFactory", "getKlass");
        return gst.gst_element_factory_get_klass(this);
    }
    
    /**
     * Gets the list of {@link StaticPadTemplate} for this factory.
     *
     * @return The list of {@link StaticPadTemplate}
     */
    public List<StaticPadTemplate> getStaticPadTemplates() {
        GList glist = gst.gst_element_factory_get_static_pad_templates(this);
        List<StaticPadTemplate> templates = new ArrayList<StaticPadTemplate>();
        GList next = glist;
        while (next != null) {
            if (next.data != null) {
                GstStaticPadTemplate temp = new GstStaticPadTemplate(next.data);
                templates.add(new StaticPadTemplate(temp.name_template, temp.direction,
                        temp.presence, gst.gst_static_caps_get(temp.static_caps)));
            }
            next = next.next();
        }
        return templates;
    }
    
    /**
     * Retrieve an instance of a factory that can produce {@link Element}s
     * 
     * @param name The type of {@link Element} to produce.
     * @return An ElementFactory that will produce {@link Element}s of the 
     * desired type.
     */
    public static ElementFactory find(String name) {
        logger.entering("ElementFactory", "find", name);
        ElementFactory factory = gst.gst_element_factory_find(name);
        if (factory == null) {
            throw new IllegalArgumentException("No such Gstreamer factory: " + name);
        }        
        return factory;
    }
    
    /**
     * Creates a new Element from the specified factory.
     *
     * @param factoryName The name of the factory to use to produce the Element
     * @param name The name to assign to the created Element
     * @return A new GstElemElement
     */
    public static Element make(String factoryName, String name) {        
        logger.entering("ElementFactory", "make", new Object[] { factoryName, name});
        return elementFor(makeRawElement(factoryName, name), factoryName);
    }

    static Pointer makeRawElement(String factoryName, String name) {
        logger.entering("ElementFactory", "makeRawElement", new Object[] { factoryName, name});
        Pointer elem = gst.ptr_gst_element_factory_make(factoryName, name);
        logger.log(DEBUG, "Return from gst_element_factory_make=" + elem);
        if (elem == null) {
            throw new IllegalArgumentException("No such Gstreamer factory: "
                    + factoryName);
        }
        return elem;
    }
    
    @SuppressWarnings("serial")
    private static final Map<String, Class<? extends Element>> typeMap
        = new HashMap<String, Class<? extends Element>>() {{
            put("appsink",    AppSink.class);
            put("appsrc",     AppSrc.class);
            put("decodebin",  DecodeBin.class);
            put("decodebin2", DecodeBin2.class);
            put("fakesink",   FakeSink.class);
            put("fakesrc",    FakeSrc.class);
            put("filesrc",    FileSrc.class);
            put("playbin",    PlayBin.class);
            put("playbin2",   PlayBin2.class);
            put("typefind",   TypeFind.class);
    }};
    @SuppressWarnings("unchecked")
    private static Element elementFor(Pointer ptr, String factoryName) {
        Class<? extends Element> cls = typeMap.get(factoryName);
        cls = (cls == null) ? (Class<Element>)GstTypes.classFor(ptr) : cls;
        cls = (cls == null || !Element.class.isAssignableFrom(cls)) ? Element.class : cls;
        return GstObject.objectFor(ptr, cls);
    }
}

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

package org.gstreamer;
import com.sun.jna.Pointer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gstreamer.lowlevel.GstAPI;

/**
 *
 */
public class ElementFactory extends GstObject {
    static Logger logger = Logger.getLogger(ElementFactory.class.getName());
    static Level DEBUG = Level.FINE;
    private static GstAPI gst = GstAPI.gst;
    private String factoryName = "";
    
    /**
     * Creates a new instance of ElementFactory
     */
    ElementFactory(Pointer ptr, boolean needRef, boolean ownsHandle) {
        super(ptr, needRef, ownsHandle);
        logger.entering("ElementFactory", "<init>", new Object[] { ptr, needRef, ownsHandle});
    }
    /**
     * Creates a new element from the factory.
     *
     * @param name the name to assign to the created Element
     * @return A new {@link Element}
     */
    public Element create(String name) {
        logger.entering("ElementFactory", "create", name);
        Pointer elem = gst.gst_element_factory_create(handle(), name);
        logger.log(DEBUG, "gst_element_factory_create returned: " + elem);
        if (elem == null || !elem.isValid()) {
            throw new IllegalArgumentException("Cannot create GstElement");
        }
        return elementFor(elem, factoryName);
    }
    /**
     * Returns the name of the person who wrote the factory.
     * 
     * @return The name of the author
     */
    public String getAuthor() {
        logger.entering("ElementFactory", "getAuthor");
        return gst.gst_element_factory_get_author(handle());
    }
    /**
     * Returns a description of the factory.
     * 
     * @return A brief description of the factory.
     */
    public String getDescription() {
        logger.entering("ElementFactory", "getDescription");
        return gst.gst_element_factory_get_description(handle());
    }
    /**
     * Returns the long, English name for the factory.
     * 
     * @return The long, English name for the factory.
     */
    public String getLongName() {
        logger.entering("ElementFactory", "getLongName");
        return gst.gst_element_factory_get_longname(handle());
    }
    
    /**
     * Returns a string describing the type of factory.
     * This is an unordered list separated with slashes ('/').
     * 
     * @return The description of the type of factory.
     */
    public String getKlass() {
        logger.entering("ElementFactory", "getKlass");
        return gst.gst_element_factory_get_klass(handle());
    }
    
    /**
     * Retrieve a handle to a factory that can produce {@link Element}s
     * 
     * @param name The type of {@link Element} to produce.
     * @return An ElementFactory that will produce {@link Element}s of the 
     * desired type.
     */
    public static ElementFactory find(String name) {
        logger.entering("ElementFactory", "find", name);
        Pointer f = gst.gst_element_factory_find(name);
        if (f == null) {
            throw new IllegalArgumentException("No such Gstreamer factory: " + name);
        }
        ElementFactory factory = ElementFactory.objectFor(f, true);
        factory.factoryName = name;
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
        return elementFor(makeRawElement(factoryName, name), name);
    }
    static ElementFactory objectFor(Pointer ptr, boolean needRef) {
        logger.entering("ElementFactory", "objectFor", new Object[] { ptr, needRef });
        return (ElementFactory) GstObject.objectFor(ptr, ElementFactory.class, needRef);
    }
    static Pointer makeRawElement(String factoryName, String name) {
        logger.entering("ElementFactory", "makeRawElement", new Object[] { factoryName, name});
        Pointer elem = gst.gst_element_factory_make(factoryName, name);
        logger.log(DEBUG, "Return from gst_element_factory_make=" + elem);
        if (elem == null || !elem.isValid()) {
            throw new IllegalArgumentException("No such Gstreamer factory: "
                    + factoryName);
        }
        return elem;
    }
    private static Map<String, Class<? extends Element>> typeMap;
    static {
        typeMap = new HashMap<String, Class<? extends Element>>();
        typeMap.put("playbin", PlayBin.class);
    }
    private static Element elementFor(Pointer ptr, String factoryName) {
        Class<? extends Element> cls = typeMap.get(factoryName);
        cls = (cls == null) ? Element.class : cls;
        return (Element) GstObject.objectFor(ptr, cls);
    }
}

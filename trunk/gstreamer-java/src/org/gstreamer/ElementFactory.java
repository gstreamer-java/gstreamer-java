/*
 * ElementFactory.java
 */

package org.gstreamer;
import com.sun.jna.Pointer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
public class ElementFactory extends GstObject {
    static Logger logger = Logger.getLogger(ElementFactory.class.getName());
    static Level DEBUG = Level.FINE;
    
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
        return Element.objectFor(elem);
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
     * Retrieve a handle to a factory that can produce @{link Element}s
     * 
     * @param name The type of @{link Element} to produce.
     * @return An ElementFactory that will produce @{link Element}s of the 
     * desired type.
     */
    public static ElementFactory find(String name) {
        logger.entering("ElementFactory", "find", name);
        Pointer f = gst.gst_element_factory_find(name);
        if (f == null) {
            throw new IllegalArgumentException("No such Gstreamer factory: " + name);
        }
        return ElementFactory.objectFor(f, true);
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
        return Element.objectFor(makeRawElement(factoryName, name));
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
}

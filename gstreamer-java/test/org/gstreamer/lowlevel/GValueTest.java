/* 
 * Copyright (c) 2008 Wayne Meissner
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

package org.gstreamer.lowlevel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.lowlevel.GValueAPI.GValue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 *
 */
public class GValueTest {
	private static final GValueAPI api = GValueAPI.GVALUE_API;
	
	public interface  GValueTestAPI extends Library {
		   @SuppressWarnings("serial")
		GValueTestAPI API = GNative.loadLibrary("gobject-2.0", GValueTestAPI.class,
		    		new HashMap<String, Object>() {});
		   
		   void g_value_set_object(Pointer value, Pointer obj);

		   Pointer g_value_get_object(Pointer pointer);
		
	}
	
    public GValueTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("GValueTest", new String[] {});
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        Gst.deinit();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test public void testInt() throws Exception {
    	GValue v = new GValue();
    	api.g_value_init(v, GType.INT);
    	api.g_value_set_int(v, 5);
    	
    	assertEquals("int value mismatch", 5, v.getValue());
    	
    	api.g_value_set_int(v, 6);
    	
    	assertEquals("int value mismatch", 6, v.getValue());
    	
    	assertTrue("type mismatch", v.getValue() instanceof Integer);
    	
    	
    }

    /**
     * Test type conversion of object value when using
     * an object created 'the proper way'
     */
    @Test public void testObjectPtrRef() throws Exception {
	// the following probably puts 'e' into the object reference map

    	Element e = ElementFactory.make("fakesink", "fakesink");
    	
    	GValue v = new GValue();
    	api.g_value_init(v, GType.OBJECT);
    	api.g_value_set_object(v, e);
    	
    	Object obj = v.getValue();
    	
    	assertTrue("type mismatch", obj instanceof Element);

    	assertEquals("object mismatch", e, obj);
    }
    
    /**
     * Test type conversion of object value trying to bypass the object reference map
     */
     @Test public void testObjectTypeMap() throws Exception {

	 Pointer p;
	 
	 {
	     /*
	      * Not using ElementFactory.make() here probably prevents the element 
	      * from being placed in the object reference map and therefore forces
	      * type mapper conversion - what we want to test
	      */
	     
	     ElementFactory factory = GstElementFactoryAPI.GSTELEMENTFACTORY_API.gst_element_factory_find("videotestsrc");
	     p = GstElementFactoryAPI.GSTELEMENTFACTORY_API.ptr_gst_element_factory_create(factory, "videotestsrc");
	 }
    	    	
    	GValue v = new GValue();
    	api.g_value_init(v, GType.OBJECT);
    	
    	GValueTestAPI.API.g_value_set_object(v.getPointer(), p);
    	
    	Object obj = v.getValue();

    	assertTrue("type mismatch", obj instanceof Element);
    }
}
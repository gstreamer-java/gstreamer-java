/* 
 * Copyright (c) 2008 Wayne Meissner
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

import java.io.File;

import org.gstreamer.lowlevel.GstNative;
import org.gstreamer.lowlevel.GstObjectAPI;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Loads and saves pipelines in an XML file
 */
public class GstXML {
    private static interface LibC extends com.sun.jna.Library {
        
        LibC INSTANCE = (LibC) Native.loadLibrary("c", LibC.class);
        
        Pointer fopen(String path, String mode);
        int fclose(Pointer fp);
    }
    private static interface GstXMLAPI extends GstObjectAPI {
        GstObject gst_xml_new();
        int gst_xml_write_file(Element element, Pointer out);
        boolean gst_xml_parse_file(GstObject xml, String fname, String root);
        Element gst_xml_get_element(GstObject xml, String name);
    }
    
    private static final GstXMLAPI gst = GstNative.load(GstXMLAPI.class);
    private final File file;
    
    /**
     * Creates a new Gstreamer XML reader/writer
     * 
     * @param file the file to load from/save to
     */
    public GstXML(java.io.File file) {
        this.file = file;
        
    }
    
    /**
     * Loads a new {@link Element} from the XML file
     * 
     * @param elementName the name of the element to load
     * 
     * @return a new <tt>Element</tt>
     */
    public Element loadElement(String elementName) {
        GstObject xml = gst.gst_xml_new();
        if (!gst.gst_xml_parse_file(xml, file.getAbsolutePath(), null)) {
            throw new GstException("Could not parse " + file);
        }
        Element element = gst.gst_xml_get_element(xml, elementName);
        if (element == null) {
            throw new GstException("Could not load " + elementName);
        }
        return element;
    }
    
    /**
     * Saves an existing pipeline to a XML file.
     * 
     * @param element the element to save
     */
    public void saveElement(Element element) {
        Pointer fp = LibC.INSTANCE.fopen(file.getAbsolutePath(), "w");
        if (fp == null) {
            throw new GstException("Could not open " + file 
                    + " [errno=" + Native.getLastError() + "]");
        }
        gst.gst_xml_write_file(element, fp);
        LibC.INSTANCE.fclose(fp);
    }
}

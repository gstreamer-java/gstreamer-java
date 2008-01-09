/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.gstreamer.example;

import org.gstreamer.Caps;
import org.gstreamer.MainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.elements.TypeFind;

/**
 *
 */
public class TypeFindTest {
    
    /** Creates a new instance of TypeFindTest */
    public TypeFindTest() {
    }
    public static void main(String[] args) {
        args = Gst.init("TypeFind Test", args);
        /* create elements */
        Pipeline pipeline = new Pipeline("my_pipeline");
        Element source = ElementFactory.make("filesrc", "source");
        source.set("location", args[0]);
        TypeFind typefind = new TypeFind("typefinder");
        
        /* you would normally check that the elements were created properly */
        
        /* put together a pipeline */
        pipeline.addMany(source, typefind);
        Element.linkMany(source, typefind);
        
        /* listen for types found */
        typefind.connect(new TypeFind.HAVE_TYPE() {

            public void typeFound(Element elem, int probability, Caps caps) {
                System.out.printf("New type found: probability=%d caps=%s\n",
                        probability, caps.toString());
            }
        });
        
        /* start the pipeline */
        pipeline.setState(State.PLAYING);
        
        new MainLoop().run();
    }
}

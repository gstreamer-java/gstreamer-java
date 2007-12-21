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

package example;

import org.gstreamer.MainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.event.ElementEvent;
import org.gstreamer.event.ElementListener;

/**
 *
 */
public class DynamicPadTest {
    
    /** Creates a new instance of DynamicPadTest */
    public DynamicPadTest() {
    }
    public static void main(String[] args) {
        args = Gst.init("Dynamic Pad Test", args);
        /* create elements */
        Pipeline pipeline = new Pipeline("my_pipeline");
        Element source = ElementFactory.make("filesrc", "source");
        source.set("location", args[0]);
        Element demux = ElementFactory.make("oggdemux", "demuxer");
        
        /* you would normally check that the elements were created properly */
        
        /* put together a pipeline */
        pipeline.addMany(source, demux);
        Element.linkPads(source, "src", demux, "sink");
        
        /* listen for newly created pads */
        demux.addElementListener(new ElementListener() {
            public void noMorePads(ElementEvent evt) {
            }
            public void padAdded(ElementEvent evt) {
                System.out.println("New Pad " + evt.getPad().getName() + " was created");
            }
            public void padRemoved(ElementEvent evt) {
            }
        });
        
        /* start the pipeline */
        pipeline.setState(State.PLAYING);
        
        new MainLoop().run();
    }
}

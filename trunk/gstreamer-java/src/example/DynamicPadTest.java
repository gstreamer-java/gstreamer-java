/*
 * DynamicPadTest.java
 */

package example;

import org.gstreamer.GMainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pad;
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
        pipeline.add(source, demux);
        Pad src = source.getPad("src");
        Pad sink = demux.getPad("sink");
        src.link(sink);
        //gst_element_link_pads (source, "src", demux, "sink");
        
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
        
        new GMainLoop().run();
    }
}

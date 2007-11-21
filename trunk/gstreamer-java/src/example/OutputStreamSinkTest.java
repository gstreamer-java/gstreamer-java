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

import java.nio.channels.Pipe;
import java.util.Map;
import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GMainLoop;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.TagList;
import org.gstreamer.io.ReadableByteChannelSrc;
import org.gstreamer.io.WriteableByteChannelSink;

public class OutputStreamSinkTest {
    static final String name = "InputStreamSrcTest";    

    public static void main(String[] args) {
       
        args = Gst.init(name, args);
        if (args.length < 1) {
            System.err.println("Usage: " + name + " <filename>");
            System.exit(1);
        }
        Pipe pipeChannel;
        try {
            pipeChannel = Pipe.open();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
        //
        // Construct a pipeline: filesrc ! OutputStreamSink
        //
        Pipeline inputPipe = new Pipeline("input pipeline");
        Element filesrc = ElementFactory.make("filesrc", "File source");
        filesrc.set("location", args[0]);
        Element outputstream = new WriteableByteChannelSink(pipeChannel.sink(), "output stream");
        inputPipe.addMany(filesrc, outputstream);
        Element.linkMany(filesrc, outputstream);
        inputPipe.play();
        //
        // Now construct the output pipeline to process the data
        //
        
        Element src = null;
        try {            
            src = new ReadableByteChannelSrc(pipeChannel.source(), "input file");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        
        Element decodeBin = ElementFactory.make("decodebin", "Decode Bin");
        Pipeline pipe = new Pipeline("main pipeline");
        pipe.addMany(src, decodeBin);
        src.link(decodeBin);
        
        /* create audio output */
        final Bin audioBin = new Bin("Audio Bin");
        
        Element conv = ElementFactory.make("audioconvert", "Audio Convert");
        Element sink = ElementFactory.make("autoaudiosink", "sink");
        audioBin.addMany(conv, sink);
        Element.linkMany(conv, sink);        
        audioBin.addPad(new GhostPad("sink", conv.getPad("sink")));
        
        pipe.add(audioBin);

        decodeBin.connect(new Element.NEWDECODEDPAD() {
            public void newDecodedPad(Element elem, Pad pad, boolean last) {
                System.out.println("newDecodedPad");
                  /* only link once */
                Pad audioPad = audioBin.getPad("sink");
                if (pad.isLinked()) {
                    return;
                }
  
                /* check media type */
                Caps caps = pad.getCaps();
                Structure struct = caps.getStructure(0);
                if (struct.getName().startsWith("audio/")) {
                    System.out.println("Got audio pad");
                    /* link'n'play */
                    pad.link(audioPad);  
                }
                
            }
        });
        Bus bus = pipe.getBus();
        bus.connect(new Bus.TAG() {
            public void tagMessage(GstObject source, TagList tagList) {
                System.out.println("Got TAG event");                
                Map<String, Object> m = tagList.getTags();
                for (String tag : m.keySet()) {
                    System.out.println("Tag " + tag + " = " + m.get(tag));
                }
            }
        });
        bus.connect(new Bus.ERROR() {
            public void errorMessage(GstObject source, int code, String message) {
                System.out.println("Error: code=" + code + " message=" + message);
            }
        });
        bus.connect(new Bus.EOS() {

            public void eosMessage(GstObject source) {
                System.out.println("Got EOS!");
            }
            
        });
        pipe.play();
        new GMainLoop().run();
    }
    
}

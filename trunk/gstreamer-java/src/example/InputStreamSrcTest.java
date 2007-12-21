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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Map;
import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.MainLoop;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.io.InputStreamSrc;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.TagList;

public class InputStreamSrcTest {
    static final String name = "InputStreamSrcTest";    

    public static void main(String[] args) {
       
        args = Gst.init(name, args);
        if (args.length < 1) {
            System.err.println("Usage: " + name + " <filename>");
            System.exit(1);
        }
        InputStreamSrc src = null;
        try {
            final FileInputStream srcFile = new FileInputStream(args[0]);
//            src = new InputStreamSrc(new BufferedInputStream(srcFile), "input file");
            src = new InputStreamSrc(srcFile, "input file");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //Element src = ElementFactory.make("filesrc", "Input File");
        //src.set("location", args[0]);
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

        decodeBin.connect(new Element.NEW_DECODED_PAD() {
            public void newDecodedPad(Element elem, Pad pad, boolean last) {
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
        new MainLoop().run();
    }
    
}

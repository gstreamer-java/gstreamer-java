/* 
 * Copyright (c) 2007, 2008 Wayne Meissner
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.gstreamer.example;

import java.nio.channels.Pipe;

import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.TagList;
import org.gstreamer.elements.DecodeBin2;
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
        
        DecodeBin2 decodeBin = (DecodeBin2) ElementFactory.make("decodebin2", "Decode Bin");
        Pipeline pipe = new Pipeline("main pipeline");
        pipe.addMany(src, decodeBin);
        src.link(decodeBin);
        
        /* create audio output */
        final Bin audioBin = new Bin("Audio Bin");
        
        Element conv = ElementFactory.make("audioconvert", "Audio Convert");
        Element sink = ElementFactory.make("autoaudiosink", "sink");
        audioBin.addMany(conv, sink);
        Element.linkMany(conv, sink);        
        audioBin.addPad(new GhostPad("sink", conv.getStaticPad("sink")));
        
        pipe.add(audioBin);

        decodeBin.connect(new Element.PAD_ADDED() {
			public void padAdded(Element element, Pad pad) {
				System.out.println("newDecodedPad");
				/* only link once */
				Pad audioPad = audioBin.getStaticPad("sink");
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
            public void tagsFound(GstObject source, TagList tagList) {
                System.out.println("Got TAG event");                
                for (String tag : tagList.getTagNames()) {
                    System.out.println("Tag " + tag + " = " + tagList.getValue(tag, 0));
                }
            }
        });
        bus.connect(new Bus.ERROR() {
            public void errorMessage(GstObject source, int code, String message) {
                System.out.println("Error: code=" + code + " message=" + message);
            }
        });
        bus.connect(new Bus.EOS() {

            public void endOfStream(GstObject source) {
                System.out.println("Got EOS!");
            }
            
        });
        pipe.play();
        Gst.main();
    }
    
}

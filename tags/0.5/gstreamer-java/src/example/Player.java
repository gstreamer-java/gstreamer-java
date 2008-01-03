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

import java.io.File;
import java.util.Map;
import org.gstreamer.MainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Bus;
import org.gstreamer.GstObject;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.TagList;
import org.gstreamer.TagMergeMode;

/**
 *
 */
public class Player {
    
    /** Creates a new instance of Player */
    public Player() {
    }
    static TagList tags;
    public static void main(String[] args) {
        for (String s : args) {
            System.out.println("cmdline arg=" + s);
        }
        
        args = Gst.init("Simple Player", args);
        for (String s : args) {
            System.out.println("Leftover arg=" + s);
        }
        MainLoop loop = new MainLoop();
        PlayBin player = new PlayBin("Example Player");
        player.setInputFile(new File(args[0]));
        Bus bus = player.getBus();
        bus.connect(new Bus.TAG() {
            public void tagMessage(GstObject source, TagList tagList) {
                System.out.println("Got TAG event");
                if (tags == null) {
                    tags = tagList;
                } else {
                    tags = tags.merge(tagList, TagMergeMode.APPEND);
                }
                Map<String, Object> m = tags.getTags();
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
        
        player.play();
        
        loop.run();
    }
}

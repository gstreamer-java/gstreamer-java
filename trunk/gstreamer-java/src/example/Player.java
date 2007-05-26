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
import org.gstreamer.GMainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Bus;
import org.gstreamer.PlayBin;
import org.gstreamer.TagList;
import org.gstreamer.TagMergeMode;
import org.gstreamer.event.BusListener;
import org.gstreamer.event.ErrorEvent;
import org.gstreamer.event.StateEvent;

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
        GMainLoop loop = new GMainLoop();
        PlayBin player = new PlayBin("Example Player");
        player.setInputFile(new File(args[0]));
        Bus bus = player.getBus();
        BusListener l = new BusListener() {
            public void eosEvent() {
                System.out.println("Got EOS event");
            }
            public void errorEvent(ErrorEvent e) {
                System.out.println("Got ERROR event");
            }
            public void infoEvent(ErrorEvent e) {
                System.out.println("Got INFO event");
            }
            public void stateEvent(StateEvent e) {
                //System.out.println("Got STATE event");
            }
            public void warningEvent(ErrorEvent e) {
                System.out.println("Got WARNING event");
            }
            public void tagEvent(TagList l) {
                System.out.println("Got TAG event");
                if (tags == null) {
                    tags = l;
                } else {
                    tags = tags.merge(l, TagMergeMode.APPEND);
                }
                Map<String, Object> m = tags.getTags();
                for (String tag : m.keySet()) {
                    System.out.println("Tag " + tag + " = " + m.get(tag));
                }
            }
        };
        bus.addBusListener(l);
        player.play();
        
        loop.run();
    }
}

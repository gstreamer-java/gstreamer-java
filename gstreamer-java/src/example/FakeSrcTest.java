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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.JFrame;
import org.gstreamer.GMainLoop;
import org.gstreamer.Gst;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.TagList;
import org.gstreamer.event.HandoffEvent;
import org.gstreamer.event.HandoffListener;
import org.gstreamer.swing.GstVideoComponent;

/**
 *
 */
public class FakeSrcTest {
    
    /** Creates a new instance of FakeSrcTest */
    public FakeSrcTest() {
    }
    static TagList tags;
    public static void main(String[] args) {
        JFrame frame = new JFrame("FakeSrcTest");
        args = Gst.init("FakeSrcTest", args);
        final int width = 320, height = 200;
        GMainLoop loop = new GMainLoop();
        Element fakesrc, flt, conv, videosink;
        /* setup pipeline */
        Pipeline pipeline = new Pipeline("pipeline");
        fakesrc = ElementFactory.make("fakesrc", "source");
        //fakesrc = ElementFactory.make("videotestsrc", "source");
        flt = ElementFactory.make("capsfilter", "flt");
        conv = ElementFactory.make("ffmpegcolorspace", "conv");
       
        Caps fltcaps = new Caps("video/x-raw-rgb, width=" + width + ", height=" + height + ", bpp=16, depth=16, framerate=25/1");
        flt.setCaps(fltcaps);
        
        GstVideoComponent panel = new GstVideoComponent();
        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel, BorderLayout.CENTER);
        videosink = panel.getElement();
        pipeline.addMany(fakesrc, flt, conv, videosink);
        fakesrc.link(flt, conv, videosink);
        fakesrc.set("signal-handoffs", true);
        fakesrc.set("sizemax", width * height * 2);
        fakesrc.set("sizetype", 2);
        fakesrc.addHandoffListener(new HandoffListener() {
            byte color = 0;
            byte[] data = new byte[width * height * 2];
            public void handoff(HandoffEvent ev) {
                Buffer buffer = ev.getBuffer();
                Arrays.fill(data, color++);
                buffer.write(0, data, 0, data.length);
            }
        });
        frame.setSize(640, 480);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        pipeline.setState(State.PLAYING);
        loop.startInBackground();
    }
}

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Closure;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.TagList;
import org.gstreamer.elements.BaseSrc;
import org.gstreamer.elements.FakeSrc;
import org.gstreamer.swing.VideoComponent;

/**
 *
 */
public class FakeSrcTest {
    
    /** Creates a new instance of FakeSrcTest */
    public FakeSrcTest() {
    }
    private static Pipeline pipeline;
    static TagList tags;
    public static void main(String[] args) {

        args = Gst.init("FakeSrcTest", args);
        final int width = 320, height = 200;
        /* setup pipeline */
        pipeline = new Pipeline("pipeline");
        final FakeSrc fakesrc = (FakeSrc) ElementFactory.make("fakesrc", "source");
        //fakesrc = ElementFactory.make("videotestsrc", "source");
        final Element srcfilter = ElementFactory.make("capsfilter", "srcfilter");
       
        Caps fltcaps = new Caps("video/x-raw-rgb, framerate=2/1"
                + ", width=" + width + ", height=" + height 
                + ", bpp=16, depth=16");
        srcfilter.setCaps(fltcaps);
        final Element videorate = ElementFactory.make("videorate", "videorate");
        final Element ratefilter = ElementFactory.make("capsfilter", "RateFilter");
        ratefilter.setCaps(Caps.fromString("video/x-raw-rgb, framerate=2/1"));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("FakeSrcTest");
                VideoComponent panel = new VideoComponent();
                panel.setPreferredSize(new Dimension(width, height));
                frame.add(panel, BorderLayout.CENTER);
                Element videosink = panel.getElement();
                pipeline.addMany(fakesrc, srcfilter, videorate, ratefilter, videosink);
                Element.linkMany(fakesrc, srcfilter, videorate, ratefilter, videosink);
                fakesrc.set("signal-handoffs", true);
                fakesrc.set("sizemax", width * height * 2);
                fakesrc.set("sizetype", 2);
                fakesrc.set("sync", true);
                fakesrc.set("is-live", true);
                fakesrc.set("filltype", 1); // Don't fill the buffer before handoff
                fakesrc.connect(new BaseSrc.HANDOFF() {
                    byte color = 0;
                    byte[] data = new byte[width * height * 2];
                    public void handoff(BaseSrc src, Buffer buffer, Pad pad) {
                        System.out.println("HANDOFF: Element=" + src.getNativeAddress()
                                + " buffer=" + buffer.getNativeAddress()
                                + " pad=" + pad.getNativeAddress());
                        Arrays.fill(data, color++);
                        buffer.getByteBuffer().put(data, 0, data.length);
                    }
                });
                fakesrc.connect("handoff", new Closure() {
                    @SuppressWarnings("unused")
                    public void invoke(Element element, Buffer buffer, Pad pad) {
                        System.out.println("Closure: Element=" + element.getNativeAddress()
                                + " buffer=" + buffer.getNativeAddress()
                                + " pad=" + pad.getNativeAddress());
                    }
                });
                frame.setSize(640, 480);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                pipeline.play();
            }
        });
        
    }
}

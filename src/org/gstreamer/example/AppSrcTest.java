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
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.TagList;
import org.gstreamer.elements.AppSrc;
import org.gstreamer.swing.VideoComponent;

/**
 *
 */
public class AppSrcTest {
    
    /** Creates a new instance of AppSrcTest */
    public AppSrcTest() {
    }
    private static Pipeline pipeline;
    static TagList tags;
    public static void main(String[] args) {

        args = Gst.init("AppSrcTest", args);
        final int width = 320, height = 200;
        /* setup pipeline */
        pipeline = new Pipeline("pipeline");
        final AppSrc appsrc = (AppSrc) ElementFactory.make("appsrc", "source");
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
                pipeline.addMany(appsrc, srcfilter, videorate, ratefilter, videosink);
                Element.linkMany(appsrc, srcfilter, videorate, ratefilter, videosink);
                appsrc.set("emit-signals", true);
                appsrc.connect(new AppSrc.NEED_DATA() {
                    byte color = 0;
                    byte[] data = new byte[width * height * 2];
                    public void needData(AppSrc elem, int size) {
                        System.out.println("NEED_DATA: Element=" + elem.getNativeAddress()
                                + " size=" + size);
                        Arrays.fill(data, color++);
                        Buffer buffer = new Buffer(data.length);
                        buffer.getByteBuffer().put(data);
        				appsrc.pushBuffer(buffer);
                    }
                });
                appsrc.connect(new AppSrc.ENOUGH_DATA() {
					public void enoughData(AppSrc elem) {
						System.out.println("NEED_DATA: Element=" + elem.getNativeAddress());
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
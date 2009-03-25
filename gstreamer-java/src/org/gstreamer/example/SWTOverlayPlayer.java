/* 
 * Copyright (c) 2009 Tamas Korodi <kotyo@zamba.fm>
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swt.VideoComponent;

public class SWTOverlayPlayer {	
    /** Creates a new instance of SwingPlayer */
    public SWTOverlayPlayer() {
    }
    
    private static Bus bus;
    
    public static void main(String[] args) {
        args = Gst.init("SWT Player", args);
        Pipeline pipe = new Pipeline("pipe");
        //Element src = ElementFactory.make("v4l2src", "src");
        //src.set("device", "/dev/video2");
        Element src = ElementFactory.make("videotestsrc", "src");
        Element src1 = ElementFactory.make("videotestsrc", "src1");
        Element src2 = ElementFactory.make("videotestsrc", "src2");
        Element src3 = ElementFactory.make("videotestsrc", "src3");
        
        
        try{
            
            
            Display display = new Display();
            Shell shell = new Shell(display);
            shell.setSize(640, 480);

            shell.setText("SWT Video Test");
            final VideoComponent canvas = new VideoComponent(shell,SWT.NONE, true);
            canvas.getElement().setName("0");
            final VideoComponent canvas1 = new VideoComponent(shell,SWT.NONE, true);
            canvas1.getElement().setName("1");
            final VideoComponent canvas2 = new VideoComponent(shell,SWT.NONE, false);
            canvas2.getElement().setName("2");
            final VideoComponent canvas3 = new VideoComponent(shell,SWT.NONE, false);
            canvas3.getElement().setName("3");
            
            canvas.setLocation(0, 0);
            canvas.setSize(320,240);
            
            canvas1.setLocation(320, 0);
            canvas1.setSize(320,240);
                    
            canvas2.setLocation(0, 240);
            canvas2.setSize(320,240);
            
            canvas3.setLocation(320, 240);
            canvas3.setSize(320,240);
 

        
                                
                
                /*
                bus = player.getBus();
                
                bus.connect(new Bus.ERROR() {
                    public void errorMessage(GstObject source, int code, String message) {
                        System.out.println("Error: code=" + code + " message=" + message);
                    }
                });
                final Element videoSink = ElementFactory.make(overlayFactory, "overlay video sink");
                player.setVideoSink(videoSink);
                //
                // Setting the overlay window ID is supposed to be done from a sync handler
                // but that doesn't work on windows
                //
                if (!Platform.isWindows()) { 
                    bus.setSyncHandler(new BusSyncHandler() {

                        public BusSyncReply syncMessage(Message msg) {
                            Structure s = msg.getStructure();
                            if (s == null || !s.hasName("prepare-xwindow-id")) {
                                return BusSyncReply.PASS;
                            }
                            XOverlay.wrap(videoSink).setWindowID(canvas);
                            return BusSyncReply.DROP;
                        }
                    });
                } else {
                    XOverlay.wrap(videoSink).setWindowID(canvas);
                } 
                
                */
            
            pipe.addMany(src, src1, src2, src3, canvas.getElement(), canvas1.getElement(), canvas2.getElement(), canvas3.getElement());
            Element.linkMany(src, canvas.getElement());
            Element.linkMany(src1, canvas1.getElement());
            Element.linkMany(src2, canvas2.getElement());
            Element.linkMany(src3, canvas3.getElement());
            
            //Element fake = ElementFactory.make("fakesink", "fake");
            //fake.set("sync", true);
            //pipe.addMany(src, q, fake);
            //Element.linkMany(src, q, fake);
            
            pipe.setState(State.PLAYING);
            
            /*
        	PlayBin player = new PlayBin("Overlay Player");                
        	player.setInputFile(new File(file));
            player.setVideoSink(canvas.getElement());
            player.play();     
            
        	PlayBin player2 = new PlayBin("Overlay Player 2");                
        	player2.setInputFile(new File(file));
            player2.setVideoSink(canvas2.getElement());
            player2.play();     
            */
                
               
                
                shell.open();
                while (!shell.isDisposed()) {
                        if (!display.readAndDispatch())
                                display.sleep();
                }
                display.dispose();

                
        } catch (Exception e) {
        	
        }
    }
}

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

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.gstreamer.Bus;
import org.gstreamer.BusSyncReply;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GMainLoop;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Message;
import org.gstreamer.PlayBin;
import org.gstreamer.Structure;
import org.gstreamer.event.BusSyncHandler;
import org.gstreamer.lowlevel.GstAPI;
import org.gstreamer.lowlevel.GstInterfacesAPI;

public class OverlayPlayer {

    /** Creates a new instance of SwingPlayer */
    public OverlayPlayer() {
    }
    private static Bus bus;
    
    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");
        
        args = Gst.init("Swing Player", args);
        if (args.length < 1) {
            System.err.println("Usage: SwingPlayer <filename>");
            System.exit(1);
        }
        final String file = args[0];
        final String overlayFactory = Platform.isWindows() ? "directdrawsink" : "xvimagesink";
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                JFrame frame = new JFrame("Overlay Test");
                final Canvas canvas = new Canvas();
                canvas.setPreferredSize(new Dimension(640, 480));
                frame.add(canvas, BorderLayout.CENTER);                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                                
                PlayBin player = new PlayBin("Overlay Player");                
                player.setInputFile(new File(file));
                bus = player.getBus();
                
                bus.connect(new Bus.ERROR() {
                    @Override
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
                            NativeLong windowID = new NativeLong(Native.getComponentID(canvas));
                            GstInterfacesAPI.INSTANCE.gst_x_overlay_set_xwindow_id(videoSink, windowID);
                            return BusSyncReply.DROP;
                        }
                    });
                } else {

                    Pointer windowID = Native.getComponentPointer(canvas);
                    System.out.println("Native window handle=" + windowID);
                    GstInterfacesAPI.INSTANCE.gst_x_overlay_set_xwindow_id(videoSink, windowID);
                } 
                player.play();       
                new GMainLoop().startInBackground();
            }  
        });
        //new GMainLoop().run();
    }
}

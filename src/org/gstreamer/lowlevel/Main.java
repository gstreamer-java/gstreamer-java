/*
 * Copyright (c) 2008 Wayne Meissner
 *
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.lowlevel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.Version;
import org.gstreamer.swing.VideoComponent;
import org.gstreamer.swing.VideoPlayer;

/**
 * Simple startup tests that can be run via: java -jar gstreamer-java.jar
 */
public class Main {
    static Pipeline pipe;
    static void diagnostics(String[] args) {
        Package pkg = com.sun.jna.Native.class.getPackage();
        String title = pkg.getSpecificationTitle();
        if (title == null) {
            title = "JNA";
        }
        String version = pkg.getSpecificationVersion();
        if (version == null) {
            version = "Cannot determine version - no package information present";
        }
        String implVersion = pkg.getImplementationVersion();
        if (implVersion == null) {
            implVersion = "unknown";
        }
        String jnaVersion = String.format(title + " API=%s Version=%s\n", version, implVersion);
        final String PREFIX = "DIAG: ";
        System.out.flush();
        System.out.println(PREFIX + jnaVersion);

        args = Gst.init("unknown", args);
        Version v = Gst.getVersion();
        System.out.println(PREFIX + "Gst.init succeeded. gstreamer version " + v);
        System.out.flush();
        pipe = new Pipeline();
        if (pipe == null) {
            throw new RuntimeException("Failed to create Pipeline");
        }
        System.out.println(PREFIX + "Pipeline created successfully");
        System.out.flush();
        final Element videosrc = ElementFactory.make("videotestsrc", "source");
        System.out.println(PREFIX + "videotestsrc created successfully");
        System.out.flush();
        final Element videofilter = ElementFactory.make("capsfilter", "flt");
        System.out.println(PREFIX + "capsfilter created successfully");
        System.out.flush();
        final int width = 400, height = 300;
        videofilter.setCaps(Caps.fromString("video/x-raw-yuv, width=" + width 
                + ", height=" + height
                + ", bpp=32, depth=32, framerate=25/1"));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Create the video component and link it in
                VideoComponent videoComponent = new VideoComponent();
                System.out.println(PREFIX + "VideoComponent created successfully");
                System.out.flush();
                Element videosink = videoComponent.getElement();
                pipe.addMany(videosrc, videofilter, videosink);
                Element.linkMany(videosrc, videofilter, videosink);

                // Now create a JFrame to display the video output
                JFrame frame = new JFrame("Swing Video Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(videoComponent, BorderLayout.CENTER);
                videoComponent.setPreferredSize(new Dimension(width, height));
                frame.pack();
                frame.setVisible(true);

                // Start the pipeline processing
                pipe.play();
                System.out.println(PREFIX + "pipeline set to PLAYING state");
                System.out.flush();
            }
        });
    }
    private static void play(String[] args) {
        args = Gst.init("gstreamer-java", args);
        final List<URI> playList = new LinkedList<URI>();
        for (String arg : args) {
            playList.add(new File(arg).toURI());
        }

        final String file = args[0];

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                JFrame frame = new JFrame("Swing Test");

                final VideoPlayer player = new VideoPlayer(file);
                player.setPreferredSize(new Dimension(640, 480));
                player.setControlsVisible(true);
                player.getMediaPlayer().setPlaylist(playList);
                frame.add(player, BorderLayout.CENTER);
                player.getMediaPlayer().play();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
    public static void main(String[] args) {
        String jnaPath = System.getProperty("jna.library.path");
        if (jnaPath == null) {
            System.setProperty("jna.library.path", "/usr/share/java:/opt/local/lib:/usr/local/lib:/usr/lib");
        }
        // Quartz is abysmally slow at scaling video for some reason, so turn it off.
        System.setProperty("apple.awt.graphics.UseQuartz", "false");

        if (args.length == 0) {
            diagnostics(args);
        } else {
            play(args);
        }
    }
}

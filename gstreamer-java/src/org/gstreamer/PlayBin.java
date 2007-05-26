/* 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.gstreamer;
import com.sun.jna.Pointer;
import java.io.File;
import java.net.URI;

/**
 *
 */
public class PlayBin extends Pipeline {
    
    /**
     * Creates a new PlayBin.
     * 
     * @param name The name used to identify this pipeline.
     */
    public PlayBin(String name) {
        super(ElementFactory.makeRawElement("playbin", name));
    }
    
    /**
     * Creates a new PlayBin.
     * 
     * @param name The name used to identify this pipeline.
     * @param uri The URI of the media file to load.
     */
    public PlayBin(String name, URI uri) {
        this(name);
        setURI(uri);
    }
    
    /**
     * Creates a new PlayBin proxy for a native gstreamer playbin
     * 
     * This constructor assumes ownership of the underlying native GstPipeline
     * and increments the reference count.
     * 
     * @param playbin The native Playbin object to wrap.
     */
    PlayBin(Pointer playbin, boolean needRef) {
        super(playbin, needRef);
    }
    
    /**
     * Creates a new PlayBin proxy.
     * 
     * @param playbin The native GstPlaybin object to wrap.
     * 
     * @param needRef true if the reference count needs to be incremented.
     * 
     * @param ownsHandle Whether this proxy should take ownership of the 
     *          native handle or not.  If true, then the underlying pipeline will be
     *          unreffed when the java object is garbage collected.
     */
    PlayBin(Pointer playbin, boolean needRef, boolean ownsHandle) {
        super(playbin, needRef, ownsHandle);
    }
    
    /**
     * Set the media file to play.
     * 
     * @param file The {@link java.io.File} to play.
     */
    public void setInputFile(File file) {
        setURI(file.toURI());
    }
    
    /**
     * Set the media URI to play.
     * 
     * @param uri The {@link java.net.URI} to play.
     */
    public void setURI(URI uri) {
        String uriString = uri.toString();
        
        // Need to fixup file:/ to be file:/// for gstreamer
        if ("file".equals(uri.getScheme())) {
            uriString = "file://" + uri.getPath();
        }
        set("uri", uriString);
    }
    
    /**
     * Set the audio output Element.
     * 
     * @param element The element to use for audio output.
     */
    public void setAudioSink(Element element) {
        setElement("audio-sink", element);
    }
    
    /**
     * Set the video output Element.
     * 
     * @param element The element to use for video output.
     */
    public void setVideoSink(Element element) {
        setElement("video-sink", element);
    }
    
    /**
     * Set the visualization output Element.
     * 
     * @param element The element to use for visualization.
     */
    public void setVisualization(Element element) {
        setElement("vis-plugin", element);
    }
    
    private void setElement(String key, Element e) {
        set(key, e);
    }
}

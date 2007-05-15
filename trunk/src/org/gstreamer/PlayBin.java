/*
 * PlayBin.java
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
     * Creates a new instance of PlayBin
     */
    public PlayBin(String name) {
        this(ElementFactory.makeRawElement("playbin", name), true, true);
    }
    public PlayBin(String name, URI uri) {
        this(name);
        setURI(uri);
    }
    PlayBin(Pointer ptr, boolean needRef) {
        this(ptr, true, needRef);
    }
    PlayBin(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
    }
    public void setInputFile(File f) {
        setURI(f.toURI());
    }
    public void setURI(URI uri) {
        String uriString = uri.toString();
        
        // Need to fixup file:/ to be file:/// for gstreamer
        if (uri.getScheme().equals("file")) {
            uriString = "file://" + uri.getPath();
        }
        set("uri", uriString);
    }
    public void setAudioSink(Element e) {
        setElement("audio-sink", e);
    }
    public void setVideoSink(Element e) {
        setElement("video-sink", e);
    }
    public void setVisualization(Element e) {
        setElement("vis-plugin", e);
    }
    private void setElement(String key, Element e) {
        set(key, e);
    }
}

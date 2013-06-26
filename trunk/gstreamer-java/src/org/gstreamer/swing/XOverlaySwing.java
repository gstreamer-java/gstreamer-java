package org.gstreamer.swing;

import org.gstreamer.Element;
import org.gstreamer.interfaces.XOverlayBase;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

public class XOverlaySwing extends XOverlayBase {

	public XOverlaySwing(Element element) {
		super(element);
	}
	
    /**
     * Wraps the {@link Element} in a <tt>XOverlay</tt> interface
     * 
     * @param element the element to use as a <tt>XOverlay</tt>
     * @return a <tt>XOverlay</tt> for the element
     */
    public static XOverlaySwing wrap(Element element) {
        return new XOverlaySwing(element);
    }

	/**
	 * Sets the native window for the {@link Element} to use to display video.
	 *
	 * @param window A native window to use to display video, or <tt>null</tt> to
	 * stop using the previously set window.
	 */
	public void setWindowHandle(java.awt.Component window) {
		
		long nativeWindow = 0;

		if (window != null) {
		
			if (window.isLightweight()) {
				throw new IllegalArgumentException("Component must be a native window");
			}
	    
			if (Platform.isWindows()) {
				nativeWindow = Pointer.nativeValue(Native.getComponentPointer(window));
			} else {
				nativeWindow = Native.getComponentID(window);
			}
		}
	     
		setWindowHandle(nativeWindow);
	}
}

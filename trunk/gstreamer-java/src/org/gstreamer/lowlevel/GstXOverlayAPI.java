package org.gstreamer.lowlevel;

import org.gstreamer.interfaces.XOverlay;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public interface GstXOverlayAPI extends Library {
	GstXOverlayAPI INSTANCE = GstNative.load("gstinterfaces",
			GstXOverlayAPI.class);

	GType gst_x_overlay_get_type();

	/* virtual class function wrappers */
	void gst_x_overlay_set_xwindow_id(XOverlay overlay, NativeLong xwindow_id);

	void gst_x_overlay_set_xwindow_id(XOverlay overlay, Pointer xwindow_id);

	void gst_x_overlay_expose(XOverlay overlay);

	void gst_x_overlay_handle_events(XOverlay overlay, boolean handle_events);
}

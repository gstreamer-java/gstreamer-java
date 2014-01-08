package org.gstreamer.lowlevel;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface AndroidAPI extends Library {
	
	public static final AndroidAPI api = (AndroidAPI) Native.loadLibrary("android", AndroidAPI.class);
			
	public void ANativeWindow_release(Pointer nativeWindow);
}

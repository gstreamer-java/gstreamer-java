package org.gstreamer.android;

import org.gstreamer.Element;
import org.gstreamer.interfaces.XOverlayBase;
import org.gstreamer.lowlevel.AndroidAPI;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sun.jna.Pointer;

// A simple SurfaceView whose width and height can be set from the outside
public abstract class GstAndroidSurfaceAttach {
		
	private Pointer nativeWindow;
	private XOverlayBase xoverlay;
    
	private native long nativeSurfaceWindow(Object surface);

	public GstAndroidSurfaceAttach(Element sinkElement, SurfaceView view) {

    	    	
    	xoverlay = XOverlayBase.wrap(sinkElement);
    	
    	view.getHolder().addCallback(new SurfaceHolder.Callback() {
    		
    		boolean surfaceReady;
    		
    		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    			Log.d("GStreamer", "Surface changed to format " + format + " width "
    					+ width + " height " + height);

    			Pointer _nativeWindow = new Pointer(nativeSurfaceWindow(holder.getSurface()));

    			if (nativeWindow != null) {
    				AndroidAPI.api.ANativeWindow_release(nativeWindow);
    			}
    			
    			if (_nativeWindow.equals(nativeWindow)) {
    				if (xoverlay != null) {
    					xoverlay.expose();
    					xoverlay.expose();
    				}
    			} else {

    				nativeWindow = _nativeWindow;
    				
    				updateXoverlay();
 
    			}
    			
    			if (nativeWindow != null && !surfaceReady) {
    				
    				surfaceReady = true;
    				
    				onCreated();
    			}

    		}

    		public void surfaceCreated(SurfaceHolder holder) {
    			Log.d("GStreamer", "Surface created: " + holder.getSurface());
    		}

    		public void surfaceDestroyed(SurfaceHolder holder) {
    			
    			Log.d("GStreamer", "Surface destroyed");
  
    			if (nativeWindow != null) {
    				AndroidAPI.api.ANativeWindow_release(nativeWindow);
    			}
 			
    			nativeWindow = null;
    			
    			updateXoverlay();
    			
    			surfaceReady = false;
    			
    			onDestroyed();
    		}
    	});
    }

	protected abstract void onCreated();

	protected abstract void onDestroyed();

	private void updateXoverlay() {
		if (xoverlay != null) {
			xoverlay.setWindowHandle(nativeWindow == null ? 0L : Pointer.nativeValue(nativeWindow));
		}
	}
}

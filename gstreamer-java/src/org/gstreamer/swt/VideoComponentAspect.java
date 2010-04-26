package org.gstreamer.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.gstreamer.Element;

import com.sun.jna.Platform;

public class VideoComponentAspect extends Composite {

	private org.gstreamer.swt.overlay.VideoComponent video;

	private int aspectNom = 0;
	private int aspectDenom = 0;

	public VideoComponentAspect(final Composite parent, int style) {
		this(parent, style, true);
	}

	public VideoComponentAspect(final Composite parent, int style, boolean overlay) {
		super(parent, style);
		video = new org.gstreamer.swt.overlay.VideoComponent(this, style);

		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				int newX = getSize().x;
				int newY = getSize().y;
				if (aspectNom != 0 && aspectDenom != 0) { // Keep aspect
					double coX = getSize().x / aspectNom;
					double coY = getSize().y / aspectDenom;
					if (coY <= coX) {
						newX = (int) (coY * aspectNom);
						newY = getSize().y;
					} else {
						newX = getSize().x;
						newY = (int) (coX * aspectDenom);
					}
				}
				video.setSize(newX, newY);
				video.setLocation(getSize().x / 2 - video.getSize().x / 2,
						getSize().y / 2 - video.getSize().y / 2);
				layout(true);
			}
		});
		// HACK for these events delegates up
		video.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event arg0) {
				notifyListeners(SWT.MouseDown, arg0);
			}
		});
		video.addListener(SWT.DragDetect, new Listener() {
			public void handleEvent(Event arg0) {
				notifyListeners(SWT.DragDetect, arg0);
			}
		});
		video.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event arg0) {
				notifyListeners(SWT.MouseUp, arg0);
			}
		});
		video.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event arg0) {
				notifyListeners(SWT.MouseDoubleClick, arg0);
			}
		});
		video.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				notifyListeners(SWT.MenuDetect, new Event());
			}
		});

		if (Platform.isLinux()) {
			((org.gstreamer.swt.overlay.VideoComponent) video).getElement().set("sync", false);
			((org.gstreamer.swt.overlay.VideoComponent) video).getElement().set("async", false);
			// System.err.println("   -- VC's name: "+video.getElement().toString());
			// System.err.println("   -- SYNC: "+video.getElement().get("sync"));
			// System.err.println("   -- ASYNC: "+video.getElement().get("async"));
		}
	}

	public void setAspectRatio(int nom, int denom) {
		this.aspectDenom = denom;
		this.aspectNom = nom;
	}

	public org.gstreamer.swt.overlay.VideoComponent getInnerVideoComponent() {
		return video;
	}

	public Element getElement() {
		return video.getElement();
	}

	public void setOverlay(String text) {
		// video.setOverlay(text);
	}

	public void showOverlay(boolean bn) {
		// video.showOverlay(bn);
	}

}

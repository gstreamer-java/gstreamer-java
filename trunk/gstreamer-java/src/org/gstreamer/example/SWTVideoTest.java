package org.gstreamer.example;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.swt.VideoComponent;

public class SWTVideoTest {

	private static List<VideoComponent> components;

	private static void createControl(Composite parent) {
		Composite controlComposite = new Composite(parent, SWT.NONE);
		controlComposite.setLayout(new GridLayout(3, false));
		controlComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Composite componentComposite = new Composite(parent, SWT.NONE);
		componentComposite.setLayout(new GridLayout(1, false));
		componentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Spinner num = new Spinner(controlComposite, SWT.NONE);
		num.setMinimum(1);
		num.setMaximum(20);
		Button add = new Button(controlComposite, SWT.NONE);
		add.setText("Add");
		add.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				List<VideoComponent> temp = new ArrayList<VideoComponent>();
				if (components.size() == 0 && num.getSelection() == 1)
					componentComposite.setLayout(new GridLayout(1, false));
				else
					componentComposite.setLayout(new GridLayout((components.size() + num.getSelection()) / 2, false));
				for (int i = 0; i < num.getSelection(); i++) {
					temp.add(createComponenet(componentComposite));
				}
				componentComposite.layout();
				List<Thread> threads = new ArrayList<Thread>();
				for (VideoComponent videoComponent : temp) {
					final Pipeline pipeline = (Pipeline) videoComponent.getData();
					Thread thread = new Thread(new Runnable() {
						public void run() {
							pipeline.play();
//							pipeline.getState();
//							pipeline.debugToDotFile(Bin.DEBUG_GRAPH_SHOW_ALL, "swt_video_test_pipeline");
						}
					}, "pipeline");
					threads.add(thread);
				}

				for (Thread thread : threads) {
					thread.start();
				}
			}
		});
		Button delete = new Button(controlComposite, SWT.NONE);
		delete.setText("Delete");
		delete.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0) {
				for (int i = 0; i < num.getSelection(); i++) {
					if (components.size() > 0) {
						VideoComponent component = components.get(components.size() - 1);
						deleteElement(component);
					}
				}
				componentComposite.layout();
			}
		});
	}

	private static void deleteElement(VideoComponent component) {
		Pipeline pipeline = (Pipeline) component.getData();
		pipeline.stop();
		pipeline.getState().equals(State.NULL);
		component.dispose();
		components.remove(component);
	}

	private static VideoComponent createComponenet(final Composite parent) {
		Pipeline pipe = new Pipeline("SWT Overlay Test");
		Element src = ElementFactory.make("videotestsrc", "videotest");
		
		//FileSrc src = new FileSrc("/tmp/pipe");
//		Element src = ElementFactory.make("tcpclientsrc", "videotest");
//		src.set("port", 6666);
//		Element depay = ElementFactory.make("gdpdepay", "gdpdepay");
//		Element caps = ElementFactory.make("capsfilter", "caps");
//		caps.setCaps(new Caps("video/x-raw-yuv,format=YUY2,width=320,height=240,framerate=30/1"));
		
		VideoComponent component = new VideoComponent(parent, SWT.NONE);
		component.getElement().setName("video");
		component.setKeepAspect(true);
		component.setLayoutData(new GridData(GridData.FILL_BOTH));
		Element sink = component.getElement();

		component.setData(pipe);
		components.add(component);

//		pipe.addMany(src, depay, caps, sink);
//		Element.linkMany(src, depay, caps, sink);
		pipe.addMany(src, sink);
		Element.linkMany(src, sink);
		return component;
	}

	public static void main(String[] args) {
		args = Gst.init("SWTVideoTest", args);
		components = new ArrayList<VideoComponent>();
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setMaximized(true);
			shell.setLayout(new GridLayout(1, false));

			shell.setText("SWT Video Test");

			createControl(shell);

			shell.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					List<VideoComponent> temp = new ArrayList<VideoComponent>();
					temp.addAll(components);
					for (VideoComponent component : temp) {
						deleteElement(component);
					}
				}
			});

			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}

			display.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

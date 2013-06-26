package org.gstreamer.lowlevel;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class PreloadLibsSuite extends Suite {
	
	@SuppressWarnings("unused")
	private static final boolean libsPreloaded = preloadLibraries();
	
	public PreloadLibsSuite(Class<?> klass, RunnerBuilder builder)
			throws InitializationError {
		super(klass, builder);
	}

	public PreloadLibsSuite(RunnerBuilder builder, Class<?>[] classes)
			throws InitializationError {
		
		super(builder, classes);
	}

	@Override
	protected void runChild(Runner runner, RunNotifier notifier) {
		super.runChild(runner, notifier);
	}
	
	private static boolean preloadLibraries() {
		
		String[] preloadLibs = {
				"gstreamer"
		};
		
		for (String lib: preloadLibs) {
			load(lib);
		}
		
		GNative.setGlobalLibName(null);
		
		return true;
	}
	
	private static void load(String lib) {
				
		for (String suf1: new String[] {"", "-1.0", "-0.10"}) {
			try {				
				for (String suf2: new String[] {"", "-0"}) {
					System.loadLibrary(lib + suf1 + suf2);
					Native.loadLibrary(lib + suf1 + suf2, Library.class);
					return;
				}
			} catch (UnsatisfiedLinkError e) {
			}
		}
		
		throw new UnsatisfiedLinkError("can not load library " + lib);
	}
}

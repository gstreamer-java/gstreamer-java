/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer;

import static org.junit.Assert.assertTrue;

import java.lang.ref.WeakReference;

import org.gstreamer.swing.VideoComponent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VideoComponentGCTest {

	public VideoComponentGCTest() {}

	@BeforeClass
	public static void setUpClass() throws Exception {
		Gst.init("test", new String[] {});
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Gst.deinit();
	}

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	public static boolean waitGC(WeakReference<?> ref) {
		System.gc();
		for (int i = 0; ref.get() != null && i < 10; ++i) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {}
			System.gc();
		}
		return ref.get() == null;
	}

	@Test
	public void testVideoComponent() {
		VideoComponent component = new VideoComponent();
		WeakReference<VideoComponent> ref = new WeakReference<VideoComponent>(component);
		component.destroy();
		component = null;
		assertTrue("VideoComponent not garbage collected", waitGC(ref));
	}
}

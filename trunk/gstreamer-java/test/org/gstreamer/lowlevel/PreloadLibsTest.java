package org.gstreamer.lowlevel;

import org.gstreamer.CapsTest;
import org.gstreamer.ElementTest;
import org.gstreamer.MessageTest;
import org.gstreamer.PadTest;
import org.gstreamer.PipelineTest;
import org.gstreamer.interfaces.MixerTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class tests <code>GNative.setGlobalLibName(null)</code>. 
 * It runs regular gstreamer-java tests in pre-load mode. This test
 * is disabled by default, because for it
 * to work, you probably need to specify both the java.library.path and 
 * jna.library.path System properties to the directory containing 
 * libgstreamer-0.10.so.
 *   
 * @see GNative#setGlobalLibName(String)
 * @see PreloadLibsSuite
 *
 */
@Ignore
@RunWith(PreloadLibsSuite.class)
@Suite.SuiteClasses({GValueTest.class, LowLevelStructureTest.class, 
	ReferenceManagerTest.class, MixerTest.class, MessageTest.class, 
	ElementTest.class, PipelineTest.class, CapsTest.class, PadTest.class} )
public class PreloadLibsTest {
}

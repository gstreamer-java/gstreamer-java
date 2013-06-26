package org.gstreamer.lowlevel;

import org.gstreamer.CapsTest;
import org.gstreamer.ElementTest;
import org.gstreamer.MessageTest;
import org.gstreamer.PadTest;
import org.gstreamer.PipelineTest;
import org.gstreamer.interfaces.MixerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(PreloadLibsSuite.class)
@Suite.SuiteClasses({GValueTest.class, LowLevelStructureTest.class, 
	ReferenceManagerTest.class, MixerTest.class, MessageTest.class, 
	ElementTest.class, PipelineTest.class, CapsTest.class, PadTest.class} )
public class PreloadLibsTest {
}

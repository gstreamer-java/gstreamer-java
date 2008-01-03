/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gstreamer;

import org.gstreamer.elements.PlayBin;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wayne
 */
public class RegistryTest {

    public RegistryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Gst.init("RegistryTest", new String[] {});
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        Gst.deinit();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void getDefault() {
        Registry registry = Registry.getDefault();
        assertNotNull("Registry.getDefault() returned null", registry);
    }
    @Test
    public void listPlugins() {
        final String PLUGIN = "vorbis"; // Use something that is likely to be there
        Registry registry = Registry.getDefault();
        // Ensure some plugins are loaded
        ElementFactory.make("playbin", "test");
        ElementFactory.make("vorbisdec", "vorbis");
        ElementFactory.make("decodebin", "decoder");
        List<Plugin> plugins = registry.getPluginList();
        assertFalse("No plugins found", plugins.isEmpty());
        boolean pluginFound = false;
        for (Plugin p : plugins) {
//            System.out.println("Found plugin: " + p.getName());
            if (p.getName().equals(PLUGIN)) {
                pluginFound = true;
            }
        }
        assertTrue(PLUGIN + " plugin not found", pluginFound);
    }
    @Test
    public void filterPlugins() {
        final String PLUGIN = "vorbis"; // Use something that is likely to be there
        Registry registry = Registry.getDefault();
        // Ensure some plugins are loaded
        ElementFactory.make("playbin", "test");
        ElementFactory.make("vorbisdec", "vorbis");
        ElementFactory.make("decodebin", "decoder");
        final boolean[] filterCalled = { false };
        List<Plugin> plugins = registry.getPluginList(new Registry.PluginFilter() {

            public boolean accept(Plugin plugin) {
                filterCalled[0] = true;
                return plugin.getName().equals(PLUGIN);
            }
        }, true);
        assertFalse("No plugins found", plugins.isEmpty());
        assertTrue("PluginFilter not called", filterCalled[0]);
        assertEquals("Plugin list should contain 1 item", 1, plugins.size());
        assertEquals(PLUGIN + " plugin not found", "flac", plugins.get(0).getName());
    }
    @Test
    public void listPluginFeatures() {
        final String PLUGIN = "vorbis"; // Use something that is likely to be there
        final String FEATURE = "vorbisdec";
        Registry registry = Registry.getDefault();
        // Ensure some plugins are loaded
        ElementFactory.make("playbin", "test");
        ElementFactory.make("vorbisdec", "vorbis");
        ElementFactory.make("decodebin", "decoder");
        List<PluginFeature> features = registry.getPluginFeatureListByPlugin(PLUGIN);
        assertFalse("No plugin features found", features.isEmpty());
        boolean pluginFound = false;
        for (PluginFeature p : features) {
//            System.out.println("Found plugin feature " + p.getName());
            if (p.getName().equals(FEATURE)) {
                pluginFound = true;
            }
        }
        assertTrue(PLUGIN + " plugin not found", pluginFound);
    }
    @Test
    public void lookupFeature() {
        PluginFeature f = Registry.getDefault().findPluginFeature("decodebin");
    }
}
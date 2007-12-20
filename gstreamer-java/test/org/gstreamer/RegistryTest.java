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
        Registry registry = Registry.getDefault();
        // Ensure some plugins are loaded
        Element playbin = new PlayBin("test");
        Element decodebin = ElementFactory.make("decodebin", "decoder");
        List<Plugin> plugins = registry.getPluginList();
        assertFalse("No plugins found", plugins.isEmpty());
        boolean playbinFound = false;
        for (Plugin p : plugins) {
            if (p.getName().equals("playbin")) {
                playbinFound = true;
            }
        }
        assertTrue("playbin plugin not found", playbinFound);
    }
    @Test
    public void listPluginFeatures() {
        Registry registry = Registry.getDefault();
        // Ensure some plugins are loaded
        Element playbin = new PlayBin("test");
        Element decodebin = ElementFactory.make("decodebin", "decoder");
        List<PluginFeature> features = registry.getPluginFeatureListByPlugin("playbin");
        assertFalse("No plugin features found", features.isEmpty());
        boolean playbinFound = false;
        for (PluginFeature p : features) {
            System.out.println("Found plugin feature " + p.getName());
            if (p.getName().equals("playbin")) {
                playbinFound = true;
            }
        }
        assertTrue("playbin plugin not found", playbinFound);
    }
    @Test
    public void lookupFeature() {
        PluginFeature f = Registry.getDefault().findPluginFeature("decodebin");
    }
}
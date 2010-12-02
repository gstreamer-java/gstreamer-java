package org.gstreamer;

public class TestAll {
  public static void main(String[] args) {
    
    if (com.sun.jna.Platform.isMac()) {
      // Load GStreamer libraries in MacOSX. Assuming the binaries from David Liu's installer:
      // http://www.itee.uq.edu.au/~davel/gstreamer/      
      final String jnaLibraryPath = System.getProperty("jna.library.path");
      final StringBuilder newJnaLibraryPath = new StringBuilder(jnaLibraryPath != null ? (jnaLibraryPath + ":") : "");
      newJnaLibraryPath.append("/System/Library/Frameworks/GStreamer.framework/Versions/0.10-" + (com.sun.jna.Platform.is64Bit() ? "x64" : "i386") + "/lib:");
      System.setProperty("jna.library.path", newJnaLibraryPath.toString());
    } 
    
    System.out.println("Testing StreamInfoTest...");
    
    StreamInfoTest test;
    test = new StreamInfoTest();
    
    try {
      System.out.println("  setUpBeforeClass()");
      StreamInfoTest.setUpBeforeClass();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      System.out.println("  setUp()");
      test.setUp();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      System.out.println("  testGetStreamType()");
      test.testGetStreamType();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    try {
      System.out.println("  testGetMute()");
      test.testGetMute();
    } catch (Exception e) {
      e.printStackTrace();
    }

    
    
    try {
      System.out.println("  testGetCaps()");
      test.testGetCaps();
    } catch (Exception e) {
      e.printStackTrace();
    }


/*
    try {
      System.out.println("  testStreamInfoGC()");
      test.testStreamInfoGC();
    } catch (Exception e) {
      e.printStackTrace();
    }
*/
    
    try {
      System.out.println("  testUseStreamInfoAfterBinStop()");
      test.testUseStreamInfoAfterBinStop();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    
    
    try {    
      System.out.println("  tearDownAfterClass()");
      StreamInfoTest.tearDownAfterClass();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    System.out.println("Done");
    
  }
}

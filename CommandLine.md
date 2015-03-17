# Introduction #

Using gstreamer-java is simple, if not totally obvious, due to its dependency on JNA.

# Details #

To use gstreamer-java, you need to download the latest versions of both gstreamer-java.jar and jna.jar and put them in the classpath.

e.g.

Download the latest JARS from the project main page (in the following examples  version numbers have been stripped):

  * gstreamer-java.jar
  * jna.jar

# Run some examples #
  * To invoke the SwingVideoTest example program on linux:
```
    java -Djna.library.path=/usr/lib -cp jna.jar:gstreamer-java.jar   org.gstreamer.example.SwingVideoTest
```
  * Launching a command-line pipeline using the **gst-launch** syntax (the simplest way perhaps to test your gstreamer/gstreamer-java installation):
```
    java -Djna.library.path=/usr/lib -cp jna.jar:gstreamer-java.jar   org.gstreamer.example.PipelineLauncher videotestsrc ! autovideosink
```

Note regarding JNA path: if your gstreamer native libraries are installed somewhere under the system search path (e.g. under /usr/lib on Linux), you don't actually need to set jna.library.path

Note: On windows, separate the jar files with a semi-colon ';' instead of a colon ':', and you should not need to set the jna.library.path

Note: On Linux, you must use the Sun JVM - GCJ will not work.  Use java -version to determine which one you have.

gstreamer-java should work out-of-the-box on any platform supporting JNA, with a reasonably recent installation of the Gstreamer native binaries.
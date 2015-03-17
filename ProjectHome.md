**The project moved to : https://github.com/lfarkas/gstreamer-java
> **

An unofficial/alternative set of java bindings for the [gstreamer](http://gstreamer.freedesktop.org/) multimedia framework.

Please use the [gstreamer-java](http://groups.google.com/group/gstreamer-java) google group to discuss using gstreamer-java or to ask any questions. See wiki page GettingHelp for additional information.

Although gstreamer is commonly associated with the gnome desktop, gstreamer itself, and these bindings are portable across operating systems.

You need a recent [JNA](https://github.com/twall/jna) version in order to be able to use gstreamer-java.

The current release is known to run on MacOSX, Linux, Windows and - for the experimental developer with the latest code - on Android. Before using gstreamer-java - and especially before opening issues against gstreamer-java - try to run your pipelines using the Gstreamer gst-launch command-line utility directly.

**Important: Current version of the gstreamer-java is NOT compatible with:
  * GStreamer 1.0 or above.
this means the last usable version are
  * GStreamer 0.10.**

**Please note:** this is not an easy-to-use multimedia framework for beginners.  It currently requires people to both know the java language, and be familiar with the gstreamer framework (or be prepared to apply things from tutorials on gstreamer programming in other languages (e.g. python or C#) to the java bindings).
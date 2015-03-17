**Note: Windows is no longer supported, as gstreamer is just too buggy on that platform**

# Introduction #

Getting gstreamer running on Windows is not as easy as Linux (where it is installed by default), so here are some helpful instructions.


# Details #

Download the Windows Binaries from [gstreamer for windows](http://gstreamer.freedesktop.org/pkg/windows/releases).

You have to install the lastest version from each package.
Use the XXXXXXsetup.zip files, because these ones have the
dependencies needed (glib, ffmpeg, etc).

You should then restart Windows to have the GST\_PLUGIN\_PATH and PATH global variables updated to include the gstreamer directories.

Test that the gstreamer binaries work with the windows command interpreter (change the file to play, and you can remove the debug if everything works):

`gst-launch-0.10 --gst-debug-level=2 playbin uri=file://C:/mymedia/mediatest.avi`
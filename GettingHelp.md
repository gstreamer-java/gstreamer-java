# Introduction #

Please consider the following before posting questions/reporting problems regarding the use of gstreamer-java:
> Gstreamer java is merely a wrapper Java library around the gstreamer native C binaries - you must be able to use the Gstreamer binaries directly (e.g. launching simple pipelines using the 'gst-launch' utility from the command-line) before trying to use gstreamer-java.

Questions such as "what pipeline elements should I use to achieve X" may better be answered by going through the [official Gstreamer site](http://gstreamer.freedesktop.org)

# Before asking for help #
## Test your gstreamer installation ##
Run a few simple pipelines from the command-line.

For example:
```
gst-launch videotestsrc ! autovideosink
```
Or
```
gst-launch audiotestsrc ! autoaudiosink
```
## Test first with _gst-launch_ ##
gstreamer-java is **not** the place to do trial-and-error testing of your pipelines. You should use _gst-launch_ from the command line for that. Only use gstreamer-java once you have a near-enough pipeline that works from the command line

## Simplify your code before sharing ##
Sometimes you may want people on the gstreamer group to look at your code and give you advice. Before posting your code, make it as simple as possible. It is easier to understand and manage code using **`Pipeline.launch()`**.

Compare:

  * `Pipeline.launch()`:
```
Pipeline pipe = Pipeline.launch("filesrc name=filesrc ! queue ! oggdemux ! vorbisdec ! audioconvert ! autoaudiosink");
pipe.getElementByName("filesrc").set("location", "/tmp/bula.ogg");
pipe.play();
```
  * Without `Pipeline.launch()`:
```
Pipeline p = new Pipeline();
Element filesrc = ElementFactory.make("filesrc", "filesrc");
Element queue = ElementFactory.make("queue", "queue");
Element oggdemux = ElementFactory.make("oggdemux", "oggdemux");
Element vorbisdec = ElementFactory.make("vorbisdec", "vorbisdec");
Element audioconvert = ElementFactory.make("audioconvert", "audioconvert");
Element autoaudiosink = ElementFactory.make("autoaudiosink", "autoaudiosink");
    
p.addMany(filesrc, queue, oggdemux, vorbisdec, audioconvert, autoaudiosink);

Element.linkMany(filesrc, queue, oggdemux, vorbisdec, audioconvert, autoaudiosink);

filesrc.set("location", "/tmp/bula.ogg");

p.play();
```
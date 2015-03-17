Although you can apply most C or python tutorials to gstreamer-java with just some
slight API changes - mostly changing gst\_foo\_bar to gstFooBar and stripping the leading 'Gst' from type names - it might be easier for people to learn with tutorials written to the gstreamer-java API.


SimplePipelineTutorial explains how to put together a pipeline and what each step does.

VideoTestTutorial demonstrates how to put together a pipeline to test video output.

AudioPlayerTutorial shows how to build a very simple audio player.

AudioPlayerWithMetadata shows how to retrieve meta-data (media file tags).

VideoPlayerTutorial shows how to build a very simple video player.

BusMessageTutorial shows how to detect pipeline changes using Bus messages.
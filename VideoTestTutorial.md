# Introduction #


Although SimplePipelineTutorial introduced the concepts of a pipeline, elements
and linking them together, it did not actually do anything useful.


# Details #

Now we will build a pipeline that actually displays something!
```
public class VideoTest {
    public VideoTest() {
    }
    private static Pipeline pipe;
    public static void main(String[] args) {
        args = Gst.init("VideoTest", args);
        pipe = new Pipeline("VideoTest");
        final Element videosrc = ElementFactory.make("videotestsrc", "source");
        final Element videofilter = ElementFactory.make("capsfilter", "filter");
        videofilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576"
                + ", bpp=32, depth=32, framerate=25/1"));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                VideoComponent videoComponent = new VideoComponent();
                Element videosink = videoComponent.getElement();
                pipe.addMany(videosrc, videofilter, videosink);
                Element.linkMany(videosrc, videofilter, videosink);
                
                // Now create a JFrame to display the video output
                JFrame frame = new JFrame("Swing Video Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(videoComponent, BorderLayout.CENTER);
                videoComponent.setPreferredSize(new Dimension(720, 576));
                frame.pack();
                frame.setVisible(true);
                
                // Start the pipeline processing
                pipe.setState(State.PLAYING);
            }
        });
    }
}

```

The first element we make is the video source.
```
        final Element videosrc = ElementFactory.make("videotestsrc", "source");
```
We use the 'videotestsrc' element to avoid any dependency on media files and
any problems that might be encountered decoding them.

Next we have a 'capsfilter' element to constrain the video output that
'videotestsrc' produces.  In this case, we want it to produce YUV output
720 pixels wide, 576 pixels high at a depth of 32bits and 25 frames per second.
```
        final Element videofilter = ElementFactory.make("capsfilter", "filter");
        videofilter.setCaps(Caps.fromString("video/x-raw-yuv, width=720, height=576"
                + ", bpp=32, depth=32, framerate=25/1"));
```
By changing the width, height and framerate parameters, you can alter the size
of the video frames produced, and the speed at which they are displayed.



We have to execute the rest on the Swing EDT since we are creating swing
components.

Now we created the video display component and link the whole pipeline together.
```
                VideoComponent videoComponent = new VideoComponent();
                Element videosink = videoComponent.getElement();
                pipe.addMany(videosrc, videofilter, videosink);
                Element.linkMany(videosrc, videofilter, videosink);
```
That should be familiar from SimplePipelineTutorial with a few small changes.


After that, we do some standard swing setup, creating a JFrame to show the
video component in.
```
                JFrame frame = new JFrame("Swing Video Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(videoComponent, BorderLayout.CENTER);
                videoComponent.setPreferredSize(new Dimension(720, 576));
                frame.pack();
                frame.setVisible(true);
```


Finally, we start the pipeline playing which will start the videotestsrc
producing video frames.
```
                pipe.setState(State.PLAYING);
```
Note that since this is a GUI program, and we store a global reference to the
pipeline, there is no need to keep the main thread around, so no call to
Gst.main() is needed.
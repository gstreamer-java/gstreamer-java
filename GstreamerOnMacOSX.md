# Introduction #

Although macports comes with a version of gstreamer, it is somewhat old and incomplete - you won't be able to play most useful files (e.g. XviD or h264) with it.  These instructions tell you how to update to the latest stable gstreamer release using MacPorts 1.6.0 or later.

Note: If you're using a version of MacPorts later than 1.6.x, check what version of gstreamer is present - I have submitted patches to macports to update gstreamer, so that might eventually get done.

# Details #



  1. Install needed ports:
    * Ports needed by gstreamer and gst-plugins-base:
> > `gzip m4 perl5.8 pkgconfig  bison flex gettext glib2 libiconv libxml2 py25-gobject py25-numeric python25 zlib liboil libogg libtheora libvorbis cdparanoia`
    * Ports needed by gst-plugins-good:
> > `libcdio aalib flac jpeg libcaca libdv  libpng libshout2  speex taglib wavpack`
    * Ports needed by gst-plugins-bad:
> > `XviD libdts faac faad2 libmusicbrainz neon libsdl`
    * Ports needed by gst-plugins-ugly:
> > `a52dec  lame libmpeg2 libmad libid3tag libdvdread`
    * Or, the complete list for a full featured build with the max amount of plugins:
> > `gzip m4 perl5.8 pkgconfig  bison flex gettext glib2 libiconv libxml2 py25-gobject py25-numeric python25 zlib liboil libogg libtheora libvorbis cdparanoia libcdio aalib flac jpeg libcaca libdv  libpng libshout2  speex taglib wavpack XviD libdts faac faad2 libmusicbrainz neon libsdl a52dec  lame libmpeg2 libmad libid3tag libdvdread`
    * Note: gst-plugins-bad would like x264, but one of x264's dependencies failed to compile on macports 1.6.0, so I left it out.
  1. Download and extract the following packages from the gstreamer download site:
    * gstreamer-0.10.15.tar.bz2
    * gst-plugins-base-0.10.15.tar.bz2
    * gst-plugins-good-0.10.6.tar.bz2
    * gst-plugins-bad-0.10.5.tar.bz2
    * gst-plugins-ugly-0.10.6.tar.bz2
    * gst-ffmpeg-0.10.3.tar.bz2
  1. Build each of gstreamer, gst-plugins-base, gst-plugins-good, gst-plugins-bad, gst-plugins-bad  with:
```
  LDFLAGS="-L/usr/local/lib -L/opt/local/lib" CFLAGS="-I/usr/local/include -I/opt/local/include"  \
  PKG_CONFIG_PATH="/usr/local/lib/pkgconfig:/opt/local/lib/pkgconfig" \
  ./configure --prefix=/usr/local && make DEPRECATED_CFLAGS="" && sudo make install
```
  1. Now build gst-ffmpeg with --disable-mmx like so:
```
  LDFLAGS="-L/usr/local/lib -L/opt/local/lib" CFLAGS="-I/usr/local/include -I/opt/local/include"  \
  PKG_CONFIG_PATH="/usr/local/lib/pkgconfig:/opt/local/lib/pkgconfig"  \
  ./configure --prefix=/usr/local --disable-mmx && make DEPRECATED_CFLAGS="" && sudo make install
```

That should be it.  Run gst-inspect to show the list of plugins - there should be at least 126 plugins present.
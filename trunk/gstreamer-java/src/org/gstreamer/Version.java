package org.gstreamer;

public class Version {
    public Version(long major, long minor, long micro, long nano) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.nano = nano;
    }
    final public long major, minor, micro, nano;
}

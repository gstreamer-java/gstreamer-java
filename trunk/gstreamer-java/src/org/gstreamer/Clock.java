/*
 * Clock.java
 */

package org.gstreamer;

import com.sun.jna.Pointer;

/**
 *
 */
public class Clock extends GstObject {

    public Clock(Pointer ptr, boolean ownsHandle, boolean needRef) {
        super(ptr, ownsHandle, needRef);
    }
}

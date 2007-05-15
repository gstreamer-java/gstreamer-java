/*
 * TagMergeMode.java
 */

package org.gstreamer;

/**
 *
 */
public enum TagMergeMode {
    UNDEFINED,
    REPLACE_ALL,
    REPLACE,
    APPEND,
    PREPEND,
    KEEP,
    KEEP_ALL,
    /* add more */
    COUNT;
    public int intValue() {
        return ordinal();
    }
}

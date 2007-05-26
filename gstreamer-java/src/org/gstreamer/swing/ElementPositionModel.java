/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.gstreamer.swing;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.gstreamer.Element;
import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.Time;
import org.gstreamer.Timeout;

/**
 *
 *
 */
public class ElementPositionModel extends DefaultBoundedRangeModel {
    
    /** Creates a new instance of MediaPositionModel */
    public ElementPositionModel(final Element element) {
        this(element, Format.TIME);
    }
    public ElementPositionModel(final Element element, final Format format) {
        this.element = element;
        this.format = format;
        timer = new Timeout(1000, new Runnable() {
            public void run() {
                final long position = element.getPosition(format);
                final Time duration = element.getDuration();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updatePosition(duration, position);
                    }
                });
            }
        });
    }
    private void startPoll() {
        timer.start();
    }
    private void stopPoll() {
        timer.stop();
    }
    public void addChangeListener(ChangeListener l) {
        if (listenerList.getListenerCount() == 0) {
            startPoll();
        }
        super.addChangeListener(l);
    }
    public void removeChangeListener(ChangeListener l) {
        super.removeChangeListener(l);
        if (listenerList.getListenerCount() == 0) {
            stopPoll();
        }
    }
    private void updatePosition(Time duration, long position) {
        // Don't update the slider when it is being dragged
        if (getValueIsAdjusting()) {
            return;
        }
        final int min = 0;
        final int max = (int)(duration.longValue() / Time.NANOSECONDS);
        final int pos = (int)(position / (format == Format.TIME ? Time.NANOSECONDS : 1));
        //System.out.printf("Setting range properties to %02d, %02d, %02d%n", min, max, pos);
        if (getMaximum() != max || getMinimum() != min) {
            setMaximum(max);
            setMinimum(min);
            setExtent(1);
        }
        
        updating = true;
        setValue(pos);
        updating = false;
    }
    
    protected void fireStateChanged() {
        super.fireStateChanged();
        // Only seek when the slider is being dragged (live seeking), and when not automatically updating the slider
        if (!updating && getValueIsAdjusting()) {
            final long pos = (long) getValue() * (format == Format.TIME ? Time.NANOSECONDS : 1);
            
            // We stop the poll during seeking, to stop the slider jumping back 
            // to the old time whilst the pipeline catches up
            if (seeking++ == 0) {
                stopPoll();
            }
            Gst.invokeLater(new Runnable() {
                public void run() {
                    element.setPosition(pos, format);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // Restart the poll if this is the last seek pending
                            if (--seeking == 0) {
                                startPoll();
                            }
                        }
                    });
                }
            });
        }
    }
    private Format format;
    private int seeking = 0;
    private Element element;
    private boolean updating = false;
    private Timeout timer;
}


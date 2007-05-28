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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.gstreamer.Element;
import org.gstreamer.Format;
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
        if (seeking.get() != 0 || getValueIsAdjusting()) {
            return;
        }
        final int min = 0;
        final int max = (int)(duration.longValue() / Time.NANOSECONDS);
        final int pos = (int)(position / (format == Format.TIME ? Time.NANOSECONDS : 1));
        //System.out.printf("Setting range properties to %02d, %02d, %02d%n", min, max, pos);
        updating = true;        
        super.setRangeProperties(pos, 1, min, max, false);
        updating = false;
    }
    
    public void setValue(int newValue) {
        super.setValue(newValue);
        //
        // Only seek when the slider is being dragged, and not when updating the 
        // position from the poll
        //
        if (!updating) {
            final long pos = (long) getValue() * (format == Format.TIME ? Time.NANOSECONDS : 1);
            
            seeking.incrementAndGet();
            
            final Runnable updater = new Runnable() {
                public void run() {
                    // Only do the seek if this is the last seek pending
                    if (seeking.get() == 0) {
                        element.setPosition(pos, format);
                        startPoll();
                    }
                }
            };
            javax.swing.Timer timer = new javax.swing.Timer(20, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    // We stop the poll during seeking, to stop the slider jumping back
                    // to the old time whilst the pipeline catches up
                    stopPoll();
                    if (seeking.decrementAndGet() == 0) {
                        bgExec.execute(updater);
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    private Format format;
    private AtomicInteger seeking = new AtomicInteger(0);
    private Element element;
    private boolean updating = false;
    private Timeout timer;
    private static final ExecutorService bgExec = Executors.newSingleThreadExecutor();
}


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

import java.util.TimerTask;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.gstreamer.Element;
import org.gstreamer.Time;

/**
 *
 
 */
public class ElementPositionModel extends DefaultBoundedRangeModel {
    
    /** Creates a new instance of MediaPositionModel */
    protected ElementPositionModel(Element element) {
        this.element = element;
    }
    private TimerTask updateTask = new TimerTask() {
        
        public void run() {
            final Time position = element.getPosition();
            final Time duration = element.getDuration();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updatePosition(duration, position);
                }
            });
        }
        
    };
    public void addChangeListener(ChangeListener l) {
        if (listenerList.getListenerCount() == 0) {
            timer = new java.util.Timer(true);
            timer.schedule(updateTask, 1000, 1000);
        }
        super.addChangeListener(l);
    }
    public void removeChangeListener(ChangeListener l) {
        super.removeChangeListener(l);
        if (listenerList.getListenerCount() == 0) {
            timer.cancel();
            timer = null;
        }
    }
    private void updatePosition(Time duration, Time position) {
        // Don't update the slider when it is being dragged
        if (getValueIsAdjusting()) {
            return;
        }
        final int min = 0;
        final int max = (int)(duration.nanoseconds() / Time.NANOSECONDS);
        final int pos = (int)(position.nanoseconds() / Time.NANOSECONDS);
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
            element.setPosition(new Time((long) getValue() * Time.NANOSECONDS));
        }
    }
    private Element element;
    private boolean updating = false;
    private java.util.Timer timer;
}


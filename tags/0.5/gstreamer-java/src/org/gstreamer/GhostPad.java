/* 
 * Copyright (C) 2007 Wayne Meissner
 * Copyright (C) 1999,2000 Erik Walthinsen <omega@cse.ogi.edu>
 *                    2000 Wim Taymans <wtay@chello.be>
 *                    2005 Andy Wingo <wingo@pobox.com>
 *		      2006 Edward Hervey <bilboed@bilboed.com>
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer;
import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 * Pseudo link pads
 *
 * GhostPads are useful when organizing pipelines with {@link Bin} like elements.
 * The idea here is to create hierarchical element graphs. The bin element
 * contains a sub-graph. Now one would like to treat the bin-element like any other
 * {@link Element}. This is where GhostPads come into play. A GhostPad acts as a
 * proxy for another pad. Thus the bin can have sink and source ghost-pads that
 * are associated with sink and source pads of the child elements.
 *
 * If the target pad is known at creation time, {@link #GhostPad(String, Pad)} is the
 * function to use to get a ghost-pad. Otherwise one can use {@link #GhostPad(String, PadDirection)}
 * to create the ghost-pad and use {@link #setTarget} to establish the association later on.
 *
 * Note that GhostPads add overhead to the data processing of a pipeline.
 * 
 * @see Pad
 */
public class GhostPad extends Pad {

    /**
     * Creates a new instance of GhostPad
     */
    GhostPad(Initializer init) { 
        super(init); 
    }
    
    /**
     * Create a new ghostpad with target as the target. The direction will be taken
     * from the target pad. The target pad must be unlinked.
     * 
     * @param name The name of the new pad, or null to assign a default name.
     * @param target The {@link Pad} to ghost.
     */
    public GhostPad(String name, Pad target) {
        this(initializer(gst.gst_ghost_pad_new(name, target)));
    }
    
    /**
     * Create a new ghostpad with target as the target. The direction will be taken
     * from the target pad. The template used on the ghostpad will be template.
     *
     * @param name The name of the new pad, or null to assign a default name.
     * @param target The {@link Pad} to ghost.
     * @param template The {@link PadTemplate} to use on the ghostpad.
     */
    public GhostPad(String name, Pad target, PadTemplate template) {
        this(initializer(gst.gst_ghost_pad_new_from_template(name, target, template)));
    }
    
    /**
     * Create a new ghostpad without a target with the given direction.
     * A target can be set on the ghostpad later with the {@link #setTarget} method.
     * <p>
     * The created ghostpad will not have a padtemplate.
     *
     * @param name The name of the new pad, or null to assign a default name.
     * @param direction The direction of the ghostpad.
     */
    public GhostPad(String name, PadDirection direction) {
        this(initializer(gst.gst_ghost_pad_new_no_target(name, direction.ordinal())));
    }
    
    /**
     * Create a new ghostpad based on template, without setting a target. The
     * direction will be taken from the template.
     *
     * @param name The name of the new pad, or null to assign a default name.
     * @param template The {@link PadTemplate} to use on the ghostpad.
     */
    public GhostPad(String name, PadTemplate template) {
        this(initializer(gst.gst_ghost_pad_new_no_target_from_template(name, template)));
    }
    
    /**
     * Get the target pad of this ghostpad.
     *
     * @return the target {@link Pad}, can be null if the ghostpad has no target set
     */
    public Pad getTarget() {
        return gst.gst_ghost_pad_get_target(this);
    }
    
    /**
     * Set the new target of the ghostpad. Any existing target
     * is unlinked and links to the new target are established.
     *
     * @param pad The new pad target.
     * @return true if the new target could be set. This function can return false
     * when the internal pads could not be linked.
     */
    public boolean setTarget(Pad pad) {
        return gst.gst_ghost_pad_set_target(this, pad);
    }
}

/* 
 * Copyright (c) 2007 Wayne Meissner
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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.gstreamer.lowlevel.GstAPI.gst;

/**
 *
 */
class GstIterator<T extends NativeObject> extends NativeObject implements java.lang.Iterable<T> {
    private Class<T> objectType;
    GstIterator(Pointer ptr, Class<T> cls) {
        super(initializer(ptr, false, true));
        objectType = cls;
    }

    public Iterator<T> iterator() {
        return new IteratorImpl();
    }
    
    protected void disposeNativeHandle(Pointer ptr) {
        gst.gst_iterator_free(ptr);
    }
    @SuppressWarnings("unchecked")
    public List<T> asList() {
        List<T> list = new LinkedList<T>();
        for (java.util.Iterator<T> it = iterator(); it.hasNext(); ) {
            list.add(it.next());
        }
        return Collections.unmodifiableList(list);
    }
    protected void ref() {}
    protected void unref() { }
    class IteratorImpl implements java.util.Iterator<T> {
        T next;        
        IteratorImpl() {
            next = getNext();
        }
        @SuppressWarnings("unchecked")
        private T getNext() {
            PointerByReference nextRef = new PointerByReference();
            if (gst.gst_iterator_next(handle(), nextRef) == 1) {                
                return NativeObject.objectFor(nextRef.getValue(), objectType, false);                
            }
            return null;
        }
        public boolean hasNext() {
            return next != null;
        }
        
        public T next() {
            T result = next;
            next = getNext();
            return result;
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Items cannot be removed.");
        }
    }
}

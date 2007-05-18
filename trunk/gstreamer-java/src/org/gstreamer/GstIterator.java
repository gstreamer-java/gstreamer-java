/*
 * GstIterator.java
 
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
class GstIterator<T> extends NativeObject implements java.lang.Iterable<T> {
    private Class objectType;
    GstIterator(Pointer ptr, Class cls) {
        super(ptr, true, false);
        objectType = cls;
    }
    GstIterator(Pointer ptr) {
        super(ptr, true, false);
        objectType = GstObject.class;
    }
    public Iterator<T> iterator() {
        return new IteratorImpl<T>();
    }
    
    void disposeNativeHandle(Pointer ptr) {
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
    void ref() {}
    void unref() { }
    class IteratorImpl<T> implements java.util.Iterator<T> {
        T next;
        IteratorImpl() {
            next = getNext();
        }
        @SuppressWarnings("unchecked")
        private T getNext() {
            PointerByReference nextRef = new PointerByReference();
            if (gst.gst_iterator_next(handle(), nextRef) == 1) {
                if (GstObject.class.isAssignableFrom(objectType)) {
                    return (T) GstObject.instanceFor(nextRef.getValue(), objectType, true, false);
                } else {
                    return (T) NativeObject.instanceFor(nextRef.getValue(), objectType, true, false);
                }
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

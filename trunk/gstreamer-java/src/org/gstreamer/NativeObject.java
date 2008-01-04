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
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gstreamer.lowlevel.GstTypes;

/**
 *
 */
public abstract class NativeObject extends org.gstreamer.lowlevel.Handle {
    private static Logger logger = Logger.getLogger(NativeObject.class.getName());
    public static Level LIFECYCLE = Level.FINE;
    
    // Use this as a dummy arg to identify the default constructor
    protected static class Initializer {
        public final Pointer ptr;
        public final boolean needRef, ownsHandle;
        public Initializer() {
            this(null, false, false);
        }
        public Initializer(Pointer ptr, boolean needRef, boolean ownsHandle) {
            this.ptr = ptr;
            this.needRef = needRef;
            this.ownsHandle = ownsHandle;
        }
    }
    protected static final Initializer defaultInit = new Initializer();
    
    protected static Initializer initializer(Pointer ptr) {
        return new Initializer(ptr, true, false);
    }
    protected static Initializer initializer(Pointer ptr, boolean needRef, boolean ownsHandle) {
        return new Initializer(ptr, needRef, ownsHandle);
    }
    /** Creates a new instance of NativeObject */
    protected NativeObject() {
        this(defaultInit);
    }
    protected NativeObject(Initializer init) {
        logger.entering("NativeObject", "<init>", new Object[] { init });
        logger.log(LIFECYCLE, "Creating " + getClass().getSimpleName() + " (" + init.ptr + ")");
        nativeRef = new NativeRef(this);
        this.handle = init.ptr;
        this.ownsHandle.set(init.ownsHandle);
        
        //
        // Only store this object in the map if we can tell when it has been disposed 
        // (i.e. must be at least a GObject - MiniObject and other NativeObject subclasses
        // don't signal destruction, so it is impossible to know if the instance 
        // is stale or not
        //
        if (GObject.class.isAssignableFrom(getClass())) {
            instanceMap.put(init.ptr, nativeRef);
        }
        if (init.ownsHandle && init.needRef) {
            ref();
        }
    }
    
    abstract protected void disposeNativeHandle(Pointer ptr);
    
    public void dispose() {
        logger.log(LIFECYCLE, "Disposing object " + this + " = " + handle);
//        System.out.println("Disposing " + handle);
        if (!disposed.getAndSet(true)) {
            instanceMap.remove(handle, nativeRef);
            if (ownsHandle.get()) {
                disposeNativeHandle(handle);
            }
        }
    }
    
    protected void invalidate() {
        logger.log(LIFECYCLE, "Invalidating object " + this + " = " + handle());
        instanceMap.remove(handle(), nativeRef);
        disposed.set(true);
        ownsHandle.set(false);
    }
    abstract protected void ref();
    abstract protected void unref();
    
    @Override
    protected void finalize() throws Throwable {
        try {
            logger.log(LIFECYCLE, "Finalizing " + getClass().getSimpleName() + " (" + handle + ")");
            dispose();
        } finally {
            super.finalize();
        }
    }
    protected Object nativeValue() {
        return handle();
    }
    protected Pointer handle() {
        if (disposed.get()) {
            throw new IllegalStateException("Native object has been disposed");
        }
        return handle;
    }
    protected static NativeObject instanceFor(Pointer ptr) {
        WeakReference<NativeObject> ref = instanceMap.get(ptr);
        
        //
        // If the reference was there, but the object it pointed to had been collected, remove it from the map
        //
        if (ref != null && ref.get() == null) {
            instanceMap.remove(ptr);
        }
        return ref != null ? ref.get() : null;
    }
    public static <T extends NativeObject> T objectFor(Pointer ptr, Class<T> cls, boolean needRef) {
        return objectFor(ptr, cls, needRef, true);
    }
    public static <T extends NativeObject> T objectFor(Pointer ptr, Class<T> cls, boolean needRef, boolean ownsHandle) {
        return objectFor(ptr, cls, needRef ? 1 : 0, ownsHandle);
    }
        
    public static <T extends NativeObject> T objectFor(Pointer ptr, Class<T> cls, int refAdjust, boolean ownsHandle) {
        logger.entering("NativeObject", "instanceFor", new Object[] { ptr, refAdjust, ownsHandle });
        
        // Ignore null pointers
        if (ptr == null) {
            return null;
        }
        NativeObject obj = GObject.class.isAssignableFrom(cls) ? NativeObject.instanceFor(ptr) : null;
        if (obj != null && cls.isInstance(obj)) {
            if (refAdjust < 0) {
                obj.unref(); // Lose the extra ref added by gstreamer
            }
            return cls.cast(obj);
        }
        
        //
        // If it is a GObject or MiniObject, read the g_class field to find
        // the most exact class match
        //
        if (GObject.class.isAssignableFrom(cls) || MiniObject.class.isAssignableFrom(cls)) {
            cls = classFor(ptr, cls);
        }
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor(Initializer.class);
            T retVal = constructor.newInstance(initializer(ptr, refAdjust > 0, ownsHandle));
            //retVal.initNativeHandle(ptr, refAdjust > 0, ownsHandle);
            return retVal;
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

    }
    
    @SuppressWarnings("unchecked")
    protected static <T extends NativeObject> Class<T> classFor(Pointer ptr, Class<T> defaultClass) {
        Class<? extends NativeObject> cls = GstTypes.classFor(ptr);
        return (cls != null && defaultClass.isAssignableFrom(cls)) ? (Class<T>) cls : defaultClass; 
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof NativeObject && ((NativeObject) o).handle().equals(handle());
    }
    
    @Override
    public int hashCode() {
        return handle.hashCode();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + handle() + ")";
    }
    
    //
    // No longer want to garbage collect this object
    //
    public void disown() {
        logger.log(LIFECYCLE, "Disowning " + handle());
        ownsHandle.set(false);
    }
    static class NativeRef extends WeakReference<NativeObject> {
        public NativeRef(NativeObject obj) {
            super(obj);
        }
    }
    private AtomicBoolean disposed = new AtomicBoolean(false);
    private Pointer handle;
    protected final AtomicBoolean ownsHandle = new AtomicBoolean(false);
    private final NativeRef nativeRef;
    private static ConcurrentHashMap<Pointer, NativeRef> instanceMap = new ConcurrentHashMap<Pointer, NativeRef>();
}

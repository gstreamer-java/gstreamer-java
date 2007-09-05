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

package org.gstreamer.lowlevel;

import com.sun.jna.CallbackInvocationContext;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.FunctionResultContext;
import com.sun.jna.MethodResultContext;
import com.sun.jna.Pointer;
import com.sun.jna.ToNativeContext;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeConverter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.gstreamer.NativeObject;
import org.gstreamer.annotations.FreeReturnValue;

/**
 *
 * @author wayne
 */
public class GTypeMapper implements com.sun.jna.TypeMapper {

    public GTypeMapper() {
    }
    private static ToNativeConverter nativeValueArgumentConverter = new ToNativeConverter() {

        public Object toNative(Object arg, ToNativeContext context) {
            return ((NativeValue) arg).nativeValue();
        }

        public Class nativeType() {
            return Void.class; // not really correct, but not used in this instance
        }        
    };
    
    private static FromNativeConverter nativeObjectResultConverter = new FromNativeConverter() {

        @SuppressWarnings(value = "unchecked")
        public Object fromNative(Object result, FromNativeContext context) {
            if (context instanceof FunctionResultContext) {
                //
                // By default, gstreamer increments the refcount on objects 
                // returned from functions, so drop a ref here
                //
                return NativeObject.objectFor((Pointer) result, context.getTargetType(), -1, true);
            }
            if (context instanceof CallbackInvocationContext) {
                return NativeObject.objectFor((Pointer) result, context.getTargetType(), 1, true);
            }
            throw new IllegalStateException("Cannot convert to NativeObject from " + context);
        }
        
        public Class<?> nativeType() {
            return Pointer.class;
        }
    };
    private static TypeConverter enumConverter = new TypeConverter() {

        @SuppressWarnings(value = "unchecked")
        public Object fromNative(Object value, FromNativeContext context) {
            Class<? extends Enum> returnType = context.getTargetType();
            try {
                Method valueOf = returnType.getDeclaredMethod("valueOf", new Class[]{int.class});
                if ((valueOf.getModifiers() & Modifier.STATIC) == 0) {
                    throw new IllegalArgumentException(returnType.getName() + ".valueOf(int) MUST be static");
                }
                return valueOf.invoke(returnType, value);
            } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("Enum requires a 'valueOf(Integer)' method", ex);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to convert int to Enum", ex);
            }
        }

        public Class<?> nativeType() {
            return Integer.class;
        }

        @SuppressWarnings("unchecked")
        public Object toNative(Object arg, ToNativeContext context) {
            Enum e = (Enum) arg;
            try {
                Method intValue = e.getClass().getMethod("intValue", new Class[]{});
                return intValue.invoke(e, new Object[]{});
            } catch (NoSuchMethodException ex) {
                return new Integer(e.ordinal());
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            } catch (InvocationTargetException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    };

    private TypeConverter stringConverter = new TypeConverter() {

        public Object fromNative(Object result, FromNativeContext context) {
            if (context instanceof MethodResultContext) {
                MethodResultContext functionContext = (MethodResultContext) context;
                Method method = functionContext.getMethod();
                Pointer ptr = (Pointer) result;
                String s = ptr.getString(0);
                if (method.isAnnotationPresent(FreeReturnValue.class)) {
                    GlibAPI.glib.g_free(ptr);
                }
                return s;
            } else if (result != null) {
                return ((Pointer) result).getString(0);
            }
            return null;
        }

        public Class<?> nativeType() {
            return Pointer.class;
        }

        public Object toNative(Object arg, ToNativeContext context) {
            // Let the default String -> native conversion handle it
            return arg;            
        }
    };

    private TypeConverter booleanConverter = new TypeConverter() {
        public Object toNative(Object arg, ToNativeContext context) {
            return Integer.valueOf(Boolean.TRUE.equals(arg) ? 1 : 0);
        }

        public Object fromNative(Object arg0, FromNativeContext arg1) {
            return Boolean.valueOf(((Integer)arg0).intValue() != 0);
        }

        public Class<?> nativeType() {
            return Integer.class;
        }
    };
    
    private TypeConverter intptrConverter = new TypeConverter() {
        
        public Object toNative(Object arg, ToNativeContext context) {
            return ((IntPtr)arg).value;            
        }

        public Object fromNative(Object arg0, FromNativeContext arg1) {
            return new IntPtr(((Number) arg0).intValue());            
        }

        public Class<?> nativeType() {
            return Pointer.SIZE == 8 ? Long.class : Integer.class;
        }
    };
  
    @SuppressWarnings("unchecked")
	public FromNativeConverter getFromNativeConverter(Class type) {
        if (Enum.class.isAssignableFrom(type)) {
            return enumConverter;              
        } else if (NativeObject.class.isAssignableFrom(type)) {
            return nativeObjectResultConverter;
        } else if (Boolean.class == type || boolean.class == type) {
            return booleanConverter;
        } else if (String.class == type) {
            return stringConverter;
        } else if (IntPtr.class == type) {
            return intptrConverter;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public ToNativeConverter getToNativeConverter(Class type) {
        if (NativeValue.class.isAssignableFrom(type)) {
            return nativeValueArgumentConverter;
        } else if (Enum.class.isAssignableFrom(type)) {
            return enumConverter;
        } else if (Boolean.class == type || boolean.class == type) {
            return booleanConverter;
        } else if (String.class == type) {
            return stringConverter;        
        } else if (IntPtr.class == type) {
            return intptrConverter;
        }
        return null;
    }
}

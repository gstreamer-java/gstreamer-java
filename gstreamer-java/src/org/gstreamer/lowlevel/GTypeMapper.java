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

import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.FunctionResultContext;
import com.sun.jna.Pointer;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeConverter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.gstreamer.GstObject;
import org.gstreamer.annotations.FreeReturnValue;

/**
 *
 * @author wayne
 */
public class GTypeMapper implements com.sun.jna.TypeMapper {
    
    public GTypeMapper() {
    }
    private static ToNativeConverter nativeValueArgumentConverter = new ToNativeConverter() {
        public Object toNative(Object arg) {
            return ((NativeValue)arg).nativeValue();
        }
    };
    private static FromNativeConverter gstObjectResultConverter = new FromNativeConverter() {
        @SuppressWarnings("unchecked")
        public Object fromNative(Object result, FromNativeContext context) {
            return GstObject.returnedObject((Pointer)result, context.getTargetType());
        }
        
        public Class nativeType() {
            return Pointer.class;
        }
    };
    private static TypeConverter enumConverter = new TypeConverter() {
        @SuppressWarnings("unchecked")
        public Object fromNative(Object value, FromNativeContext context) {
            Class<? extends Enum> returnType = context.getTargetType();
            try {
                Method valueOf = returnType.getDeclaredMethod("valueOf", new Class[] { int.class });
                return valueOf.invoke(returnType, value);
            } catch (Exception e) {
                return new Integer(0);
            }
        }
        
        public Class nativeType() {
            return Integer.class;
        }
        
        public Object toNative(Object arg) {
            Enum e = (Enum) arg;
            try {
                Method intValue = e.getClass().getMethod("intValue", new Class[] {});
                return intValue.invoke(e, new Object[] {});
            } catch (NoSuchMethodException ex) {
                return new Integer(e.ordinal());
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(ex);
            } catch (InvocationTargetException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    };
    
    private FromNativeConverter stringResultConverter = new FromNativeConverter() {
        
        public Object fromNative(Object result, FromNativeContext context) {
            FunctionResultContext functionContext = (FunctionResultContext) context;
            Method method = functionContext.getFunction().getMethod();
            Pointer ptr = (Pointer) result;
            String s = ptr.getString(0, false);
            if (method.isAnnotationPresent(FreeReturnValue.class)) {
                GlibAPI.glib.g_free(ptr);
            }
            return s;
        }
        
        public Class nativeType() {
            return Pointer.class;
        }
    };
    
    private ToNativeConverter booleanArgumentConverter = new ToNativeConverter() {
        static final int TRUE = 1, FALSE = 0;
        
        public Object toNative(Object arg) {
            return Boolean.TRUE.equals(arg) ? TRUE : FALSE;
        }
    };
    public FromNativeConverter getFromNativeConverter(Class type) {
        if (Enum.class.isAssignableFrom(type)) {
            return enumConverter;
        } else if (GstObject.class.isAssignableFrom(type)) {
            return gstObjectResultConverter;
        } else if (String.class.isAssignableFrom(type)) {
            return stringResultConverter;
        }
        return null;
    }
    public ToNativeConverter getToNativeConverter(Class type) {
        if (NativeValue.class.isAssignableFrom(type)) {
            return nativeValueArgumentConverter;
        } else if (Enum.class.isAssignableFrom(type)) {
            return enumConverter;
            
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return booleanArgumentConverter;
            
        }
        return null;
    }
    
}

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

import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Structure;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 */
public class GNative {
    
    public GNative() {
    }
    
    public static Library loadLibrary(String name, Class<? extends Library> interfaceClass) {
        return (Library) Proxy.newProxyInstance(interfaceClass.getClassLoader(), 
                new Class[] { interfaceClass }, new Handler(NativeLibrary.getInstance(name)));
    }
    static class Handler implements InvocationHandler {
        NativeLibrary library;
        public Handler(NativeLibrary library) {
            this.library = library;
        }
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Function f = library.getFunction(method.getName());
            if (args != null) {
                //
                // Turn a java varargs argument into a NULL terminated C varargs array
                //
                if (args[args.length - 1] != null) {
                    Object arg = args[args.length - 1];
                    Class cls = arg.getClass();
                    if (cls.isArray() && !cls.getComponentType().isPrimitive()
                            && !Structure.class.isAssignableFrom(cls.getComponentType())) {
                        Object[] varargs = (Object[]) arg;
                        Object[] newArgs = new Object[args.length + varargs.length];
                        System.arraycopy(args, 0, newArgs, 0, args.length - 1);
                        System.arraycopy(varargs, 0, newArgs, args.length - 1, varargs.length);
                        newArgs[newArgs.length - 1] = null;
                        args = newArgs;
                    }
                }
                // Convert any local types into standard JNA types
                for (int i = 0; i < args.length; ++i) {
                    
                    if (args[i] instanceof NativeValue) {
                        args[i] = ((NativeValue)args[i]).nativeValue();
                    } else if (args[i] instanceof Enum) {
                        Enum e = (Enum) args[i];
                        try {
                            Method intValue = e.getClass().getMethod("intValue", new Class[] {});
                            args[i] = intValue.invoke(e, new Object[] {});
                        } catch (NoSuchMethodException ex) {
                            args[i] = new Integer(e.ordinal());
                        }
                    } else if (args[i] instanceof Boolean) {
                        args[i] = new Integer(Boolean.TRUE.equals(args[i]) ? 1 : 0);
                    }
                }
            }
            return f.invoke(method.getReturnType(), args);
        }
    }
}

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


import org.gstreamer.lowlevel.GMainContext;
import com.sun.jna.Memory;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gstreamer.lowlevel.GlibAPI;
import static org.gstreamer.lowlevel.GstAPI.gst;
import static org.gstreamer.lowlevel.GlibAPI.glib;

/**
 *
 */
public final class Gst {
    static Logger logger = Logger.getLogger(Gst.class.getName());
    /** Creates a new instance of Gst */
    private Gst() {
    }
    
    public static Version getVersion() {
        LongByReference major = new LongByReference(0);
        LongByReference minor = new LongByReference(0);
        LongByReference micro = new LongByReference(0);
        LongByReference nano = new LongByReference(0);
        gst.gst_version(major, minor, micro, nano);
        return new Version(major.getValue(), minor.getValue(), micro.getValue(), nano.getValue());
    }
    public static String getVersionString() {
        return gst.gst_version_string();
    }
    public static void execute(Runnable r) {
        invokeLater(r);
    }
    private static final List<Runnable> bgTasks = new LinkedList<Runnable>();
    private static final GlibAPI.GSourceFunc bgCallback = new GlibAPI.GSourceFunc() {
        public boolean callback(Pointer source) {
            //            System.out.println("Running g_idle callbacks");
            List<Runnable> tasks = new ArrayList<Runnable>();
            synchronized (bgTasks) {
                tasks.addAll(bgTasks);
                bgTasks.clear();
            }
            for (Runnable r : tasks) {
                r.run();
            }
            glib.g_source_unref(source);
            return false;
        }
    };
    public static void invokeLater(final Runnable r) {
        //        System.out.println("Scheduling idle callbacks");
        synchronized (bgTasks) {
            boolean empty = bgTasks.isEmpty();
            bgTasks.add(r);
            // Only trigger the callback if there were no existing elements in the list
            // otherwise it is already triggered
            if (empty) {
                Pointer source = glib.g_idle_source_new();
                glib.g_source_set_callback(source, bgCallback, source, null);
                glib.g_source_attach(source, mainContext);
            }
        }
    }
    public static void invokeAndWait(Runnable r) {
        FutureTask<Object> task = new FutureTask<Object>(r, null);
        invokeLater(task);
        try {
            task.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getCause());
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
    public static GMainContext getMainContext() {
        return mainContext;
    }
    public static final String[] init() {
        return init("unknown", new String[] {});
    }
    
    public static final String[] init(String progname, String[] args) throws GError {
        if (initialized.get()) {
            return args;
        }
        NativeArgs argv = new NativeArgs(progname, args);
        Logger.getLogger("org.gstreamer").setLevel(Level.WARNING);
        PointerByReference errRef = new PointerByReference();
        
        if (!gst.gst_init_check(argv.argcRef, argv.argvRef, errRef)) {
            throw new GError(errRef.getValue());
        }
        
        logger.fine("after gst_init, argc=" + argv.argcRef.getValue());
        if (useDefaultContext) {
            mainContext = GMainContext.getDefaultContext();
        } else {
            mainContext = new GMainContext();
        }
        initialized.set(true);
        return argv.toStringArray();
    }

    public static void addStaticShutdownTask(Runnable task) {
        shutdownTasks.add(task);
    }
    public static void deinit() {
        for (Runnable task : shutdownTasks) {
            task.run();
        }
        mainContext = null;
        System.gc(); // Make sure any dangling objects are unreffed before calling deinit().
        initialized.set(false);
        gst.gst_deinit();
    }
    
    public static void setUseDefaultContext(boolean useDefault) {
        useDefaultContext = useDefault;
    }
    private static GMainContext mainContext;
    private static boolean useDefaultContext = false;
    private static AtomicBoolean initialized = new AtomicBoolean(false);
    private static List<Runnable> shutdownTasks = Collections.synchronizedList(new ArrayList<Runnable>());
}

class NativeArgs {
    IntByReference argcRef;
    PointerByReference argvRef;
    Memory[] argsCopy;
    Memory argvMemory;
    public NativeArgs(String progname, String[] args) {
        //
        // Allocate some native memory to pass the args down to the native layer
        //
        argvMemory = new Memory((args.length + 2) * Pointer.SIZE);
        
        //
        // Insert the program name as argv[0]
        //
        Memory arg = new Memory(progname.length() + 1);
        argsCopy = new Memory[args.length + 1];
        arg.setString(0, progname, false);
        argsCopy[0] = arg;
        argvMemory.setPointer(0, arg);
        
        for (int i = 0; i < args.length; i++) {
            arg = new Memory(args[i].length() + 1);
            arg.setString(0, args[i], false);
            argvMemory.setPointer((i + 1) * Pointer.SIZE, arg);
            argsCopy[i + 1] = arg;
        }
        
        // Make sure the array is NULL terminated
        argvMemory.setPointer((args.length + 1) * Pointer.SIZE, null);
        argvRef = new PointerByReference(argvMemory);
        argcRef = new IntByReference(args.length + 1);
    }
    String[] toStringArray() {
        //
        // Unpack the native arguments back into a String array
        //
        List<String> args = new ArrayList<String>();
        Pointer argv = argvRef.getValue();
        for (int i = 1; i < argcRef.getValue(); i++) {
            Pointer arg = argv.getPointer(i * Pointer.SIZE);
            if (arg != null) {
                args.add(arg.getString(0, false));
            }
        }
        return args.toArray(new String[args.size()]);
    }
    
}
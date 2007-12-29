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

package org.gstreamer.elements;

import org.gstreamer.*;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.gstreamer.lowlevel.BaseAPI;
import org.gstreamer.lowlevel.GObjectAPI.GBaseInitFunc;
import org.gstreamer.lowlevel.GObjectAPI.GClassInitFunc;
import org.gstreamer.lowlevel.GObjectAPI.GTypeInfo;
import org.gstreamer.lowlevel.GType;
import org.gstreamer.lowlevel.GstAPI.GstSegmentStruct;
import static org.gstreamer.lowlevel.GObjectAPI.gobj;
import static org.gstreamer.lowlevel.GstAPI.gst;

abstract public class CustomSrc extends BaseSrc {
    private final static Logger logger = Logger.getLogger(CustomSrc.class.getName());
    private static class CustomSrcInfo {
        GType type;
        PadTemplate template;
        Caps caps;
        
        // Per-class callbacks used by gstreamer to initialize the subclass
        GClassInitFunc classInit;
        GBaseInitFunc baseInit;
        
        // Per-instance callback functions
        BaseAPI.GstBaseSrcClass baseSrc;
        BaseAPI.Create create;
        BaseAPI.Seek seek;
        BooleanFunc1 isSeekable;
        BooleanFunc1 start;
        BooleanFunc1 stop;
    }
    private static final Map<Class<? extends CustomSrc>, CustomSrcInfo>  customSubclasses = new ConcurrentHashMap<Class<? extends CustomSrc>, CustomSrcInfo>();
    protected CustomSrc(Class<? extends CustomSrc> subClass, String name) {
        super(initializer(gobj.g_object_new(getSubclassType(subClass), "name", name)));
    }
    private static CustomSrcInfo getSubclassInfo(Class<? extends CustomSrc> subClass) {
       synchronized (subClass) {
            CustomSrcInfo info = customSubclasses.get(subClass);
            if (info == null) {
                init(subClass);
                info = customSubclasses.get(subClass);
            }
            return info;
        } 
    }
    private static GType getSubclassType(Class<? extends CustomSrc> subClass) {
        return getSubclassInfo(subClass).type;
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface SrcCallback {
        public String value();
    }
    
    /**
     * Used when more control of Buffer creation is desired than fillBuffer() affords.
     * 
     * @param offset 
     * @param size
     * @param bufRef
     * @return 
     */
    @SrcCallback("create")
    protected FlowReturn srcCreateBuffer(long offset, int size, Buffer[] bufRef) throws IOException {
        System.out.println("CustomSrc.createBuffer");
        return FlowReturn.NOT_SUPPORTED;
    } 
    
    /**
     * Used when you just want to fill a Buffer with data.  The Buffer
     * will be allocated and initialized by gstreamer.
     * @param offset
     * @param size
     * @param buffer
     * @return 
     */
    @SrcCallback("create")
    protected FlowReturn srcFillBuffer(long offset, int size, Buffer buffer) throws IOException {
        logger.info("CustomSrc.srcFillBuffer");
        return FlowReturn.NOT_SUPPORTED;
    }
    @SrcCallback("is_seekable")
    protected boolean srcIsSeekable() {
        logger.info("CustomSrc.srcIsSeekable");
        return false;
    }
    @SrcCallback("seek")
    protected boolean srcSeek(GstSegmentStruct segment) throws IOException {
        logger.info("CustomSrc.srcSeek");
        return false;
    }
    @SrcCallback("start")
    protected boolean srcStart() { 
        logger.info("CustomSrc.srcStart");
        return false; 
    }
    
    @SrcCallback("stop")
    protected boolean srcStop() { 
        logger.info("CustomSrc.srcStop");
        return false; 
    }
    
    @SrcCallback("negotiate")
    protected boolean srcNegotiate() { 
        logger.info("CustomSrc.srcNegotiate");
        return false; 
    }
    
    @SrcCallback("get_caps")
    protected Caps srcGetCaps() { 
        logger.info("CustomSrc.srcGetCaps");
        return null; 
    }
    
    @SrcCallback("set_caps")
    protected boolean srcSetCaps(Caps caps) { 
        logger.info("CustomSrc.srcSetCaps");
        return false; 
    }
    
    @SrcCallback("get_size")
    protected long srcGetSize() { 
        logger.info("CustomSrc.srcGetSize");
        return -1; 
    }
    
    @SrcCallback("event")
    protected boolean srcEvent(Event ev) { 
        logger.info("CustomSrc.srcEvent");
        return false; 
    }
    
    private static final BaseAPI.Create fillBufferCallback = new BaseAPI.Create() {

        public FlowReturn callback(BaseSrc element, long offset, int size, Pointer bufRef) {                  
            try {      
                Buffer buffer = new Buffer(size);
                buffer.setOffset(offset);  
                buffer.struct.timestamp = System.currentTimeMillis();
                //System.out.println("Sending buf=" + buf);
                FlowReturn retVal = ((CustomSrc) element).srcFillBuffer(offset, size, buffer);
                buffer.struct.write();            
                bufRef.setPointer(0, buffer.struct.getPointer());
                buffer.disown();

                return retVal;
            } catch (Exception ex) {
                return FlowReturn.UNEXPECTED;
            }                    
        }
        
    };
    private static final BaseAPI.Create createBufferCallback = new BaseAPI.Create() {

        public FlowReturn callback(BaseSrc element, long offset, int size, Pointer bufRef) {                  
            try {      
                Buffer[] buffers = new Buffer[1];
                FlowReturn retVal = ((CustomSrc) element).srcCreateBuffer(offset, size, buffers);
                if (buffers[0] != null) {
                    Buffer buffer = buffers[0];
                    buffer.struct.write();   
                    bufRef.setPointer(0, buffer.struct.getPointer());
                    buffer.disown();
                }                
                return retVal;
            } catch (Exception ex) {
                return FlowReturn.UNEXPECTED;
            }                    
        }
        
    };
    private static class BooleanFunc1 implements BaseAPI.BooleanFunc1 {
        private Method method;
        public BooleanFunc1(String methodName) {
            try {
                method = CustomSrc.class.getDeclaredMethod(methodName, new Class[0]);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        public boolean callback(Element element) {
            try {
                return ((Boolean) method.invoke(element)).booleanValue();
            } catch (Exception ex) {
                return false;
            }
        }
        
    }
    private static final BooleanFunc1 isSeekableCallback = new BooleanFunc1("srcIsSeekable");
    private static final BooleanFunc1 startCallback = new BooleanFunc1("srcStart");
    private static final BooleanFunc1 stopCallback = new BooleanFunc1("srcStop");
    private static final BaseAPI.Seek seekCallback = new BaseAPI.Seek() {
       
        public boolean callback(BaseSrc element, GstSegmentStruct segment) {
            try {
                return ((CustomSrc) element).srcSeek(segment);
            } catch (Exception ex) {
                return false;
            }
        }        
    };
    private static void init(Class<? extends CustomSrc> srcClass) {
        final CustomSrcInfo info = new CustomSrcInfo();
        customSubclasses.put(srcClass, info);
        
        //
        // Trawl through all the methods in the subclass, looking for ones that 
        // over-ride the ones in CustomSrc
        //
        for (Method m : CustomSrc.class.getDeclaredMethods()) {
            SrcCallback cb = m.getAnnotation(SrcCallback.class);
            if (cb == null) {
                continue;
            }
            try {
                Method srcMethod = srcClass.getDeclaredMethod(m.getName(), m.getParameterTypes());
                if (srcMethod.equals(m)) {
                    // Skip it if it is the same as the method in CustomSrc
                    continue;
                }
                if (m.getName().equals("srcSeek")) {
                    info.seek = seekCallback;
                } else if (m.getName().equals("srcIsSeekable")) {
                    info.isSeekable = isSeekableCallback;                            
                } else if (m.getName().equals("srcFillBuffer")) {
                    info.create = fillBufferCallback;
                } else if (m.getName().equals("srcCreateBuffer")) {
                    info.create = createBufferCallback;
                } else if (m.getName().equals("srcStart")) {
                    info.start = startCallback;
                } else if (m.getName().equals("srcStop")) {
                    info.stop = stopCallback;
                }
            } catch (NoSuchMethodException ex) { 
//            } catch (NoSuchFieldException ex) {
//            } catch (IllegalAccessException ex) {                
            }
            
            
        }
        info.classInit = new GClassInitFunc() {
            public void callback(Pointer g_class, Pointer class_data) {
                BaseAPI.GstBaseSrcClass base = new BaseAPI.GstBaseSrcClass(g_class);
                base.create = info.create;
                base.is_seekable = info.isSeekable;
                base.seek = info.seek;
                base.start = info.start;
                base.stop = info.stop;
                base.write();            
            }
        };
        info.baseInit = new GBaseInitFunc() {

            public void callback(Pointer g_class) {
                info.caps = Caps.anyCaps();
                info.template = new PadTemplate("src", PadDirection.SRC, info.caps);
                gst.gst_element_class_add_pad_template(g_class, info.template);
            }
        };
        
        //
        // gstreamer boilerplate to hook the plugin in
        //
        GTypeInfo ginfo = new GTypeInfo();
        ginfo.class_init = info.classInit;
        ginfo.base_init = info.baseInit;
        ginfo.instance_init = null;
        ginfo.class_size = (short)new BaseAPI.GstBaseSrcClass().size();
        ginfo.instance_size = (short)new BaseAPI.GstBaseSrcStruct().size();
        
        GType type = gobj.g_type_register_static(BaseAPI.INSTANCE.gst_base_src_get_type(), 
                srcClass.getSimpleName(), ginfo, 0);
        info.type = type;
    }
}

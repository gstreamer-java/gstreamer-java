#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>

// following prevents different size [-Wpointer-to-int-cast] warning
#if GLIB_SIZEOF_VOID_P == 8
# define POINTER_TO_JLONG_CAST (jlong)
#else
# define POINTER_TO_JLONG_CAST (jlong)(jint)
#endif

jlong Java_org_gstreamer_android_GstAndroidSurfaceAttach_nativeSurfaceWindow (JNIEnv *env, jobject thiz, jobject surface) {

  return POINTER_TO_JLONG_CAST ANativeWindow_fromSurface(env, surface);

}

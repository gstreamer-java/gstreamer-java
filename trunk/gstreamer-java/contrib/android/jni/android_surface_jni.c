#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <gst/gst.h>

GST_DEBUG_CATEGORY_STATIC (debug_category);
#define GST_CAT_DEFAULT debug_category

/*
 * Private methods
 */

static jlong get_native_surface_window (JNIEnv *env, jobject thiz, jobject surface) {

  return ANativeWindow_fromSurface(env, surface);

}


/*
 * Private methods
 */

jlong Java_org_gstreamer_android_GstAndroidSurfaceAttach_nativeSurfaceWindow (JNIEnv *env, jobject thiz, jobject surface) {

  return get_native_surface_window(env, thiz, surface);

}

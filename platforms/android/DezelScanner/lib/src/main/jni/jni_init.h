#ifndef JNI_INIT_H
#define JNI_INIT_H

#include <jni.h>
#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "DEZEL", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "DEZEL", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "DEZEL", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "DEZEL", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "DEZEL", __VA_ARGS__)

/**
 * Checks and trigger an exception if needed.
 * @macro JNI_CHECK_EXCEPTION
 * @since 0.1.0
 */
#define JNI_CHECK_EXCEPTION(ENV) \
	if (ENV->ExceptionCheck()) ENV->ExceptionDescribe();

/**
 * Calls a JNI method.
 * @macro JNI_CALL_VOID_METHOD
 * @since 0.1.0
 */
#define JNI_CALL_VOID_METHOD(ENV, OBJECT, METHOD, ...) \
	ENV->CallVoidMethod(OBJECT, METHOD, ##__VA_ARGS__); \
	JNI_CHECK_EXCEPTION(ENV);

/**
 * @function JNIGetClass
 * @since 0.1.0
 * @hidden
 */
jclass JNIGetClass(JNIEnv *env, const char *name);

/**
 * @function JNIGetField
 * @since 0.1.0
 * @hidden
 */
jfieldID JNIGetField(JNIEnv *env, jclass klass, const char *name, const char *sign);

/**
 * @function JNIGetStaticField
 * @since 0.1.0
 * @hidden
 */
jfieldID JNIGetStaticField(JNIEnv *env, jclass klass, const char *name, const char *sign);

/**
 * @function JNIGetMethod
 * @since 0.1.0
 * @hidden
 */
jmethodID JNIGetMethod(JNIEnv *env, jclass klass, const char *name, const char *sign);

/**
 * @function JNIGetStaticMethod
 * @since 0.1.0
 * @hidden
 */
jmethodID JNIGetStaticMethod(JNIEnv *env, jclass klass, const char *name, const char *sign);

#endif

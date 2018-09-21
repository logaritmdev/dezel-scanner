#include <DLScanner.h>
#include <DLScannerAndroid.h>
#include "wrappers/DLScannerWrapper.h"
#include "ca_logaritm_dezel_scanner_scanner_ScannerExternal.h"
#include "jni_module_scanner.h"
#include "jni_init.h"

#define SAVE_ENV(ENV) \
	auto _wrp = reinterpret_cast<DLScannerWrapperRef>(DLScannerGetData(reinterpret_cast<DLScannerRef>(scannerPtr))); \
	auto _env = _wrp->env; \
	_wrp->env = ENV;

#define REST_ENV(ENV) \
	_wrp->env = _env;

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    create
 * Signature: (Ljava/lang/Object;)J
 */
jlong Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_create(JNIEnv *env, jclass, jobject object) {
	DLScannerRef handle = DLScannerCreate();
	DLScannerSetData(handle, DLScannerWrapperCreate(env, object, handle));
	return reinterpret_cast<jlong>(handle);
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    delete
 * Signature: (J)V
 */
void Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_delete(JNIEnv *env, jclass, jlong scannerPtr) {

	DLScannerRef scanner = reinterpret_cast<DLScannerRef>(scannerPtr);
	DLScannerWrapperRef wrapper = reinterpret_cast<DLScannerWrapperRef>(DLScannerGetData(scanner));

	wrapper->env->DeleteGlobalRef(wrapper->obj);

	delete wrapper;

	DLScannerDelete(scanner);
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    setDebug
 * Signature: (JZ)V
 */
void Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_setDebug(JNIEnv *env, jclass, jlong scannerPtr, jboolean debug) {
	SAVE_ENV(env);
	DLScannerSetDebug(reinterpret_cast<DLScannerRef>(scannerPtr), debug);
	REST_ENV(env);
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    setEnabled
 * Signature: (JZ)V
 */
void Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_setEnabled(JNIEnv *env, jclass, jlong scannerPtr, jboolean enabled) {
	SAVE_ENV(env);
	DLScannerSetEnabled(reinterpret_cast<DLScannerRef>(scannerPtr), enabled);
	REST_ENV(env);
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    reset
 * Signature: (J)V
 */
void Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_reset(JNIEnv *env, jclass, jlong scannerPtr) {
	SAVE_ENV(env);
	DLScannerReset(reinterpret_cast<DLScannerRef>(scannerPtr));
	REST_ENV(env);
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    restart
 * Signature: (J)V
 */
void Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_restart(JNIEnv *env, jclass, jlong scannerPtr) {
	SAVE_ENV(env);
	DLScannerRestart(reinterpret_cast<DLScannerRef>(scannerPtr));
	REST_ENV(env);
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    process
 * Signature: (JIILjava/nio/ByteBuffer;)V
 */
void Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_process(JNIEnv *env, jclass, jlong scannerPtr, jint w, jint h, jobject buffer) {
	SAVE_ENV(env);
	DLScannerProcessFrame(env, reinterpret_cast<DLScannerRef>(scannerPtr), w, h, buffer);
	REST_ENV(env);
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    getExtractedImage
 * Signature: (J)Landroid/graphics/Bitmap;
 */
jobject Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_getExtractedImage(JNIEnv *env, jclass, jlong scannerPtr) {
	return DLScannerGetExtractedImage(env, reinterpret_cast<DLScannerRef>(scannerPtr));
}

/*
 * Class:     ca_logaritm_dezel_scanner_scanner_ScannerExternal
 * Method:    getProcessedImage
 * Signature: (J)Landroid/graphics/Bitmap;
 */
jobject Java_ca_logaritm_dezel_scanner_scanner_ScannerExternal_getProcessedImage(JNIEnv *env, jclass, jlong scannerPtr) {
	return DLScannerGetProcessedImage(env, reinterpret_cast<DLScannerRef>(scannerPtr));
}


/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ca_logaritm_scanner_scanner_ScannerExternal */

#ifndef _Included_ca_logaritm_scanner_scanner_ScannerExternal
#define _Included_ca_logaritm_scanner_scanner_ScannerExternal
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    create
 * Signature: (Ljava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_create
  (JNIEnv *, jclass, jobject);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_delete
  (JNIEnv *, jclass, jlong);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    setDebug
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_setDebug
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    setEnabled
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_setEnabled
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    reset
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_reset
  (JNIEnv *, jclass, jlong);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    restart
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_restart
  (JNIEnv *, jclass, jlong);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    process
 * Signature: (JII[B)V
 */
JNIEXPORT void JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_process
  (JNIEnv *, jclass, jlong, jint, jint, jbyteArray);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    getExtractedImage
 * Signature: (J)Landroid/graphics/Bitmap;
 */
JNIEXPORT jobject JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_getExtractedImage
  (JNIEnv *, jclass, jlong);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    getDebuggingImage
 * Signature: (J)Landroid/graphics/Bitmap;
 */
JNIEXPORT jobject JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_getDebuggingImage
  (JNIEnv *, jclass, jlong);

/*
 * Class:     ca_logaritm_scanner_scanner_ScannerExternal
 * Method:    pullImage
 * Signature: (Landroid/graphics/Bitmap;IILandroid/graphics/PointF;Landroid/graphics/PointF;Landroid/graphics/PointF;Landroid/graphics/PointF;)Landroid/graphics/Bitmap;
 */
JNIEXPORT jobject JNICALL Java_ca_logaritm_scanner_scanner_ScannerExternal_pullImage
  (JNIEnv *, jclass, jobject, jint, jint, jobject, jobject, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif
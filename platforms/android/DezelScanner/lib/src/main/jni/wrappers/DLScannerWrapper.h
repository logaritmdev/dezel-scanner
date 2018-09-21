#ifndef DLSCANNERWRAPPER_H
#define DLSCANNERWRAPPER_H

#include <jni.h>
#include <DLScanner.h>

/**
 * @struct ScannerWrapper
 * @since 0.1.0
 */
struct DLScannerWrapper {
	JNIEnv *env;
	jobject obj;
};

/**
 * @typedef ScannerWrapperRef
 * @since 0.1.0
 */
typedef struct DLScannerWrapper* DLScannerWrapperRef;

/**
 * @function DLScannerWrapperCreate
 * @since 0.1.0
 */
DLScannerWrapperRef DLScannerWrapperCreate(JNIEnv *env, jobject object, DLScannerRef node);

#endif
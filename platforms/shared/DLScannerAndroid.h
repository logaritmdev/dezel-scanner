#ifndef DLScannerPrivateAndroid_h
#define DLScannerPrivateAndroid_h

#include <opencv2/opencv.hpp>
#include "DLScanner.h"
#include "DLScannerPrivate.h"

/**
 * @function DLScannerProcessFrame
 * @since 0.1.0
 * @hidden
 */
void DLScannerProcessFrame(JNIEnv* env, DLScannerRef scanner, int imgc, int imgr, jobject image);

/**
 * @function DLScannerGetProcessedImage
 * @since 0.1.0
 * @hidden
 */
jobject DLScannerGetProcessedImage(JNIEnv* env, DLScannerRef scanner);

/**
 * @function DLScannerGetExtractedImage
 * @since 0.1.0
 * @hidden
 */
jobject DLScannerGetExtractedImage(JNIEnv* env, DLScannerRef scanner);

/**
 * @function DLScannerExportImage
 * @since 0.1.0
 * @hidden
 */
jobject DLScannerExportImage(JNIEnv* env, const cv::Mat &mat);

#endif
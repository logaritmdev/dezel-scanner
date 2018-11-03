#ifndef DLScannerIOSPrivate_hpp
#define DLScannerIOSPrivate_hpp

#include <opencv2/opencv.hpp>
#include <CoreGraphics/CoreGraphics.h>
#include <CoreFoundation/CoreFoundation.h>

/**
 * @function DLScannerImportImage
 * @since 0.1.0
 * @hidden
 */
void DLScannerImportImage(CGImageRef image, cv::Mat &mat);

/**
 * @function DLScannerExportImage
 * @since 0.1.0
 * @hidden
 */
CGImageRef DLScannerExportImage(const cv::Mat &mat);

#endif

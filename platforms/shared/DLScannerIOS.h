#ifndef DLScannerIOS_h
#define DLScannerIOS_h

#include "DLScanner.h"

typedef struct CGImage* CGImageRef; // Forward declaration

#if __cplusplus
extern "C" {
#endif

/**
 * @function DLScannerProcessFrame
 * @since 0.1.0
 * @hidden
 */
void DLScannerProcessFrame(DLScannerRef scanner, int imgc, int imgr, CGImageRef image);

/**
 * @function DLScannerGetProcessedImage
 * @since 0.1.0
 * @hidden
 */
CGImageRef DLScannerGetProcessedImage(DLScannerRef scanner);

/**
 * @function DLScannerGetExtractedImage
 * @since 0.1.0
 * @hidden
 */
CGImageRef DLScannerGetExtractedImage(DLScannerRef scanner);

#if __cplusplus
}
#endif
#endif

#include <opencv2/opencv.hpp>
#include <CoreGraphics/CoreGraphics.h>
#include <CoreFoundation/CoreFoundation.h>
#include "DLScannerIOS.h"
#include "DLScannerIOSPrivate.h"
#include "DLScannerDetection.h"
#include "DLScannerPrivate.h"

void DLScannerProcessFrame(DLScannerRef scanner, int imgc, int imgr, CGImageRef image)
{
	cv::Mat src(imgr, imgc, CV_8UC4);
	DLScannerConvertImage(image, src);
	DLScannerProcessImage(scanner, imgc, imgr, src);
}

CGImageRef
DLScannerGetDebuggingImage(DLScannerRef scanner)
{
	return DLScannerExportImage(scanner->debuggingImage);
}

CGImageRef
DLScannerGetExtractedImage(DLScannerRef scanner)
{
	return DLScannerExportImage(scanner->extractedImage);
}

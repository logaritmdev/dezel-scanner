#include <opencv2/opencv.hpp>
#include <CoreGraphics/CoreGraphics.h>
#include <CoreFoundation/CoreFoundation.h>
#include "DLScannerIOS.h"
#include "DLScannerDetection.h"
#include "DLScannerPrivate.h"

void
DLScannerImportImage(CGImageRef image, cv::Mat &mat)
{
	CGColorSpaceRef space = CGImageGetColorSpace(image);
	CGFloat cols = CGImageGetWidth(image);
	CGFloat rows = CGImageGetHeight(image);

	CGContextRef context = CGBitmapContextCreate(
		mat.data,    // Pointer to  data
		cols,        // Width of bitmap
		rows,        // Height of bitmap
		8,           // Bits per component
		mat.step[0], // Bytes per row
		space,       // Colorspace
		kCGImageAlphaNoneSkipLast | // Bitmap info flags
		kCGBitmapByteOrderDefault
	);

	CGContextDrawImage(
		context,
		CGRectMake(0, 0, cols, rows),
		image
	);

	CGContextRelease(context);
}

CGImageRef
DLScannerExportImage(const cv::Mat &mat)
{
	if (mat.rows == 0 &&
		mat.cols == 0) {
		return NULL;
	}

	CFDataRef data = CFDataCreateWithBytesNoCopy(kCFAllocatorDefault, mat.data, mat.elemSize() * mat.total(), kCFAllocatorDefault);

	CGColorSpaceRef space;

	if (mat.elemSize() == 1) {
		space = CGColorSpaceCreateDeviceGray();
	} else {
		space = CGColorSpaceCreateDeviceRGB();
	}

	CGDataProviderRef provider = CGDataProviderCreateWithCFData(data);

	CGImageRef image = CGImageCreate(
		mat.cols,                                      // width
		mat.rows,                                      // height
		8,                                             // bits per component
		8 * mat.elemSize(),                            // bits per pixel
		mat.step[0],                                   // bytesPerRow
		space,                                         // colorspace
		kCGImageAlphaNone | kCGBitmapByteOrderDefault, // bitmap info
		provider,                                      // CGDataProviderRef
		NULL,                                          // decode
		false,                                         // should interpolate
		kCGRenderingIntentDefault                      // intent
	);

	CGDataProviderRelease(provider);
	CGColorSpaceRelease(space);

	return image;
}

void
DLScannerProcessFrame(DLScannerRef scanner, int imgc, int imgr, CGImageRef image)
{
	cv::Mat src(imgr, imgc, CV_8UC4);
	cv::Mat img;

	DLScannerImportImage(image, src);

	cvtColor(src, img, CV_RGBA2GRAY);

	DLScannerProcessImage(scanner, imgc, imgr, img);
}

CGImageRef
DLScannerGetProcessedImage(DLScannerRef scanner)
{
	return DLScannerExportImage(scanner->processed);
}

CGImageRef
DLScannerGetExtractedImage(DLScannerRef scanner)
{
	return DLScannerExportImage(scanner->extracted);
}


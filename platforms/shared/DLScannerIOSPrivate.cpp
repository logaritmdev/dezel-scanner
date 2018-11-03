#include "DLScannerIOSPrivate.h"

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

	CFDataRef data = CFDataCreate(kCFAllocatorDefault, mat.data, mat.elemSize() * mat.total());

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

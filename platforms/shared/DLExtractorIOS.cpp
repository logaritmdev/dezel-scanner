#include <opencv2/opencv.hpp>
#include <CoreGraphics/CoreGraphics.h>
#include <CoreFoundation/CoreFoundation.h>
#include "DLExtractorIOS.h"
#include "DLScannerIOS.h"
#include "DLScannerIOSPrivate.h"
#include "DLScannerDetection.h"
#include "DLScannerPrivate.h"

CGImageRef
DLExtractorPullImage(CGImageRef image, int imgc, int imgr, CGPoint p1, CGPoint p2, CGPoint p3, CGPoint p4)
{
	cv::Mat src(imgr, imgc, CV_8UC4);

	DLScannerConvertImage(image, src);

	std::vector<cv::Point> points;
	points.push_back(cv::Point(p1.x, p1.y));
	points.push_back(cv::Point(p2.x, p2.y));
	points.push_back(cv::Point(p3.x, p3.y));
	points.push_back(cv::Point(p4.x, p4.y));

	DLScannerRef scanner = DLScannerCreate();
	scanner->naturalImageCols = imgc;
	scanner->naturalImageRows = imgr;

	cv::Mat extracted;
	DLScannerExtractTrackedObject(scanner, src, src, extracted, points);
	DLScannerDelete(scanner);

	return DLScannerExportImage(extracted);
}

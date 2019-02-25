#include <jni.h>
#include <opencv2/opencv.hpp>
#include "DLExtractorAndroid.h"
#include "DLScannerAndroid.h"
#include "DLScannerAndroidPrivate.h"
#include "DLScannerDetection.h"
#include "DLScannerPrivate.h"
#include "jni_init.h"
#include "jni_module_scanner.h"

jobject
DLExtractorPullImage(JNIEnv* env, jobject image, int imgc, int imgr, jobject p1, jobject p2, jobject p3, jobject p4)
{
	cv::Mat src(imgr, imgc, CV_8UC4);

	DLScannerConvertImage(env, image, src);

	jint p1x = (jint) env->GetFloatField(p1, PointFX);
	jint p1y = (jint) env->GetFloatField(p1, PointFY);

	jint p2x = (jint) env->GetFloatField(p2, PointFX);
	jint p2y = (jint) env->GetFloatField(p2, PointFY);

	jint p3x = (jint) env->GetFloatField(p3, PointFX);
	jint p3y = (jint) env->GetFloatField(p3, PointFY);

	jint p4x = (jint) env->GetFloatField(p4, PointFX);
	jint p4y = (jint) env->GetFloatField(p4, PointFY);

	std::vector<cv::Point> points;
	points.push_back(cv::Point(p1x, p1y));
	points.push_back(cv::Point(p2x, p2y));
	points.push_back(cv::Point(p3x, p3y));
	points.push_back(cv::Point(p4x, p4y));

	DLScannerRef scanner = DLScannerCreate();
	scanner->naturalImageCols = imgc;
	scanner->naturalImageRows = imgr;

	cv::Mat extracted;
	DLScannerExtractTrackedObject(scanner, src, src, extracted, points);
	DLScannerDelete(scanner);

	return DLScannerExportImage(env, extracted);
}
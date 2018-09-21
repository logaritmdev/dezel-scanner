#include <vector>
#include <string>
#include <opencv2/opencv.hpp>
#include "DLScanner.h"
#include "DLScannerPrivate.h"
#include "DLScannerDetection.h"

#ifdef ANDROID
#include "jni_init.h"
#endif

void
DLScannerProcessImage(DLScannerRef scanner, int imgc, int imgr, const cv::Mat &src)
{
	if (scanner->state == DETECTED) {
		return;
	}

	scanner->imgc = imgc;
	scanner->imgr = imgr;

	float r = (float) imgc / (float) imgr;

	int cols = DL_SCANNER_RESIZE_COLS;
	int rows = DL_SCANNER_RESIZE_COLS * r;

	cv::Mat out;
	cv::Mat mat;

	resize(src, mat, cv::Size(cols, rows));

	cv::Mat structuringElmt = getStructuringElement(cv::MORPH_RECT, cv::Size(5, 5));
	morphologyEx(mat, mat, cv::MORPH_OPEN, structuringElmt);
	morphologyEx(mat, mat, cv::MORPH_CLOSE, structuringElmt);
	GaussianBlur(mat, mat, cv::Size(7, 7), 0);
	Canny(mat, mat, 0, 84);

	cvtColor(mat, out, CV_GRAY2RGBA);

	if (scanner->enabled) {

		vector<vector<cv::Point>> all;
		vector<vector<cv::Point>> maybes;
		vector<vector<cv::Point>> valids;

		/*
		 * Find all the object and put them in 2 different vectors, one that
		 * contains object are are very likely to be documents and other that
		 * are less likely.
		 */

		DLScannerDetectObjects(scanner, mat, valids, maybes, all);

		if (scanner->debug) {
			drawContours(out, all,    -1, cv::Scalar(255, 0, 0, 255), 1);
			drawContours(out, maybes, -1, cv::Scalar(0, 255, 0, 255), 1);
			drawContours(out, valids, -1, cv::Scalar(0, 0, 255, 255), 1);
		}

		scanner->processed = out.clone();

		/*
		 * We are not tracking anything but we found something. Let start tracking
		 * this object.
		 */

		if (scanner->state == SCANNING) {
			DLScannerStartTrackingObject(scanner, src, mat, valids);
			return;
		}

		/*
		 * Either continue tracking an object or start tracking one. When an object
		 * is tracked for some time its most likely to be a valid object.
		 */

		if (scanner->state == TRACKING) {
			DLScannerContinueTrackingObject(scanner, src, mat, valids);
			return;
		}
	}
}

void
DLScannerDetectObjects(DLScannerRef scanner, const cv::Mat &mat, vector<vector<cv::Point>> &valids, vector<vector<cv::Point>> &maybes, vector<vector<cv::Point>> &rects)
{
	const double maybeArea = (mat.cols * mat.rows) * 0.1;
	const double validArea = (mat.cols * mat.rows) * 0.18;

	vector<vector<cv::Point>> contours;

	findContours(mat, contours, cv::RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

	for (size_t i = 0; i < contours.size(); i++) {

		vector<cv::Point> approx;
		vector<cv::Point> contour = contours[i];

		approxPolyDP(contour, approx, 0.02 * arcLength(contour, true), true);

		if (approx.size() == 4) {

			const double area = fabs(contourArea(approx));

			if (area >= validArea) {
				valids.push_back(approx);
				continue;
			}

			if (area >= maybeArea) {
				maybes.push_back(approx);
				continue;
			}

			if (scanner->debug) {
				rects.push_back(approx);
			}
		}
	}

	sort(valids.begin(), valids.end(), DLScannerCompareContourArea);
	sort(maybes.begin(), maybes.end(), DLScannerCompareContourArea);
}

void
DLScannerStartTrackingObject(DLScannerRef scanner, const cv::Mat &src, const cv::Mat &mat, const vector<vector<cv::Point>> &objects)
{
	assert(scanner->state == SCANNING);

	if (objects.size() == 0) {
		return;
	}

	scanner->state = TRACKING;

	auto points = objects.front();

	scanner->object = new OpaqueDLDetectedObject;
	scanner->object->shape = points;
	scanner->object->began = DLScannerGetTime();
	scanner->object->until = DLScannerGetTime();

	vector<cv::Point> scaled;
	vector<cv::Point> ordered;

	DLScannerScalePoints(scanner, points, scaled);
	DLScannerOrderPointsClockwise(scanner, scaled, ordered);

	if (scanner->spotObjectCallback) {
		scanner->spotObjectCallback(
			scanner,
			ordered[0].x, ordered[0].y,
			ordered[1].x, ordered[1].y,
			ordered[2].x, ordered[2].y,
			ordered[3].x, ordered[3].y
		);
	}
}

void
DLScannerContinueTrackingObject(DLScannerRef scanner, const cv::Mat &src, const cv::Mat &mat, const vector<vector<cv::Point>> &objects)
{
	assert(scanner->state == TRACKING);

	if (objects.size() == 0) {
		DLScannerStopTrackingObject(scanner);
		return;
	}

	int index = DLScannerFindTrackedObject(scanner, mat, objects);
	if (index == -1) {
		DLScannerStopTrackingObject(scanner);
		return;
	}

	auto points = objects[index];
	scanner->object->shape = points;
	scanner->object->until = DLScannerGetTime();

	if (DLScannerIsTrackedObjectValid(scanner)) {

		scanner->state = DETECTED;

		cv::Mat extracted;
		DLScannerExtractTrackedObject(scanner, src, mat, extracted, points);
		scanner->extracted = extracted;

		vector<cv::Point> scaled;
		vector<cv::Point> ordered;

		DLScannerScalePoints(scanner, points, scaled);
		DLScannerOrderPointsClockwise(scanner, scaled, ordered);

		if (scanner->findObjectCallback) {
			scanner->findObjectCallback(
				scanner,
				ordered[0].x, ordered[0].y,
				ordered[1].x, ordered[1].y,
				ordered[2].x, ordered[2].y,
				ordered[3].x, ordered[3].y
			);
		}

		return;
	}

	/*
	 * The object currently being tracked is found again but
	 * not for enough time. In this case we simply tell the
	 * new position of the object.
	 */

	vector<cv::Point> scaled;
	vector<cv::Point> ordered;

	DLScannerScalePoints(scanner, points, scaled);
	DLScannerOrderPointsClockwise(scanner, scaled, ordered);

	if (scanner->spotObjectCallback) {
		scanner->spotObjectCallback(
			scanner,
			ordered[0].x, ordered[0].y,
			ordered[1].x, ordered[1].y,
			ordered[2].x, ordered[2].y,
			ordered[3].x, ordered[3].y
		);
	}
}

void
DLScannerStopTrackingObject(DLScannerRef scanner)
{
	assert(scanner->state == TRACKING);

	if (DLScannerIsTrackedObjectLost(scanner)) {

		delete scanner->object;
		scanner->state = SCANNING;
		scanner->object = NULL;

		if (scanner->loseObjectCallback) {
			scanner->loseObjectCallback(scanner);
		}
	}
}

int
DLScannerFindTrackedObject(DLScannerRef scanner, const cv::Mat &mat, const vector<vector<cv::Point>> &objects)
{
	if (scanner->object == NULL) {
		return -1;
	}

	for (int i = 0; i < objects.size(); i++) {

		auto points = objects[i];

		if (abs(points[0].x - scanner->object->shape[0].x) / mat.cols > 0.1 ||
			abs(points[0].y - scanner->object->shape[0].y) / mat.rows > 0.1) {
			continue;
		}

		if (abs(points[1].x - scanner->object->shape[1].x) / mat.cols > 0.1 ||
			abs(points[1].y - scanner->object->shape[1].y) / mat.rows > 0.1) {
			continue;
		}

		if (abs(points[2].x - scanner->object->shape[2].x) / mat.cols > 0.1 ||
			abs(points[2].y - scanner->object->shape[2].y) / mat.rows > 0.1) {
			continue;
		}

		if (abs(points[3].x - scanner->object->shape[3].x) / mat.cols > 0.1 ||
			abs(points[3].y - scanner->object->shape[3].y) / mat.rows > 0.1) {
			continue;
		}

		return i;
	}

	return -1;
}

bool
DLScannerIsTrackedObjectLost(DLScannerRef scanner)
{
	return DLScannerGetTime() - scanner->object->until > DL_SCANNER_OBJECT_LOSE_TIME;
}

bool
DLScannerIsTrackedObjectValid(DLScannerRef scanner)
{
	return scanner->object->until - scanner->object->began > DL_SCANNER_OBJECT_FIND_TIME;
}

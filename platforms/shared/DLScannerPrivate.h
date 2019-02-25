#ifndef DLScannerPrivate_h
#define DLScannerPrivate_h

#include <string>
#include <vector>
#include <chrono>
#include <opencv2/opencv.hpp>

using std::pair;
using std::string;
using std::vector;

/**
 * @struct OpaqueDLDetectedObject
 * @since 0.1.0
 */
enum DLScannerState {
	SCANNING,
	TRACKING,
	DETECTED,
	FAILED
};

/**
 * @struct OpaqueDLScanner
 * @since 0.3.0
 */
struct OpaqueDLScanner {
	DLScannerState state = SCANNING;
	int naturalImageCols = 0;
	int naturalImageRows = 0;
	int resizedImageCols = 0;
	int resizedImageRows = 0;
	bool debug = false;
	bool enabled = true;
	DLDetectedObjectRef object = NULL;
	DLScannerFindObjectCallback findObjectCallback = NULL;
	DLScannerSpotObjectCallback spotObjectCallback = NULL;
	DLScannerMissObjectCallback missObjectCallback = NULL;
	DLScannerLoseObjectCallback loseObjectCallback = NULL;
	void* data = NULL;
	cv::Mat debuggingImage;
	cv::Mat extractedImage;
};

/**
 * @struct OpaqueDLDetectedObject
 * @since 0.1.0
 */
struct OpaqueDLDetectedObject {
	vector<cv::Point> shape;
	long began = 0;
	long until = 0;
};

/**
 * @function DLScannerComparePointX
 * @since 0.1.0
 * @hidden
 */
bool DLScannerComparePointX(const cv::Point &a, const cv::Point &b);

/**
 * @function DLScannerComparePointY
 * @since 0.1.0
 * @hidden
 */
bool DLScannerComparePointY(const cv::Point &a, const cv::Point &b);

/**
 * @function DLScannerCompareContourArea
 * @since 0.1.0
 * @hidden
 */
bool DLScannerCompareContourArea(const vector<cv::Point> &a, const vector<cv::Point> &b);

/**
 * @function DLSCannerCompareDistance
 * @since 0.1.0
 * @hidden
 */
bool DLScannerCompareDistance(const pair<cv::Point, cv::Point> &a, const pair<cv::Point, cv::Point> &b);

/**
 * @function DLScannerScalePoints
 * @since 0.1.0
 * @hidden
 */
void DLScannerScalePoints(DLScannerRef scanner, const vector<cv::Point> &points, vector<cv::Point> &out);

/**
 * @function DLScannerScalePointsBy
 * @since 0.1.0
 * @hidden
 */
void DLScannerScalePointsBy(DLScannerRef scanner, const vector<cv::Point> &points, vector<cv::Point> &out, float sx, float sy);

/**
 * @function DLScannerOrderPoints
 * @since 0.1.0
 * @hidden
 */
void DLScannerOrderPoints(DLScannerRef scanner, vector<cv::Point> points, vector<cv::Point> &ordered);

/**
 * @function DLScannerOrderPointsClockwise
 * @since 0.1.0
 * @hidden
 */
void DLScannerOrderPointsClockwise(DLScannerRef scanner, vector<cv::Point> points, vector<cv::Point> &ordered);

/**
 * @function DLScannerExtractTrackedObject
 * @since 0.1.0
 * @hidden
 */
void DLScannerExtractTrackedObject(DLScannerRef scanner, const cv::Mat &src, const cv::Mat &mat, cv::Mat &dst, const vector<cv::Point> &points);

/**
 * @function DLScannerGetTime
 * @since 0.1.0
 * @hidden
 */
long DLScannerGetTime();

#endif

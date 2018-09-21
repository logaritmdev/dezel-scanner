#ifndef DLScannerDetection_h
#define DLScannerDetection_h

#include <string>
#include <vector>
#include <opencv2/opencv.hpp>

using std::string;
using std::vector;

/**
 * @function DLScannerProcessImage
 * @since 0.1.0
 * @hidden
 */
void DLScannerProcessImage(DLScannerRef scanner, int imgc, int imgr, const cv::Mat &src);

/**
 * @function DLScannerDetectObjects
 * @since 0.1.0
 * @hidden
 */
void DLScannerDetectObjects(DLScannerRef scanner, const cv::Mat &mat, vector<vector<cv::Point>> &larges, vector<vector<cv::Point>> &smalls, vector<vector<cv::Point>> &rects);

/**
 * @function DLScannerStartTrackingObject
 * @since 0.1.0
 * @hidden
 */
void DLScannerStartTrackingObject(DLScannerRef scanner, const cv::Mat &src, const cv::Mat &mat, const vector<vector<cv::Point>> &objects);

/**
 * @function DLScannerContinueTrackingObject
 * @since 0.1.0
 * @hidden
 */
void DLScannerContinueTrackingObject(DLScannerRef scanner, const cv::Mat &src, const cv::Mat &mat, const vector<vector<cv::Point>> &objects);

/**
 * @function DLScannerStopTrackingObject
 * @since 0.1.0
 * @hidden
 */
void DLScannerStopTrackingObject(DLScannerRef scanner);

/**
 * @function DLScannerFindTrackedObject
 * @since 0.1.0
 * @hidden
 */
int DLScannerFindTrackedObject(DLScannerRef scanner, const cv::Mat &mat, const vector<vector<cv::Point>> &objects);

/**
 * @function DLScannerIsTrackedObjectLost
 * @since 0.1.0
 * @hidden
 */
bool DLScannerIsTrackedObjectLost(DLScannerRef scanner);

/**
 * @function DLScannerIsTrackedObjectValid
 * @since 0.1.0
 * @hidden
 */
bool DLScannerIsTrackedObjectValid(DLScannerRef scanner);

#endif

#include <chrono>
#include <utility>
#include "DLScanner.h"
#include "DLScannerPrivate.h"
#include <opencv2/opencv.hpp>

#ifdef IOS

#endif

#ifdef ANDROID
#include <jni_init.h>
#endif

using namespace std::chrono;
using std::abs;
using std::pair;
using std::sort;
using std::make_pair;

#define DIST(P1, P2) sqrt(((P1.x - P2.x) * (P1.x - P2.x)) + ((P1.y  - P2.y) * (P1.y - P2.y)));

bool
DLScannerComparePointX(const cv::Point &a, const cv::Point &b)
{
	return (a.x < b.x);
}

bool
DLScannerComparePointY(const cv::Point &a, const cv::Point &b)
{
	return (a.y < b.y);
}

bool
DLScannerCompareContourArea(const vector<cv::Point> &a, const vector<cv::Point> &b)
{
	return contourArea(a) > contourArea(b);
}

bool
DLScannerCompareDistance(const pair<cv::Point, cv::Point> &a, const pair<cv::Point, cv::Point> &b)
{
	return (norm(a.first - a.second) < norm(b.first - b.second));
}

void
DLScannerOrderPoints(DLScannerRef scanner, vector<cv::Point> points, vector<cv::Point> &ordered)
{
	sort(
		points.begin(),
		points.end(),
		DLScannerComparePointX
	);

	vector<cv::Point> lm(
		points.begin(),
		points.begin() + 2
	);

	vector<cv::Point> rm(
		points.end() - 2,
		points.end()
	);

	sort(
		lm.begin(),
		lm.end(),
		DLScannerComparePointY
	);

	cv::Point tl(lm[0]);
	cv::Point bl(lm[1]);
	vector<pair<cv::Point, cv::Point>> tmp;

	for (size_t i = 0; i < rm.size(); i++) {
		tmp.push_back(make_pair(tl, rm[i]));
	}

	sort(
		tmp.begin(),
		tmp.end(),
		DLScannerCompareDistance
	);

	cv::Point tr(tmp[0].second);
	cv::Point br(tmp[1].second);

	ordered.push_back(tl);
	ordered.push_back(tr);
	ordered.push_back(bl);
	ordered.push_back(br);
}

void
DLScannerOrderPointsClockwise(DLScannerRef scanner, vector<cv::Point> points, vector<cv::Point> &ordered)
{
	sort(points.begin(), points.end(), DLScannerComparePointX);

	vector<cv::Point2f> lfs;
	vector<cv::Point2f> rgs;
	lfs.push_back(points[0]);
	lfs.push_back(points[1]);
	rgs.push_back(points[2]);
	rgs.push_back(points[3]);

	sort(rgs.begin(), rgs.end(), DLScannerComparePointY);
	sort(lfs.begin(), lfs.end(), DLScannerComparePointY);

	ordered.push_back(lfs[0]);
	ordered.push_back(rgs[0]);
	ordered.push_back(rgs[1]);
	ordered.push_back(lfs[1]);
}

void
DLScannerScalePoints(DLScannerRef scanner, const vector<cv::Point> &points, vector<cv::Point> &out)
{
	float sx = (float) scanner->imgc / (float) scanner->processed.cols;
	float sy = (float) scanner->imgr / (float) scanner->processed.rows;

	DLScannerScalePointsBy(scanner, points, out, sx, sy);
}

void
DLScannerScalePointsBy(DLScannerRef scanner, const vector<cv::Point> &points, vector<cv::Point> &out, float sx, float sy)
{
	for (auto &point : points) {
		out.push_back(cv::Point(
			(int)(point.x * sx),
			(int)(point.y * sy)
		));
	}
}

void
DLScannerExtractTrackedObject(DLScannerRef scanner, const cv::Mat &src, const cv::Mat &mat, cv::Mat &dst, const vector<cv::Point> &points)
{
	vector<cv::Point> ordered;
	vector<cv::Point> scaled;

	float sx = (float) scanner->imgc / (float) mat.cols;
	float sy = (float) scanner->imgr / (float) mat.rows;

	DLScannerScalePointsBy(scanner, points, scaled, sx, sy);

	float u0 = (src.cols) / 2.0;
	float v0 = (src.rows) / 2.0;

	DLScannerOrderPoints(scanner, scaled, ordered);

	auto c0 = cv::Point2f(ordered[0].x, ordered[0].y);
	auto c1 = cv::Point2f(ordered[1].x, ordered[1].y);
	auto c2 = cv::Point2f(ordered[2].x, ordered[2].y);
	auto c3 = cv::Point2f(ordered[3].x, ordered[3].y);

	float wa = (float) DIST(c0, c1);
	float wb = (float) DIST(c2, c3);
	float ha = (float) DIST(c0, c2);
	float hb = (float) DIST(c1, c3);

	float mw = (float) std::max(wa, wb);
	float mh = (float) std::max(ha, hb);

	cv::Mat m1 = (cv::Mat_<float>(3, 1) << c0.x, c0.y, 1);
	cv::Mat m2 = (cv::Mat_<float>(3, 1) << c1.x, c1.y, 1);
	cv::Mat m3 = (cv::Mat_<float>(3, 1) << c2.x, c2.y, 1);
	cv::Mat m4 = (cv::Mat_<float>(3, 1) << c3.x, c3.y, 1);

	float k2 = (m1.cross(m4).dot(m3)) / ((m2.cross(m4)).dot(m3));
	float k3 = (m1.cross(m4).dot(m2)) / ((m3.cross(m4)).dot(m2));

	cv::Mat n2 = (k2 * m2) - m1;
	cv::Mat n3 = (k3 * m3) - m1;

	float n21 = n2.at<float>(0, 0);
	float n22 = n2.at<float>(1, 0);
	float n23 = n2.at<float>(2, 0);

	float n31 = n3.at<float>(0, 0);
	float n32 = n3.at<float>(1, 0);
	float n33 = n3.at<float>(2, 0);

	float f = sqrt(abs(1.0 / ( n23 * n33)) * ((n21 * n31 - (n21 * n33 + n23 * n31) * u0 + n23 * n33 * u0 * u0) + (n22 * n32 - (n22 * n33 + n23 * n32) * v0 + n23 * n33 * v0 * v0)));

	cv::Mat a = (cv::Mat_<float>(3, 3) <<
		f, 0, u0,
		0, f, v0,
		0, 0, 1
	);

	cv::Mat ratio = (n2.t() * (a.inv().t()) * (a.inv()) * n2) / (n3.t() * (a.inv().t()) * (a.inv()) * n3);

  	float r = sqrt(ratio.at<float>(0,0));
	float w;
	float h;

	if (isnan(r)) {

		float tw = abs(c1.x - c0.x);
		float bw = abs(c3.x - c2.x);

		w = mw;
		h = mh * (bw / tw);

	} else {

		float orinal = mw / mh;

		if (r < orinal) {
			w = mw;
			h = w / r;
		} else {
			h = mh;
			w = r * h;
		}

	}

	cv::Point2f sp[] = { c0, c1, c2, c3 };
	cv::Point2f dp[] = {
		cv::Point2f(0, 0),
		cv::Point2f(w, 0),
		cv::Point2f(0, h),
		cv::Point2f(w, h)
	};

	cv::Mat m = getPerspectiveTransform(sp, dp);

	warpPerspective(src, dst, m, cv::Size(w, h));
}

long 
DLScannerGetTime() {
	return duration_cast<milliseconds>(time_point_cast<milliseconds>(system_clock::now()).time_since_epoch()).count();	
}

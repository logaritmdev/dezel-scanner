#include <android/log.h>
#include <android/bitmap.h>
#include "DLScannerAndroid.h"
#include "DLScanner.h"
#include "DLScannerDetection.h"
#include "DLScannerPrivate.h"
#include "wrappers/DLScannerWrapper.h"
#include "jni_init.h"
#include "jni_module_scanner.h"

void
DLScannerProcessFrame(JNIEnv* env, DLScannerRef scanner, int imgc, int imgr, jbyteArray buffer)
{
	jbyte *syuv = env->GetByteArrayElements(buffer, 0);

	cv::Mat yuv(imgr + imgr / 2, imgc, CV_8UC1, (unsigned char *) syuv);
	cv::Mat src(imgr, imgc, CV_8UC4);

	cv::cvtColor(yuv, src, CV_YUV420sp2BGR, 4);
	cv::transpose(src, src);
	cv::flip(src, src, 1);

	// update size for rotation
	int w = imgr;
	int h = imgc;

	DLScannerProcessImage(scanner, w, h, src);

	env->ReleaseByteArrayElements(buffer, syuv, 0);
}

jobject
DLScannerGetProcessedImage(JNIEnv* env, DLScannerRef scanner)
{
	return DLScannerExportImage(env, scanner->processed);
}

jobject
DLScannerGetExtractedImage(JNIEnv* env, DLScannerRef scanner)
{
	return DLScannerExportImage(env, scanner->extracted);
}

void
DLScannerImportImage(JNIEnv* env, jobject bitmap, cv::Mat &dst)
{
	AndroidBitmapInfo  info;
	void*              pixels = 0;

	try {

		CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
		CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
				  info.format == ANDROID_BITMAP_FORMAT_RGB_565);

		CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
		CV_Assert(pixels);

		dst.create(info.height, info.width, CV_8UC4);

		if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {

			cv::Mat tmp(info.height, info.width, CV_8UC4, pixels);
			tmp.copyTo(dst);

		} else {

			cv::Mat tmp(info.height, info.width, CV_8UC2, pixels);
			cvtColor(tmp, dst, cv::COLOR_BGR5652RGBA);

		}

		AndroidBitmap_unlockPixels(env, bitmap);

		return;

	} catch(const cv::Exception& e) {
		AndroidBitmap_unlockPixels(env, bitmap);
		jclass je = env->FindClass("org/opencv/core/CvException");
		if(!je) je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, e.what());
	} catch (...) {
		AndroidBitmap_unlockPixels(env, bitmap);
		jclass je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
	}
}

jobject
DLScannerExportImage(JNIEnv* env, const cv::Mat &src)
{
	if (src.cols == 0 ||
		src.rows == 0) {
		return NULL;
	}

	AndroidBitmapInfo  info;
	void*              pixels = 0;

	jobject bitmap = env->CallStaticObjectMethod(
		BitmapClass,
		BitmapCreate,
		src.cols, src.rows,
		env->GetStaticObjectField(BitmapConfigClass, Bitmap_ARGB_8888)
	);

	try {

		CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
		CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
				  info.format == ANDROID_BITMAP_FORMAT_RGB_565);

		CV_Assert(src.dims == 2);
		CV_Assert(src.rows == info.height);
		CV_Assert(src.cols == info.width);

		CV_Assert(
			src.type() == CV_8UC1 ||
			src.type() == CV_8UC3 ||
			src.type() == CV_8UC4
		);

		CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
		CV_Assert(pixels);

		if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {

			cv::Mat tmp(info.height, info.width, CV_8UC4, pixels);

			if (src.type() == CV_8UC1) {
				cvtColor(src, tmp, CV_GRAY2RGBA);
			} else if (src.type() == CV_8UC3) {
				cvtColor(src, tmp, CV_RGB2RGBA);
			} else if (src.type() == CV_8UC4) {
				src.copyTo(tmp);
			}

		} else {

			cv::Mat tmp(info.height, info.width, CV_8UC2, pixels);

			if (src.type() == CV_8UC1) {
				cvtColor(src, tmp, CV_GRAY2BGR565);
			} else if (src.type() == CV_8UC3) {
				cvtColor(src, tmp, CV_RGB2BGR565);
			} else if (src.type() == CV_8UC4) {
				cvtColor(src, tmp, CV_RGBA2BGR565);
			}

		}

		AndroidBitmap_unlockPixels(env, bitmap);
		return bitmap;

	} catch(cv::Exception e) {
		AndroidBitmap_unlockPixels(env, bitmap);
		return NULL;
	} catch (...) {
		AndroidBitmap_unlockPixels(env, bitmap);
		return NULL;
	}
}
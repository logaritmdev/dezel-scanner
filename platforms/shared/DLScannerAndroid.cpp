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
DLScannerProcessFrame(JNIEnv* env, DLScannerRef scanner, int imgc, int imgr, jobject buffer)
{
	cv::Mat src(imgr, imgc, CV_8UC1, env->GetDirectBufferAddress(buffer));
	cv::Mat img(imgc, imgr, CV_8UC1);

	transpose(src, img);
	flip(img, img, 1);

	DLScannerProcessImage(scanner, imgc, imgr, img);
}

jobject
DLScannerGetProcessedImage(JNIEnv* env, DLScannerRef scanner)
{
	return DLScannerExportImage(env, scanner->processedim);
}

jobject
DLScannerGetExtractedImage(JNIEnv* env, DLScannerRef scanner)
{
	return DLScannerExportImage(env, scanner->extractedim);
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
			} else if (src.type() == CV_8UC3){
				cvtColor(src, tmp, CV_RGB2RGBA);
			} else if (src.type() == CV_8UC4){
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
		LOGE("Error during convertion to bitmap: %s", e.what());
		return NULL;
	} catch (...) {
		AndroidBitmap_unlockPixels(env, bitmap);
		LOGE("Error during convertion to bitmap.");
		return NULL;
	}
}
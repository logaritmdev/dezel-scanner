#include <chrono>
#include <string>
#include <vector>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>
#include "DLScanner.h"
#include "DLScannerPrivate.h"
#include "DLScannerDetection.h"

#ifdef IOS
#include <CoreGraphics/CoreGraphics.h>
#endif

#ifdef ANDROID
#include "jni_init.h"
#include "wrappers/DLScannerWrapper.h"
#include "DLScannerAndroid.h"
#endif

using namespace std::chrono;

using std::string;
using std::vector;
using std::sort;
using cv::Mat;

DLScannerRef
DLScannerCreate()
{
	return new OpaqueDLScanner;
}

void
DLScannerDelete(DLScannerRef scanner)
{
	delete scanner;
}

void
DLScannerSetDebug(DLScannerRef scanner, bool debug)
{
	scanner->debug = debug;
}

void
DLScannerSetEnabled(DLScannerRef scanner, bool enabled)
{
	scanner->enabled = enabled;
}

void
DLScannerSetFindObjectCallback(DLScannerRef scanner, DLScannerFindObjectCallback callback)
{
	scanner->findObjectCallback = callback;
}

void
DLScannerSetSpotObjectCallback(DLScannerRef scanner, DLScannerSpotObjectCallback callback)
{
	scanner->spotObjectCallback = callback;
}

void
DLScannerSetLoseObjectCallback(DLScannerRef scanner, DLScannerLoseObjectCallback callback)
{
	scanner->loseObjectCallback = callback;
}

void
DLScannerSetMissObjectCallback(DLScannerRef scanner, DLScannerMissObjectCallback callback)
{
	scanner->missObjectCallback = callback;
}

void
DLScannerSetData(DLScannerRef scanner, void* data)
{
	scanner->data = data;
}

void*
DLScannerGetData(DLScannerRef scanner)
{
	return scanner->data;
}

void
DLScannerReset(DLScannerRef scanner)
{
	if (scanner->state == TRACKING) {
		scanner->state = SCANNING;
	}

	if (scanner->object) {
		if (scanner->loseObjectCallback) {
			scanner->loseObjectCallback(scanner);
		}
	}

	delete scanner->object;

	scanner->object = NULL;
}

void
DLScannerRestart(DLScannerRef scanner)
{
	DLScannerReset(scanner);
	scanner->state = SCANNING;
}

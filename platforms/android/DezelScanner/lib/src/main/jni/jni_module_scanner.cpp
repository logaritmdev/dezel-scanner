#include "jni_module_scanner.h"

jclass PointFClass;
jclass BitmapClass;
jclass BitmapConfigClass;
jclass ScannerClass;
jfieldID Bitmap_ARGB_8888;
jmethodID PointFConstructor;
jmethodID BitmapCreate;
jmethodID ScannerOnFindObject;
jmethodID ScannerOnSpotObject;
jmethodID ScannerOnLoseObject;
jmethodID ScannerOnMissObject;
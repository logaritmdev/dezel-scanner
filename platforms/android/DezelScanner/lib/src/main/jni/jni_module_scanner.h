#ifndef JNI_Module_Scanner_h
#define JNI_Module_Scanner_h

#include <jni.h>

extern jclass PointFClass;
extern jclass BitmapClass;
extern jclass BitmapConfigClass;
extern jclass ScannerClass;
extern jfieldID Bitmap_ARGB_8888;
extern jfieldID PointFX;
extern jfieldID PointFY;
extern jmethodID PointFConstructor;
extern jmethodID BitmapCreate;
extern jmethodID ScannerOnFindObject;
extern jmethodID ScannerOnSpotObject;
extern jmethodID ScannerOnLoseObject;
extern jmethodID ScannerOnMissObject;

#endif

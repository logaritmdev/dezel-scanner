#include <DLScanner.h>
#include <DLScannerAndroid.h>
#include "DLScannerWrapper.h"
#include "jni_init.h"
#include "jni_module_scanner.h"

static void
DLFindObjectCallback(DLScannerRef scanner, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
{
	DLScannerWrapperRef wrapper =  reinterpret_cast<DLScannerWrapperRef>(DLScannerGetData(scanner));
	if (wrapper == NULL) {
		return;
	}

	jobjectArray shape = wrapper->env->NewObjectArray(4, PointFClass, NULL);
	wrapper->env->SetObjectArrayElement(shape, 0, wrapper->env->NewObject(PointFClass, PointFConstructor, x1, y1));
	wrapper->env->SetObjectArrayElement(shape, 1, wrapper->env->NewObject(PointFClass, PointFConstructor, x2, y2));
	wrapper->env->SetObjectArrayElement(shape, 2, wrapper->env->NewObject(PointFClass, PointFConstructor, x3, y3));
	wrapper->env->SetObjectArrayElement(shape, 3, wrapper->env->NewObject(PointFClass, PointFConstructor, x4, y4));

	JNI_CALL_VOID_METHOD(
		wrapper->env,
		wrapper->obj,
		ScannerOnFindObject,
		shape,
		DLScannerGetExtractedImage(wrapper->env, scanner)
	);
}

static void
DLSpotObjectCallback(DLScannerRef scanner, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
{
	DLScannerWrapperRef wrapper =  reinterpret_cast<DLScannerWrapperRef>(DLScannerGetData(scanner));
	if (wrapper == NULL) {
		return;
	}

	jobjectArray shape = wrapper->env->NewObjectArray(4, PointFClass, NULL);
	wrapper->env->SetObjectArrayElement(shape, 0, wrapper->env->NewObject(PointFClass, PointFConstructor, x1, y1));
	wrapper->env->SetObjectArrayElement(shape, 1, wrapper->env->NewObject(PointFClass, PointFConstructor, x2, y2));
	wrapper->env->SetObjectArrayElement(shape, 2, wrapper->env->NewObject(PointFClass, PointFConstructor, x3, y3));
	wrapper->env->SetObjectArrayElement(shape, 3, wrapper->env->NewObject(PointFClass, PointFConstructor, x4, y4));

	JNI_CALL_VOID_METHOD(
		wrapper->env,
		wrapper->obj,
		ScannerOnSpotObject,
		shape
	);
}

static void
DLLoseObjectCallback(DLScannerRef scanner)
{
	DLScannerWrapperRef wrapper =  reinterpret_cast<DLScannerWrapperRef>(DLScannerGetData(scanner));
	if (wrapper == NULL) {
		return;
	}

	JNI_CALL_VOID_METHOD(
		wrapper->env,
		wrapper->obj,
		ScannerOnLoseObject
	);
}

static void
DLMissObjectCallback(DLScannerRef scanner)
{
	DLScannerWrapperRef wrapper =  reinterpret_cast<DLScannerWrapperRef>(DLScannerGetData(scanner));
	if (wrapper == NULL) {
		return;
	}

	JNI_CALL_VOID_METHOD(
		wrapper->env,
		wrapper->obj,
		ScannerOnMissObject
	);
}

DLScannerWrapperRef
DLScannerWrapperCreate(JNIEnv *env, jobject object, DLScannerRef scanner)
{
	DLScannerWrapperRef wrapper = new DLScannerWrapper();
	wrapper->env = env;
	wrapper->obj = env->NewGlobalRef(object);

	DLScannerSetFindObjectCallback(scanner, &DLFindObjectCallback);
	DLScannerSetSpotObjectCallback(scanner, &DLSpotObjectCallback);
	DLScannerSetLoseObjectCallback(scanner, &DLLoseObjectCallback);
	DLScannerSetMissObjectCallback(scanner, &DLMissObjectCallback);

	return wrapper;
}

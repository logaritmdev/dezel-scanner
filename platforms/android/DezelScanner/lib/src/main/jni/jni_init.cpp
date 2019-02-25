#include <stddef.h>
#include "jni_init.h"
#include "jni_module_scanner.h"

jclass
JNIGetClass(JNIEnv *env, const char *name)
{
	auto cls = env->FindClass(name);
	if (cls == NULL) LOGE("Unable to find class %s", name);
	return cls;
}

jfieldID
JNIGetField(JNIEnv *env, jclass cls, const char *name, const char *sign)
{
	auto res = env->GetFieldID(cls, name, sign);
	if (res == NULL) LOGE("Unable to find field %s with signature %s", name, sign);
	return res;
}

jfieldID
JNIGetStaticField(JNIEnv *env, jclass cls, const char *name, const char *sign)
{
	auto res = env->GetStaticFieldID(cls, name, sign);
	if (res == NULL) LOGE("Unable to find static field %s with signature %s", name, sign);
	return res;
}

jmethodID
JNIGetMethod(JNIEnv *env, jclass cls, const char *name, const char *sign)
{
	auto res = env->GetMethodID(cls, name, sign);
	if (res == NULL) LOGE("Unable to find method %s with signature %s", name, sign);
	return res;
}

jmethodID
JNIGetStaticMethod(JNIEnv *env, jclass cls, const char *name, const char *sign)
{
	auto res = env->GetStaticMethodID(cls, name, sign);
	if (res == NULL) LOGE("Unable to find static field %s with signature %s", name, sign);
	return res;
}

jint
JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env;

	if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
		return -1;
	}

	PointFClass = JNIGetClass(
		env,
		"android/graphics/PointF"
	);

	BitmapConfigClass = JNIGetClass(
		env,
		"android/graphics/Bitmap$Config"
	);

	BitmapClass = JNIGetClass(
		env,
		"android/graphics/Bitmap"
	);

	ScannerClass = JNIGetClass(
		env,
		"ca/logaritm/scanner/scanner/Scanner"
	);

	Bitmap_ARGB_8888 = JNIGetStaticField(
		env,
		BitmapConfigClass,
		"ARGB_8888",
		"Landroid/graphics/Bitmap$Config;"
	);

	PointFX = JNIGetField(
		env,
		PointFClass,
		"x",
		"F"
	);

	PointFY = JNIGetField(
		env,
		PointFClass,
		"y",
		"F"
	);

	PointFConstructor = JNIGetMethod(
		env,
		PointFClass,
		"<init>",
		"(FF)V"
	);

	BitmapCreate = JNIGetStaticMethod(
		env,
		BitmapClass,
		"createBitmap",
		"(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;"
	);

	ScannerOnFindObject = JNIGetMethod(
		env,
		ScannerClass,
		"onFindObject",
		"([Landroid/graphics/PointF;Landroid/graphics/Bitmap;)V"
	);

	ScannerOnSpotObject = JNIGetMethod(
		env,
		ScannerClass,
		"onSpotObject",
		"([Landroid/graphics/PointF;)V"
	);

	ScannerOnLoseObject = JNIGetMethod(
		env,
		ScannerClass,
		"onLoseObject",
		"()V"
	);

	ScannerOnMissObject = JNIGetMethod(
		env,
		ScannerClass,
		"onMissObject",
		"()V"
	);

	PointFClass       = (jclass) env->NewGlobalRef(PointFClass);
	BitmapConfigClass = (jclass) env->NewGlobalRef(BitmapConfigClass);
	BitmapClass       = (jclass) env->NewGlobalRef(BitmapClass);
	ScannerClass      = (jclass) env->NewGlobalRef(ScannerClass);

	return JNI_VERSION_1_6;
}
LOCAL_PATH  := $(call my-dir)
SHARED_PATH := $(call my-dir)/../../../../../../shared

include $(CLEAR_VARS)

OPENCVROOT:= $(LOCAL_PATH)/opencv
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/jni/OpenCV.mk

LOCAL_MODULE    := dezel_scanner
LOCAL_SRC_FILES := wrappers/DLScannerWrapper.cpp \
                   jni_init.cpp \
                   jni_module_scanner.cpp \
                   ca_logaritm_dezel_scanner_scanner_ScannerExternal.cpp \
                   $(SHARED_PATH)/DLScanner.cpp \
                   $(SHARED_PATH)/DLScannerDetection.cpp \
                   $(SHARED_PATH)/DLScannerAndroid.cpp \
                   $(SHARED_PATH)/DLScannerPrivate.cpp

LOCAL_CPPFLAGS := -std=c++11 \
                  -frtti \
                  -fexceptions \
                  -I$(LOCAL_PATH)/opencv/jni/include/opencv \
                  -I$(SHARED_PATH) \
                  -O3

LOCAL_LDFLAGS  := -llog -ljnigraphics -landroid -DANDROID

include $(BUILD_SHARED_LIBRARY)
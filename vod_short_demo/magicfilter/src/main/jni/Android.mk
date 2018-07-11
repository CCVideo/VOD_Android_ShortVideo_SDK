LOCAL_PATH:= $(call my-dir)
# 一个完整模块编译
include $(CLEAR_VARS)
LOCAL_SRC_FILES := MagicJni.cpp bitmap/BitmapOperation.cpp bitmap/Conversion.cpp beautify/MagicBeautify.cpp
LOCAL_C_INCLUDES := $(JNI_H_INCLUDE)
LOCAL_MODULE := libMagicBeautify
#LOCAL_SHARED_LIBRARIES := libutils
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_TAGS :=optional
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
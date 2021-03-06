LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := videodecoder
LOCAL_SRC_FILES := h264decoder.c

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
	LOCAL_C_INCLUDES := $(LOCAL_C_INCLUDES) $(LOCAL_PATH)/ffmpeg-android/armv7-a/include
	LOCAL_LDFLAGS := -L$(LOCAL_PATH)/ffmpeg-android/armv7-a/lib
endif

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
	LOCAL_C_INCLUDES := $(LOCAL_C_INCLUDES) $(LOCAL_PATH)/ffmpeg-android/arm64-v8a/include
	LOCAL_LDFLAGS := -L$(LOCAL_PATH)/ffmpeg-android/arm64-v8a/lib
endif

ifeq ($(TARGET_ARCH_ABI),armeabi)
	LOCAL_C_INCLUDES := $(LOCAL_C_INCLUDES) $(LOCAL_PATH)/ffmpeg-android/armv5te/include
	LOCAL_LDFLAGS := -L$(LOCAL_PATH)/ffmpeg-android/armv5te/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86)
	LOCAL_C_INCLUDES := $(LOCAL_C_INCLUDES) $(LOCAL_PATH)/ffmpeg-android/i686/include
	LOCAL_LDFLAGS := -L$(LOCAL_PATH)/ffmpeg-android/i686/lib
endif

ifeq ($(TARGET_ARCH_ABI),x86_64)
	LOCAL_C_INCLUDES := $(LOCAL_C_INCLUDES) $(LOCAL_PATH)/ffmpeg-android/x86-64/include
	LOCAL_LDFLAGS := -L$(LOCAL_PATH)/ffmpeg-android/x86-64/lib
endif

LOCAL_CFLAGS := -std=c99 -Wall -fvisibility=hidden
LOCAL_LDFLAGS := $(LOCAL_LDFLAGS) -Wl,--gc-sections 
LOCAL_LDLIBS := -llog -lavformat -lavcodec -lswscale -lavutil 

include $(BUILD_SHARED_LIBRARY)
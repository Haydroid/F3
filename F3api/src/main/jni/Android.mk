#
# Copyright 2009 Cedric Priscal
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License. 
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := A6jniA
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \

LOCAL_SRC_FILES := \
	D:\AndroidStudioProjects\CCB\A6-30test\A6api\src\main\jni\Android.mk \
	D:\AndroidStudioProjects\CCB\A6-30test\A6api\src\main\jni\Application.mk \
	D:\AndroidStudioProjects\CCB\A6-30test\A6api\src\main\jni\f5_idcrdr.cpp \
	D:\AndroidStudioProjects\CCB\A6-30test\A6api\src\main\jni\f5_jni.cpp \
	D:\AndroidStudioProjects\CCB\A6-30test\A6api\src\main\jni\stdafx.cpp \

LOCAL_C_INCLUDES += D:\AndroidStudioProjects\CCB\A6-30test\A6api\src\main\jni
LOCAL_C_INCLUDES += D:\AndroidStudioProjects\CCB\A6-30test\A6api\src\debug\jni


include $(BUILD_SHARED_LIBRARY)

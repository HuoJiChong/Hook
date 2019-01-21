//
// Created by J.C. on 2019/1/21.
//

#ifndef HOOK_LOG_H
#define HOOK_LOG_H

#include "android/log.h"

#define TAG "android_log"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

#endif //HOOK_LOG_H

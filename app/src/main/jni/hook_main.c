//
// Created by J.C. on 2019/1/8.
//
#include "com_aly_roger_hook_JNI.h"
#include "inlineHook.h"
#include "Log.h"

int (*old_puts)(const char*) = NULL;
int new_puts(const char* msg)
{
    LOGD("log print %s",msg);
    old_puts("inlineHook success");
}

int hookPuts()
{
    if (registerInlineHook((uint32_t)puts,(uint32_t)new_puts,(uint32_t**)&old_puts) != ELE7EN_OK)
    {
        printf("register inline hook failed");
        return -1;
    }

    if (inlineHook((uint32_t)puts) != ELE7EN_OK)
    {
        printf("inline hook failed");
        return -1;
    }
    puts("Roger Hook ");
    return 0;
}

int unHook()
{
    if (inlineUnHook((uint32_t)puts) != ELE7EN_OK)
    {
        printf("inline unhook failed");
        return -1;
    }
    return 0;
}



JNIEXPORT void JNICALL Java_com_aly_roger_hook_MainActivity_nativeActivityTest(JNIEnv *env , jobject obj)
{
    puts("test before");

    hookPuts();

    puts("test after");
    unHook();

}
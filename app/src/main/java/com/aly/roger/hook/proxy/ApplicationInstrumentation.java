package com.aly.roger.hook.proxy;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

public class ApplicationInstrumentation extends Instrumentation {
    private static final String TAG = "ApplicationInstr";
    Instrumentation mBase;
    public ApplicationInstrumentation(Instrumentation mInstrumentation) {
        mBase = mInstrumentation;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options){
        Log.e(TAG,"startActivity,参数如下："+"who:["+who+"],ContextThread:["+contextThread+"],Token:["+token+"]");
        Method execStartActivity = null;
        try {
            execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity",Context.class,IBinder.class,IBinder.class,Activity.class,Intent.class,int.class,Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(mBase,who,contextThread,token,target,intent,requestCode,options);
        } catch (Exception e) {
           throw new RuntimeException("do not support!!! ");
        }
    }
}

package com.aly.roger.hook.proxy;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AMSHookInvocationHandler implements InvocationHandler {

    public static final String ORITINALLY_INIENT = "originallyIntent";
    private Object mAmsObj;
    private String mPackageName;
    private String cls;

    public AMSHookInvocationHandler(Object amsObj, String pmName, String hostClzName) {
        mAmsObj = amsObj;
        mPackageName = pmName;
        cls = hostClzName;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (method.getName().equals("startActivity")){
            int index = 0;
            for (int i = 0 ;i<objects.length;++i){
                if (objects[i] instanceof Intent){
                    index = i;
                    break;
                }
            }

//            取出真实的Intent
            Intent originallyIntent = (Intent)objects[index];

            Log.i("AmsHookUtil","AMSHookInvocationHandler:"+originallyIntent.getComponent().getClassName());
            Intent proxyIntent = new Intent();
            ComponentName componentName = new ComponentName(mPackageName,cls);
            proxyIntent.setComponent(componentName);
            proxyIntent.putExtra(ORITINALLY_INIENT,originallyIntent);
            objects[index] = proxyIntent;
        }
        return method.invoke(mAmsObj,objects);
    }
}

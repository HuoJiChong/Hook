package com.aly.roger.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * hook  Class  gate
 * Created by J.C. on 2019/1/2.
 */

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.aly.roger.hook"))
            return;
        XposedBridge.log("Loaded app: " + lpparam.packageName);
    }

}

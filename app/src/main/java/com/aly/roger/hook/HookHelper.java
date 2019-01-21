package com.aly.roger.hook;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.aly.roger.hook.proxy.ClipboardHookRemoteBinderHandler;
import com.aly.roger.hook.proxy.HookedClickListenerProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Hook 帮助类
 * Created by J.C. on 2019/1/1.
 */

public class HookHelper {

    private static final String Tag = "HookHelper";
    /**
     * 思路
     第一步：获取 ListenerInfo 对象
     从 View 的源代码，我们可以知道我们可以通过 getListenerInfo 方法获取，于是，我们利用反射得到 ListenerInfo 对象

     第二步：获取原始的 OnClickListener事件方法
     从上面的分析，我们知道 OnClickListener 事件被保存在 ListenerInfo 里面，同理我们利用反射获取

     第三步：偷梁换柱，用 Hook代理类 替换原始的 OnClickListener
     * @param view
     * @throws Exception
     */
    public static void hookOnClickListener(View view) throws Exception {
        Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
        getListenerInfo.setAccessible(true);
//      mListenerInfo 字段
        Object mListenerInfo = getListenerInfo.invoke(view);
        Class<?> ListenerInfo = Class.forName("android.view.View$ListenerInfo");
//        获取 ListenerInfo 类里面的 mOnClickListener 字段
        Field mOnClickListener = ListenerInfo.getDeclaredField("mOnClickListener");
        mOnClickListener.setAccessible(true);
//        获取到传入的对象的原始的Click监听,
        View.OnClickListener mOriginOnClickListener = (View.OnClickListener) mOnClickListener.get(mListenerInfo);
//        创建一个新的Hook Listener
        View.OnClickListener mHookOnClickListener = new HookedClickListenerProxy(mOriginOnClickListener);
//        将传入的对象（mListenerInfo）的mOnClickListener 字段设置为hook
        mOnClickListener.set(mListenerInfo,mHookOnClickListener);

    }

    /**
     * 思路
     第一步：得到 NotificationManager 的 sService
     第二步：因为 sService 是接口，所以我们可以使用动态代理，获取动态代理对象
     第三步：偷梁换柱，使用动态代理对象 proxyNotiMng 替换系统的 sService
     * @param context
     */
    public static void hookNotificationManager(final Context context) throws Exception {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Method getService = NotificationManager.class.getDeclaredMethod("getService");
        getService.setAccessible(true);
//        获取原始的sService
        final Object sOriginService = getService.invoke(notificationManager);

        Class iNotiMngClz = Class.forName("android.app.INotificationManager");
        Object proxyNotiMng = Proxy.newProxyInstance(context.getClass().getClassLoader(), new Class[]{iNotiMngClz}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                Log.e(Tag,"invoke.method():"+method);
                Log.e(Tag,"invoke.name:"+method.getName());

                if (args != null && args.length > 0) {
                    for (Object arg : args) {
                        Log.d(Tag, "invoke: arg=" + arg);
                    }
                }

                Log.e(Tag,"检测到有人发通知了");
                // 操作交由 sOriginService 处理，不拦截通知
                return method.invoke(sOriginService, args);
                // 拦截通知，什么也不做
                // return null;
                // 或者是根据通知的 Tag 和 ID 进行筛选

            }
        });

        Field sService = NotificationManager.class.getDeclaredField("sService");
        sService.setAccessible(true);
        sService.set(notificationManager,proxyNotiMng);

    }

    /**
     *
     static private IClipboard getService() {
         synchronized (sStaticLock) {
             if (sService != null) {
                return sService;
             }
             IBinder b = ServiceManager.getService("clipboard");
             sService = IClipboard.Stub.asInterface(b);
             return sService;
         }
     }
     * Hook 剪切板
     * @param context
     * @throws Exception
     */

    public static void hookClipboardManager(final Context context) throws Exception {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        Method getService = ClipboardManager.class.getDeclaredMethod("getService");
        getService.setAccessible(true);
//      获取原始的剪切板服务
        final Object sOriginService = getService.invoke(clipboardManager);

        Class iClipboard = Class.forName("android.content.IClipboard");
        Object proxyClipboard = Proxy.newProxyInstance(context.getClass().getClassLoader(), new Class[]{iClipboard}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                Log.e(Tag,"method:name"+method.getName());

                String methodName = method.getName();
                if (args != null && args.length > 0) {
                    for (Object arg : args) {
                        Log.d(Tag, "invoke: arg=" + arg);
                    }
                }
                if ("getPrimaryClip".equals(methodName)){
                    Log.e(Tag,"there is someone getting clipboard data");
                }else if ("setPrimaryClip".equals(methodName)){
                    Log.e(Tag,"there is someone setting clipboard data");
                    Object arg = args[0];
                    if (arg instanceof ClipData){
                        ClipData data = (ClipData) arg;
                        int itemCount = data.getItemCount();
                        for (int i = 0;i<itemCount;i++){
                            ClipData.Item item = data.getItemAt(i);
                            Log.e(Tag,"ClipData index: "+i+" item: "+item.getText());
                        }
                    }
                }
                return method.invoke(sOriginService,args);
            }
        });

//      剪切板替换
        Field sService = ClipboardManager.class.getDeclaredField("sService");
        sService.setAccessible(true);
        sService.set(clipboardManager,proxyClipboard);

    }

    /**
     * 第一步：通过反射获取剪切板服务的远程Binder对象，这里我们可以通过 ServiceManager getService 方法获得
     * 第二步：创建我们的动态代理对象，动态代理原来的Binder对象
     * 第三步：偷梁换柱，把我们的动态代理对象设置进去
     */
    public static void hookClipboardService() throws Exception {
//      通过反射获取剪切板服务的远程Binder对象
        Class serviceManager = Class.forName("android.os.ServiceManager");
        Method getServiceMethod = serviceManager.getMethod("getService",String.class);
        getServiceMethod.setAccessible(true);
        IBinder remoteBinder = (IBinder) getServiceMethod.invoke(null,Context.CLIPBOARD_SERVICE);
//      新建一个我们需要的Binder，动态代理原来的Binder对象
        IBinder hookBinder = (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),
                new Class[]{IBinder.class}, new ClipboardHookRemoteBinderHandler(remoteBinder));

//      通过反射获取ServiceManger存储Binder对象的缓存集合，把我们新建的代理Binder放入缓存集合
        Field sCacheField = serviceManager.getDeclaredField("sCache");
        sCacheField.setAccessible(true);
        Map<String,IBinder> sCache = (Map<String,IBinder>)sCacheField.get(null);
        sCache.put(Context.CLIPBOARD_SERVICE,hookBinder);

//        Main main = new Main();
//        main.nativeTest();
//        main.handleLoadPackage();
    }
}

package com.aly.roger.hook;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String Tag = "MainActivity";

    static {
        System.loadLibrary("Hook");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_hook =  findViewById(R.id.button_hook);
        button_hook.setOnClickListener(this);
        Button btn_hookClipSet = findViewById(R.id.btn_hookClipSet);
        btn_hookClipSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("text","Hello world");
                clipboardManager.setPrimaryClip(data);
            }
        });

        Button btn_hookClipGet = findViewById(R.id.btn_hookClipGet);
        btn_hookClipGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = clipboardManager.getPrimaryClip();
                Log.d(Tag, (String) data.getItemAt(0).getText());
            }
        });

        try {
            HookHelper.hookOnClickListener(button_hook);
            HookHelper.hookNotificationManager(this);
//            HookHelper.hookClipboardManager(this);
            HookHelper.hookClipboardService();


//            JNI.nativeTest();
            nativeActivityTest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this,"this is normal click",Toast.LENGTH_LONG);
        Log.d(Tag,"onclick");

        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle("我是标题")
                //设置内容
                .setContentText("我是内容")
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //首次进入时显示效果
                .setTicker("我是测试内容")
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                .setDefaults(Notification.DEFAULT_SOUND);
        //发送通知请求
        notificationManager.notify(10, mBuilder.build());

    }
    public static native void nativeActivityTest();
}

package com.aly.roger.hook.proxy;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * 点击监听器的代理
 * Created by J.C. on 2019/1/1.
 */

public class HookedClickListenerProxy implements View.OnClickListener {
    private final static String Tag = "HookedClickListerProxy";

    private View.OnClickListener origin;

    public HookedClickListenerProxy(View.OnClickListener origin) {
        this.origin = origin;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Hook Click Listener", Toast.LENGTH_SHORT).show();
        Log.e(Tag,"onClick");
        if (origin != null) {
            origin.onClick(v);
        }
    }

}

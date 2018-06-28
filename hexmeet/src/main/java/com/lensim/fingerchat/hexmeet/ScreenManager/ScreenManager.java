
package com.lensim.fingerchat.hexmeet.ScreenManager;

import android.content.Intent;
import android.content.IntentFilter;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;

/**
 * 管理屏幕的亮灭
 *
 */
public class ScreenManager implements OnInitializedListener, OnCloseListener {

    private final static String TAG = "ScreenManager";
    private final ScreenReceiver screenReceiver;
    private final static ScreenManager instance= new ScreenManager();

    public static ScreenManager getInstance() {
        return instance;
    }

    private ScreenManager() {
        screenReceiver = new ScreenReceiver();
    }

    @Override
    public void onInitialized() {
        L.i(TAG,"屏幕管理:" + "onInitialized：注册屏幕广播");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        ContextHelper.getContext().registerReceiver(screenReceiver, filter);
    }

    @Override
    public void onClose() {
        ContextHelper.getContext().unregisterReceiver(screenReceiver);
    }


    public void onScreen(Intent intent) {

        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
//            ConnectionManager.getInstance().updateConnections(false);
            L.i("屏幕点亮");


        } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            // TODO: 2016/10/12 屏幕关闭了，这时候应该做点事情
            L.i("屏幕被关闭");
        }
    }

}

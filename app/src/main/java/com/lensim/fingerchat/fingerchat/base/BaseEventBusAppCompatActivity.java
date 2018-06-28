package com.lensim.fingerchat.fingerchat.base;

import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * EventBus生命周期管理
 * Created by zm on 2018/6/4.
 */
public abstract class BaseEventBusAppCompatActivity extends BaseToolbarAppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBase(IEventProduct event) {
        if (event != null) {
            L.i("base event succuss");
        }
    }
}

package com.lensim.fingerchat.fingerchat.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.ConflictListener;
import com.fingerchat.api.message.ConflictMessage;
import com.lensim.fingerchat.commons.helper.AppManager;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;

public abstract class FGAppCompatActivity<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends BaseAppCompatActivity implements ConflictListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        ClientConfig.I.registerListener(ConflictListener.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientConfig.I.removeListener(ConflictListener.class, this);
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;

    }

    @Override
    public void onReceivedConflictListener(ConflictMessage message) {
        Intent intent = ActivitysRouter.getInstance()
            .invoke(this, ActivityPath.USER_CONFLICT_ACTIVITY_PATH);
        if (intent != null) {
            intent.putExtra(ActivityPath.CLOSE_ERROR, 0);
            startActivity(intent);
        }
    }
}

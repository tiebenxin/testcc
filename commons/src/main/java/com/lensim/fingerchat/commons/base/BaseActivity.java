package com.lensim.fingerchat.commons.base;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.helper.AppManager;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.utils.L;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LL130386 on 2017/11/14.
 */

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        if (!AuthorityManager.getInstance().screenShot()) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initMVPView();
        registerEventBus();
        initView();
        initData(savedInstanceState);
    }

    public void initMVPView() {
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().finishActivity(this);
        unregisterEventBus();
        super.onDestroy();
    }


    public abstract void initView();


    public void toActivity(Class<?> clazz) {
        toActivity(clazz, null);
    }


    public void toActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void toActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void initData(Bundle savedInstanceState) {
    }


    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }


    private void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBase(IEventProduct event) {
        if (event != null) {
            L.i("base event succuss");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setOverflowIconVisible(Window.FEATURE_ACTION_BAR, menu);
        return super.onMenuOpened(featureId, menu);
    }

    public static void setOverflowIconVisible(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    @SuppressLint("PrivateApi")
                    Method m = menu.getClass()
                        .getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e("OverflowIcon", e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backPressed();
            return true;
        }
        return false;

    }

    public void backPressed() {
        finish();
    }


    public void initBackButton(Toolbar bar, boolean isHas) {
        if (getSupportActionBar() == null) {
            setSupportActionBar(bar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(isHas);
    }

    public boolean hasInitToolBar() {
        return getSupportActionBar() != null;
    }

    public void setHasBackButton(boolean isHas) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isHas);
        }
    }

    public void hideSoftKeyboard(View v) {
        if (v == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) this
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}

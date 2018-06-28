package com.lensim.fingerchat.hexmeet.activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import java.io.File;

public class FullscreenActivity extends android.app.Activity {

    static String SDCardRoot =
        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    public String getSDCardRoot() {
        return SDCardRoot;

    }

    public AssetManager getAssetManager() {
        return getResources().getAssets();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}

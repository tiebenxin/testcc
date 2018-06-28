package com.lensim.fingerchat.hexmeet.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.lensim.fingerchat.hexmeet.App;


public class ScreenUtil {

  public static int getWidth(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  public static int getHeight() {
    return App.getContext().getResources().getDisplayMetrics().heightPixels;
  }

  public static int getHeight(Context context) {
    return context.getResources().getDisplayMetrics().heightPixels;
  }

  public static int dp_to_px(float dp) {
    return (int) (dp * App.getContext().getResources().getDisplayMetrics().density + 0.5);
  }

  public static void initStatusBar(Activity act) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window win = act.getWindow();
      WindowManager.LayoutParams winParams = win.getAttributes();
      winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      win.setAttributes(winParams);
    }

  }
}
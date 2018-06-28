package com.lensim.fingerchat.hexmeet.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Utils {


  public static String getVersion() {
    PackageManager packageManager = App.getContext().getPackageManager();
    String packageName = App.getContext().getPackageName();
    int flags = 0;
    PackageInfo packageInfo = null;
    try {
      packageInfo = packageManager.getPackageInfo(packageName, flags);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }

    return packageInfo != null ? packageInfo.versionName : "";
  }

  public static void showToast(Context context, int resourceId) {
    if (!App.isScreenLocked() && App.isForground() && !App.getHexmeetSdkInstance().hasOngoingCall()) {
      SingleToast.showToast(context, resourceId);
    }
  }

  public static void showToast(Context context, String message) {
    if (!App.isScreenLocked() && App.isForground()) {
      SingleToast.showToast(context, message);
    }
  }

  private static class SingleToast {

    private static Toast mToast;
    private static Handler cancelHandler = new Handler();
    private static Runnable cancelRunner = new Runnable() {
      @Override
      public void run() {
        mToast.cancel();
        mToast = null;
      }
    };

    public static void showToast(Context context, String text) {
      cancelHandler.removeCallbacks(cancelRunner);
      if (mToast != null) {
        mToast.setText(text);
      } else {
        mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
      }

      cancelHandler.postDelayed(cancelRunner, 3000);
      mToast.show();
    }

    public static void showToast(Context context, int resId) {
      showToast(context, context.getResources().getString(resId));
    }
  }

  public static void showToastWithCustomLayout(Context context, final String message) {
    if (!App.isScreenLocked() && App.isForground()) {
      SingleCenterToast.showToast(context, message);
    }
  }

  private static class SingleCenterToast {

    private static Toast mToast;
    private static Handler cancelHandler = new Handler();
    private static Runnable cancelRunner = new Runnable() {
      @Override
      public void run() {
        mToast.cancel();
        mToast = null;
      }
    };

    public static void showToast(Context context, String message) {
      cancelHandler.removeCallbacks(cancelRunner);
      View layout = LayoutInflater.from(context).inflate(R.layout.my_toast, null);
      TextView text = (TextView) layout.findViewById(R.id.message);
      ImageView mImageView = (ImageView) layout.findViewById(R.id.spinnerImageView);
      mImageView.setBackgroundResource(R.drawable.icon_error);
      text.setText(message);
      if (mToast == null) {
        mToast = new Toast(context);
      }
      mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
      mToast.setDuration(Toast.LENGTH_SHORT);
      mToast.setView(layout);

      cancelHandler.postDelayed(cancelRunner, 3000);
      mToast.show();
    }
  }

  private static class ToastThread extends Thread {

    private Context context;
    private Handler mHandler;
    private Looper mLooper;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public ToastThread(Context context) {
      this.context = context;
      start();
    }

    @Override
    public void run() {
      Looper.prepare();
      mLooper = Looper.myLooper();
      mHandler = new Handler(mLooper) {
        @Override
        public void handleMessage(Message message) {
          Toast.makeText(context, (String) message.obj, Toast.LENGTH_SHORT).show();
          mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              quit();
            }
          }, 5000);
        }
      };

      try {
        lock.lock();
        condition.signal();
      } finally {
        lock.unlock();
      }
      Looper.loop();
    }

    public void show(String message) {
      try {
        lock.lock();
        while (mLooper == null || mHandler == null) {
          condition.await();
        }

        Message msg = Message.obtain();
        msg.obj = message;
        mHandler.sendMessage(msg);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        lock.unlock();
      }
    }

    private void quit() {
      if (mLooper != null) {
        mLooper.quit();
      }
    }
  }

  public static void showToastInNewThread(Context context, int resId) {
    if (!App.isScreenLocked() && App.isForground()) {
      new ToastThread(context).show(context.getResources().getString(resId));
    }
  }

  public static void showToastInNewThread(Context context, String message) {
    if (!App.isScreenLocked() && App.isForground()) {
      new ToastThread(context).show(message);
    }
  }

  public static boolean isForground() {
    Context context = App.getContext();
    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
    if (appProcesses != null) {
      for (RunningAppProcessInfo appProcess : appProcesses) {
        if (appProcess.processName.equals(context.getPackageName())) {
          if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return true;
          }

          return false;
        }
      }
    }

    return false;
  }

  public static boolean copyFile(File src, File dst) {
    FileChannel inChannel = null;
    FileChannel outChannel = null;
    try {
      inChannel = new FileInputStream(src).getChannel();
      outChannel = new FileOutputStream(dst).getChannel();

      inChannel.transferTo(0, inChannel.size(), outChannel);

      return true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (inChannel != null) {
        try {
          inChannel.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (outChannel != null) {
        try {
          outChannel.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }
}

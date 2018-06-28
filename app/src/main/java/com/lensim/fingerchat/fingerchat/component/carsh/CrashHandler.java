package com.lensim.fingerchat.fingerchat.component.carsh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.lensim.fingerchat.commons.helper.AppManager;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.observer.FGObserver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * 全局捕获异常
 * Create by zm on 2018/5/11
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    /**
     * 系统默认UncaughtExceptionHandler
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * context
     */
    private Context mContext;

    /**
     * 存储异常和参数信息
     */
    private Map<String, String> paramsMap = new HashMap<>();

    /**
     * 格式化时间
     */
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private String TAG = this.getClass().getSimpleName();

    private static CrashHandler mInstance;

    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例
     */
    public static synchronized CrashHandler getInstance() {
        if (null == mInstance) {
            mInstance = new CrashHandler();
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * uncaughtException 回调函数
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            AppManager.getAppManager().AppExit(mContext);
        }
    }

    /**
     * 收集错误信息.发送到服务器
     *
     * @return 处理了该异常返回true, 否则false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        collectDeviceInfo(mContext);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序开小差了呢..", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        sendCrashInfo(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {

        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String appName = ctx.getResources().getString(pi.applicationInfo.labelRes);
                String appPackageName = pi.packageName;
                String versionName = pi.versionName;
                String deviceVersion = String.valueOf(Build.VERSION.RELEASE);
                @SuppressLint({"NewApi", "LocalSuppress", "MissingPermission"})
                String deviceToken = ((TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE)).getImei();

                paramsMap.put("appName", appName);
                paramsMap.put("appVersion", versionName);
                paramsMap.put("appPackageName", appPackageName);
                paramsMap.put("deviceToken", deviceToken);
                paramsMap.put("deviceVersion", deviceVersion);
                paramsMap.put("deviceType", "android");

            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        addCustomInfo();
    }

    /**
     * 添加自定义参数
     */
    private void addCustomInfo() {
        paramsMap.put("channel", "");
    }

    /**
     * 将错误信息发送到服务器
     *
     * @param ex
     */
    private void sendCrashInfo(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        paramsMap.put("content", result);
        try {
            String time = format.format(new Date());
            paramsMap.put("crashDate", time);
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }

        // 发送至服务器
        Http.uploadLogger(paramsMap)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseBody>() {
                @Override
                public void onHandleSuccess(ResponseBody responseBody) {
                    // 不做处理
                }
            });
    }
}

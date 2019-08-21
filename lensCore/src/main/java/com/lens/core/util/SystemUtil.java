package com.lens.core.util;


import com.lens.core.BuildConfig;

/**
 * Created by zm on 2017/3/8.
 */

public class SystemUtil {

    /**
     * 是否支持log输出
     */
    public static boolean isDeveloperLog = false;

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static boolean isCanLog() {
        return BuildConfig.DEBUG || isDeveloperLog;
    }

    public static void updateDeveloperLogControl(boolean b) {
        isDeveloperLog = b;
    }
}

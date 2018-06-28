package com.lensim.fingerchat.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtil {
    /**
     * dp转px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px to dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     *
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Activity activity) {
        int fullWidth = getDisplayMetrics(activity).widthPixels;
        return fullWidth;
    }

    public static int getScreenHeight(Activity activity) {
        int heightPixels = getDisplayMetrics(activity).heightPixels;
        return heightPixels;
    }

    static DisplayMetrics sDisplay = null;

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        if (sDisplay == null) {
            sDisplay = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(sDisplay);
        }
        return sDisplay;
    }

}

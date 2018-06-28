package com.lensim.fingerchat.fingerchat.ui.me.utils;

import android.graphics.Bitmap;
import com.lensim.fingerchat.fingerchat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;


public class BitmapUtil {

    public static DisplayImageOptions getAvatarOptions() {
        return new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageOnFail(R.drawable.ease_default_avatar)
            .showImageOnLoading(R.drawable.ease_default_avatar)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .delayBeforeLoading(100)//载入图片前稍做延时可以提高整体滑动的流畅度
            .build();
    }

    public static DisplayImageOptions getImageOptions() {
        return new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageOnFail(R.drawable.ease_default_image)
            .showImageOnLoading(R.drawable.ease_default_image)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    }

}

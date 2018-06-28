package com.lensim.fingerchat.fingerchat.cache;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by LY309313 on 2016/12/3.
 */

public class GlideCacheModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // TODO: 2016/12/3 缓存设置
        //builder.setDiskCache(new )
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}

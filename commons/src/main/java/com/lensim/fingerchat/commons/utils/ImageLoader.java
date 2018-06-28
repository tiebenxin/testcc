package com.lensim.fingerchat.commons.utils;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;

/**
 * Created by LL130386 on 2018/5/24.
 */

public class ImageLoader {
    public static void loadAvatarPrivate(String url, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(R.drawable.default_avatar)
            .placeholder(R.drawable.default_avatar)
            .centerCrop()
            .into(imageView);
    }

    public static void loadImage(String url, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(R.drawable.default_image)
            .placeholder(R.drawable.default_image)
            .centerCrop()
            .into(imageView);
    }
}

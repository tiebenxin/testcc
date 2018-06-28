package com.lens.chatmodel.helper;

import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lens.chatmodel.R;
import com.lens.chatmodel.view.CustomShapeTransformation;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/2/6.
 * 图片帮助类
 */

public class ImageHelper {


    public static List<Integer> getImageSize(String size) {
        List<Integer> list = null;
        if (!TextUtils.isEmpty(size) && size.contains("x")) {
            String[] arr = size.split("x");
            if (arr.length == 2) {
                list = new ArrayList<>();
                if (!TextUtils.isEmpty(arr[0])) {
                    list.add(Integer.parseInt(arr[0]));
                }
                if (!TextUtils.isEmpty(arr[1])) {
                    list.add(Integer.parseInt(arr[1]));
                }
            }
        }
        return list;
    }

    //获取复写imagesize
    public static List<Integer> getOverrideImageSize(String size) {
        List<Integer> temp = getImageSize(size);
        List<Integer> result = new ArrayList<>();
        if (temp != null && temp.size() == 2) {
            int width = temp.get(0);
            int height = temp.get(1);
            double rat = width * 1.0 / height;

            if (width > height) {
                width = (int) (TDevice.getScreenWidth() / 4);
                height = (int) (width / rat);

            } else if (height > width) {
                height = (int) (TDevice.getScreenHeight() / 4);
                width = (int) (height * rat);
            } else {
                width = height = (int) (TDevice.getScreenWidth() / 4);
            }
            result.add(width);
            result.add(height);
            return result;
        }
        return null;
    }

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

    public static void loadDrawableImage(int drawable, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(drawable)
            .error(R.drawable.default_avatar)
            .placeholder(R.drawable.default_avatar)
            .centerCrop()
            .into(imageView);
    }

    public static void loadAvatarPrivate(String url, ImageView imageView, boolean isValid,
        boolean isQuit) {
        if (imageView == null) {
            return;
        }
        int drawable;
        if (isValid) {
            drawable = R.drawable.default_avatar_normal;
        } else {
            if (isQuit) {
                drawable = R.drawable.default_avatar_quited;
            } else {
                drawable = R.drawable.default_avatar;
            }
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(drawable)
            .placeholder(drawable)
            .centerCrop()
            .into(imageView);
    }

    public static void loadAvatarMuc(ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load("")
            .error(R.drawable.default_avatar_muc)
            .placeholder(R.drawable.default_avatar_muc)
            .centerCrop()
            .into(imageView);
    }

    public static void loadImageGif(String url, ImageView imageView, int w, int h) {
        if (imageView == null) {
            return;
        }

        Glide.with(ContextHelper.getContext())
            .load(url)
            .asGif()
            .error(R.drawable.ease_default_expression)
            .placeholder(R.drawable.ease_default_expression)
            .override(w, h)//复写尺寸
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(imageView);
    }

    public static void loadGif(String url, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .asGif()
            .error(R.drawable.ease_default_expression)
            .placeholder(R.drawable.ease_default_expression)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(imageView);
    }

    public static void loadImageOverrideSize(String url, ImageView imageView, int w, int h) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(R.drawable.default_image)
            .placeholder(R.drawable.default_image)
            .override(w, h)//复写尺寸
            .fitCenter()
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
            .fitCenter()
            .into(imageView);
    }

    public static void loadImageOverrideSize(int drawableId, ImageView imageView, int w, int h) {
        if (imageView == null) {
            return;
        }

        Glide.with(ContextHelper.getContext())
            .load(drawableId)
            .error(R.drawable.ease_default_expression)
            .placeholder(R.drawable.ease_default_expression)
            .override(w, h)//复写尺寸
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(imageView);
    }

    public static void loadMessageImage(String url, ImageView imageView,
        CustomShapeTransformation transformation) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(R.drawable.default_image)
            .placeholder(R.drawable.default_image)
            .centerCrop()
            .transform(transformation)//自定义裁剪
            .into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                    GlideAnimation<? super GlideDrawable> glideAnimation) {
                    if (resource != null) {
                        imageView.setImageDrawable(resource);
                    } else {
                        imageView.setImageResource(R.drawable.default_image);
                    }
                }
            });
    }

    public static void loadImageDefault(String url, int defaultDrawable, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(defaultDrawable)
            .placeholder(defaultDrawable)
            .centerCrop()
            .into(imageView);
    }


    /*
    * 加载个人中心头像，CircleImageView
    * */
    public static void loadUserImage(String url, int defaultDrawable, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(defaultDrawable)
            .placeholder(defaultDrawable)
            .centerCrop()
            .into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                    GlideAnimation<? super GlideDrawable> glideAnimation) {
                    if (resource != null) {
                        imageView.setImageDrawable(resource);
                    } else {
                        imageView.setImageResource(defaultDrawable);
                    }
                }
            });
    }


}

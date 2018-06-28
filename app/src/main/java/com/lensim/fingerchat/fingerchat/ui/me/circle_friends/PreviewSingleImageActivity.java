package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.view.photoview.PhotoViewAttacher;
import com.lens.chatmodel.view.photoview.ZoomImageView;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by LL130386 on 2018/6/28.
 * 查看单一图片
 */

public class PreviewSingleImageActivity extends BaseActivity {

    private ZoomImageView iv_image;

    public static Intent nenIntent(Context context, String url) {
        Intent intent = new Intent(context, PreviewSingleImageActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_preview_single);
        iv_image = findViewById(R.id.iv_image);

        String imageUrl = getIntent().getStringExtra("url");

        ImageHelper.loadImage(imageUrl, iv_image);

        iv_image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                PreviewSingleImageActivity.this.finish();
            }

            @Override
            public void onOutsidePhotoTap() {
                PreviewSingleImageActivity.this.finish();
            }
        });

    }
}

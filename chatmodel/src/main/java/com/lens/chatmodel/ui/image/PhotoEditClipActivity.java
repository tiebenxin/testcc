package com.lens.chatmodel.ui.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.edmodo.cropper.CropImageView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.FileUtil;


/**
 * Created by LY309313 on 2017/4/15.
 */

public class PhotoEditClipActivity extends FGActivity implements OnClickListener {


    CropImageView mCropImageView;
    private String filePath;
    private FGToolbar toolbar;

    //    private  final DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .cacheInMemory(false)
//            .cacheOnDisk(false)
//            .showImageOnLoading(R.drawable.ease_default_image)
//            .bitmapConfig(Bitmap.Config.ARGB_8888)
//            .build();
    @Override
    public void initView() {
        setContentView(R.layout.activity_edit_clip);
        toolbar = findViewById(R.id.viewTitleBar);
        initToolBar();
        mCropImageView = findViewById(R.id.mCropImageView);
    }

    private void initToolBar() {
        toolbar.setTitleText("图片裁剪");
        initBackButton(toolbar,true);
        toolbar.setConfirmBt(v -> confirm());
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        filePath = intent.getStringExtra("clip_file_path");
        Glide.with(ContextHelper.getContext())
            .load(filePath)
            .asBitmap()
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource,
                    GlideAnimation<? super Bitmap> glideAnimation) {
                    mCropImageView.setImageBitmap(resource);
                }
            });
    }


    protected void confirm() {
        Bitmap croppedImage = mCropImageView.getCroppedImage();

        if (croppedImage != null) {
            showProgress(ContextHelper.getString(R.string.saving), false);
            String filepath = FileUtil.saveToPicDir(croppedImage, filePath);
            dismissProgress();
            Intent intent = new Intent();
            intent.putExtra("clip_result", filepath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    public void onClick(View v) {

    }
}

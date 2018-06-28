package com.lensim.fingerchat.fingerchat.ui.me.collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.fingerchat.R;


public class DisplayImageActivity extends FragmentActivity {

    public final static String PARAMS_CONTENT = "content";
    private String mImgUrl = null;

    public static void openActivity(Activity context, String url) {
        Intent intent = new Intent(context, DisplayImageActivity.class);
        intent.putExtra(PARAMS_CONTENT, url);
        context.startActivity(intent);
    }

    private uk.co.senab.photoview.PhotoView photoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_photoview_layout);
        this.photoView = findViewById(R.id.animation);

        Bundle extras = null;
        try {
            extras = getIntent().getExtras();
        } catch (Exception e) {
            finish();
            return;
        }
        if (extras == null) {
            finish();
            return;
        }
        try {
            mImgUrl = getIntent().getStringExtra(PARAMS_CONTENT);
        } catch (Exception e) {
            finish();
            return;
        }
        init();
    }

    public void init() {
        photoView.setOnViewTapListener((view, x, y) -> onBackPressed());
        if (!StringUtils.isEmpty(mImgUrl)) {
            Glide.with(DisplayImageActivity.this).load(mImgUrl).into(photoView);
        }
    }
}

package com.lens.chatmodel.ui.video;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.utils.AnimationUtility;

/**
 * Created by LY309313 on 2016/11/9.
 */

public class LookUpVideoActivity extends BaseActivity {


    private BaseFragment fragment;
    private ColorDrawable backgroundColor;
    private View background;

    @Override
    public void initView() {
        setContentView(R.layout.activity_videos);
        background = AnimationUtility.getAppContentView(this);
    }

    public static Intent newIntent(Context context, AnimationRect rect, String uri, String type) {
        Intent intent = new Intent(context, LookUpVideoActivity.class);
        intent.putExtra("rect", rect);
        intent.putExtra("path", uri);
        intent.putExtra("type", type);
        return intent;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        showBackgroundImmediately();
        Intent intent = getIntent();
        AnimationRect rect = intent.getParcelableExtra("rect");
        String uri = intent.getStringExtra("path");
        String type = intent.getStringExtra("type");
        boolean isSilent = intent.getBooleanExtra("isSilent", false);
        if (savedInstanceState == null) {
            if (!TextUtils.isEmpty(type) && type.equals("gallery")) {
                fragment = FragmentVideoPlay.newInstance(uri, rect, isSilent, type);
            } else {
                fragment = LookupVideoFragment.newInstance(uri, rect, isSilent, type);


            }
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mVideosContainer, fragment)
                .commit();
        }
    }

    public void showBackgroundImmediately() {
        if (background.getBackground() == null) {
            backgroundColor = new ColorDrawable(Color.BLACK);
            background.setBackgroundDrawable(backgroundColor);
        }
    }


    @Override
    public void onBackPressed() {

        if (fragment != null) {
            backgroundColor = new ColorDrawable(Color.BLACK);
            ObjectAnimator bgAnim = ObjectAnimator.ofInt(backgroundColor, "alpha", 0);
            bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    background.setBackgroundDrawable(backgroundColor);
                }
            });
            bgAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    LookUpVideoActivity.super.finish();
                    overridePendingTransition(-1, -1);
                }
            });
            if (fragment instanceof LookupVideoFragment) {
                ((LookupVideoFragment) fragment).animationExit(bgAnim);
            } else if (fragment instanceof FragmentVideoPlay) {
                LookUpVideoActivity.super.finish();
            }
        } else {
            super.onBackPressed();
        }
    }

}

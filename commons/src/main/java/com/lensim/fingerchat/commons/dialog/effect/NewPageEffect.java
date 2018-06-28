package com.lensim.fingerchat.commons.dialog.effect;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by LY309313 on 2016/11/10.
 */

public class NewPageEffect extends BaseEffects {


    @Override
    protected void setupAnimation(View view) {

        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "rotation", 720,360,0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration*3/2),
                ObjectAnimator.ofFloat(view, "scaleX", 0.1f, 0.5f, 1).setDuration(mDuration),
                ObjectAnimator.ofFloat(view,"scaleY",0.1f,0.5f,1).setDuration(mDuration)

        );
    }
}

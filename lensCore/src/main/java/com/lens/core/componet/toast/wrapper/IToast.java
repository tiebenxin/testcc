package com.lens.core.componet.toast.wrapper;

import android.view.View;

/**
 *  Created by zm on 2018/3/2.
 */

public interface IToast {

    IToast setGravity(int gravity, int xOffset, int yOffset);

    IToast setDuration(int duration);

    IToast setView(View view);

    IToast setMargin(float horizontalMargin, float verticalMargin);

    IToast setText(String text);

    void show();

    void cancel();

}

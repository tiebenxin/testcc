package com.lens.core.componet.image;

import android.graphics.Bitmap;

/**
 * Created by zm on 2018/4/20.
 */

public interface LoaderListener {

    void onSuccess(Bitmap bitmap);

    void onError();
}

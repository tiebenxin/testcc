package com.lens.core;

import android.content.Context;

/**
 * Created by zm on 2018/3/2.
 */
public class LensCore {
    public static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }
}

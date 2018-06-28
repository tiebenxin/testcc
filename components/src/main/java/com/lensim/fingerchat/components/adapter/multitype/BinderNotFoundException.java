package com.lensim.fingerchat.components.adapter.multitype;

import android.support.annotation.NonNull;

/**
 * author ll147996
 * date 2017/12/18
 * describe
 */


class BinderNotFoundException extends RuntimeException {

    BinderNotFoundException(@NonNull Class<?> clazz) {
        super("Do you have registered the binder for {className}.class in the adapter/pool?"
            .replace("{className}", clazz.getSimpleName()));
    }
}

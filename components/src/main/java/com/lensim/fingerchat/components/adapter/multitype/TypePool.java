package com.lensim.fingerchat.components.adapter.multitype;

import android.support.annotation.NonNull;

/**
 * author ll147996
 * date 2017/12/18
 * describe
 */

public interface TypePool {

    <T> void register(
            @NonNull Class<? extends T> clazz,
            @NonNull ItemViewBinder<T, ?> binder);


    boolean unregister(@NonNull Class<?> clazz);

    boolean checkType(@NonNull Class<?> clazz);


    int size();


    int firstIndexOf(@NonNull Class<?> clazz);


    @NonNull
    Class<?> getClass(int index);


    @NonNull
    ItemViewBinder<?, ?> getItemViewBinder(int index);
}

/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lensim.fingerchat.components.adapter.multitype;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * author ll147996
 * date 2017/12/18
 * describe
 */

public abstract class ItemViewBinder<T, VH extends ViewHolder> {

    /* internal */
    MultiTypeAdapter adapter;


    protected abstract @NonNull
    VH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);


    protected abstract void onBindViewHolder(@NonNull VH holder, @NonNull T item);




    protected final int getPosition(@NonNull final ViewHolder holder) {
        return holder.getAdapterPosition();
    }


    protected final @NonNull
    MultiTypeAdapter getAdapter() {
        if (adapter == null) {
            throw new IllegalStateException("ItemViewBinder " + this + " not attached to MultiTypeAdapter. " +
                "You should not call the method before registering the binder.");
        }
        return adapter;
    }



    protected long getItemId(@NonNull T item) {
        return RecyclerView.NO_ID;
    }


    protected void onViewRecycled(@NonNull VH holder) {}



    protected boolean onFailedToRecycleView(@NonNull VH holder) {
        return false;
    }



    protected void onViewAttachedToWindow(@NonNull VH holder) {}



    protected void onViewDetachedFromWindow(@NonNull VH holder) {}
}

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;


public class MultiTypeAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "MultiTypeAdapter";

    private @NonNull
    List<?> items;
    private @NonNull
    TypePool typePool;


    public MultiTypeAdapter() {
        this(Collections.emptyList());
    }



    public MultiTypeAdapter(@NonNull List<?> items) {
        this(items, new MultiTypePool());
    }



    public MultiTypeAdapter(@NonNull List<?> items, int initialCapacity) {
        this(items, new MultiTypePool(initialCapacity));
    }



    public MultiTypeAdapter(@NonNull List<?> items, @NonNull TypePool pool) {
        this.items = items;
        this.typePool = pool;
    }



    public <T> void register(@NonNull Class<? extends T> clazz, @NonNull ItemViewBinder<T, ?> binder) {
//        checkAndRemoveAllTypesIfNeeded(clazz);
        checkType(clazz);
        typePool.register(clazz, binder);
        binder.adapter = this;
    }

    public void setItems(@NonNull List<?> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    public @NonNull
    List<?> getItems() {
        return items;
    }



    @Override
    public final int getItemViewType(int position) {
        Object item = items.get(position);
        return indexInTypesOf(item);
    }


    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int indexViewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewBinder<?, ?> binder = typePool.getItemViewBinder(indexViewType);
        return binder.onCreateViewHolder(inflater, parent);
    }



    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(ViewHolder holder, int position) {
        Object item = items.get(position);
        ItemViewBinder binder = typePool.getItemViewBinder(holder.getItemViewType());
        binder.onBindViewHolder(holder, item);
    }



    @Override
    public final int getItemCount() {
        return items.size();
    }



    @Override
    @SuppressWarnings("unchecked")
    public final long getItemId(int position) {
        Object item = items.get(position);
        int itemViewType = getItemViewType(position);
        ItemViewBinder binder = typePool.getItemViewBinder(itemViewType);
        return binder.getItemId(item);
    }


    @Override
    @SuppressWarnings("unchecked")
    public final void onViewRecycled(@NonNull ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewRecycled(holder);
    }



    @Override
    @SuppressWarnings("unchecked")
    public final boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        return getRawBinderByViewHolder(holder).onFailedToRecycleView(holder);
    }



    @Override
    @SuppressWarnings("unchecked")
    public final void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewAttachedToWindow(holder);
    }



    @Override
    @SuppressWarnings("unchecked")
    public final void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewDetachedFromWindow(holder);
    }


    private @NonNull
    ItemViewBinder getRawBinderByViewHolder(@NonNull ViewHolder holder) {
        return typePool.getItemViewBinder(holder.getItemViewType());
    }


    private int indexInTypesOf(@NonNull Object item) {
        int index = typePool.firstIndexOf(item.getClass());
        if (index != -1) {
            return index;
        }
        throw new BinderNotFoundException(item.getClass());
    }


    private void checkAndRemoveAllTypesIfNeeded(@NonNull Class<?> clazz) {
        if (typePool.unregister(clazz)) {
            Log.w(TAG, "You have registered the " + clazz.getSimpleName() + " type. " +
                    "It will override the original binder(s).");
        }
    }

    private void checkType(@NonNull Class<?> clazz) {
        if (typePool.checkType(clazz)) {
            throw new TypesNotSameException(clazz);
        }
    }

}

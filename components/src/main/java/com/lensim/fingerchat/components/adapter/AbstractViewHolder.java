package com.lensim.fingerchat.components.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by LL130386 on 2017/11/30.
 */

public abstract class AbstractViewHolder<T> extends RecyclerView.ViewHolder {

  public AbstractViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void bindData(T bean);

}

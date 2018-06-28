package com.lensim.fingerchat.fingerchat.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lensim.fingerchat.fingerchat.component.recycleview.RecycleViewItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zm on 2018/6/4.
 */
public abstract class BaseRecycleViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected static RecycleViewItemListener itemListener;
    protected List<T> datas = new ArrayList<>();

    public List<T> getDatas() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        return datas;
    }

    public void setDatas(@NonNull List<T> datas) {
        setDatas(datas, true);
    }

    /**
     * 更新数据
     *
     * @param datas
     * @param isNotify 是否更新界面
     */
    public void setDatas(@NonNull List<T> datas, boolean isNotify) {
        this.datas = datas;
        if (isNotify) {
            notifyDataSetChanged();
        }
    }

    public void setItemListener(RecycleViewItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public static class BaseRecycleViewHolder extends RecyclerView.ViewHolder {

        public BaseRecycleViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                if (itemListener != null) {
                    itemListener.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (itemListener != null) {
                    return itemListener.onItemLongClick(getAdapterPosition());
                }
                return false;
            });
        }
    }
}

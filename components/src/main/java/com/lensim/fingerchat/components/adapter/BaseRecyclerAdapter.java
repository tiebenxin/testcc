package com.lensim.fingerchat.components.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ll147996 on 2017/12/8.
 *
 */
public abstract class BaseRecyclerAdapter<E extends BaseRecyclerAdapter.VH, T> extends RecyclerView.Adapter<E> {

    protected Context mContext;

    protected List<T> items;

    private OnItemClickListener mListener;

    public BaseRecyclerAdapter(Context ctx) {
        mContext = ctx;
    }

    //RecyclerView.ViewHolder
    @Override
    public void onBindViewHolder(final E holder, final int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        //在使用RecyclerView进行开发时，使用notifyItemRemoved()方法删除条目
                        // 和notifyItemMoved()方法移动条目，遇到删除和移动的条目出现紊乱。
                        //如果需要监听条目点击的事件，使用ViewHolder的getLayoutPosition()
                        // （getPosition()被抛弃）方法才能获得更新后的条目位置
                        mListener.onItemClick(holder, holder.getLayoutPosition());
                    }
                }
            });
        }

    }

    /**
     * ViewHolder
     */
    public static class VH extends RecyclerView.ViewHolder {

        // 是否绑定Listener
        protected boolean isBindListener = false;

        public VH(View itemView) {
            super(itemView);
        }
    }


    protected LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    public void addItems(List<T> newItems) {
        if (null == items) {
            items = new ArrayList<>();
        }
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setItems(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * 设置Item点击事件
     *
     * @param l
     */
    public void setOnItemClickListener(OnItemClickListener l) {
        mListener = l;
    }


    /**
     * OnItemClickListener
     */
    public interface OnItemClickListener {

        void onItemClick(RecyclerView.ViewHolder view, int position);
    }

}

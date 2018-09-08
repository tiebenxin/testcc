package com.lensim.fingerchat.fingerchat.ui.work_center;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.components.adapter.multitype.ItemViewBinder;
import com.lensim.fingerchat.data.work_center.WorkItem;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ItemWorkCenterItemBinding;


/**
 * date on 2017/12/22
 * author ll147996
 * describe
 */

public class WorkCenterItemAdapter extends ItemViewBinder<WorkItem, WorkCenterItemAdapter.HV> {

    private OnItemClickListener listener;

    private OnItemTouchListener touchListener;
    private Context mContext;
    private int textSize;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTouchListener(OnItemTouchListener touchListener) {
        this.touchListener = touchListener;
    }


    public WorkCenterItemAdapter(Context context) {
        mContext = context;
        if (textSize <= 0) {
            int factor = SPHelper.getInt("font_size", 1);
            if (factor > 4) {
                factor = 4;
            }
            textSize = factor * 2 + 12;
        }
    }

    @NonNull
    @Override
    protected HV onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ItemWorkCenterItemBinding binding =
            DataBindingUtil.inflate(inflater, R.layout.item_work_center_item, parent, false);
        return new HV(binding.getRoot());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onBindViewHolder(@NonNull HV holder, @NonNull WorkItem item) {
        ItemWorkCenterItemBinding binding = DataBindingUtil.getBinding(holder.itemView);
        binding.itemTitle.setTextSize(textSize);
        binding.itemTitle.setText(item.getFuncName());
        String url = String.format(Route.WORK_ITEM_IMG, item.getFuncLogo());
        Glide.with(mContext).load(url).placeholder(R.drawable.add_to).into(binding.itemPic);
        binding.itemSl.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v, item);
            }
        });

        binding.itemSl.setOnTouchListener((v, event) -> {
            if (touchListener != null) {
                touchListener.onTouch(v, event);
            }
            return false;
        });
    }

    static class HV extends RecyclerView.ViewHolder {

        HV(View itemView) {
            super(itemView);
        }
    }

    interface OnItemClickListener {

        void onClick(View view, WorkItem item);
    }

    interface OnItemTouchListener {

        void onTouch(View view, MotionEvent event);
    }
}

package com.lensim.fingerchat.fingerchat.ui.work_center;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lensim.fingerchat.components.adapter.multitype.ItemViewBinder;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ItemWorkCenterClassBinding;

/**
 * date on 2017/12/22
 * author ll147996
 * describe
 */

public class WorkCenterClassAdapter extends ItemViewBinder<String, WorkCenterClassAdapter.HV> {

    private Context mContext;
    public WorkCenterClassAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    protected HV onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ItemWorkCenterClassBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.item_work_center_class, parent, false);
        return new HV(binding.getRoot());
    }

    @Override
    protected void onBindViewHolder(@NonNull HV holder, @NonNull String item) {
        ItemWorkCenterClassBinding binding = DataBindingUtil.getBinding(holder.itemView);
        binding.workCenterTitle.setText(item);
    }

    static class HV extends RecyclerView.ViewHolder {
         HV(View itemView) {
            super(itemView);
         }
    }
}

package com.lens.chatmodel.ui.multi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.transfor.BaseTransforEntity;
import com.lens.chatmodel.bean.transfor.MultiMessageEntity;
import com.lens.chatmodel.controller.multi.FactoryMultiCell;
import com.lens.chatmodel.controller.multi.MultiCellBase;
import java.util.ArrayList;

/**
 * Created by LL130386 on 2017/12/9.
 * 查看合并转发消息求详情列表
 */

public class AdapterMultiList extends AbstractRecyclerAdapter {


    private ArrayList<BaseTransforEntity> list;
    private FactoryMultiCell viewFactory;
    private String preUser;

   public AdapterMultiList(Context ctx) {
        super(ctx);
        list = new ArrayList<>();
    }

    public void setData(ArrayList<BaseTransforEntity> l) {
        if (l != null && !l.isEmpty()) {
            list.addAll(l);
        }
    }

    public void setEntity(MultiMessageEntity entity) {
        if (entity != null && entity.getBody() != null) {
            list.addAll(entity.getBody());
        }
    }

    public void setViewFactory(FactoryMultiCell f) {
        viewFactory = f;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EMultiCellLayout cellLayout = EMultiCellLayout.fromOrdinal(viewType);
        MultiCellBase baseCell = viewFactory.createController(cellLayout);
        return new RecyclerViewHolder(baseCell.getView());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultiCellBase baseCell = (MultiCellBase) holder.itemView.getTag();
        BaseTransforEntity entity = list.get(position);
        if (preUser != null && preUser.equalsIgnoreCase(entity.getSenderUserid())) {
            baseCell.isShowAvatar(false);
        } else {
            baseCell.isShowAvatar(true);

        }
        baseCell.setModel(entity);
        preUser = entity.getSenderUserid();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getCellLayoutId().ordinal();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }
}

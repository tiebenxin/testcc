package com.lens.chatmodel.ui.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.SearchMessageBean;
import java.util.List;

/**
 * Created by LY309313 on 2017/2/23.
 */

public class MoreRecordAdapter extends RecyclerView.Adapter {


    private List<SearchMessageBean> beanList;
    private LayoutInflater inflater;
    private EResultType searchType;
    private String condition;
    private ISearchEventListener listener;

    public MoreRecordAdapter(Context context, List<SearchMessageBean> beanList) {
        inflater = LayoutInflater.from(context);
        this.beanList = beanList;
        this.searchType = EResultType.RECORD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_record_cell, parent, false);
        return new ControllerMoreRecordItem(view, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ControllerMoreRecordItem controller = (ControllerMoreRecordItem) holder;
        SearchMessageBean bean = beanList.get(position);
        controller.setData(bean, condition, searchType);
//        controller.setListener(listener);

    }


    @Override
    public int getItemCount() {
        return beanList.size();
    }

    public void setResult(AllResult result) {
        this.beanList = result.getResults();
        notifyDataSetChanged();
    }

    public void clear() {
        beanList.clear();
        notifyDataSetChanged();
    }

    public void setType(EResultType searchType) {
        this.searchType = searchType;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setClickListener(ISearchEventListener l) {
        listener = l;
    }


}

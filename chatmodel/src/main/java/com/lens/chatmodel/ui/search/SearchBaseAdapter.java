package com.lens.chatmodel.ui.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.AllResult;
import com.lensim.fingerchat.commons.utils.L;
import java.util.List;

/**
 * Created by LY309313 on 2017/2/23.
 */

public class SearchBaseAdapter extends RecyclerView.Adapter {

    private List<AllResult> results;
    private Context context;
    private LayoutInflater inflater;
    private String condition;
    private ISearchEventListener listener;

    public SearchBaseAdapter(Context context, List<AllResult> results) {
        this.context = context;
        this.results = results;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search_record, parent, false);
        return new ControllerBaseSearch(view, inflater, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        L.i("SearchResultAdapter", "位置有没有刷新:" + position);
        ControllerBaseSearch controller = (ControllerBaseSearch) holder;
        AllResult allResult = results.get(position);
        controller.setData(allResult, condition);

    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public int getItemCount() {
        L.i("SearchResultAdapter", "显示的数量是多少:" + results.size());
        return results.size();
    }

    public void setResults(List<AllResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void setClickListener(ISearchEventListener l) {
        listener = l;
    }

}

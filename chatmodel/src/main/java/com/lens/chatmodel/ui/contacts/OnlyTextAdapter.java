package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.utils.UIHelper;
import java.util.List;

/**
 * Created by LY309313 on 2017/2/27.
 */

public class OnlyTextAdapter extends RecyclerView.Adapter {
    private Context context;

    private List<String> names;

    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    public OnlyTextAdapter(Context context ,List<String> names){
        this.context = context;
        this.names = names;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_only_text_item,parent,false);
       final TextViewHolder holder =  new TextViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content =  holder.content.getText().toString();
                if(onItemClickListener!=null){
                    onItemClickListener.onclick(content);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UIHelper.setTextSize2(14,((TextViewHolder) holder).content);
        ((TextViewHolder) holder).content.setText(names.get(position));
       // ((TextViewHolder) holder).content.setTag(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public void setData(List<String> names) {
        this.names = names;
        notifyDataSetChanged();
    }

    private class TextViewHolder extends RecyclerView.ViewHolder{


        private final TextView content;

        public TextViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);

        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onclick(String content);
    }
}

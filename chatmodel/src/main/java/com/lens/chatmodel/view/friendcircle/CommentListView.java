package com.lens.chatmodel.view.friendcircle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.lens.chatmodel.view.spannable.SpannableClickable;
import java.util.ArrayList;
import java.util.List;

/**
 * date on 2018/2/2
 * author ll147996
 * describe
 */

public class CommentListView extends LinearLayout {

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public CommentListView(Context context) {
        super(context);
    }

    public CommentListView(Context context, AttributeSet attrs){
        super(context, attrs);

    }

    public CommentListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }


    public void setAdapter(Adapter adapter){
        adapter.bindListView(this);
    }

    public void setOnItemClick(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClick(OnItemLongClickListener listener){
        mOnItemLongClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return mOnItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener(){
        return mOnItemLongClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }


    public abstract static class Adapter<T> {

        protected Context mContext;

        public CommentListView getListview() {
            return mListview;
        }

        private CommentListView mListview;
        protected List<T> mDatas;

        public Adapter(Context context){
            mContext = context;
            mDatas = new ArrayList<>();
        }

        public void bindListView(CommentListView listView){
            if(listView == null){
                throw new IllegalArgumentException("CommentListView is null....");
            }
            mListview = listView;
        }

        public void setItems(List<T> datas){
            if(datas == null ){
                datas = new ArrayList<>();
            }
            mDatas = datas;
        }

        public List<T> getItems(){
            return mDatas;
        }

        public int getCount(){
            if(mDatas == null){
                return 0;
            }
            return mDatas.size();
        }

        public void removeOne(int position){
            mDatas.remove(position);
            notifyDataSetChanged();
        }

        public T getItem(int position){
            if(mDatas == null){
                return null;
            }
            if(position < mDatas.size()){
                return mDatas.get(position);
            }else{
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        public abstract View getView(final int position);

        public void setListener(OnSpannableClickListener listener) {
            this.listener = listener;
        }

        private OnSpannableClickListener listener;

        public interface OnSpannableClickListener {
            void onClick(View view, String id);
        }


        @NonNull
        protected SpannableString setClickableSpan(final String textStr, final String id) {
            SpannableString subjectSpanText = new SpannableString(textStr);
            subjectSpanText.setSpan(new SpannableClickable(){
                                        @Override
                                        public void onClick(View widget) {
                                            // 此处应该是跳转至相册的操作
                                            if (listener != null) {
                                                listener.onClick(widget, id);
                                            }
                                        }
                                    }, 0, subjectSpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return subjectSpanText;
        }

        public void notifyDataSetChanged(){
            if(mListview == null){
                throw new NullPointerException("listview is null, please bindListView first...");
            }
            mListview.removeAllViews();
            if(mDatas == null || mDatas.size() == 0){
                return;
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for(int i=0; i<mDatas.size(); i++){
                View view = getView(i);
                mListview.addView(view, i, layoutParams);
            }
        }
    }
}

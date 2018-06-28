package com.lens.chatmodel.view.friendcircle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.lens.chatmodel.R;
import com.lens.chatmodel.view.spannable.CircleMovementMethod;
import com.lens.chatmodel.view.spannable.ISpanClick;
import com.lens.chatmodel.view.spannable.MyImageSpan;
import com.lens.chatmodel.view.spannable.SpannableClickable;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import java.util.List;


/**
 * date on 2018/2/2
 * author ll147996
 * describe
 */

public class FavortListView extends android.support.v7.widget.AppCompatTextView {

    public FavortListView(Context context) {
        super(context);
    }

    public FavortListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavortListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(Adapter adapter){
        adapter.bindListView(this);
    }


    public static class Adapter {

        private int imageResourceId = R.drawable.likeicon;
        private FavortListView mListView;
        private List<String> items;
        private ISpanClick mSpanClickListener;

        public void setSpanClickListener(ISpanClick listener){
            mSpanClickListener = listener;
        }

        public List<String> getitems() {
            return items;
        }

        public void setitems(List<String> items) {
            this.items = items;
        }


        public void bindListView(@NonNull FavortListView listview){
            mListView = listview;
        }

        public void notifyDataSetChanged(){
            if(mListView == null){
                throw new NullPointerException("listview is null, please bindListView first...");
            }
            SpannableStringBuilder builder = new SpannableStringBuilder();
            if(items != null && items.size() > 0){
                //添加点赞图标
                builder.append(setImageSpan());
                //builder.append("  ");
                for (int i=0; i<items.size(); i++){
                    String item = items.get(i);
                    builder.append(setClickableSpan(CyptoConvertUtils.decryptString(item), i));
                    if(i != items.size()-1){
                        builder.append(", ");
                    }
                }
            }
            mListView.setText(builder);
            mListView.setMovementMethod(new CircleMovementMethod(R.color.name_selector_color));
            UIHelper.setTextSize(14, mListView);
        }


        @NonNull
        private SpannableString setClickableSpan(String textStr, final int position) {
            SpannableString subjectSpanText = new SpannableString(textStr);
            subjectSpanText.setSpan(new SpannableClickable(){
                                        @Override
                                        public void onClick(View widget) {
                                            if(mSpanClickListener!=null){
                                                mSpanClickListener.onClick(position);
                                            }
                                        }
                                    }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return subjectSpanText;
        }

        private SpannableString setImageSpan(){
            String text = "  ";
            SpannableString imgSpanText = new SpannableString(text);
            imgSpanText.setSpan(new MyImageSpan(ContextHelper.getApplication(), getImageResourceId(),
                    DynamicDrawableSpan.ALIGN_BASELINE), 0 , 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return imgSpanText;
        }

        public void setImageResourceId(int resourceId) {
            imageResourceId = resourceId;
        }

        public int getImageResourceId() {
            return imageResourceId;
        }
    }

}

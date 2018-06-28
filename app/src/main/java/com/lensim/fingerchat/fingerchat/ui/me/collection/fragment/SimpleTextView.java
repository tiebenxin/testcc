package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.collection.SearchNoteActivity;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;

/**
 * date on 2018/3/14
 * author ll147996
 * describe
 */

public class SimpleTextView implements AbsContentView {

    public static final String TEXT = "text";
    private String text;
    private String keyWords;
    private TextView simpleText;
    Context mContext;
    public SimpleTextView(Context ctx, Content content) {
        mContext = ctx;
        text = content.getText();
    }

//    public static SimpleTextView newInstance(String text) {
//        SimpleTextView newFragment = new SimpleTextView();
//        Bundle bundle = new Bundle();
//        bundle.putString(TEXT, text);
//        newFragment.setArguments(bundle);
//        return newFragment;
//    }
//
//
//    public void setKeyWords(String word) {
//        this.keyWords = word;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle args = getArguments();
//        if (args != null) {
//            text = args.getString(TEXT);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//        @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.viewstub_collect_text, container,false);
//        simpleText = view.findViewById(R.id.simple_text);
//        setSimpleText();
//        return view;
//    }

    private void setSimpleText() {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        int textSize = SPSaveHelper.getIntValue("font_size", 1) * 4 + 12;
        Spannable span = SpannableUtil.getAtText(SmileUtils.getSmiledText(mContext, text,
                (int) TDevice.dpToPixel(textSize + 10)));

        simpleText.setText(span);
        if (mContext instanceof SearchNoteActivity && !TextUtils.isEmpty(keyWords)) {
            span = getSpan(text, keyWords);
            if (null != span) {
                simpleText.setText(span);
            }
        }
        simpleText.setEllipsize(TextUtils.TruncateAt.END); // 收缩
        simpleText.setMaxLines(6);
    }

    private Spannable getSpan(String message, String condition) {
        SpannableString ss = new SpannableString(message);
        int start = message.indexOf(condition);
        int end = start + condition.length();
        if (start < 0 || end > message.length()) {
            return null;
        }
        ss.setSpan(new ForegroundColorSpan(Color.GREEN), start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    @Override
    public View getFrameLayoutView(LayoutInflater mInflater, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.viewstub_collect_text, parent,false);
        simpleText = view.findViewById(R.id.simple_text);
        setSimpleText();
        return view;
    }
}

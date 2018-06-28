package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.collection.note.RichTextView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;

/**
 * date on 2018/3/14
 * author ll147996
 * describe
 */

public class NoteView implements AbsContentView {

    public static final String TEXT = "text";
    private String text;
    private RichTextView simpleINote;
    Context mContext;

    public NoteView(Context ctx, Content content) {
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
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle args = getArguments();
//        if (args != null) {
//            text = args.getString(TEXT);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.viewstub_collect_note, container,false);
//        simpleINote = view.findViewById(R.id.simple_note);
//        setNote();
//        return view;
//    }

    private void setNote() {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        simpleINote.clearAllLayout();
        simpleINote.showDataSync(text);
    }

    @Override
    public View getFrameLayoutView(LayoutInflater mInflater, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.viewstub_collect_note, parent,false);
        simpleINote = view.findViewById(R.id.simple_note);
        setNote();
        return view;
    }
}

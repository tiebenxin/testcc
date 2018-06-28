package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;

/**
 * date on 2018/3/14
 * author ll147996
 * describe
 */

public class SimpleImageView implements AbsContentView {

    public static final String URL = "url";
    private ImageView simpleImage;
    private String url;
    Context mContext;

    public SimpleImageView(Context ctx, Content content) {
        mContext = ctx;
        url = content.getText();
    }

//    public static SimpleImageView newInstance(String url) {
//        SimpleImageView newFragment = new SimpleImageView();
//        Bundle bundle = new Bundle();
//        bundle.putString(URL, url);
//        newFragment.setArguments(bundle);
//        return newFragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle args = getArguments();
//        if (args != null) {
//            url = args.getString(URL);
//        }
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.viewstub_collect_image, container,false);
//        simpleImage = view.findViewById(R.id.simple_image);
//        setSimpleImage();
//        return view;
//    }

    private void setSimpleImage() {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        Glide.with(mContext).load(url).centerCrop().into(simpleImage);
    }

    @Override
    public View getFrameLayoutView(LayoutInflater mInflater, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.viewstub_collect_image, viewGroup,false);
        simpleImage = view.findViewById(R.id.simple_image);
        setSimpleImage();
        return view;
    }
}

package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.bean.AllResult;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.FragmentSearchMainBinding;
import com.lensim.fingerchat.fingerchat.ui.me.collection.CollectFrameAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by LY309313 on 2017/3/28.
 *
 */

public class SearchNoteFragment extends BaseFragment {

    private String type;
    private LinearLayoutManager mLayouManager;
    private List<AllResult> results;
    private final List<FavJson> items = new ArrayList<>();
    private String mKeyWord = "";
    FragmentSearchMainBinding ui;
    private CollectFrameAdapter adapter;



    public static SearchNoteFragment newInstance(String subject) {
        SearchNoteFragment fragment = new SearchNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("subject", subject);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        ui = DataBindingUtil.inflate(inflater, R.layout.fragment_search_main, container, false);
        return ui.getRoot();
    }


    @Override
    protected void initView() {
        ui.tvSearchText.setOnClickListener(v -> click(v.getId()));
        ui.tvSearchImg.setOnClickListener(v -> click(v.getId()));
        ui.tvSearchVideo.setOnClickListener(v -> click(v.getId()));

        results = new ArrayList<>();
        ui.mSearchNoteList.setHasFixedSize(true);
        mLayouManager = new LinearLayoutManager(getActivity());
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        ui.mSearchNoteList.setLayoutManager(mLayouManager);

        adapter = new CollectFrameAdapter(getActivity());
        ui.mSearchNoteList.setAdapter(adapter);
        ui.mSearchNoteList.setItemAnimator(new DefaultItemAnimator());
        ui.mSearchNoteList.addItemDecoration(
            new CustomDocaration(getActivity(), LinearLayoutManager.HORIZONTAL, 10, ContextCompat
                .getColor(getActivity(), R.color.bg_search_note)));
    }

    public static final String MSG_TYPE_TEXT = "1";
    public static final String MSG_TYPE_PIC = "2";
    public static final String MSG_TYPE_VIDEO = "3";
    //1.text 文字 2.image 图片 3.video 视频 4.gif 大表情
    private void click(int id) {
        switch (id) {
            case R.id.tv_search_text:
                type = MSG_TYPE_TEXT;
                break;
            case R.id.tv_search_img:
                type = MSG_TYPE_PIC;
                break;
            case R.id.tv_search_video:
                type = MSG_TYPE_VIDEO;
                break;
        }
        filterAll("", type);
    }


    public void search(Editable s) {
        if (s == null || TextUtils.isEmpty(s.toString())) {
            results.clear();
            adapter.notifyDataSetChanged();
        } else {
            filterAll(s.toString(), type);
        }
    }

    private void filterAll(final String word, final String type) {
        Observable
            .just(word).debounce(500, TimeUnit.MILLISECONDS)
            .map(new Function<String, List<FavJson>>() {
                @Override
                public List<FavJson> apply(@io.reactivex.annotations.NonNull String s)
                    throws Exception {
                    mKeyWord = s;
                    return CollectionManager.getInstance().queryContent(s, type);
                }
            }).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe(new Consumer<List<FavJson>>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<FavJson> mStoreDataList)
                throws Exception {
                items.clear();
                if (null != mStoreDataList && !mStoreDataList.isEmpty()) {
                    items.addAll(mStoreDataList);
                }
                adapter.setWord(word);
                adapter.setItems(items);
            }
        });
    }



    public void refresh() {
        filterAll(mKeyWord, type);
    }

}

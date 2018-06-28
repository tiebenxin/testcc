package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.components.helper.DivItemDecoration;
import com.lensim.fingerchat.components.helper.OnDoubleClickListener;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityLookupCommentBinding;
import com.lensim.fingerchat.fingerchat.ui.me.photo.CommentDetailActivity;
import com.lensim.fingerchat.fingerchat.ui.me.photo.InnerCommentsAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookupCommentActivity extends BaseActivity {

    public static final String CIRCLE_COMMENT = "circle_comment";
    public final static int REQUEST_CODE = 1;
    public final static int TYPE_PULLREFRESH = 991;//上拉
    public final static int TYPE_FIRST_TIME = 992;//评论列表页面第一次进入
    public final static int TYPE_LOADMORE = 993;//下拉
    public final static int TYPE_LOAD_NO_SEE = 994;//朋友圈列表页面，提示用户未查看的评论
    private final String XML_NAME = "SavedTimeStamp";
    private final String XML_KEY = "key";
    private final String TIME_FORMATER = "yyyy-MM-dd HH:mm:ss";

    private ActivityLookupCommentBinding ui;

    private Map<String, CircleItem> items = new HashMap<>();
    private InnerCommentsAdapter mAdapter;
    private int PAGE_NUM = 0;
    boolean isCommentChanged = false;


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_lookup_comment);
        initBackButton(ui.toolbar,true);
        ui.toolbar.setTitleText("查看评论");
        ui.toolbar.setConfirmBt("清除", v -> {
            mAdapter.empty();
            getBase64Now();
        });
        ui.toolbar.setOnTouchListener(
            new OnDoubleClickListener(() -> {
                ui.recyclerViewComment.scrollTo(0, 0);
                ui.recyclerViewComment.smoothScrollToPosition(0);
            }));
        initAdapter();
        ui.mCommentRefresh.setOnLoadListener(() -> loadData(TYPE_LOADMORE, PAGE_NUM, getTimeStamp()));
        ui.mCommentRefresh.setOnRefreshListener(() -> updateLoadData(TYPE_PULLREFRESH, null));
        ui.mCommentRefresh.setRefreshing(true);

    }


    private void initAdapter() {
        LinearLayoutManager mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        mAdapter = new InnerCommentsAdapter(this);
        ui.recyclerViewComment.setLayoutManager(mLayouManager);
        ui.recyclerViewComment.addItemDecoration(
            new CustomDocaration(this, LinearLayoutManager.HORIZONTAL, 1, ContextCompat
                .getColor(this, R.color.custom_divider_color)));

        ui.recyclerViewComment.setAdapter(mAdapter);
        ui.recyclerViewComment.addItemDecoration(new DivItemDecoration(2, true));
        ui.recyclerViewComment.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(this, CommentDetailActivity.class);
            NewComment newComment = mAdapter.getItem(position);
            if (TextUtils.isEmpty(newComment.getPHO_ImagePath())) {
                circleBlankDialog();
            } else {
                intent.putExtra(CommentDetailActivity.NEW_COMEMNT, newComment);
                if (items.containsKey(newComment.getPHO_Serno())) {
                    intent.putExtra(CommentDetailActivity.CIRCLE_ITEM, items.get(newComment.getPHO_Serno()));
                }
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        ui.recyclerViewComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Glide.with(getApplicationContext()).resumeRequests();
            }

            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(getApplicationContext()).pauseRequests();
                    ImageLoader.getInstance().pause();
                } else {
                    ImageLoader.getInstance().resume();
                }
            }
        });

    }



    @Override
    public void initData(Bundle savedInstanceState) {
        SPSaveHelper.setValue(UserInfoRepository.getUserName() + "circle_comment", 0);
        int type = getIntent().getIntExtra("lookcomment_type", 0);
        if (type == 2) {
            loadData(TYPE_FIRST_TIME, PAGE_NUM, getTimeStamp());
        } else {
            loadData(TYPE_LOAD_NO_SEE, 0, "0");
        }
    }


    //朋友圈已经删除
    public void circleBlankDialog() {
        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(LookupCommentActivity.this);
        builder.withTitle("提示")
            .withMessage(R.string.txt_cirlce_already_delete)
            .withButton1Text("确定")
            .setButton1Click(v -> builder.dismiss())
            .show();
    }


    @Override
    public void backPressed() {
        onReturn();
    }

    public void onReturn() {
        ArrayList<String> deletedCircleList = new ArrayList<String>();
        for (Map.Entry<String, CircleItem> entry : items.entrySet()) {
            if (null != entry.getKey()) {
                deletedCircleList.add(entry.getKey());
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("list", deletedCircleList);
        intent.putExtra("isDelete", isCommentChanged);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onReturn();
        }
        return super.onKeyDown(keyCode, event);
    }



    public void loadData(final int loadType, int iPageNum, String timeStamp) {
        switch (loadType) {
            case LookupCommentActivity.TYPE_FIRST_TIME:
                sendRequest(LookupCommentActivity.TYPE_FIRST_TIME, iPageNum, timeStamp);
                break;
            case LookupCommentActivity.TYPE_LOADMORE:
                sendRequest(LookupCommentActivity.TYPE_LOADMORE, iPageNum, timeStamp);
                break;
            case LookupCommentActivity.TYPE_LOAD_NO_SEE:
                getNewComment(LookupCommentActivity.TYPE_LOAD_NO_SEE);
                break;
            default:
                break;
        }
    }

    private final int PAGE_SIZE = 10;
    public void sendRequest(final int loadType, int iPageNum, String timeStamp) {
        Http.getCommentsByPage(UserInfoRepository.getUserName(), iPageNum + "", PAGE_SIZE + "", timeStamp.trim())
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {
                Gson gson = new Gson();
                List<NewComment> comments =
                    gson.fromJson(stringRetObjectResponse.retData, new TypeToken<List<NewComment>>() {}.getType());
                updateLoadData(loadType, comments);
                addSeeCommentTime();
            },
            throwable -> {
                disableRefresh();
                updateLoadData(loadType, null);
            });
    }


    public void getNewComment(final int loadType) {
        Http.getNewComment("getnewComment", UserInfoRepository.getUserName())
            .compose(RxSchedulers.io_main())
            .subscribe(newComments -> {
                updateLoadData(loadType, newComments);
                addSeeCommentTime();
            },
            throwable -> disableRefresh());
    }

    private void addSeeCommentTime() {
        Http.addSeeCommentTime("addSeeComment", UserInfoRepository.getUserName())
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {

                },
            throwable -> T.show(throwable.getMessage()));
    }

    /***
     **删除了评论中的朋友圈，而刷新
     * */
    public void reload() {
        isCommentChanged = true;
        if (mAdapter != null) {
            mAdapter.empty();
        }
        PAGE_NUM = 0;
        loadData(TYPE_FIRST_TIME, PAGE_NUM, getTimeStamp());
    }


    public void updateLoadData(int loadType, List<NewComment> datas) {
        disableRefresh();
        switch (loadType) {
            case TYPE_FIRST_TIME:
                if (datas != null) {
                    mAdapter.setItems(datas);
                    PAGE_NUM++;
                }
                break;
            case TYPE_PULLREFRESH:
                if (datas != null) mAdapter.addHeader(datas);
                break;
            case TYPE_LOADMORE:
                if (ui.mCommentRefresh.isLoading()) {
                    if (datas == null) {
                        ui.mCommentRefresh.setLoading(false, false);
                    } else {
                        ui.mCommentRefresh.setLoading(false, true);
                    }
                }
                if (datas != null) {
                    mAdapter.addFooter(datas);
                    PAGE_NUM++;
                }
                break;
            case TYPE_LOAD_NO_SEE:
                if (datas != null) mAdapter.setItems(datas);
                break;
            default:
                break;
        }
    }


    public void disableRefresh() {
        if (ui.mCommentRefresh.isRefreshing()) {
            ui.mCommentRefresh.setRefreshing(false);
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                CircleItem item = data.getParcelableExtra("circleitem");
                if (item != null) {
                    items.put(item.id, item);
                }
                if (data.getBooleanExtra("isDeleteCircle", false)) {
                    reload();
                }
            }
        }
    }


    private String getBase64Now() {
        Date nowTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMATER);
        String strTimeStamp = Base64.encodeToString(formatter.format(nowTime).getBytes(), Base64.DEFAULT);
        SharedPreferences preferences = getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(XML_KEY, strTimeStamp);
        editor.commit();
        return strTimeStamp;
    }


    /***
     * 默认只能查看半年内评论
     * */
    private String getBase64MonthEarlier() {
        Date nowTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMATER);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.MONTH, -6);
        return Base64.encodeToString(formatter.format(calendar.getTime()).getBytes(), Base64.DEFAULT);
    }


    private String getTimeStamp() {
        SharedPreferences preferences = getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        String time = preferences.getString(XML_KEY, "-1");
        if (time.equals("-1")) {
            time = getBase64MonthEarlier();
        }
        return time;
    }
}

package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lens.chatmodel.ui.video.CameraActivity;
import com.lens.chatmodel.view.emoji.EmotionKeyboard;
import com.lens.chatmodel.view.friendcircle.CommentListView;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseMvpActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.mvp.factory.CreatePresenter;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.NetWorkUtil;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.helper.DivItemDecoration;
import com.lensim.fingerchat.components.helper.OnDoubleClickListener;
import com.lensim.fingerchat.components.pulltorefresh.CustomProgressDrawable;
import com.lensim.fingerchat.components.widget.CustomDocaration;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.circle_friend.CommentConfig;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityCircleFriendsBinding;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.CircleFriendsAdapter;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.circle_friends_multitype.CircleViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.photo.VideoStatuActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

/**
 * Created by ll147996 on 2017/12/15.
 * 朋友圈
 */

@CreatePresenter(CircleFriendsPresenter.class)
public class CircleFriendsActivity extends BaseMvpActivity<CircleFirendsContract.View, CircleFirendsContract.Presenter>
    implements CircleFirendsContract.View {

    public final static int TYPE_OTHERREFRESH = 0;//其它的刷新
    public final static int TYPE_PULLREFRESH = 1;//下拉刷新
    public final static int TYPE_UPLOADREFRESH = 2;//更新朋友圈
    public final static int TYPE_LOADMORE = 3;//上拉加载更多
    public static final int REQUEST_NEW_STATUS = 11;

    private ActivityCircleFriendsBinding ui;
    private CircleFriendsAdapter mAdapter;
    private LinearLayoutManager mLayouManager;

    private int mCurrentKeyboardH;
    private int mScreenHeight;
    private int mEditTextBodyHeight;
    private int mSelectCircleItemH;
    private int mSelectCommentItemOffset;


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_circle_friends);
        ui.circleToolbar.setTitleText(ContextHelper.getString(R.string.circle_friend));
        initBackButton(ui.circleToolbar,true);
        initAdapter();
        initCircleInput();
        initListener();
        setViewTreeObserver();
        getMvpPresenter().setHeaderItem();
        firstRefresh();
//        AccountManager.getInstance().setHasNewCircle(false);
    }

    private void initAdapter(){
        mAdapter = new CircleFriendsAdapter(this, getMvpPresenter());
        mLayouManager = new LinearLayoutManager(this);
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        ui.recyclerView.setLayoutManager(mLayouManager);
        ui.recyclerView.addItemDecoration(new CustomDocaration(this, LinearLayoutManager.HORIZONTAL, 1,
            ContextCompat.getColor(this, R.color.custom_divider_color)));
        ui.recyclerView.setAdapter(mAdapter);
        ui.recyclerView.addItemDecoration(new DivItemDecoration(2, true));
    }

    private void initCircleInput() {
        EmotionKeyboard emotionKeyboard = EmotionKeyboard.with(this).bindToContent(ui.bodyLayout);
        ui.circleInput.init(emotionKeyboard, null);
        ui.circleInput.hideExtendMenuContainer();
        ui.circleInput.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        ui.circleToolbar.setOnTouchListener(new OnDoubleClickListener(() -> {
            ui.recyclerView.scrollTo(0, 0);
            ui.recyclerView.smoothScrollToPosition(0);
        }));

        ui.circleToolbar.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.performClick();
            }
            if (ui.circleInput.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE);
                return true;
            }
            return false;
        });


        ui.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Glide.with(ContextHelper.getContext()).resumeRequests();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(ContextHelper.getContext()).pauseRequests();
                } else {
                    Glide.with(ContextHelper.getContext()).resumeRequests();
                }
            }
        });

        ui.recyclerView.setOnTouchListener((v, event) -> {
            if (ui.circleInput.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE, null);
                return true;
            }
            return false;
        });

        mAdapter.setOnItemCliclkListener((view, position) -> {
            if (ui.circleInput.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE, null);
            }
        });

        ui.mCricleRefresh.setOnLoadListener(() ->
            getMvpPresenter().updateFriendCircle(TYPE_LOADMORE));
        ui.mCricleRefresh.setOnRefreshListener(() -> {
            // 此处可能先取获取最新的朋友圈数量和评论数量较好
            L.i("开始刷新");
            getMvpPresenter().updateFriendCircle(TYPE_PULLREFRESH);
        });
        if (NetWorkUtil.isNetworkAvailable(this)) {
            ui.mCricleRefresh.setRefreshing(true);
            getMvpPresenter().updateFriendCircle(TYPE_PULLREFRESH);
        }

        //发布评论
        ui.circleInput.setChatInputMenuListener(content -> {
            if (TextUtils.isEmpty(content)) {
                showToast("评论内容不能为空...");
                return;
            }
            getMvpPresenter().addComment(content);

            updateEditTextBodyVisible(View.GONE);
        });

    }

    private void firstRefresh() {
        CustomProgressDrawable drawable = new CustomProgressDrawable(this, ui.mCricleRefresh);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle_of_friends);
        drawable.setBitmap(bitmap);
        ui.mCricleRefresh.setProgressDrawable(drawable);
        getMvpPresenter().updateFriendCircle(TYPE_UPLOADREFRESH);
    }

    @Override
    public void  updateEditTextBodyVisible(int visibility) {
        updateEditTextBodyVisible(visibility, null);
    }

    /**
     * 弹出或者隐藏键盘
     */
    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        ui.circleInput.setVisibility(visibility);
        if (commentConfig != null) {
            measureCircleItemHighAndCommentItemOffset(commentConfig);
        }

        if (View.VISIBLE == visibility && commentConfig != null) {
            ui.circleInput.setCirclePrimaryMenuHint("回复" + commentConfig.replyUsername + "...");
        } else if (View.GONE == visibility) {
            //隐藏键盘
            ui.circleInput.hideKeyboard();
            ui.circleInput.postDelayed(() -> ui.circleInput.setVisibility(View.GONE), 50);
        }
    }

    private void measureCircleItemHighAndCommentItemOffset(@NonNull CommentConfig commentConfig) {
        int firstPosition = mLayouManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = mLayouManager.getChildAt(
            commentConfig.circlePosition + CircleViewHolder.HEADVIEW_SIZE - firstPosition);

        if (selectCircleItem == null) {
            return;
        }
        mSelectCircleItemH = selectCircleItem.getHeight();

        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            CommentListView commentLv = selectCircleItem.findViewById(R.id.commentList);
            if (commentLv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    mSelectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            mSelectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    private void setViewTreeObserver() {
        final ViewTreeObserver swipeRefreshLayoutVTO = ui.bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            ui.bodyLayout.getWindowVisibleDisplayFrame(r);
            int statusBarH = getStatusBarHeight();//状态栏高度
            int screenH = ui.bodyLayout.getRootView().getHeight();
            if (r.top != statusBarH) {
                //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，
                // 通过getStatusBarHeight获取状态栏高度
                r.top = statusBarH;
            }
            int keyboardH = screenH - (r.bottom - r.top);
            //有变化时才处理，否则会陷入死循环
            if (keyboardH == mCurrentKeyboardH) return;

            mCurrentKeyboardH = keyboardH;
            mScreenHeight = screenH;//应用屏幕的高度
            mEditTextBodyHeight = ui.circleInput.getHeight();

            if(keyboardH<150){//说明是隐藏键盘的情况
                updateEditTextBodyVisible(View.GONE, null);
                return;
            }

            //偏移listview
            if (mLayouManager != null && getMvpPresenter().getCommentConfig() != null) {
                mLayouManager.scrollToPositionWithOffset(
                    getMvpPresenter().getCommentConfig().circlePosition
                        + CircleViewHolder.HEADVIEW_SIZE,
                    getListviewOffset(getMvpPresenter().getCommentConfig()));
            }
        });
    }


    /**
     * 测量偏移量
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if (commentConfig == null) return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset =
            mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight
                - ((int) TDevice.dpToPixel(56));
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            listviewOffset = listviewOffset + mSelectCommentItemOffset;
        }
        L.d("listviewOffset : " + listviewOffset);
        return listviewOffset;
    }

    /**
     * 获取状态栏高度
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 朋友圈更新
     */
    @Override
    public void updateFriendCircle(int type, List<CircleItem> items) {
        if (type == TYPE_PULLREFRESH) {
            refresh();
        } else if (type == TYPE_UPLOADREFRESH) {
            L.d("updateFriendCircle","updateFriendCircle");
        } else if (type == TYPE_LOADMORE) {
            if (ui.mCricleRefresh.isLoading()) {
                ui.mCricleRefresh.setLoading(false, items == null ? false : true);
            }
            load();
        }
        if (items != null) {
            mAdapter.setDatas(items);
        }
    }


    /**
     * 上拉加载更多
     */
    @Override
    public void load() {
        super.load();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void refresh() {
        super.refresh();
        if (ui.mCricleRefresh.isRefreshing()) {
            ui.mCricleRefresh.setRefreshing(false);
        }
    }



    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_SINGLE_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<ImageBean> path = data.getParcelableArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null) {
                    String imagePath = path.get(0).path;
                    L.d("选择了一张图片", imagePath);
                    getMvpPresenter().uploadThemeImg(imagePath);
                }
            }
        } else if (requestCode == AppConfig.REQUEST_VIDEO) {
           if (resultCode == 102) {
               String path = data.getStringExtra("videoPath");
               Intent intent = new Intent(this, VideoStatuActivity.class);
               intent.putExtra(VideoStatuActivity.PATH, path);
               startActivityForResult(intent, REQUEST_NEW_STATUS);
           }
        } else if (requestCode == 12 || requestCode == REQUEST_NEW_STATUS) {
            if (resultCode == RESULT_OK) {
                ui.mCricleRefresh.post(() -> ui.mCricleRefresh.setRefreshing(true));
//                getMvpPresenter().isRefresh = true;
                getMvpPresenter().updateFriendCircle(TYPE_PULLREFRESH);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (ui.circleInput != null && ui.circleInput.getVisibility() == View.VISIBLE) {
                ui.circleInput.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_circle, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_text:
                sendText();
                break;
            case R.id.action_picture:
                new RxPermissions(this)
                    .request(Manifest.permission.CAMERA)
                    .subscribe(bool -> sendImage());
                break;
            case R.id.action_video:
                new RxPermissions(this)
                    .request(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                    .subscribe(bool -> sendVideo());
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void sendText() {
        Intent intentText = new Intent(this, StatuActivity.class);
        intentText.putExtra(StatuActivity.TEXT, true);
        startActivityForResult(intentText, REQUEST_NEW_STATUS);
    }

    @Override
    public void sendImage() {
        Intent intent = new Intent(this, StatuActivity.class);
        startActivityForResult(intent, REQUEST_NEW_STATUS);
    }

    @Override
    public void sendVideo() {
        CameraActivity.start(this, AppConfig.REQUEST_VIDEO, CameraActivity.BUTTON_STATE_ONLY_RECORDER);
    }

}

package com.lensim.fingerchat.components.widget.circle_friends;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.R;
import com.lensim.fingerchat.components.dialog.BaseDialog;


/**
 * Created by LL117394---2017/05/10
 * 发朋友圈：收藏——图片、视频
 */

public class CollectDialog extends BaseDialog {

    private LinearLayout mVideoSilent;
    private LinearLayout mLLTransfer;
    private LinearLayout mCollect1;
    private TextView txtMenu;
    private TextView txtMenu1;
    boolean isVideo = false;
    boolean isShowTransfer = false;
    String menuStr = "";

    // 弹窗子类项选中时的监听
    private OnItemClickListener mItemClickListener;

    public CollectDialog(Context context, boolean isVideo) {
        super(context);
        this.isVideo = isVideo;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public CollectDialog(Context context, boolean isVideo, boolean isShowTransfer, String strMenu) {
        super(context);
        this.isVideo = isVideo;
        this.isShowTransfer = isShowTransfer;
        this.menuStr = strMenu;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void initView() {
        setContentView(R.layout.pop_collect_menu);
        mVideoSilent = findViewById(R.id.ll_pop_collect_silent);
        mLLTransfer = findViewById(R.id.ll_pop_trans);
        mCollect1 = findViewById(R.id.ll_pop_collect1);

        txtMenu = findViewById(R.id.dialog_menu_bottom);
        txtMenu1 = findViewById(R.id.dialog_menu_bottom1);

        if (!isVideo) {
            mVideoSilent.setVisibility(View.GONE);
        }

        if (isShowTransfer && !StringUtils.isEmpty(menuStr)) {
            mLLTransfer.setVisibility(View.VISIBLE);
            txtMenu1.setText(menuStr);
        }
    }

    @Override
    public void initEvent() {
        mVideoSilent.setOnClickListener(this);
        mLLTransfer.setOnClickListener(this);
        mCollect1.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        if (view.getId() == R.id.ll_pop_collect_silent) {
            mItemClickListener.onItemClick(0, itemIndex);
        } else if (view.getId() == R.id.ll_pop_trans) {
            mItemClickListener.onItemClick(1, itemIndex);
        } else if (view.getId() == R.id.ll_pop_collect1 ) {
            mItemClickListener.onItemClick(2, itemIndex);
        }
        dismiss();
    }

    public void setCollectItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private int itemIndex = 0;

    public void setDeleteIndex(int index) {
        this.itemIndex = index;
    }

    /***
     *笔记没有转发
     */
    public void hideTrans() {
        mLLTransfer.setVisibility(View.GONE);
    }


    /**
     * 功能描述：弹窗子类项按钮监听事件
     */
    public interface OnItemClickListener {

        void onItemClick(int position, int dataPosition);
    }
}

package com.lensim.fingerchat.components.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.lensim.fingerchat.commons.utils.TDevice;

/**
 * Created by caizhiming on 2016/2/4.
 */
public class RefreshHeader extends FrameLayout {

    private ProgressBar mProgressView;
    private float mHeaderHeight = 0;

    private int mStatus = XCPullToLoadMoreListView.REFRESH_STATUS_PULL_REFRESH;
    private static final int ROTATE_ANIM_DURATION = 200;
    public RefreshHeader(Context context) {
        this(context, null);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public float getHeaderHeight() {
        return this.mHeaderHeight;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mHeaderHeight = TDevice.dpToPixel(50);
        //初始化自己


        //add progress view
        mProgressView = new ProgressBar(context);
        LayoutParams lp2 = new LayoutParams((int) TDevice.dpToPixel(20), (int)TDevice.dpToPixel(20));
        lp2.gravity = Gravity.CENTER;
        this.addView(mProgressView, lp2);
        mProgressView.setVisibility(View.GONE);

    }

    public void updateRefreshStatus(int status) {

        if(status == XCPullToLoadMoreListView.REFRESH_STATUS_RELEASE_REFRESH
                && mStatus == XCPullToLoadMoreListView.REFRESH_STATUS_PULL_REFRESH){
            mProgressView.setVisibility(View.VISIBLE);
        }
        if(status == XCPullToLoadMoreListView.REFRESH_STATUS_PULL_REFRESH
                && mStatus == XCPullToLoadMoreListView.REFRESH_STATUS_RELEASE_REFRESH){
            mProgressView.setVisibility(View.VISIBLE);
        }
        if(status == XCPullToLoadMoreListView.REFRESH_STATUS_REFRESHING){
            mProgressView.setVisibility(View.VISIBLE);
        }
        if(status == XCPullToLoadMoreListView.REFRESH_STATUS_REFRESH_FINISH){
            mProgressView.setVisibility(View.GONE);

        }
        if(status == XCPullToLoadMoreListView.REFRESH_STATUS_PULL_REFRESH
                && mStatus == XCPullToLoadMoreListView.REFRESH_STATUS_REFRESH_FINISH){
            mProgressView.setVisibility(View.GONE);
        }
        mStatus = status;
    }
}

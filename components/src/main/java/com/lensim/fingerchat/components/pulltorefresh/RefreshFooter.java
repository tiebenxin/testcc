package com.lensim.fingerchat.components.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lensim.fingerchat.commons.utils.DensityUtil;


/**
 * Created by caizhiming on 2016/2/4.
 */
public class RefreshFooter extends LinearLayout {

  private TextView mStatusTextView;

  private ImageView mStatusImageView;
  private Animation mRotateUpAnim;
  private Animation mRotateDownAnim;
  private float mHeaderHeight = 0;
  private String mTxtStatus = "正在加载...";

  private int mStatus = SwipeRefreshLayout.REFRESH_STATUS_PULL_REFRESH;
  private static final int ROTATE_ANIM_DURATION = 200;

  public RefreshFooter(Context context) {
    this(context, null);
  }

  public RefreshFooter(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  public float getHeaderHeight() {
    return this.mHeaderHeight;
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    // mHeaderHeight = TDevice.dpToPixel(50);
    mHeaderHeight = DensityUtil.dip2px(context, 50);
    //初始化自己
    LayoutParams lpRoot = new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT);
    this.setOrientation(LinearLayout.HORIZONTAL);
    this.setGravity(Gravity.CENTER_HORIZONTAL);
    this.setLayoutParams(lpRoot);
    this.setBackgroundColor(Color.parseColor("#ffffffff"));

    //add progress view
    ProgressBar progressBar = new ProgressBar(context);
    LayoutParams lp2 = new LayoutParams(DensityUtil.dip2px(context, 15),
        DensityUtil.dip2px(context, 15));
    lp2.gravity = Gravity.CENTER_VERTICAL;

    this.addView(progressBar, lp2);
    LayoutParams lp = new LayoutParams(
        DensityUtil.dip2px(context, 20), DensityUtil.dip2px(context, 20));
    lp.gravity = Gravity.BOTTOM;
    lp.bottomMargin = (int) (mHeaderHeight / 5);

    //add refresh text status TextView
    mStatusTextView = new TextView(context);
    mStatusTextView.setText(mTxtStatus);
    mStatusTextView.setTextColor(Color.BLACK);
    lp = new LayoutParams(
        DensityUtil.dip2px(context, 100), LayoutParams.WRAP_CONTENT);
    lp.gravity = Gravity.CENTER_VERTICAL;
    lp.leftMargin = DensityUtil.dip2px(context, 5);
    // lp.bottomMargin = (int) (mHeaderHeight / 10);
    mStatusTextView.setGravity(Gravity.CENTER);
    this.addView(mStatusTextView, lp);


  }

}

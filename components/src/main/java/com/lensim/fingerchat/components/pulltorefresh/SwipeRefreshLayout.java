package com.lensim.fingerchat.components.pulltorefresh;

/**
 * Created by LY309313 on 2016/11/2.
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.TDevice;

/**
 * The SwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The SwipeRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to show just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and
 * progress animation, call setEnabled(false) on the view.
 * <p>
 * This layout should be made the parent of the view that will be refreshed as a
 * result of the gesture and can only support one direct child. This view will
 * also be made the target of the gesture and will be forced to match both the
 * width and the height supplied in this layout. The SwipeRefreshLayout does not
 * provide accessibility events; instead, a menu item must be provided to allow
 * refresh of the content wherever this gesture is used.
 * </p>
 */
public class SwipeRefreshLayout extends FrameLayout implements NestedScrollingParent,
    NestedScrollingChild {
    // Maps to ProgressBar.Large style
    public static final int LARGE = CustomMaterialProgressDrawable.LARGE;
    // Maps to ProgressBar default style
    public static final int DEFAULT = CustomMaterialProgressDrawable.DEFAULT;

    @VisibleForTesting
    static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER_LARGE = 56;

    private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private static final int SCALE_DOWN_DURATION = 150;

    private static final int ALPHA_ANIMATION_DURATION = 300;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default background for the progress spinner
    // 默认的进度条背景
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    //默认的进度条应该停止下拉的高度
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private View mTarget; // the target of the gesture
    OnRefreshListener mListener;
    OnLoadListener mLoadListener;

    boolean mRefreshing = false; //正在刷新或者正在加载都可以
    private int mTouchSlop; //触摸的距离
    private float mTotalDragDistance = -1; //总的拖拽距离
    private float mTotalLoadDistance = 0;
    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed; //
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2]; //父view消费的距离
    private final int[] mParentOffsetInWindow = new int[2];//父view在窗口中的偏移量
    private boolean mNestedScrollInProgress; //刷新时嵌套滚动

    private int mMediumAnimationDuration;
    int mCurrentTargetOffsetTop;
    int mCurrentTargetOffsetBottom;

    private float mInitialMotionY;
    //手指第一次触摸屏幕的Y值
    private float mInitialDownY;
    private boolean mIsBeingDragged;//是否被拽住
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped
    boolean mScale;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    //目标回到它的初始偏移量，因为刷新被取消或者已经完成
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.enabled
    };
    //刷新进度条
    CustomCircleImageView mCircleView;
  //  RefreshFooter mFooterView;
   // private DecelerateInterpolator mInterpolator = new DecelerateInterpolator(5);
    private int mCircleViewIndex = -1;
    //private int mFootViewIndex = -2;
    //总的偏移量
    protected int mFrom;

    float mStartingScale;

    protected int mOriginalOffsetTop;
    protected int mOriginalOffsetBottom;

    CustomMaterialProgressDrawable mProgress;

    private Animation mScaleAnimation;

    private Animation mScaleDownAnimation;

    private Animation mAlphaStartAnimation;

    private Animation mAlphaMaxAnimation;

    private Animation mScaleDownToStartAnimation;

    float mSpinnerFinalOffset;

    boolean mNotify;

    private int mCircleDiameter;

    // Whether the client has set a custom starting position;
    boolean mUsingCustomStart;

    public static final int REFRESH_STATUS_PULL_REFRESH = 0;//查看更早记录...;
    public static final int REFRESH_STATUS_RELEASE_REFRESH = 1;//松开开始加载...
    public static final int REFRESH_STATUS_REFRESHING = 2;// "正在加载...";
    public static final int REFRESH_STATUS_REFRESH_FINISH = 3;//"加载完成";
    private int mRefreshStatus = REFRESH_STATUS_PULL_REFRESH;

    private boolean up;
    private boolean down;
    private boolean mloading;
    private boolean loadComplete;
   // private boolean isLoadMore = false;
   // private boolean isLoading = false;

    private OnChildScrollUpCallback mChildScrollUpCallback;

    private OnChildScrollDownCallback mChildScrollDownCallback;

    private Animator.AnimatorListener mLoadingListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mloading) {
                if (mNotify) {
                    if (mLoadListener != null) {
                        mLoadListener.onLoad();
                    }
                }
                mCurrentTargetOffsetBottom = mRefreshFooter.getBottom();
                mTotalLoadDistance = -mBottomHeight;
                if(mTarget instanceof ListView){
                    lastCount = ((ListView) mTarget).getAdapter().getCount();
                }else if(mTarget instanceof RecyclerView){
                    lastCount = ((RecyclerView) mTarget).getAdapter().getItemCount();
                }
            } else {
                loadReset();
            }

            if(mRefreshFooter.getVisibility()!=VISIBLE && mTarget.getTranslationY()!=0){
                mTarget.animate().translationY(0).setDuration(200).start();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void loadReset() {
        loadComplete = false;
    }

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress.setAlpha(MAX_ALPHA);
                mProgress.start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
                mCurrentTargetOffsetTop = mCircleView.getTop();
            } else {
                reset();
            }
        }
    };
    private RefreshFooter mRefreshFooter;
    private int mBottomHeight;
    private int lastCount;

    //  private RefreshFooter mRefreshFooter;
   // private int mBottomHeight;
  //  private float mStartY;
   /// private float mCurY;
   // private int mHeight;
   // private int mLastCount;
   // private int mLastBottom;

    void reset() {
        mCircleView.clearAnimation();
        mProgress.stop();
        mCircleView.setVisibility(View.GONE);
        setColorViewAlpha(MAX_ALPHA);
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0 /* animation complete and view is hidden */);
        } else {
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop,
                    true /* requires update */);
        }
        mCurrentTargetOffsetTop = mCircleView.getTop();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    private void setColorViewAlpha(int targetAlpha) {
        mCircleView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }

    /**
     * The refresh indicator starting and resting position is always positioned
     * near the top of the refreshing content. This position is a consistent
     * location, but can be adjusted in either direction based on whether or not
     * there is a fg_toolbar or actionbar present.
     * <p>
     * <strong>Note:</strong> Calling this will reset the position of the refresh indicator to
     * <code>start</code>.
     * </p>
     *
     * @param scale Set to true if there is no view at a higher z-order than where the progress
     *              spinner is set to appear. Setting it to true will cause indicator to be scaled
     *              up rather than clipped.
     * @param start The offset in pixels from the top of this view at which the
     *              progress spinner should appear.
     * @param end The offset in pixels from the top of this view at which the
     *            progress spinner should come to rest after a successful swipe
     *            gesture.
     */
    public void setProgressViewOffset(boolean scale, int start, int end) {
        mScale = scale;
        mOriginalOffsetTop = start;
        mSpinnerFinalOffset = end;
        mUsingCustomStart = true;
        reset();
        mRefreshing = false;
    }

    /**
     * The refresh indicator resting position is always positioned near the top
     * of the refreshing content. This position is a consistent location, but
     * can be adjusted in either direction based on whether or not there is a
     * fg_toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than where the progress
     *              spinner is set to appear. Setting it to true will cause indicator to be scaled
     *              up rather than clipped.
     * @param end The offset in pixels from the top of this view at which the
     *            progress spinner should come to rest after a successful swipe
     *            gesture.
     */
    public void setProgressViewEndTarget(boolean scale, int end) {
        mSpinnerFinalOffset = end;
        mScale = scale;
        mCircleView.invalidate();
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    public void setSize(int size) {
        if (size != CustomMaterialProgressDrawable.LARGE && size != CustomMaterialProgressDrawable.DEFAULT) {
            return;
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (size == CustomMaterialProgressDrawable.LARGE) {
            mCircleDiameter = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
        } else {
            mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null);
        mProgress.updateSizes(size);
        mCircleView.setImageDrawable(mProgress);
    }

    /**
     * Simple constructor to use when creating a SwipeRefreshLayout from code.
     *
     * @param context
     */
    public SwipeRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);
        //添加头部刷新view

        createProgressView();
        createBottomView(context);
        // TODO: 2017/1/23 添加底部刷新
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        mOriginalOffsetTop = mCurrentTargetOffsetTop = -mCircleDiameter;

        mOriginalOffsetBottom = mCurrentTargetOffsetBottom = TDevice.getScreenHeightWithoutTitlebar(context) + mBottomHeight;
        moveToStart(1.0f);
       // moveLoadToStart(1.0f);
        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }



    private void createBottomView(Context context) {
        mRefreshFooter = new RefreshFooter(context);
        mBottomHeight = (int) mRefreshFooter.getHeaderHeight();
      //  FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mBottomHeight);
      //  params.gravity = Gravity.BOTTOM;
        mRefreshFooter.setVisibility(GONE);
        addView(mRefreshFooter);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mCircleViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mCircleViewIndex;
        } /*else if(i == childCount - 1){
            return mFootViewIndex;
        }*/else if (i >= mCircleViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }

    /**
     * 添加头部刷新view
     */
    private void createProgressView() {
        mCircleView = new CustomCircleImageView(getContext(), CIRCLE_BG_LIGHT);
        mProgress = new CustomMaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mListener = listener;
    }

    public void setOnLoadListener(SwipeRefreshLayout.OnLoadListener listener){
        mLoadListener = listener;
    }
    /**
     * Pre API 11, alpha is used to make the progress circle appear instead of scale.
     */
    private boolean isAlphaUsedForScale() {
        return android.os.Build.VERSION.SDK_INT < 11;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop,
                    true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mCircleView.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // Pre API 11, alpha is used in place of scale up to show the
            // progress circle appearing.
            // Don't adjust the alpha during appearance otherwise.
            mProgress.setAlpha(MAX_ALPHA);
        }
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleAnimation);
    }

    /**
     * Pre API 11, this does an alpha animation.
     * @param progress
     */
    void setAnimationProgress(float progress) {
        if (isAlphaUsedForScale()) {
            setColorViewAlpha((int) (progress * MAX_ALPHA));
        } else {
            ViewCompat.setScaleX(mCircleView, progress);
            ViewCompat.setScaleY(mCircleView, progress);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    public void setLoading(boolean loading,boolean requireFlush){
        if (loading && mloading != loading) {
            // scale and show
            mloading = loading;
            int endTarget = 0;

            endTarget = mOriginalOffsetBottom;

            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetBottom,
                    true /* requires update */);
            mNotify = false;
           // startShowLoadingAnimation(mLoadingListener);
            animateLoadViewOffsetToCorrectPosition(mCurrentTargetOffsetBottom,mLoadingListener);
        } else {
            setLoading(loading, false /* notify */,requireFlush);
        }
    }


    private void setLoading(boolean loading, boolean notify,boolean requireFlush) {

            mloading = loading;
            mNotify = notify;
            ensureTarget();
            mloading = loading;
            if(mloading){
                animateLoadViewOffsetToCorrectPosition(mCurrentTargetOffsetBottom,mLoadingListener);
            }else{
                loadingComplete(requireFlush);
               // animateLoadViewToStart(mLoadingListener);
            }

    }

    private void loadingComplete(boolean requireFlush) {
        mloading = false;
        mNotify = false;
        mRefreshFooter.setVisibility(GONE);
        int bottom = mTarget.getBottom();
        float translationY = mTarget.getTranslationY();
        L.i("loadingComplete","加载完成底部:" + bottom + "偏移"  + translationY);
        if(translationY < -mBottomHeight){
            translationY+=mBottomHeight;
            loadComplete = true;
        }else{
            translationY =0;
        }
        mTarget.setTranslationY(translationY);
        if(requireFlush&&mRefreshFooter.getTop() < mTarget.getBottom()){
            int offset = mTarget.getBottom() - mRefreshFooter.getTop();
            ViewCompat.offsetTopAndBottom(mRefreshFooter,offset);
            mCurrentTargetOffsetBottom = mOriginalOffsetBottom;
            if(mTarget instanceof ListView){
                ((ListView) mTarget).setSelectionFromTop(lastCount, (int) (bottom-offset -translationY));
            }else if(mTarget instanceof RecyclerView){
                RecyclerView rc = (RecyclerView) this.mTarget;
                LinearLayoutManager layoutManager = (LinearLayoutManager) rc.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(lastCount,(int) (bottom-offset -translationY));
            }
        }

    }

    private void animateLoadViewToStart(Animator.AnimatorListener mLoadingListener) {
        ValueAnimator anim = ValueAnimator.ofInt(mCurrentTargetOffsetBottom-mOriginalOffsetBottom,0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mTarget.setTranslationY(value);

                L.i("animateLoadViewOffsetToCorrectPosition","offset==="+value);
                int offset =  value + mOriginalOffsetBottom - mRefreshFooter.getBottom();
                ViewCompat.offsetTopAndBottom(mRefreshFooter,offset);
            }
        });
        anim.addListener(mLoadingListener);
        anim.setDuration(ANIMATE_TO_START_DURATION);
        anim.setInterpolator(mDecelerateInterpolator);
        anim.start();
    }

    private void animateLoadViewOffsetToCorrectPosition(int mCurrentTargetOffsetBottom, Animator.AnimatorListener listener) {
        ValueAnimator anim = ValueAnimator.ofInt(mCurrentTargetOffsetBottom-mOriginalOffsetBottom,-mBottomHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mTarget.setTranslationY(value);

                L.i("animateLoadViewOffsetToCorrectPosition","offset==="+value);
               int offset =  value + mOriginalOffsetBottom - mRefreshFooter.getBottom();
                ViewCompat.offsetTopAndBottom(mRefreshFooter,offset);
            }
        });
        anim.addListener(listener);
        anim.setDuration(ANIMATE_TO_START_DURATION);
        anim.setInterpolator(mDecelerateInterpolator);
        anim.start();
    }

    void startScaleDownAnimation(Animation.AnimationListener listener) {
//        mScaleDownAnimation = new Animation() {
//            @Override
//            public void applyTransformation(float interpolatedTime, Transformation t) {
//                setAnimationProgress(1 - interpolatedTime);
//            }
//        };
//        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        //修改返回的动画
        final int deltaY = -mCircleView.getBottom();
        mScaleDownAnimation = new TranslateAnimation(0,0,0,deltaY);
//      mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mScaleDownAnimation.setDuration(500);

        mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        // Pre API 11, alpha is used in place of scale. Don't also use it to
        // show the trigger point.
        if (mScale && isAlphaUsedForScale()) {
            return null;
        }
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress.setAlpha(
                        (int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        mCircleView.setAnimationListener(null);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(alpha);
        return alpha;
    }

    /**
     * @deprecated Use {@link #setProgressBackgroundColorSchemeResource(int)}
     */
    @Deprecated
    public void setProgressBackgroundColor(int colorRes) {
        setProgressBackgroundColorSchemeResource(colorRes);
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param colorRes Resource id of the color.
     */
    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), colorRes));
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color
     */
    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        mCircleView.setBackgroundColor(color);
        mProgress.setBackgroundColor(color);
    }



    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    public void setColorSchemeColors(@ColorInt int... colors) {
        ensureTarget();
        mProgress.setColorSchemeColors(colors);
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     *         progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleView)&&!child.equals(mRefreshFooter)) {
                    mTarget = child;
                    L.i("找到这个列表了");
                    break;
                }
            }
        }
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        //定位目标view
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();
//        mCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop,
//                (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
        //定位顶部刷新视图
        mCircleView.layout(childLeft, mCurrentTargetOffsetTop,
                childLeft+circleWidth, mCurrentTargetOffsetTop + circleHeight);

        //int sh = DensityUtil.getScreenWidthAndHeight(getContext())[1];
//        mRefreshFooter.layout(childLeft, mCurrentTargetOffsetTop,
//                childLeft+circleWidth, mCurrentTargetOffsetTop + circleHeight);
        mRefreshFooter.layout(childLeft,mCurrentTargetOffsetBottom - mBottomHeight,
                childLeft + childWidth,mCurrentTargetOffsetBottom);

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量完毕之后，测量子view，确保子view覆盖父view
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));

        mRefreshFooter.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mBottomHeight, MeasureSpec.EXACTLY));

        mCircleViewIndex = -1;
        //mFootViewIndex = -2;
        // Get the index of the circleview.
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index;
                break;
            }
        }

//        for (int index = 0; index < getChildCount(); index++) {
//            if (getChildAt(index) == mRefreshFooter) {
//                mFootViewIndex = index;
//                break;
//            }
//        }
    }

    /**
     * Get the diameter of the progress circle that is displayed as part of the
     * swipe to refresh layout.
     *
     * @return Diameter in pixels of the progress circle view.
     */
    public int getProgressCircleDiameter() {
        return mCircleDiameter;
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     *         scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        //根据回调来确定子view是否可以滑动
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        //没有回调就正常判断
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    public boolean canChildScrollDown() {
        //根据回调来确定子view是否可以滑动
        if (mChildScrollDownCallback != null) {
            return mChildScrollDownCallback.canChildScrollDown(this, mTarget);
        }
        //没有回调就正常判断
        if(mTarget.getTranslationY() != 0){
            return false;
        }
         return ViewCompat.canScrollVertically(mTarget, 1);
    }
    /**
     * Set a callback to override {@link android.support.v4.widget.SwipeRefreshLayout#canChildScrollUp()} method. Non-null
     * callback will return the value provided by the callback and ignore all internal logic.
     * @param callback Callback that should be called when canChildScrollUp() is called.
     */
    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        L.i("onInterceptTouchEvent：","事件处理");
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;
        //如果正在返回并且动作是落下那么，取消返回状态
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        //如果 不能触发动作  正在返回顶部   子view可以滑动  正在刷新  刷新时嵌套滚动 都不拦截事件
        if (!isEnabled() || mReturningToStart || (canChildScrollUp() && canChildScrollDown())
                || mRefreshing || mNestedScrollInProgress || mloading) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        if(loadComplete)return true;
        if(!canChildScrollUp()){
            setBackgroundColor(Color.BLACK);
            up=true;
            down = false;
        }

        if(!canChildScrollDown()){
            setBackgroundColor(Color.WHITE);
            down = true;
            up = false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                L.i("onInterceptTouchEvent：","ACTION_DOWN");
                if(up)
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop(), true);
                else if(down){
                    setTargetLoadOffsetTopAndBottom(mOriginalOffsetBottom - mRefreshFooter.getBottom(),true);
                    L.i("onInterceptTouchEvent：","上拉加载");
                }
                mActivePointerId = ev.getPointerId(0); //获取id
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId); //获取索引
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                L.i("onInterceptTouchEvent：","ACTION_MOVE");
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                L.i("onInterceptTouchEvent：","滚动了哈哈");
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }



    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
          //  L.i("什么也没做，默认是不拦截");
        } else {
            //L.i("继承父元素，默认是不拦截");
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        // 如果 能触法事件 不是正在返回  不在刷新  并且是垂直滑动  就可以嵌套滚动了
        L.i("onStartNestedScroll：","能触法事件 不是正在返回  不在刷新  并且是垂直滑动  就可以嵌套滚动了");
        return isEnabled() && !mReturningToStart && !mRefreshing && !loadComplete
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        L.i("onNestedScrollAccepted：","接收子滚动");
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        //通知子view可以嵌套滚动了
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if(loadComplete)return;
        //dx,dy是消费的距离
        L.i("onNestedPreScroll：","子view消费dx==" + dx + "dy===" + dy + "consumed[1]===" + consumed[1] + "总的未消费:" + mTotalUnconsumed);
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        // 上拉时dy大于零 下拉时dy小于零 起始点减去终点坐标
        if (dy > 0 && mTotalUnconsumed > 0 && up) { //如果有消费 ， 并且总的未消费也大于0 ，那么就让子view协同滚动
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0; //消费过多，置空
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(mTotalUnconsumed);
        }else if(dy < 0 && mTotalUnconsumed < 0 && down){ //上拉时往回走
            if (dy < mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0; //消费过多，置空
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveLoadView(mTotalUnconsumed,false);
        }else if(dy <0 && mCurrentTargetOffsetBottom < mOriginalOffsetBottom){
            if (dy < mTotalLoadDistance) {
                consumed[1] = dy - (int) mTotalLoadDistance;
                mTotalLoadDistance = 0; //消费过多，置空
            } else {
                mTotalLoadDistance -= dy;
                consumed[1] = dy;
            }

//
//            if(mTotalLoadDistance >= 0){
//                mTotalLoadDistance = -mBottomHeight;
//            }else
//            mTotalLoadDistance -= dy;

            moveLoadView(mTotalLoadDistance,true);
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            mCircleView.setVisibility(View.GONE);
        }

        // Now let our nested parent consume the leftovers
        // 现在让嵌套的父view消费剩下的部分
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        L.i("onStopNestedScroll：","停止嵌套滚动");
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
            mTarget.animate().translationY(0).setDuration(200).start();
        }else if(mTotalUnconsumed < 0){
            finishLoadView(mTotalUnconsumed);
            mTotalUnconsumed = 0;
           // mTarget.animate().translationY(-mBottomHeight).setDuration(200).start();
        }
        // Dispatch up our nested parent 将事件向上分发
        stopNestedScroll();
    }



    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        L.i("onNestedScroll：","嵌套滚动");
        if(loadComplete){
            return;
        }
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
//        这是一个hack位。嵌套的自底向上滚动的控件,我们有时在两个嵌套的
//        滚动视图,我们需要一种方法能够知道什么时候
//        嵌套滚动的父视图已经停止处理事件。我们通过使用
//        在窗口中的偏移的功能来看我们已经从事件被移动了。
//        这可以正确的指示我们是否需要接管事件流
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
        }

        if(dy>0 && !canChildScrollDown()){
            mTotalUnconsumed -= dy;
            moveLoadView(mTotalUnconsumed,false);
            L.i("向上移动:dy===" + mTotalUnconsumed);
        }

    }



    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }
    public void setProgressDrawable(CustomMaterialProgressDrawable mProgress){
        this.mProgress = mProgress;
        mCircleView.setImageDrawable(mProgress);
    }
    private void moveSpinner(float overscrollTop) {
        //当华东到顶部时
        //overscrollTop未消费的值
        mProgress.showArrow(true);
       // L.i("overscrollTop:",""+overscrollTop);
        mTarget.setTranslationY(overscrollTop);
        float originalDragPercent = overscrollTop / mTotalDragDistance;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
        float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetTop
                : mSpinnerFinalOffset;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;
      //  L.i("extraMove:",""+extraMove);
        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        // where 1.0f is a full circle
        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
        }
        if (!mScale) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        }

        if (mScale) {
            setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
        }
        if (overscrollTop < mTotalDragDistance) {
            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA
                    && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation(); //回到起点
            }
        } else {
            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation(); //到达最大位置
            }
        }
        float strokeStart = adjustedPercent * .8f;
        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress.setArrowScale(Math.min(1f, adjustedPercent));

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress.setProgressRotation(rotation);
        //setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
//      最终刷新的位置
        int endTarget;
        if (!mUsingCustomStart) {
//        没有修改使用默认的值
            endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
        } else {
//        否则使用定义的值
            endTarget = (int) mSpinnerFinalOffset;
        }
        if(targetY>=endTarget){
//        下移的位置超过最终位置后就不再下移，第一个参数为偏移量
            setTargetOffsetTopAndBottom(0, true /* requires update */);
        }else{
//        否则继续继续下移
            setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);

        }


    }

    private void moveLoadView(float overscrollTop,boolean down) {
       // float starty = mTarget.getTranslationY();

        if(mloading && !down){
            overscrollTop -= mBottomHeight;
        }

        mTarget.setTranslationY(overscrollTop);
       // int endTarget = getMeasuredHeight();
//
//
//        float originalDragPercent = overscrollTop / mBottomHeight;
//
//        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
//     //   float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
//        float extraOS = Math.abs(overscrollTop) - mBottomHeight;
//        float slingshotDist = mBottomHeight;
//        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
//                / slingshotDist);
//        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
//                (tensionSlingshotPercent / 4), 2)) * 2f;
//        float extraMove = (slingshotDist) * tensionPercent * 2;
        //  L.i("extraMove:",""+extraMove);
      //  int targetY = mOriginalOffsetBottom - (int) ((slingshotDist * dragPercent) + extraMove);
        int targetY = (int) (mOriginalOffsetBottom +  overscrollTop);
        // where 1.0f is a full circle
        if (mRefreshFooter.getVisibility() != View.VISIBLE) {
            mRefreshFooter.setVisibility(View.VISIBLE);
        }

//        if (Math.abs(overscrollTop) < mBottomHeight) {
//           L.i("回到起点");
//        } else {
//            L.i("去到最大位置");
//        }

//        if(targetY<=endTarget){
////        下移的位置超过最终位置后就不再下移，第一个参数为偏移量
//           // setTargetLoadOffsetTopAndBottom(0, true /* requires update */);
//        }else{
//        否则继续继续下移
      //  mRefreshFooter.setTranslationY(overscrollTop);
        //int bottom = mTarget.getBottom();
       // int offset = mRefreshFooter.getTop() - bottom;


      //  }
        setTargetLoadOffsetTopAndBottom(targetY - mCurrentTargetOffsetBottom, true /* requires update */);
    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > mTotalDragDistance) {
            setRefreshing(true, true /* notify */);
        } else {
            // cancel refresh
            mRefreshing = false;
            mProgress.setStartEndTrim(0f, 0f);
            Animation.AnimationListener listener = null;
            if (!mScale) {
                listener = new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!mScale) {
                            startScaleDownAnimation(null);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                };
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
            mProgress.showArrow(false);
        }
    }

    private void finishLoadView(float overscrollTop) {
            if(!loadComplete){
                if(mloading)
                setLoading(true,false,false);
                else{
                    setLoading(true,true,false);
                }
            }else{
                animateLoadViewToStart(mLoadingListener);
            }

    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || (canChildScrollUp()&&canChildScrollDown())
                || mRefreshing || mNestedScrollInProgress || mloading) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        if(loadComplete)return true;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }



                final float y = ev.getY(pointerIndex);
                startDragging(y);
              //  final float y = MotionEventCompat.getY(ev, pointerIndex);
//          记录手指移动的距离,mInitialMotionY是初始的位置，DRAG_RATE是拖拽因子，默认为0.5。
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
//          赋值给mTarget的top使之产生拖动效果
                Log.e("手指移动的距离:",overscrollTop + "");
                mTarget.setTranslationY(overscrollTop);

                if (mIsBeingDragged) {
                   // final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) {

                        moveSpinner(overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {

                //松开时回到顶部
                mTarget.animate().translationY(0).setDuration(200).start();

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
           // mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        }
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener);
        } else {
            mFrom = from;
            mAnimateToStartPosition.reset();
            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
                mCircleView.setAnimationListener(listener);
            }
            mCircleView.clearAnimation();
            mCircleView.startAnimation(mAnimateToStartPosition);
        }
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mCircleView.getTop();
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
            mProgress.setArrowScale(1 - interpolatedTime);
        }
    };

    void moveToStart(float interpolatedTime) {
        int targetTop = 0;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mCircleView.getTop();
        setTargetOffsetTopAndBottom(offset, false /* requires update */);
    }
    private void moveLoadToStart(float interpolatedTime) {
//        int targetTop = 0;
//        targetTop = mOriginalOffsetBottom;
        int offset = mOriginalOffsetBottom - mRefreshFooter.getBottom();
        setTargetOffsetTopAndBottom(offset, false /* requires update */);
    }
    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };



    private void startScaleDownReturnToStartAnimation(int from,
                                                      Animation.AnimationListener listener) {
        mFrom = from;
        if (isAlphaUsedForScale()) {
            mStartingScale = mProgress.getAlpha();
        } else {
            mStartingScale = ViewCompat.getScaleX(mCircleView);
        }
        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale  * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownToStartAnimation);
    }

    void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mCircleView.bringToFront();
        ViewCompat.offsetTopAndBottom(mCircleView, offset); //顶部和底部同时偏移
        mCurrentTargetOffsetTop = mCircleView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    void setTargetLoadOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mRefreshFooter.bringToFront();
        ViewCompat.offsetTopAndBottom(mRefreshFooter, offset); //顶部和底部同时偏移
        mCurrentTargetOffsetBottom = mRefreshFooter.getBottom();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    public boolean isLoading() {
        return mloading;
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    public interface OnLoadListener{

        void onLoad();
    }


    /**
     * Classes that wish to override {@link android.support.v4.widget.SwipeRefreshLayout#canChildScrollUp()} method
     * behavior should implement this interface.
     */
    public interface OnChildScrollUpCallback {
        /**
         * Callback that will be called when {@link android.support.v4.widget.SwipeRefreshLayout#canChildScrollUp()} method
         * is called to allow the implementer to override its behavior.
         *
         * @param parent SwipeRefreshLayout that this callback is overriding.
         * @param child The child view of SwipeRefreshLayout.
         *
         * @return Whether it is possible for the child view of parent layout to scroll up.
         */
        boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child);
    }


    public interface OnChildScrollDownCallback{

        boolean canChildScrollDown(SwipeRefreshLayout parent, @Nullable View child);
    }
}


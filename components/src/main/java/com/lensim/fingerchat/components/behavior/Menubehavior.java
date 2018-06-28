package com.lensim.fingerchat.components.behavior;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.ImageView;


import com.lensim.fingerchat.components.R;

import java.util.List;


public class Menubehavior extends CoordinatorLayout.Behavior<ImageView> {
    private static final boolean SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;
    private WindowInsetsCompat mLastInsets;
    private Rect mTmpRect;
    private boolean mIsHiding;
    public Menubehavior(){
        super();
    }
    public Menubehavior(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   ImageView child, View dependency) {
        // 依赖snackbar，也可以不用这个方法
        return SNACKBAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child,
                                          View dependency) {
       if (dependency instanceof AppBarLayout) {
            // 如果视图依赖了appbarlayout，那就让其跟随appbarlayout自动显示和隐藏
            // 这时用anchor去依赖
            updateFabVisibility(parent, (AppBarLayout) dependency, child);
        }
        return false;
    }



    private boolean updateFabVisibility(CoordinatorLayout parent,
                                        AppBarLayout appBarLayout, ImageView child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.getAnchorId() != appBarLayout.getId()) {
            // 再次判断是否依赖appbarlayout

            return false;
        }

//        if (child.getVisibility() != View.VISIBLE) {
//            // The view isn't set to be visible so skip changing it's visibility
//            return false;
//        }

        if (mTmpRect == null) {
            mTmpRect = new Rect();
        }

        // 得到依赖视图的可见区域
        final Rect rect = mTmpRect;
        offsetDescendantRect(parent, appBarLayout, rect);
        if (rect.bottom <= getMinimumHeightForVisibleOverlappingContent(appBarLayout)) {
            // 如果可见区域小于最低限度，就执行消失动画
           hide(child);
        } else {
            // 反之，让其显示
          show(child);
        }
        return true;
    }

    private  final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
    private  final ThreadLocal<RectF> sRectF = new ThreadLocal<>();
    private  final Matrix IDENTITY = new Matrix();

    private  void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
        rect.set(0, 0, child.getWidth(), child.getHeight());
        Matrix m = sMatrix.get();
        if (m == null) {
            m = new Matrix();
            sMatrix.set(m);
        } else {
            m.set(IDENTITY);
        }
        //以上将matrix设置到线程工具中
        offsetDescendantMatrix(group, child, m);

        RectF rectF = sRectF.get();
        if (rectF == null) {
            rectF = new RectF();
        }
        rectF.set(rect);
        m.mapRect(rectF);
        rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f),
                (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
    }

    private void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View && parent != target) {
            final View vp = (View) parent;
            offsetDescendantMatrix(target, vp, m);
            m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
        }

        m.preTranslate(view.getLeft(), view.getTop());

        if (!view.getMatrix().isIdentity()) {
            m.preConcat(view.getMatrix());
        }
    }

    void hide(final View mView) {
        if (mIsHiding || mView.getVisibility() != View.VISIBLE) {
            // A hide animation is in progress, or we're already hidden. Skip the call
            return;
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(
                mView.getContext(), R.anim.menu_out);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsHiding = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsHiding = false;
                mView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mView.startAnimation(anim);
    }

    private void show(View mView) {
        if (mView.getVisibility() != View.VISIBLE || mIsHiding) {
            // If the view is not visible, or is visible and currently being hidden, run
            // the show animation
            mView.clearAnimation();
            mView.setVisibility(View.VISIBLE);
            Animation anim = android.view.animation.AnimationUtils.loadAnimation(
                    mView.getContext(), R.anim.munu_in);
            anim.setDuration(200);
            anim.setInterpolator(new FastOutLinearInInterpolator());

            mView.startAnimation(anim);
        }
    }
    final int getMinimumHeightForVisibleOverlappingContent(AppBarLayout view) {
        final int topInset = getTopInset();
        final int minHeight = ViewCompat.getMinimumHeight(view);
        if (minHeight != 0) {
            // If this layout has a min height, use it (doubled)
            return (minHeight * 2) + topInset;
        }

        // Otherwise, we'll use twice the min height of our last child
        final int childCount = view.getChildCount();
        return childCount >= 1
                ? (ViewCompat.getMinimumHeight(view.getChildAt(childCount - 1)) * 2) + topInset
                : 0;
    }
    private int getTopInset() {
        return mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, ImageView child,
                                 int layoutDirection) {
        // First, lets make sure that the visibility of the FAB is consistent
        final List<View> dependencies = parent.getDependencies(child);
        for (int i = 0, count = dependencies.size(); i < count; i++) {
            final View dependency = dependencies.get(i);
            if (dependency instanceof AppBarLayout
                    && updateFabVisibility(parent, (AppBarLayout) dependency, child)) {
                break;
            }
        }
        // Now let the CoordinatorLayout lay out the FAB
        parent.onLayoutChild(child, layoutDirection);
        // Now offset it if needed

        return true;
    }

}

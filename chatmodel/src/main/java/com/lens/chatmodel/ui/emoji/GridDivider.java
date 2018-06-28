package com.lens.chatmodel.ui.emoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by LY309313 on 2016/9/20.
 */

public class GridDivider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    private int height;
    private Paint mPaint;

    /**
     * 默认分割线
     */
    public GridDivider(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        height = mDivider.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     */
    public GridDivider(Context context, int height, int dividerColor) {

        this.height = height;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);

    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //这个时候垂直和水平方向都需要重新绘制
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {

            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();

        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                + height;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + height;
            if (mDivider != null/*&& !isLastRaw(parent,i,spanCount,childCount)*/) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
            if (mPaint != null/* && !isLastRaw(parent,i,spanCount,childCount)*/) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + height;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
            if (mPaint != null) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    /**
     * 判断是否是最后一列
     */
    private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是最后一行
     */
    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int lastchild = childCount - childCount % spanCount;
            System.out.println("位置:" + pos + "子view的数量:" + childCount + "最后一行:" + lastchild);
            if (lastchild == childCount) {
                if (pos + 4 >= lastchild)// 如果是最后一行，则不需要绘制底部
                {
                    return true;
                }
            } else {
                if (pos >= lastchild)// 如果是最后一行，则不需要绘制底部
                {
                    return true;
                }
            }

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount) {
                    return true;
                }
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
        RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.indexOfChild(view);
        boolean islastraw = isLastRaw(parent, itemPosition, spanCount, childCount);
        boolean islastcol = isLastColum(parent, itemPosition, spanCount, childCount);
        if (islastraw || islastcol) {
            if (islastraw)// 如果是最后一行，则不需要绘制底部
            {
                System.out.println("最后一行");
                outRect.set(0, 0, height, 0);
            }

            if (islastcol)// 如果是最后一列，则不需要绘制右边
            {
                System.out.println("最后一列");
                outRect.set(0, 0, 0, height);
            }

            if (islastcol && islastraw) {
                System.out.println("最后一行");
                outRect.set(0, 0, height, 0);
            }
        } else {
            outRect.set(0, 0, height, height);
        }
    }

}

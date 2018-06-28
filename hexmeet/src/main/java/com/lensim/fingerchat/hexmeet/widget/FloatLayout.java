package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FloatLayout extends ViewGroup {

  public FloatLayout(Context context) {
    super(context);
  }

  public FloatLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int count = getChildCount();

    int maxHeight = 0;
    int maxWidth = 0;

    // Find out how big everyone wants to be
    measureChildren(widthMeasureSpec, heightMeasureSpec);

    // Find rightmost and bottom-most child
    for (int i = 0; i < count; i++) {
      View child = getChildAt(i);
      if (child.getVisibility() != GONE) {
        int childRight;
        int childBottom;

        LayoutParams lp = (LayoutParams) child.getLayoutParams();

        childRight = lp.x + child.getMeasuredWidth();
        childBottom = lp.y + child.getMeasuredHeight();

        maxWidth = Math.max(maxWidth, childRight);
        maxHeight = Math.max(maxHeight, childBottom);
      }
    }

    // Account for padding too
    // maxWidth += mPaddingLeft + mPaddingRight;
    // maxHeight += mPaddingTop + mPaddingBottom;

    // Check against minimum height and width
    maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
    maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

    setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
        resolveSize(maxHeight, heightMeasureSpec));
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int count = getChildCount();

    for (int i = 0; i < count; i++) {
      View child = getChildAt(i);
      if (child.getVisibility() != GONE) {

        LayoutParams lp = (LayoutParams) child.getLayoutParams();

        // int childLeft = mPaddingLeft + lp.x;
        // int childTop = mPaddingTop + lp.y;
        int childLeft = lp.x;
        int childTop = lp.y;
        child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop
            + child.getMeasuredHeight());

      }
    }
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  @Override
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  @Override
  protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
  }

  @Override
  protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof LayoutParams;
  }

  public static class LayoutParams extends ViewGroup.LayoutParams {

    public int x;

    public int y;

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int w, int h) {
      super(w, h);
    }

    public LayoutParams(ViewGroup.LayoutParams arg0) {
      super(arg0);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(getClass().getName()).append("[");
      sb.append("width: ").append(width);
      sb.append(" height: ").append(height);
      sb.append(" x: ").append(x);
      sb.append(" y: ").append(y).append("]");
      return sb.toString();
    }
  }
}

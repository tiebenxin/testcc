package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class ChangeFocusLayout extends LinearLayout {

  public ChangeFocusLayout(Context context) {
    super(context);
  }

  public ChangeFocusLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public ChangeFocusLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  private View toLoseFocus = null;
  private View toGetFocus = null;

  public void setToLoseFocus(View toLoseFocus) {
    this.toLoseFocus = toLoseFocus;
  }

  public void setToGetFocus(View toGetFocus) {
    this.toGetFocus = toGetFocus;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (toLoseFocus != null) {
      toLoseFocus.clearFocus();
      toGetFocus.requestFocus();
      toLoseFocus = null;
      toGetFocus = null;
    }
    return false;
  }
}

package com.lensim.fingerchat.hexmeet.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageButton;
import com.lensim.fingerchat.hexmeet.App;

public class LongPressButton extends ImageButton {

  private int last_x, last_y;
  private boolean isMoved;
  private boolean isReleased;
  private int downCount = 0;
  private Runnable runnable;
  private static final int TOUCH_SLOP = ViewConfiguration.get(App.getContext()).getScaledTouchSlop();
  private static int delay = 3000;

  public LongPressButton(Context context) {
    super(context);
    setRunnable();
  }

  public LongPressButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    setRunnable();
  }

  private void setRunnable() {
    runnable = new Runnable() {
      @Override
      public void run() {
        downCount--;
        if (downCount > 0 || isReleased || isMoved) {
          return;
        }
        delay = 0;
        performClick();
      }
    };
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    int x = (int) event.getX();
    int y = (int) event.getY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        last_x = x;
        last_y = y;
        downCount++;
        isReleased = false;
        isMoved = false;
        postDelayed(runnable, delay);
        break;
      case MotionEvent.ACTION_MOVE:
        if (isMoved) {
          break;
        }
        if (Math.abs(last_x - x) > TOUCH_SLOP || Math.abs(last_y - y) > TOUCH_SLOP) {
          isMoved = true;
        }
        break;
      case MotionEvent.ACTION_UP:
        downCount = 0;
        isReleased = true;
        break;
    }
    return true;
  }
}
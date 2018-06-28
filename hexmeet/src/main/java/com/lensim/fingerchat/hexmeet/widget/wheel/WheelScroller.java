//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class WheelScroller {

  private static final int SCROLLING_DURATION = 400;
  public static final int MIN_DELTA_FOR_SCROLLING = 1;
  private ScrollingListener listener;
  private Context context;
  private GestureDetector gestureDetector;
  private Scroller scroller;
  private int lastScrollY;
  private float lastTouchedY;
  private boolean isScrollingPerformed;
  private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      WheelScroller.this.lastScrollY = 0;
      int maxY = 2147483647;
      int minY = -2147483647;
      WheelScroller.this.scroller.fling(0, WheelScroller.this.lastScrollY, 0, (int) (-velocityY), 0, 0, -2147483647, 2147483647);
      WheelScroller.this.setNextMessage(0);
      return true;
    }
  };
  private final int MESSAGE_SCROLL = 0;
  private final int MESSAGE_JUSTIFY = 1;
  private Handler animationHandler = new Handler() {
    public void handleMessage(Message msg) {
      WheelScroller.this.scroller.computeScrollOffset();
      int currY = WheelScroller.this.scroller.getCurrY();
      int delta = WheelScroller.this.lastScrollY - currY;
      WheelScroller.this.lastScrollY = currY;
      if (delta != 0) {
        WheelScroller.this.listener.onScroll(delta);
      }

      if (Math.abs(currY - WheelScroller.this.scroller.getFinalY()) < 1) {
        currY = WheelScroller.this.scroller.getFinalY();
        WheelScroller.this.scroller.forceFinished(true);
      }

      if (!WheelScroller.this.scroller.isFinished()) {
        WheelScroller.this.animationHandler.sendEmptyMessage(msg.what);
      } else if (msg.what == 0) {
        WheelScroller.this.justify();
      } else {
        WheelScroller.this.finishScrolling();
      }

    }
  };

  public WheelScroller(Context context, ScrollingListener listener) {
    this.gestureDetector = new GestureDetector(context, this.gestureListener);
    this.gestureDetector.setIsLongpressEnabled(false);
    this.scroller = new Scroller(context);
    this.listener = listener;
    this.context = context;
  }

  public void setInterpolator(Interpolator interpolator) {
    this.scroller.forceFinished(true);
    this.scroller = new Scroller(this.context, interpolator);
  }

  public void scroll(int distance, int time) {
    this.scroller.forceFinished(true);
    this.lastScrollY = 0;
    this.scroller.startScroll(0, 0, 0, distance, time != 0 ? time : 400);
    this.setNextMessage(0);
    this.startScrolling();
  }

  public void stopScrolling() {
    this.scroller.forceFinished(true);
  }

  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case 0:
        this.lastTouchedY = event.getY();
        this.scroller.forceFinished(true);
        this.clearMessages();
      case 1:
      default:
        break;
      case 2:
        int distanceY = (int) (event.getY() - this.lastTouchedY);
        if (distanceY != 0) {
          this.startScrolling();
          this.listener.onScroll(distanceY);
          this.lastTouchedY = event.getY();
        }
    }

    if (!this.gestureDetector.onTouchEvent(event) && event.getAction() == 1) {
      this.justify();
    }

    return true;
  }

  private void setNextMessage(int message) {
    this.clearMessages();
    this.animationHandler.sendEmptyMessage(message);
  }

  private void clearMessages() {
    this.animationHandler.removeMessages(0);
    this.animationHandler.removeMessages(1);
  }

  private void justify() {
    this.listener.onJustify();
    this.setNextMessage(1);
  }

  private void startScrolling() {
    if (!this.isScrollingPerformed) {
      this.isScrollingPerformed = true;
      this.listener.onStarted();
    }

  }

  void finishScrolling() {
    if (this.isScrollingPerformed) {
      this.listener.onFinished();
      this.isScrollingPerformed = false;
    }

  }

  public interface ScrollingListener {

    void onScroll(int var1);

    void onStarted();

    void onFinished();

    void onJustify();
  }
}

package com.lens.chatmodel.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

@SuppressLint("AppCompatCustomView")
public class TimeTextView extends TextView {

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    // 在控件被销毁时移除消息
    handler.removeMessages(0);
  }
  long Time;
  private boolean run = true; // 是否启动了
  @SuppressLint("NewApi")
  private Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
          if (run) {
            long mTime = Time;
            if (mTime >= 0) {
              TimeTextView.this.setText(Time / 1000 + " s'"+"后销毁");

              if (0 == Time / 1000){
              }

              Time = Time - 1000;
              handler.sendEmptyMessageDelayed(0, 1000);
            }
          }/* else {
            TimeTextView.this.setVisibility(View.GONE);
          }*/
          break;
      }
    }
  };


  public TimeTextView(Context context) {
    this(context, null);
  }

  public TimeTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @SuppressLint("NewApi")
  public void setTimes(long mT) {
    // 标示已经启动
    Time = mT;
    if (Time > 0) {
      handler.removeMessages(0);
      handler.sendEmptyMessage(0);
    } else {
      TimeTextView.this.setVisibility(View.GONE);
    }
  }

  public void stop() {
    run = false;
  }
  public void start() {
    run = true;
  }

}

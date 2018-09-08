package com.lensim.fingerchat.fingerchat.ui.secretchat.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.lensim.fingerchat.fingerchat.ui.secretchat.TimerCountDownListener;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */
@SuppressLint("AppCompatCustomView")
public class PwdTimeTextView extends TextView {
  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    // 在控件被销毁时移除消息
    handler.removeMessages(0);
  }

  long Time;
  private TimerCountDownListener timerCountDownListener;
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

              if (Time / 60000 == 0){
                if (null != timerCountDownListener)
                timerCountDownListener.countDownFinished(true);
              }
              PwdTimeTextView.this.setText("请"+Time / 60000 + "分钟后重试！");
              Time = Time - 1000;
              handler.sendEmptyMessageDelayed(0, 1000);
            }
          } else {
            PwdTimeTextView.this.setVisibility(View.GONE);
          }
          break;
      }
    }
  };


  public PwdTimeTextView(Context context) {
    this(context, null);
  }

  public PwdTimeTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PwdTimeTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @SuppressLint("NewApi")
  public void setTimes(long mT,TimerCountDownListener timerCountDownListener) {
    this.timerCountDownListener = timerCountDownListener;
    // 标示已经启动
    Time = mT;
    if (Time > 0) {
      handler.removeMessages(0);
      handler.sendEmptyMessage(0);
    } else {
      PwdTimeTextView.this.setVisibility(View.GONE);
    }
  }

  public void stop() {
    run = false;
  }
}

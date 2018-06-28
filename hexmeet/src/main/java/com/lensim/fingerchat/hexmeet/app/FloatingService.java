package com.lensim.fingerchat.hexmeet.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.RelativeLayout;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.activity.Conversation;

public class FloatingService extends Service {

  private RelativeLayout mFloatLayout;
  private LayoutParams wmParams;
  private WindowManager mWindowManager;
  private RelativeLayout audio_image_view;
  private Chronometer timer_chronometer;
  private int downX = -1;
  private int downY = -1;
  private int width = -1;
  private int height = -1;
  private boolean isMoved = false;
  private int touchSlop;

  private boolean isVideoCall = false;
  private HandlerThread handlerThread = null;

  @Override
  public void onCreate() {
    super.onCreate();


    touchSlop = ViewConfiguration.get(App.getContext()).getScaledTouchSlop();

    wmParams = new LayoutParams();
    mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
    wmParams.type = LayoutParams.TYPE_TOAST;
    wmParams.format = PixelFormat.RGBA_8888;
    wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
    wmParams.gravity = Gravity.START | Gravity.TOP;
    wmParams.width = LayoutParams.WRAP_CONTENT;
    wmParams.height = LayoutParams.WRAP_CONTENT;

    LayoutInflater inflater = LayoutInflater.from(getApplication());
    mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.floating_service, null);
    mWindowManager.addView(mFloatLayout, wmParams);

    audio_image_view = (RelativeLayout) mFloatLayout.findViewById(R.id.audio_image_view);

    mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

    width = mFloatLayout.getMeasuredWidth() / 2;
    height = mFloatLayout.getMeasuredHeight() / 2;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      isVideoCall = intent.getBooleanExtra("isVideoCall", false);
    }

    audio_image_view.setVisibility(View.VISIBLE);
    View view = audio_image_view;

    view.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          isMoved = false;
          downX = x;
          downY = y;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
          if (isMoved || Math.abs(x - downX) > touchSlop || Math.abs(y - downY) > touchSlop) {
            if (!isMoved) {
              isMoved = true;
            }
            wmParams.x = x - width;
            wmParams.y = y - height;
            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
          }
        }

        return false;
      }
    });

    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isMoved) {
          return;
        }
        Intent i = new Intent(FloatingService.this, Conversation.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
      }
    });


      timer_chronometer = (Chronometer) mFloatLayout.findViewById(R.id.timer_chronometer);
      timer_chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer ch) {
          long time = SystemClock.elapsedRealtime() - ch.getBase();
          long hour = time / 3600000;
          long m = time % 3600000 / 60000;
          long s = time % 60000 / 1000;
          ch.setText(String.format("%02d:%02d:%02d", hour, m, s));
          ch.setTextColor(Color.parseColor("#2bbb6a"));
        }
      });
      if (intent != null) {
        timer_chronometer.setBase(intent.getLongExtra("starttime", SystemClock.elapsedRealtime()));
      } else {
        timer_chronometer.setBase(SystemClock.elapsedRealtime());
      }
      timer_chronometer.start();

    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (handlerThread != null) {
      handlerThread.quit();
    }
    if (mFloatLayout != null) {
      mWindowManager.removeView(mFloatLayout);
    }
  }
}

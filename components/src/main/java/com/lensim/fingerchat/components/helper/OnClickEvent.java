package com.lensim.fingerchat.components.helper;

import android.support.design.widget.Snackbar;
import android.view.View;
import com.lensim.fingerchat.commons.utils.StringUtils;

/**
 * 防止频繁点击
 */
public abstract class OnClickEvent implements View.OnClickListener {

  private static long lastTime;
  private long delay;
  private String message;

  public OnClickEvent(long delay) {
    this.delay = delay;
  }

  public OnClickEvent(long delay, String msg) {
    this.delay = delay;
    this.message = msg;
  }

  public abstract void singleClick(View v);

  @Override
  public void onClick(View v) {
    if (onMoreClick(v)) {
      if (!StringUtils.isEmpty(message)) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
      }
      return;
    }
    singleClick(v);
  }

  public boolean onMoreClick(View v) {
    boolean flag = false;
    long time = System.currentTimeMillis() - lastTime;
    if (time < delay) {
      flag = true;
    }
    lastTime = System.currentTimeMillis();
    return flag;
  }
}
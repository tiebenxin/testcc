package com.lensim.fingerchat.fingerchat.controller;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2017/11/24.
 *
 */

public class ControllerBadNet {

  private OnControllerClickListenter listenter;

  public ControllerBadNet(View v) {
    init(v);
  }

  private void init(View v) {
    TextView tv_reload = v.findViewById(R.id.tv_reload);

    tv_reload.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (listenter != null) {
          listenter.onClick();
        }
      }
    });
  }

  public void setOnClickListener(OnControllerClickListenter l) {
    listenter = l;
  }

}

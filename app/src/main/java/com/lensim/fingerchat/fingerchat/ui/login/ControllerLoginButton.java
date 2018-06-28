package com.lensim.fingerchat.fingerchat.ui.login;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class ControllerLoginButton {

  private Button button;
  private OnControllerClickListenter listenter;

  public ControllerLoginButton(View v) {
    init(v);
  }

  private void init(View v) {
    button = (Button) v.findViewById(R.id.bt_login);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listenter != null) {
          listenter.onClick();
        }
      }
    });
  }

  public void setText(int s) {
    button.setText(ContextHelper.getString(s));
  }

  public void setTextColor(int color) {
    button.setTextColor(ContextHelper.getColor(color));
  }

  public void setTextSize(int size) {
    button.setTextSize(size);
  }

  public void setOnControllerClickListener(OnControllerClickListenter l) {
    listenter = l;
  }


}

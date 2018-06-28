package com.lensim.fingerchat.fingerchat.manager;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow.OnDismissListener;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.view.RegisterBottomMenu;
import com.lensim.fingerchat.commons.utils.TDevice;

/**
 * Created by LL130386 on 2017/11/16.
 */

public class PopManager {

  public static RegisterBottomMenu createRegisterMenu(final Context context, View view) {
    //pop显示的时候,页面变暗
    WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
    lp.alpha = .3f;
    ((Activity)context).getWindow().setAttributes(lp);

    View contentView = LayoutInflater.from(context).inflate(
        R.layout.pop_bottom_menu, null);
    RegisterBottomMenu menu = new RegisterBottomMenu(contentView, (int) TDevice.getScreenWidth(),
        (int) TDevice.getScreenHeight() / 3, true);
    menu.setOnDismissListener(new OnDismissListener() {

      @Override
      public void onDismiss() {
        //pop消失的时候，页面恢复亮度
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = 1.0f;
        ((Activity) context).getWindow().setAttributes(lp);
      }
    });

    menu.setAnimationStyle(R.style.anim_popup_dir);
    menu.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    return menu;
  }

}

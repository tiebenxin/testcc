package com.lensim.fingerchat.fingerchat.view;


import android.view.View;
import android.widget.TextView;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.components.popupwindow.BasePopupWindow;
import com.lensim.fingerchat.fingerchat.interf.IPopItemClickListener;

public class RegisterBottomMenu extends BasePopupWindow {

  private TextView mTvCamera;
  private TextView mTvImageFolder;
  private TextView mTvDefault;
  private TextView mTvCancel;
  private IPopItemClickListener listener;


  public RegisterBottomMenu(View contentView, int width, int height,
      boolean focusable) {
    super(contentView, width, height, focusable);

  }

  @Override
  public void initView() {
    mTvCamera = (TextView) findViewById(R.id.pop_bottom_camera);
    mTvImageFolder = (TextView) findViewById(R.id.pop_bottom_imagefolder);
    mTvDefault = (TextView) findViewById(R.id.pop_bottom_default);
    mTvCancel = (TextView) findViewById(R.id.pop_bottom_cancel);
  }

  @Override
  public void initListener() {
    mTvCamera.setOnClickListener(this);
    mTvImageFolder.setOnClickListener(this);
    mTvDefault.setOnClickListener(this);
    mTvCancel.setOnClickListener(this);

  }

  public void setItemClick(IPopItemClickListener l) {
    listener = l;
  }


  @Override
  public void processClick(View v) {
    if (listener == null) {
      return;
    }

    switch (v.getId()) {
      case R.id.pop_bottom_camera:
        listener.callCamera();
        dismiss();
        break;
      case R.id.pop_bottom_imagefolder:
        listener.callGallery();
        dismiss();
        break;
      case R.id.pop_bottom_default:
        listener.defaultAvatar();
        dismiss();
        break;
      case R.id.pop_bottom_cancel:
        dismiss();
        break;
    }
  }

}

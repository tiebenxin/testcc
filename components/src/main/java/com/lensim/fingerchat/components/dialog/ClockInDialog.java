package com.lensim.fingerchat.components.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.R;


/**
 * Created by LL117394 on 2017/9/6
 */

public class ClockInDialog extends BaseDialog {

  private TextView tv_pop_clock_time, tv_pop_clock_address;
  private LinearLayout mLLOK;
  private Context mContext;
  private boolean isSuccess;//打卡是否成功
  private String time, adderss;


  public ClockInDialog(Context context, boolean success, int theme, String pTime, String pAdderss) {
    super(context, theme);
    mContext = context;
    time = pTime;
    adderss = pAdderss;
    isSuccess = success;
  }

  @Override
  public void initView() {
    setContentView(isSuccess ? R.layout.pop_clock_in_success : R.layout.pop_clock_in_fail);
    mLLOK = (LinearLayout) findViewById(R.id.ll_pop_clock_ok);
    tv_pop_clock_time = (TextView) findViewById(R.id.tv_pop_clock_time);
    tv_pop_clock_address = (TextView) findViewById(R.id.tv_pop_clock_address);
  }

  @Override
  public void initdata() {
    if (!StringUtils.isEmpty(time)) {
      tv_pop_clock_time.setText(time);
    }
    if (!StringUtils.isEmpty(adderss)) {
      tv_pop_clock_address.setText(adderss);
    }
    mLLOK.setOnClickListener(this);
  }

  @Override
  public void processClick(View view) {
      if (view.getId() == R.id.ll_pop_clock_ok) {
          dismiss();
      }
  }
}

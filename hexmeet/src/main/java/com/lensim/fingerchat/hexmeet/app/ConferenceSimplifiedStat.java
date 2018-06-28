package com.lensim.fingerchat.hexmeet.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import com.hexmeet.sdk.HexmeetCallState;
import com.hexmeet.sdk.HexmeetReason;
import com.hexmeet.sdk.HexmeetRegistrationState;
import com.hexmeet.sdk.HexmeetSdkListener;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;

public class ConferenceSimplifiedStat extends FragmentActivity {

  private Activity context;

  private HexmeetSdkListener mListener;

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0).setAlpha(0.99f);
    if (resultCode == Activity.RESULT_OK && requestCode == 1) {
      finish();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.conference_stat_simplified);

    boolean isVideoCall = getIntent().getBooleanExtra("isVideoCall", true);
    setRequestedOrientation(isVideoCall ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    context = this;

    findViewById(R.id.back).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        context.finish();
      }
    });

    if (!isVideoCall) {
      View view = findViewById(R.id.call_statistics);
      LayoutParams params = view.getLayoutParams();
      int width = Math.min(ScreenUtil.getWidth(context), ScreenUtil.getHeight(context));
      int height = Math.max(ScreenUtil.getWidth(context), ScreenUtil.getHeight(context));
      params.width = width;
      params.height = height - ScreenUtil.dp_to_px(48);
      view.setLayoutParams(params);
    }

    mListener = new HexmeetSdkListener() {
      @Override
      public void globalState() {

      }

      @Override
      public void registrationState(HexmeetRegistrationState hexmeetRegistrationState) {

      }

      @Override
      public void callState(HexmeetCallState state, HexmeetReason reason) {
        Log.e("call state", state.toString());
        if (!App.getHexmeetSdkInstance().hasOngoingCall()) {
          ConferenceSimplifiedStat.this.finish();
          return;
        }

        if (state == HexmeetCallState.Connected) {
          ConferenceSimplifiedStat.this.finish();
        }
      }
    };

    App.getHexmeetSdkInstance().addHexmeetSdkListener(mListener);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    App.getHexmeetSdkInstance().removeHexmeetSdkListener(mListener);
  }
}

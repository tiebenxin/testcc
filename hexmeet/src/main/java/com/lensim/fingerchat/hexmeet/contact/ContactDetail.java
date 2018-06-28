package com.lensim.fingerchat.hexmeet.contact;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hexmeet.sdk.HexmeetCallState;
import com.hexmeet.sdk.HexmeetReason;
import com.hexmeet.sdk.HexmeetRegistrationState;
import com.hexmeet.sdk.HexmeetSdkListener;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.activity.ConnectActivity;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestResult;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import com.lensim.fingerchat.hexmeet.utils.DialOutRetryHandler;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import com.lensim.fingerchat.hexmeet.widget.MenuItem;
import com.lensim.fingerchat.hexmeet.widget.PopupMenuBottom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactDetail extends FragmentActivity {

  private RestContact restContact;

  private TextView username;
  private TextView account;
  private TextView sipNumber;
  private TextView cellphone;
  private TextView telephone;
  private TextView email;

  private ImageView headPicture;
  private ImageView background;
  private LinearLayout backImage;
  private LinearLayout delete_contact_btn;
  private LinearLayout start_video_communication_btn;
  private LinearLayout start_audio_communication_btn;
  private RelativeLayout contact_btn;
  private PopupMenuBottom popuMenu;
  private Activity context;

  private HexmeetSdkListener mListener;

  public static void actionStart(Context context, RestContact contact) {
    Intent intent = new Intent(context, ContactDetail.class);
    intent.putExtra("contact", contact);
    context.startActivity(intent);
  }

  public static void actionStart(Context context, boolean hideAction, RestContact contact) {
    Intent intent = new Intent(context, ContactDetail.class);
    intent.putExtra("contact", contact);
    intent.putExtra("hideAction", hideAction);
    context.startActivity(intent);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    ScreenUtil.initStatusBar(this);
    setContentView(R.layout.contact_detail);
    context = this;

    restContact = (RestContact) getIntent().getSerializableExtra("contact");
    boolean hideAction = getIntent().getBooleanExtra("hideAction", false);

    headPicture = (ImageView) findViewById(R.id.contact_headPicture);

    String imageUrl = restContact.getImageURL();
    if (!StringUtils.isEmpty(imageUrl)) {
      AvatarLoader.load(imageUrl, headPicture);
    }


    background = (ImageView) findViewById(R.id.background);
    background.setAlpha(100);

    username = (TextView) findViewById(R.id.username);
    username.setText(restContact.getName());

    account = (TextView) findViewById(R.id.account);
    account.setText(restContact.getUserName());

    sipNumber = (TextView) findViewById(R.id.weishi_number);
    cellphone = (TextView) findViewById(R.id.contact_cellphone);
    telephone = (TextView) findViewById(R.id.contact_telephone);
    email = (TextView) findViewById(R.id.contact_email);
    if (StringUtils.isEmpty(restContact.getCallNumber())) {
      if (StringUtils.isNotEmpty(restContact.getTelephone())) {
        sipNumber.setText(restContact.getTelephone());
      }
    } else {
      sipNumber.setText(restContact.getCallNumber());
    }

    cellphone.setText(restContact.getCellphone() != null ? restContact.getCellphone() : "");
    telephone.setText(restContact.getTelephone() != null ? restContact.getTelephone() : "");
    email.setText(restContact.getEmail() != null ? restContact.getEmail() : "");

    backImage = (LinearLayout) findViewById(R.id.back_icon);
    backImage.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    delete_contact_btn = (LinearLayout) findViewById(R.id.delete_icon);
    start_video_communication_btn = (LinearLayout) findViewById(R.id.start_video_communication_btn);
    start_audio_communication_btn = (LinearLayout) findViewById(R.id.start_audio_communication_btn);
    contact_btn = (RelativeLayout) findViewById(R.id.contact_btn);
    RestUser logUser = RuntimeData.getLogUser();
    if (hideAction) {
      delete_contact_btn.setVisibility(View.GONE);
      contact_btn.setVisibility(View.GONE);
    }

    if (logUser != null && logUser.getId() == restContact.getUserId()) {
      delete_contact_btn.setVisibility(View.GONE);
      contact_btn.setVisibility(View.GONE);
    }

    popuMenu = new PopupMenuBottom(context);
    popuMenu.addItem(new MenuItem(context, getString(R.string.delete_contact), Color.parseColor("#F57070"), 0));
    popuMenu.setItemOnClickListener(new PopupMenuBottom.OnItemOnClickListener() {
      @Override
      public void onItemClick(MenuItem item, int position) {
        if (position == 0) {
          if (!NetworkUtil.isNetConnected(context)) {
            Utils.showToast(context, R.string.server_unavailable);
            return;
          }

          ApiClient.deleteContact(restContact.getId(), new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
              if (response.isSuccessful()) {
                App.removeContact(restContact.getCallNumber());
                finish();
              } else {
                Utils.showToast(context, ApiClient.fromErrorResponse(response));
              }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable e) {
            }
          });
        }
      }
    });

    delete_contact_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        popuMenu.show(v);
      }
    });

    start_video_communication_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!NetworkUtil.isSipServerReachable(context)) {
          return;
        }

        if (NetworkUtil.is3GConnected(context)) {
          showDialogMsg(true);
        } else {
          startVideo();
        }
      }
    });

    start_audio_communication_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!NetworkUtil.isSipServerReachable(context)) {
          return;
        }

        if (NetworkUtil.is3GConnected(context)) {
          showDialogMsg(false);
        } else {
          startAudio();
        }
      }
    });

    initVideoView();
  }

  private void startVideo() {
    if (StringUtils.isEmpty(sipNumber.getText().toString())) {
      Utils.showToast(getApplicationContext(), R.string.peer_has_not_video_cap);
      return;
    }

    if (StringUtils.isAlphanumeric(sipNumber.getText().toString())) {
      Intent intent = new Intent();
      intent.setClass(context, ConnectActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

      Bundle bundle = new Bundle();
      bundle.putBoolean("isVideoCall", true);
      bundle.putString("sipNumber", sipNumber.getText().toString());
      intent.putExtras(bundle);

      startActivity(intent);

      DialOutRetryHandler.getInstance().cancel();
      DialOutRetryHandler.getInstance().init();
      DialOutRetryHandler.getInstance().startDialing(sipNumber.getText().toString(),
          restContact, true);
    }
  }

  private void showDialogMsg(final boolean isVideo) {
    LayoutInflater factory = LayoutInflater.from(context);
    final View view = factory.inflate(R.layout.alertdialog_warning_4g, null);
    final AlertDialog dlg = new AlertDialog.Builder(context).setView(view).create();
    dlg.show();
    Button submit = (Button) view.findViewById(R.id.submit);
    Button cancel = (Button) view.findViewById(R.id.cancel);

    submit.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (isVideo) {
          startVideo();
        } else {
          startAudio();
        }
        dlg.dismiss();
      }
    });

    cancel.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        dlg.dismiss();
      }
    });
  }

  private void startAudio() {
    if (StringUtils.isEmpty(sipNumber.getText().toString())) {
      Utils.showToast(getApplicationContext(), R.string.peer_has_not_video_cap);
      return;
    }

    if (StringUtils.isAlphanumeric(sipNumber.getText().toString())) {
      Intent intent = new Intent();
      intent.setClass(context, ConnectActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

      Bundle bundle = new Bundle();
      bundle.putBoolean("isVideoCall", false);
      bundle.putString("sipNumber", sipNumber.getText().toString());
      intent.putExtras(bundle);

      startActivity(intent);

      DialOutRetryHandler.getInstance().cancel();
      DialOutRetryHandler.getInstance().init();
      DialOutRetryHandler.getInstance().startDialing(sipNumber.getText().toString(),
          restContact, false);
    }
  }

  private void initVideoView() {

    mListener = new HexmeetSdkListener() {
      @Override
      public void globalState() {

      }

      @Override
      public void registrationState(HexmeetRegistrationState hexmeetRegistrationState) {

      }

      @Override
      public void callState(HexmeetCallState state, HexmeetReason reason) {
        if (!App.getHexmeetSdkInstance().hasOngoingCall()) {
          //cancel incoming call window
          return;
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

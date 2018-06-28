package com.lensim.fingerchat.hexmeet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestContactReq;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import com.lensim.fingerchat.hexmeet.utils.CallRecordManager;
import com.lensim.fingerchat.hexmeet.utils.Convertor;
import com.lensim.fingerchat.hexmeet.utils.DialOutRetryHandler;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import com.lensim.fingerchat.hexmeet.widget.CallHistoryView;
import com.lensim.fingerchat.hexmeet.widget.MenuItem;
import com.lensim.fingerchat.hexmeet.widget.PopupMenuBottom;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialingDetail extends FragmentActivity {

    private ImageView avatar;
    private TextView name;
    private PopupMenuBottom popuMenu;
    private Activity context;
    private String peerSip;

    public static void actionStart(Context context, String peerSip) {
        Intent intent = new Intent(context, DialingDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("peerSip", peerSip);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScreenUtil.initStatusBar(this);
        setContentView(R.layout.dialing_detail);
        context = this;

        peerSip = getIntent().getStringExtra("peerSip");
        if (App.getContact(peerSip) == null && peerSip.length() <= 4) {
            findViewById(R.id.add_as_contact_layout).setVisibility(View.VISIBLE);
        }

        avatar = (ImageView) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        final RestContact restContact = App.getContact(peerSip);
        if (restContact != null) {
            if (restContact.getUserId() > 0) {
                String host = "https://" + RuntimeData.getUcmServer();
                AvatarLoader.load(host + restContact.getImageURL(), avatar);
            }

            name.setText(restContact.getName());
        } else {
            name.setText(peerSip);
        }

        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.call_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isSipServerReachable(context)) {
                    return;
                }

                if (NetworkUtil.is3GConnected(context)) {
                    warning4gCall(true, restContact);
                } else {
                    makeCall(true, restContact);
                }
            }
        });

        findViewById(R.id.call_audio).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isSipServerReachable(context)) {
                    return;
                }

                if (NetworkUtil.is3GConnected(context)) {
                    warning4gCall(false, restContact);
                } else {
                    makeCall(false, restContact);
                }
            }
        });

        findViewById(R.id.add_as_contact).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isUcmReachable(context)) {
                    return;
                }

                ApiClient.getUsers("", new Callback<List<RestUser>>() {
                    @Override
                    public void onResponse(Call<List<RestUser>> call,
                        Response<List<RestUser>> response) {
                        if (response.isSuccessful()) {
                            List<RestUser> rests = response.body();
                            boolean found = false;
                            for (RestUser restUser : rests) {
                                if (peerSip.equals(restUser.getCallNumber())) {
                                    found = true;
                                    RestContactReq restContactReq = new RestContactReq();
                                    restContactReq.setUserId(restUser.getId());
                                    ApiClient
                                        .addContact(restContactReq, new Callback<RestContact>() {
                                            @Override
                                            public void onResponse(Call<RestContact> call,
                                                Response<RestContact> response) {
                                                if (response.isSuccessful()) {
                                                    RestContact rest = response.body();
                                                    App.addContact(rest.getCallNumber(), rest);
                                                    String imageUrl = Convertor.getAvatarUrl(rest);
                                                    if (imageUrl != null && !"".equals(imageUrl)) {
                                                        AvatarLoader.load(imageUrl, avatar);
                                                    }
                                                    name.setText(rest.getName());
                                                    findViewById(R.id.add_as_contact_layout)
                                                        .setVisibility(View.GONE);
                                                } else {
                                                    Utils.showToast(App.getContext(),
                                                        R.string.not_found_user);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<RestContact> call,
                                                Throwable e) {
                                            }
                                        });
                                    break;
                                }
                            }

                            if (!found) {
                                Utils.showToast(App.getContext(), R.string.not_found_related_user);
                            }
                        } else {
                            Utils.showToast(App.getContext(), R.string.action_fail);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RestUser>> call, Throwable e) {
                    }
                });
            }
        });

        findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popuMenu = new PopupMenuBottom(context);
                popuMenu.setHint(getString(R.string.confirm_delete) + "?");
                popuMenu.addItem(new MenuItem(context, getString(R.string.delete_recording), Color
                    .parseColor("#F57070"), 0));
                popuMenu.setItemOnClickListener(new PopupMenuBottom.OnItemOnClickListener() {
                    @Override
                    public void onItemClick(MenuItem item, int pos) {
                        if (pos == 0) {
                            CallRecordManager.delete(peerSip);
                            finish();
                        }
                    }
                });
                popuMenu.show(avatar);
            }
        });
    }

    private void warning4gCall(final boolean isVideo, final RestContact restContact) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.alertdialog_warning_4g, null);
        final AlertDialog dlg = new AlertDialog.Builder(context).setView(view).create();
        dlg.show();
        Button submit = (Button) view.findViewById(R.id.submit);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(isVideo, restContact);
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

    private void makeCall(boolean isVideo, RestContact restContact) {
        Intent intent = new Intent();
        intent.setClass(context, ConnectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Bundle bundle = new Bundle();
        bundle.putBoolean("isVideoCall", isVideo);
        bundle.putString("sipNumber", peerSip);
        bundle.putBoolean("isFromDialing", true);
        intent.putExtras(bundle);

        startActivity(intent);

        DialOutRetryHandler.getInstance().cancel();
        DialOutRetryHandler.getInstance().init();
        DialOutRetryHandler.getInstance().startDialing(peerSip, restContact,
            isVideo);

        callFromThisPage = true;
    }

    boolean callFromThisPage = false;

    @Override
    protected void onResume() {
        super.onResume();

        LinearLayout historyLayout = (LinearLayout) findViewById(R.id.call_history_layout);
        CallHistoryView historyView = new CallHistoryView(context);
        historyView.setPeerSip(peerSip);
        historyLayout.removeAllViews();
        historyLayout.addView(historyView);

        historyView.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) historyView
            .getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ScreenUtil.dp_to_px(100);
        params.gravity = Gravity.TOP;
        historyView.setLayoutParams(params);
        historyLayout.invalidate();
    }
}

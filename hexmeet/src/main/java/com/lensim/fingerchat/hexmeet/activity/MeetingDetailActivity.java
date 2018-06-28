package com.lensim.fingerchat.hexmeet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.hexmeet.VideoMeeting;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.response.ret.RetArrayResponse;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestResult;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.bean.HexMessageItem;
import com.lensim.fingerchat.hexmeet.bean.VideoMeetingParticipants;
import com.lensim.fingerchat.hexmeet.fragment.HexMeetContentFrag;
import com.lensim.fingerchat.hexmeet.utils.DialOutRetryHandler;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import com.lensim.fingerchat.hexmeet.widget.MenuItem;
import com.lensim.fingerchat.hexmeet.widget.PopupMenuBottom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingDetailActivity extends BaseActivity {

    public static final int MSG_TYPE_HEX_MEET = 48;

    private Activity context;
    private HexMeetContentFrag contentFrag;
    private RestMeeting meeting;
    private View root;
    private ImageButton btn_end;
    private boolean isSDKMeeting = false;
    private int mFGMeetingID;
    private List<String> existContacts;
    private List<String> mJidList;
    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_meeting_detail);
        toolbar = findViewById(R.id.toolbar_hexmeet_create);
        toolbar.setTitleText("会议详情");
        initBackButton(toolbar,true);
        context = this;
        init();
    }

    private void init() {
        existContacts = new ArrayList<String>();
        mJidList = new ArrayList<String>();
        contentFrag = (HexMeetContentFrag) getSupportFragmentManager()
            .findFragmentById(R.id.hex_content_frag);
        meeting = contentFrag.getMeeting();
        for (RestContact item : meeting.getContacts()) {
            existContacts.add(item.getImageURL());
        }

        mFGMeetingID = meeting.getFGMeetingId();
        if (RestMeeting.DEFAULT_CONF_ID == mFGMeetingID) {
            getFGMeetingID();
        }
        root = contentFrag.getContentView();
        contentFrag.getHeadTitle().setText(R.string.conference_detail);

        RelativeLayout title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        title_bar.setVisibility(View.GONE);

        TextView contacts_hint = contentFrag.getContacts_hint();
        GridView contactGridView = contentFrag.getContactGridView();
        if (meeting.getUsers().size() + meeting.getContacts().size() == 0) {
            contacts_hint.setVisibility(View.GONE);
            contactGridView.setVisibility(View.GONE);
        }

        boolean invitedMe = false;
        int loginUserId = RuntimeData.getLogUser() == null ? -1 : RuntimeData.getLogUser().getId();
        List<RestContact> contacts = meeting.getContacts();
        for (RestContact contact : contacts) {
            if (contact.getUserId() == loginUserId) {
                invitedMe = true;
                break;
            }
        }
        if (!invitedMe) {
            List<RestUser> users = meeting.getUsers();
            for (RestUser user : users) {
                if (user.getId() == loginUserId) {
                    invitedMe = true;
                    break;
                }
            }
        }

        root.findViewById(R.id.buttons).setVisibility(View.VISIBLE);

        View conf_name = root.findViewById(R.id.title_view);
        conf_name.setClickable(false);
        conf_name.setEnabled(false);
        conf_name.setFocusable(false);

        View password = root.findViewById(R.id.password_view);
        password.setClickable(false);
        password.setEnabled(false);
        password.setFocusable(false);

        btn_end = (ImageButton) root.findViewById(R.id.btn_end);
        boolean applicantIsCurrentUser = false;
        String creater = getIntent().getStringExtra("creater");
        if (!StringUtils.isEmpty(creater)) {
            applicantIsCurrentUser = creater
                .equals(UserInfoRepository.getInstance().getUserInfo().getUsernick());
        }

        if (applicantIsCurrentUser) {
            root.findViewById(R.id.ll_btn_end).setVisibility(View.VISIBLE);
            btn_end = (ImageButton) root.findViewById(R.id.btn_end);
            btn_end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    terminateMeeting();
                }
            });
        }

        TextView videoJoin = (TextView) root.findViewById(R.id.left_button);
        findViewById(R.id.left_button_avatar).setVisibility(View.VISIBLE);
        videoJoin.setText(R.string.join_via_video);
        videoJoin.setTextColor(Color.parseColor("#2bbb6a"));
        root.findViewById(R.id.left_button_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isSipServerReachable(context)) {
                    return;
                }

                if (isSDKMeeting && !meeting.getStatus().equalsIgnoreCase("ONGOING")) {
                    Utils.showToast(context, R.string.conf_is_being_launched);
                    return;
                }

                if (NetworkUtil.is3GConnected(context)) {
                    warning4gConversation(true);
                } else {
                    joinMeeting(true);
                }
            }
        });

        root.findViewById(R.id.button_divider).setVisibility(View.VISIBLE);
        TextView audioJoin = (TextView) root.findViewById(R.id.right_button);
        findViewById(R.id.right_button_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.right_button_avatar).setVisibility(View.VISIBLE);
        audioJoin.setText(R.string.join_via_audio);
        audioJoin.setTextColor(Color.parseColor("#1f9ee6"));
        root.findViewById(R.id.right_button_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isSipServerReachable(context)) {
                    return;
                }

                if (isSDKMeeting && !meeting.getStatus().equalsIgnoreCase("ONGOING")) {
                    Utils.showToast(context, R.string.conf_is_being_launched);
                    return;
                }

                if (NetworkUtil.is3GConnected(context)) {
                    warning4gConversation(false);
                } else {
                    joinMeeting(false);
                }
            }
        });

        contentFrag.cancelEditable();
    }

    private void warning4gConversation(final boolean isVideo) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.alertdialog_warning_4g, null);
        final AlertDialog dlg = new AlertDialog.Builder(context).setView(view).create();
        dlg.show();

        view.findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                joinMeeting(isVideo);
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }

    private void joinMeeting(boolean isVideo) {
        String password = "";
        if (!StringUtils.isEmpty(meeting.getConfPassword())) {
            password = "*" + meeting.getConfPassword();
        }
        String confNumeric = meeting.getNumericId() + password;

        Intent intent = new Intent();
        intent.setClass(context, ConnectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Bundle bundle = new Bundle();
        bundle.putBoolean("isVideoCall", isVideo);
        bundle.putString("sipNumber", meeting.getNumericId() + "");
        intent.putExtras(bundle);

        startActivity(intent);

        DialOutRetryHandler.getInstance().cancel();
        DialOutRetryHandler.getInstance().init();
        DialOutRetryHandler.getInstance().startDialing(confNumeric, null, isVideo);
    }


    /**
     * Hex SDK服务器上结束会议
     **/
    private void terminateMeeting() {
        if (!NetworkUtil.isUcmReachable()) {
            Utils.showToast(context, R.string.network_unconnected);
            return;
        }

        PopupMenuBottom popuMenu = new PopupMenuBottom(context);
        popuMenu.setHint(getString(R.string.confirm_terminate) + "[" + meeting.getName() + "]?");
        popuMenu.addItem(
            new MenuItem(context, getString(R.string.terminate_conference),
                Color.parseColor("#F57070"),
                0));
        popuMenu.setItemOnClickListener(new PopupMenuBottom.OnItemOnClickListener() {
            @Override
            public void onItemClick(MenuItem item, int position) {
                if (position == 0) {
                    ApiClient.terminateMeeting(meeting.getId(), new Callback<RestResult>() {
                        @Override
                        public void onResponse(Call<RestResult> call,
                            Response<RestResult> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent();
                                intent.putExtra("confName", meeting.getName());
                                deleteFGMeeting();
                            } else {
                                Utils.showToast(context,
                                    getString(R.string.end) + "\"" + meeting.getName() + "\""
                                        + getString(R.string.fail) + ApiClient
                                        .fromErrorResponse(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResult> call, Throwable e) {
                        }
                    });
                }
            }
        });
        popuMenu.show(contentFrag.getHeadTitle());
    }


    /**
     * 飞鸽服务器上结束会议
     **/
    public void deleteFGMeeting() {
        String token = RuntimeData.getFGToken();
        String userName = UserInfoRepository.getUserName();
        if (RestMeeting.DEFAULT_CONF_ID == mFGMeetingID) {
            return;
        }
        Http.deleteHexMeeting(mFGMeetingID + "", userName, token)
            .compose(RxSchedulers.<RetObjectResponse<String>>compose())
            .subscribe(new BaseObserver<RetObjectResponse<String>>() {
                @Override
                public void onNext(RetObjectResponse<String> response) {
                    String msg = response.retMsg;
                    if (!TextUtils.isEmpty(msg) && !"null".equals(msg)) {
                        Utils.showToast(MeetingDetailActivity.this, msg);
                    }
                    context.finish();
                }
            });
    }

    /**
     * 给已存在的会议添加联系人
     **/
    public void JoinToExistMeeting() {
        if (contentFrag != null && contentFrag.getMeeting().getContacts() != null) {
            final List<VideoMeetingParticipants> listParticipants = new ArrayList<VideoMeetingParticipants>();
            List<RestContact> contactList = contentFrag.getMeeting().getContacts();
            Map<String, String> JIDMap = contentFrag.getContactsJIDMap();
            int count = contactList.size();
            RestContact temItem;
            for (int i = 0; i < count; i++) {
                temItem = contactList.get(i);
                if (!existContacts.contains(temItem.getImageURL())) {
                    listParticipants.add(
                        new VideoMeetingParticipants(JIDMap.get(temItem.getImageURL()),
                            temItem.getName(), temItem.getImageURL()));
                    existContacts.add(temItem.getImageURL());
                    mJidList.add(JIDMap.get(temItem.getImageURL()));
                }
            }
            //没有新加的人
            if (listParticipants.size() <= 0) {
                return;
            }

            Gson gson = new Gson();
            VideoMeeting pj = new VideoMeeting();
            pj.setToken(RuntimeData.getFGToken());
            pj.setMeetingParticipants(gson.toJson(listParticipants));
            pj.setMeetingconfId(meeting.getId() + "");

            Http.JoinToExistMeeting(pj)
                .compose(RxSchedulers.<RetObjectResponse<String>>compose())
                .subscribe(new BaseObserver<RetObjectResponse<String>>() {
                    @Override
                    public void onNext(RetObjectResponse<String> response) {
                        String msg = response.retMsg;
                        if (1 == response.retCode) {
                            sendMessages(meeting);
                        } else if (!StringUtils.isEmpty(msg) && !msg.equals("null")) {
                            Utils.showToast(MeetingDetailActivity.this, msg);
                        }
                    }
                });
        }
    }

    /***
     * 给与会人发与会消息
     * 不给我自己发消息
     * */
    private void sendMessages(RestMeeting meet) {
        HexMessageItem item = new HexMessageItem(MSG_TYPE_HEX_MEET, "",
            "", UserInfoRepository.getUsernick(), meet.getName(),
            meet.getNumericId() + "", meet.getStartTime() + "",
            meet.getDuration() + "", meet.getConfPassword(), meet.getRemarks());
        Gson gson = new Gson();
        final String toJson = gson.toJson(item);

        for (int i = 0, size = mJidList.size(); i < size; i++) {

            IChatRoomModel chatRoomModel = MessageManager.getInstance()
                .createMessage(mJidList.get(i), UserInfoRepository.getUserName(), toJson,
                    UserInfoRepository.getUsernick(), false, EMessageType.TEXT);
            MessageManager.getInstance().sendMessage(chatRoomModel);

        }
    }

    private void getFGMeetingID() {

        String ssoToken = SSOTokenRepository.getToken();
        String userName = SSOTokenRepository.getUserName();
        Http.getHexMeetingList(ssoToken, "2", userName, "10", "0")
            .compose(RxSchedulers.<RetArrayResponse<VideoMeeting>>compose())
            .subscribe(new BaseObserver<RetArrayResponse<VideoMeeting>>() {
                @Override
                public void onNext(RetArrayResponse<VideoMeeting> response) {
                    if (1 == response.retCode) {
                        for (int i = 0; i < response.retData.size(); i++) {
                            VideoMeeting meetingEntity = response.retData.get(i);
                            if (meetingEntity.getMeetingSIP().equals(meeting.getNumericId() + "")) {
                                mFGMeetingID = meetingEntity.getMeetingId();
                                break;
                            }
                        }
                    }
                }
            });
    }

}

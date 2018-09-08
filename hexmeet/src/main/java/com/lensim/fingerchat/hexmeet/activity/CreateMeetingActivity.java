package com.lensim.fingerchat.hexmeet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.BaseURL;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.hexmeet.LockUser;
import com.lensim.fingerchat.data.hexmeet.LockUser.DataBean;
import com.lensim.fingerchat.data.hexmeet.VideoMeeting;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestEndpoint;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestMeetingReq;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.bean.HexMessageItem;
import com.lensim.fingerchat.hexmeet.bean.VideoMeetingParticipants;
import com.lensim.fingerchat.hexmeet.conf.DefaultMeeting;
import com.lensim.fingerchat.hexmeet.fragment.HexMeetContentFrag;
import com.lensim.fingerchat.hexmeet.login.LoginService;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.ProgressUtil;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMeetingActivity extends FGActivity implements DefaultMeeting {

    private FGToolbar toolbar;
    private Activity context;
    private View root;
    private View submitButton;
    private TextView submitText;
    private HexMeetContentFrag contentFrag;
    private SharedPreferences sp;
    private ProgressUtil progress;
    private List<DataBean> mApplyedAccounts;
    private List<RestContact> contactList;


    @Override
    public void initView() {
        setContentView(R.layout.activity_create_meeting);
        toolbar = findViewById(R.id.toolbar_hexmeet_create);
        toolbar.setTitleText("创建会议");
        initBackButton(toolbar,true);
        context = this;

        initFragmengView();
    }


    private void initFragmengView() {
        contentFrag = (HexMeetContentFrag) getSupportFragmentManager().findFragmentById(R.id.hex_content_frag);
        root = contentFrag.getView();
        RelativeLayout title = root.findViewById(R.id.title_bar);
        title.setVisibility(View.GONE);

        submitButton = root.findViewById(R.id.left_button_layout);
        submitText = root.findViewById(R.id.left_button);
        progress = new ProgressUtil(context, 10000, new Runnable() {
            @Override
            public void run() {
                submitButton.setEnabled(true);
                submitText.setTextColor(Color.parseColor("#f04848"));
                Utils.showToast(context, R.string.schedule_meeting_timeout);
            }
        }, getString(R.string.schedulering));

        contentFrag.getHeadTitle().setText(R.string.conference_scheduler);
        contentFrag.getConfNumRow().setVisibility(View.GONE);
        submitText.setText(R.string.scheduler);
        submitButton.setOnClickListener(submitListener);
    }

    OnClickListener submitListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!NetworkUtil.isNetConnected(context)) {
                Utils.showToast(context, R.string.server_unavailable);
                return;
            }
            RestMeeting meeting = contentFrag.getMeeting();
            final List<RestContact> temp = meeting.getContacts();
            if (0 == meeting.getContacts().size()) {
                Toast.makeText(CreateMeetingActivity.this, getString(R.string.at_least_one_contact), Toast.LENGTH_LONG).show();
                return;
            }
            applyForCameras(meeting.getContacts().size(), temp);
        }
    };

    /**
     * 申请视频会议帐号
     */
    private void applyForCameras(int len, final List<RestContact> temp) {
        Http.applyForCameras(len - 1)
            .compose(RxSchedulers.<LockUser>compose())
            .subscribe(new BaseObserver<LockUser>() {
                @Override
                public void onNext(LockUser lockUser) {
                    if (lockUser.getResultCode() == 1) {
                        mApplyedAccounts = lockUser.getData();
                        contactList = new ArrayList<RestContact>();
                        String myJID = UserInfoRepository.getUserName() + "@" + BaseURL.DEFAULT_SERVER_NAME;
                        for (RestContact item : temp) {
                            if (!myJID.equals(item.getFgJID())) contactList.add(item);
                        }

                        if (contactList.size() == mApplyedAccounts.size()) {
                            for (int i = 0; i < mApplyedAccounts.size(); i++) {
                                RestContact contact = contactList.get(i);
                                if (!StringUtils.isEmpty(mApplyedAccounts.get(i).getId())) {
                                    contact.setId(Integer.parseInt(mApplyedAccounts.get(i).getId()));
                                }
                            }
                        }
                        //加入我自己
                        addMy(myJID);
                    } else {
                        Utils.showToast(context, lockUser.getErrMsg());
                    }
                    addMeeting();
                }
            });

    }

    //加入我自己
    private void addMy(String myJID) {
        RestContact contact = new RestContact();
        sp = context.getSharedPreferences(LoginService.HEX_SP_NAME, Context.MODE_PRIVATE);
        int hexID = sp.getInt(UserInfoRepository.getUserName() + LoginService.HEX_ID, 0);
        contact.setId(hexID);
        contact.setFgJID(myJID);
        contact.setName(UserInfoRepository.getUsernick());
        contact.setImageURL(String.format(Route.obtainAvater, UserInfoRepository.getUserName()));

        contactList.add(contact);
    }


    /***
     * 创建会议
     * */
    private void addMeeting() {
        submitButton.setEnabled(false);
        submitText.setTextColor(Color.parseColor("#919191"));
        progress.showDelayed(1500);

        final RestMeeting meeting = contentFrag.getMeeting();
        meeting.setName(contentFrag.getTitle());
        RestMeetingReq req = new RestMeetingReq();
        req.setName(meeting.getName());
        req.setStartTime(meeting.getStartTime() <=
            System.currentTimeMillis() + 300000 ? 0 : meeting.getStartTime());
        req.setDuration(meeting.getDuration());
        req.setLayout(meeting.getLayout());
        req.setGroupId(meeting.getGroupId());
        req.setRemarks(meeting.getRemarks());
        req.setConfPassword(meeting.getConfPassword());

        List<Integer> contactIds = new ArrayList<Integer>();
        final List<VideoMeetingParticipants> listParticipants = new ArrayList<VideoMeetingParticipants>();

        for (int i = 0; i < contactList.size(); i++) {
            RestContact temItem = contactList.get(i);
            contactIds.add(temItem.getId());
            listParticipants.add(new VideoMeetingParticipants(temItem.getFgJID(), temItem.getName(),
                temItem.getImageURL()));
        }
        req.setContactIds(contactIds);

        List<Integer> endpointIds = new ArrayList<Integer>();
        List<RestEndpoint> endpoints = meeting.getEndpoints();
        for (int i = 0; i < endpoints.size(); i++) {
            endpointIds.add(endpoints.get(i).getId());
        }
        req.setEndpointIds(endpointIds);

        addMeetingHttp(req, listParticipants);
    }


    private void addMeetingHttp(RestMeetingReq req, final List<VideoMeetingParticipants> listParticipants) {
        ApiClient.addMeeting(req, new Callback<RestMeeting>() {
            @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(Call<RestMeeting> call, Response<RestMeeting> response) {
                if (response.isSuccessful()) {
                    progress.dismiss();
                    RestMeeting meet = (RestMeeting) response.body();
                    if (null != meet) {
                        meet.setContacts(contactList);
                        sendMessages(meet);

                        Gson gson = new Gson();
                        String toJsonlistParticipants = gson.toJson(listParticipants);
                        postMeetingToFG(meet, toJsonlistParticipants);
                    }
                    finish();
                } else {
                    progress.dismiss();
                    submitButton.setEnabled(true);
                    submitText.setTextColor(Color.parseColor("#f04848"));
                    Utils.showToast(context, getString(R.string.scheduler_fail) + ApiClient.fromErrorResponse(response));
                }
            }

            @Override
            public void onFailure(Call<RestMeeting> call, Throwable e) {
            }
        });
    }

    /**
     * 加入到飞鸽的服务器
     */
    public void postMeetingToFG(RestMeeting meeting, String toJsonlistParticipants) {
        VideoMeeting pj = new VideoMeeting();
        pj.setToken(SSOTokenRepository.getInstance().getSSOToken().getFxToken());
        pj.setMeetingCreater(UserInfoRepository.getInstance().getUserInfo().getUsernick());
        pj.setMeetingName(meeting.getName());
        pj.setMeetingStart(TimeUtils.getDateString(meeting.getStartTime() + ""));

        pj.setMeetingEnd(TimeUtils.getDateString(meeting.getStartTime() + meeting.getDuration() + ""));
        pj.setMeetingPwd(meeting.getConfPassword());
        pj.setMeetingSIP(meeting.getNumericId() + "");
        pj.setMeetingRemark(meeting.getRemarks());
        pj.setMeetingParticipants(toJsonlistParticipants);
        pj.setMeetingconfId(meeting.getId() + "");

        postHexMeeting(pj, meeting);
    }


    private void postHexMeeting(VideoMeeting pj, final RestMeeting meeting) {
        Http.postHexMeeting(pj)
            .compose(RxSchedulers.<RetObjectResponse<String>>compose())
            .subscribe(new BaseObserver<RetObjectResponse<String>>() {
                @Override
                public void onNext(RetObjectResponse response) {
                    if (1 == response.retCode) {
                        Intent intent = new Intent(context, MeetingDetailActivity.class);
                        intent.putExtra("meeting", meeting);
                        intent.putExtra("creater", UserInfoRepository.getInstance().getUserInfo().getUserid());
                        context.startActivity(intent);
                    } else {
                        submitButton.setEnabled(true);
                        submitText.setTextColor(Color.parseColor("#f04848"));
                    }

                    Utils.showToast(context, response.retMsg);
                }
            });
    }


    @Override
    public RestMeeting getDefaultMeeting() {
        RestMeeting meeting = new RestMeeting();

        sp = getSharedPreferences("settings", 0);
        long duration = 2 * 3600000L;
        long five_minutes = 300000L;
        long now = System.currentTimeMillis();
        long start = (now / five_minutes + 1) * five_minutes;
        meeting.setStartTime(start);
        meeting.setDuration(duration);

        meeting.setLayout("0X0");
        meeting.setRemarks("");
        meeting.setStatus(null);
        int maxBandwidth = Integer
            .valueOf(sp.getString("callspeed_wifi", "128 Kbps").split(" ")[0]);
        meeting.setMaxBandwidth(maxBandwidth);

        meeting.setGroupId(-1);
        List<RestContact> contacts = new ArrayList<RestContact>();
        RestContact myself = RuntimeData.getSelfContact();
        if (myself != null) {
            myself.setName(UserInfoRepository.getUsernick());
            String string = String.format(Route.obtainAvater, UserInfoRepository.getUserName());
            myself.setImageURL(string);
            myself.setFgJID(UserInfoRepository.getUserName() + "@" + BaseURL.DEFAULT_SERVER_NAME);
            contacts.add(myself);
        }

        meeting.setContacts(contacts);
        meeting.setEndpoints(new ArrayList<RestEndpoint>());
        meeting.setUsers(new ArrayList<RestUser>());
        return meeting;
    }

    /***
     * 给与会人发与会消息
     *
     * TODO 不给我自己发消息
     * */
    private void sendMessages(RestMeeting meet) {
        List<String> fgUsersList = getFGUsers();
        if (fgUsersList != null && mApplyedAccounts != null && mApplyedAccounts.size() == fgUsersList.size()) {
            for (int i = 0, size = fgUsersList.size(); i < size; i++) {
                DataBean bean = mApplyedAccounts.get(i);
                String userId = fgUsersList.get(i);
                if (bean != null && userId != null) {
                    sendMessages(bean, meet, userId);
                }
            }
        }
    }


    //视频会议
    private static final int MSG_TYPE_HEX_MEET = 48;
    private void sendMessages(DataBean bean, RestMeeting meet, String userId) {
        HexMessageItem item = new HexMessageItem(MSG_TYPE_HEX_MEET,
            bean.getSIP(), bean.getUserCode(), UserInfoRepository.getUsernick(), meet.getName(),
            meet.getNumericId() + "", meet.getStartTime() + "", meet.getDuration() + "",
            meet.getConfPassword(), meet.getRemarks());
        Gson gson = new Gson();
        final String toJson = gson.toJson(item);

//        IChatRoomModel chatRoomModel = MessageManager.getInstance()
//            .createMessage(userId, UserInfoRepository.getUserName(), toJson,
//                UserInfoRepository.getUsernick(), false, EMessageType.TEXT);
//        MessageManager.getInstance().sendMessage(chatRoomModel);

    }



    private List<String> getFGUsers() {
        List<String> list;
        String hexUsers = SPSaveHelper.getStringValue(
            UserInfoRepository.getUserName() + "HexMeet", "fgUser", "");
        if (!TextUtils.isEmpty(hexUsers)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            list = gson.fromJson(hexUsers, type);
            return list;
        }
        return null;
    }
}

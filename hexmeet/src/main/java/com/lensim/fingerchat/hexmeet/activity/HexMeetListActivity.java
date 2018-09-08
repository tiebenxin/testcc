package com.lensim.fingerchat.hexmeet.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hexmeet.sdk.HexmeetFarendUserInfo;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.adapter.BaseRecyclerAdapter.OnItemClickListener;
import com.lensim.fingerchat.components.springview.container.DefaultFooter;
import com.lensim.fingerchat.components.springview.container.DefaultHeader;
import com.lensim.fingerchat.components.springview.widget.SpringView;
import com.lensim.fingerchat.data.hexmeet.VideoMeeting;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.response.ret.RetArrayResponse;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.ScreenManager.ScreenReceiver;
import com.lensim.fingerchat.hexmeet.adapter.LoadMoreAdapter;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.app.NetworkStateService;
import com.lensim.fingerchat.hexmeet.bean.VideoMeetingParticipants;
import com.lensim.fingerchat.hexmeet.db.Convertor;
import com.lensim.fingerchat.hexmeet.db.DaoSession;
import com.lensim.fingerchat.hexmeet.db.RestContact_;
import com.lensim.fingerchat.hexmeet.type.DatabaseHelper;
import com.lensim.fingerchat.hexmeet.type.DatabaseHelper.DatabaseType;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HexMeetListActivity extends BaseActivity {

    public final static String MEETING_ID = "meetingID";
    private static WeakReference<HexMeetListActivity> hexMeet = null;

    private final int REQUEST_MEET_DETAIL = 345;
    private final String PAGE_SIZE = "10";
    private int PAGE_NUM = 0;
    private SpringView springView;
    private RecyclerView recyclerView;
    private RelativeLayout noConference;
    private LoadMoreAdapter mAdapter;
    private List<VideoMeeting> items;
    private BroadcastReceiver screenReceiver;
    private String meetingID;
    private RestMeeting mSDKmeeting = null;
    private VideoMeeting mFGmeeting = null;
    private FGToolbar toolbar;


    public static HexMeetListActivity getInstance() {
        if (hexMeet != null) {
            return hexMeet.get();
        }
        return null;
    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_hex_meet_list);
        toolbar = findViewById(R.id.toolbar_hexmeet_main);
        toolbar.setTitleText("会议列表");
        initBackButton(toolbar,true);

        toolbar.setBtSearchDrawable(R.drawable.join_me, new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(HexMeetListActivity.this, "");
            }
        });

        toolbar.setBtMessageDrawable(R.drawable.appointment_me, new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                    new Intent(HexMeetListActivity.this, CreateMeetingActivity.class));
            }
        });
        springView = findViewById(R.id.springview_hexmeet);
        recyclerView = findViewById(R.id.recycler_hexmeet);
        noConference = findViewById(R.id.no_conference);

        if (getIntent() != null) {
            meetingID = getIntent().getStringExtra("meetingID");
        }
        hexMeet = new WeakReference<HexMeetListActivity>(this);

        stopService(new Intent(this, NetworkStateService.class));
        startService(new Intent(this, NetworkStateService.class));
    }

    @Override
    public void initData(final Bundle savedInstanceState) {
        initSpringView();
        initRecyclerView();

        screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(screenReceiver, filter);

        NetworkUtil.setSdkCallRate(HexMeetListActivity.this);
        initContactList();

        if (!StringUtils.isEmpty(meetingID)) {
            openActivity(this, meetingID);
        }
    }

    private void openActivity(Context context, String num) {
        Intent intent = new Intent(context, DialingActivity.class);
        intent.putExtra(DialingActivity.PARAMS_PHONE_NUM, num);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        getFGToken();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
        stopService(new Intent(this, NetworkStateService.class));
    }

    public void showVideoWindow() {
        if (App.getHexmeetSdkInstance().hasOngoingCall()) {
            Bundle b = new Bundle();
            boolean isVideoCall = false;
            boolean isRemoteVideoEnable = false;
            String strFarEndUserName = "";
            HexmeetFarendUserInfo farendUserInfo = App.getHexmeetSdkInstance().getFarendInfo();
            strFarEndUserName = farendUserInfo.getDisplayName();
            isRemoteVideoEnable = App.getHexmeetSdkInstance().isCurrentCallVideoEnabled();
            isVideoCall = isRemoteVideoEnable && App.isLocalVideoEnabled();

            b.putString("callName", strFarEndUserName);
            b.putBoolean("isVideoCall", isVideoCall);
            b.putLong("starttime", SystemClock.elapsedRealtime());

            Intent intent = new Intent(this, Conversation.class);
            intent.putExtras(b);

            startActivity(intent);
        } else {

        }
    }

    /****
     * 是否显示列表
     * */
    public void hideList(boolean ishide) {
        if (ishide) {
            noConference.setVisibility(View.VISIBLE);
            springView.setVisibility(View.GONE);
        } else {
            noConference.setVisibility(View.GONE);
            springView.setVisibility(View.VISIBLE);
        }
    }


    public void initRecyclerView() {
        mAdapter = new LoadMoreAdapter(HexMeetListActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder view, int position) {
                mFGmeeting = items.get(position);
                int confID = 0;
                try {
                    confID = Integer.parseInt(mFGmeeting.getMeetingconfId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getSDKMeeting(confID, mFGmeeting.getMeetingId());
            }
        });
    }

    /***
     * 飞鸽meeting转成SDK meeting
     *
     * **/

    public void toMeetingDetail(int FGmeetingId) {
        List<RestContact> contacts = new ArrayList<RestContact>();
        List<RestUser> users = new ArrayList<RestUser>();
        String str = mFGmeeting.getMeetingParticipants();
        String creater = mFGmeeting.getMeetingCreater();
        Gson gson = new Gson();
        if (!StringUtils.isEmpty(str)) {
            List<VideoMeetingParticipants> peopleList = gson.fromJson(str, new TypeToken<ArrayList<VideoMeetingParticipants>>() {
            }.getType());
            int len = peopleList.size();
            RestContact item;
            for (int i = 0; i < len; i++) {
                item = new RestContact();
                item.setImageURL(peopleList.get(i).getHeadPortrait());
                item.setUserName(peopleList.get(i).getNickname());
                item.setName(peopleList.get(i).getNickname());
                item.setFgJID(peopleList.get(i).getParticipants());
                contacts.add(item);
            }
        }
        mSDKmeeting.setContacts(contacts);
        mSDKmeeting.setUsers(users);
        mSDKmeeting.setFGMeetingId(FGmeetingId);

        Intent intent = new Intent(HexMeetListActivity.this, MeetingDetailActivity.class);
        intent.putExtra("meeting", mSDKmeeting);
        intent.putExtra("creater", creater);
        startActivityForResult(intent, REQUEST_MEET_DETAIL);
    }

    public void refresh() {
        if (items != null)
            items.clear();
        PAGE_NUM = 0;
        loadMore();
    }

    public void initSpringView() {
        springView.setHeader(new DefaultHeader(this));
        springView.setFooter(new DefaultFooter(this));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                onSpringRefresh();
            }

            @Override
            public void onLoadMore() {
                onSpringLoadMore();
            }
        });
    }

    public void onSpringRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefresh();
            }
        }, 1000);
    }

    public void onSpringLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMore();
            }
        }, 1000);
    }

    private void loadMore() {

        String ssoToken = SSOTokenRepository.getToken();
        String userName = SSOTokenRepository.getUserName();
        Http.getHexMeetingList(ssoToken, "2", userName, PAGE_SIZE, PAGE_NUM + "")
            .compose(RxSchedulers.<RetObjectResponse<String>>compose())
            .subscribe(new BaseObserver<RetObjectResponse<String>>() {
                @Override
                public void onNext(RetObjectResponse<String> response) {
                    Gson gson = new Gson();
                    if (1 == response.retCode) {
                        PAGE_NUM++;
                        Type type = new TypeToken<List<VideoMeeting>>() {}.getType();
                        items = gson.fromJson(response.retData, type);
                    }


                    if (!StringUtils.isEmpty(response.retMsg) && !response.retMsg.equals("null")) {
                            Utils.showToast(HexMeetListActivity.this, response.retMsg);
                        }

                    if (null != items && !items.isEmpty()) {
                        mAdapter.setItems(items);
                        hideList(false);
                    } else {
                        hideList(true);
                    }

                    springView.onFinishFreshAndLoad();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    springView.onFinishFreshAndLoad();
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    springView.onFinishFreshAndLoad();
                }
            });
    }

    private void pullToRefresh() {
        springView.onFinishFreshAndLoad();
    }


    private void initContactList() {
        DaoSession daoSession = DatabaseHelper.getSession(HexMeetListActivity.this, DatabaseType.CONTACT_LIST);
        List<RestContact_> query = daoSession.queryBuilder(RestContact_.class).list();
        if (query == null || query.size() == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!App.isNetworkConnected()) {
                        return;
                    }
                    App.clearContacts();
                    ApiClient.getContacts(new Callback<List<RestContact>>() {
                        @Override
                        public void onResponse(Call<List<RestContact>> call, retrofit2.Response<List<RestContact>> resp) {
                            List<RestContact> restContacts = resp.body();
                            if (restContacts != null && restContacts.size() > 0) {
                                for (RestContact rest : restContacts) {
                                    if (rest.getUserId() != 0) {
                                        if (rest.getUserId() == RuntimeData.getLogUser().getId()) {
                                            RuntimeData.setSelfContact(rest);
                                            AvatarLoader.downloadSelfAvatar();
                                        }

                                        if (rest.getCallNumber() != null && !rest.getCallNumber().trim().equals("")) {
                                            App.addContact(rest.getCallNumber(), rest);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<RestContact>> call, Throwable e) {
                        }
                    });
                }
            }).start();

            return;
        }

        final List<RestContact_> dbContacts = new ArrayList<RestContact_>();
        for (RestContact_ contact_ : query) {
            dbContacts.add(contact_);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.clearContacts();

                final List<RestContact> restContacts = new ArrayList<RestContact>();
                for (RestContact_ contact_ : dbContacts) {
                    restContacts.add(Convertor.fromDbRestContact(contact_));
                }

                for (RestContact rest : restContacts) {
                    if (rest.getUserId() != 0) {
                        if (rest.getUserId() == RuntimeData.getLogUser().getId()) {
                            RuntimeData.setSelfContact(rest);
                            AvatarLoader.downloadSelfAvatar();
                        }

                        if (rest.getCallNumber() != null && !rest.getCallNumber().trim().equals("")) {
                            App.addContact(rest.getCallNumber(), rest);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 1、先从飞鸽服务器取
     * 2、若不成功，循环5次
     * 3、若不成功，切换为SDK服务器再试
     * 4、最后，切换到飞鸽服务器
     **/
    private void getSDKMeeting(final int meetingId, final int FGmeetingId) {
        ApiClient.getMeeting(meetingId, RuntimeData.getFGToken(), new Callback<RestMeeting>() {
            @Override
            public void onResponse(Call<RestMeeting> call, Response<RestMeeting> response) {
                if (response.code() == 200) {
                    if (response.body() instanceof RestMeeting) {
                        if (response.body().getStatus().equalsIgnoreCase("FINISHED")) {
                            Toast.makeText(HexMeetListActivity.this,
                                getString(R.string.meeting_finished), Toast.LENGTH_LONG).show();
                        } else {
                            mSDKmeeting = (RestMeeting) response.body();
                            toMeetingDetail(FGmeetingId);
                        }
                    }
                } else {
                    try {
                        JSONObject obj1 = new JSONObject(response.errorBody().string());
                        if (obj1 != null) {
                            Toast.makeText(HexMeetListActivity.this,
                                obj1.getString("errorInfo"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestMeeting> call, Throwable e) {
            }
        });
    }

    private void getFGToken() {
        Http.getTempToken()
            .compose(RxSchedulers.<String>compose())
            .subscribe(new BaseObserver<String>() {
                @Override
                public void onNext(String s) {
                    if (!StringUtils.isEmpty(s)) {
                        RuntimeData.setFGToken(s);
                    }
                }
            });
    }

}

package com.lens.chatmodel.ui.profile;


import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_CHANGE_CONFIG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.SearchTableBean;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.UserInfoBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.contacts.ActivityUserSetting;
import com.lens.chatmodel.ui.contacts.FriendAliasActivity;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.toolbar.FGToolbar.OnFGToolbarClickListenter;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendDetailActivity extends BaseUserInfoActivity implements OnClickListener {

    private boolean isInvite;
    private ImageView iv_avatar, iv_gender;
    private TextView tv_name, tv_group_name, tv_userid, tv_group_title;
    private FrameLayout fl_detail_root;
    private LinearLayout ll_group_root;
    private LinearLayout ll_phontos_root;
    private ImageView iv_photo1;
    private ImageView iv_photo2;
    private ImageView iv_photo3;
    private ImageView iv_arrow;
    private Button bt_add_friend;
    private String userId;
    private IChatUser userBean;
    private boolean isSelf;
    private FGToolbar toolbar;
    private boolean hasChangeInfo;

    public static Intent createNormalIntent(Context context, String userId) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra(AppConfig.FRIEND_NAME, userId);
        return intent;
    }

    public static Intent createNormalIntent(Context context, UserBean user) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_friend_detail);
        toolbar = findViewById(R.id.viewTitleBar);
        initUser();
        initToolBar();
        iv_avatar = findViewById(R.id.iv_avatar);
        iv_gender = findViewById(R.id.iv_gender);
        tv_name = findViewById(R.id.tv_name);
        tv_userid = findViewById(R.id.tv_userid);
        ll_group_root = findViewById(R.id.ll_group_root);
        tv_group_name = findViewById(R.id.tv_group_name);
        tv_group_title = findViewById(R.id.divide_group_names);
        iv_arrow = findViewById(R.id.iv_arrow);
        ll_phontos_root = findViewById(R.id.ll_phontos_root);
        iv_photo1 = findViewById(R.id.iv_photo1);
        iv_photo2 = findViewById(R.id.iv_photo2);
        iv_photo3 = findViewById(R.id.iv_photo3);
        bt_add_friend = findViewById(R.id.bt_add_friend);
        fl_detail_root = findViewById(R.id.fl_detail_root);
        setButtonText(isInvite);
        initLisitener();
    }

    private void initUser() {
        Intent intent = getIntent();
        userId = intent.getStringExtra(AppConfig.FRIEND_NAME);
        if (intent.getExtras() != null) {
            Object object = intent.getExtras().get("user");
            if (object != null) {
                if (object instanceof UserBean) {
                    userBean = (UserBean) object;
                    if (!userBean.getUserId().equalsIgnoreCase(getUserId())) {//排出自己
                        if (userBean.getRelationStatus() == ERelationStatus.FRIEND.ordinal()) {
                            isInvite = false;
                        } else {
                            isInvite = true;
                        }
                    } else {
                        isInvite = false;
                    }
                }
                if (userBean != null) {
                    userId = userBean.getUserId();
                    setRightView();
                    if (userId.equalsIgnoreCase(getUserId())) {//是自己的
                        isSelf = true;
                    }
                }
            }

        }
        if (userBean == null) {
            loadUserInfo();
        }
        doSearch(userId);

    }

    private void initToolBar() {
        toolbar.setTitleText(ContextHelper.getString(R.string.private_info));
        initBackButton(toolbar, true);
        toolbar.setConfirmListener(new OnFGToolbarClickListenter() {
            @Override
            public void onClick() {
                if (userBean != null && userBean.getRelationStatus() == ERelationStatus.FRIEND
                    .ordinal()) {
                    Intent intent = new Intent(FriendDetailActivity.this,
                        ActivityUserSetting.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", (UserBean) userBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    T.show("非好友不能设置");
                }
            }
        });
    }

    /*
    * 是否初始化
    * */
    private void setRightView() {
        if (!TextUtils.isEmpty(userId) && !userId.equalsIgnoreCase(getUserId())) {
            toolbar.initRightView(createButton());
        }
    }

    public ImageView createButton() {
        ImageView button = new ImageView(this);
        button.setImageResource(R.drawable.title_add);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        int d = DensityUtil.dip2px(ContextHelper.getContext(), 1);
        params.setMargins(0, 0, d, 0);
        button.setLayoutParams(params);
        button.setPadding(d, d / 2, d, d / 2);
        return button;
    }


    private void initLisitener() {
        bt_add_friend.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
        ll_group_root.setOnClickListener(this);
        ll_phontos_root.setOnClickListener(this);
        fl_detail_root.setOnClickListener(this);
    }

    private void setButtonText(boolean invite) {
        isInvite = invite;
        if (invite) {
            bt_add_friend.setText(ContextHelper.getString(R.string.confirm_add_friend));
        } else {
            bt_add_friend.setText(ContextHelper.getString(R.string.send_message));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProfile();
        if (!isInvite) {
            ll_phontos_root.setVisibility(View.VISIBLE);

            getUserPhotos(userId);
        } else {
            ll_phontos_root.setVisibility(View.GONE);
        }
    }

    private void showProfile() {
        if (isSelf) {
            showSelfDetails();
        } else {
            showOtherDetails();
        }
    }


    //展示自己数据
    private void showSelfDetails() {
        if (userBean == null) {
            return;
        }
        ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(), iv_avatar);
        UIHelper.setGenderImage(userBean.getSex(), iv_gender);
        tv_name.setText(StringUtils.getUserNick(userBean.getUserNick(), userBean.getUserId()));
        tv_group_name.setText("");
        tv_userid.setText(userBean.getUserId());
    }

    //展示别人数据
    private void showOtherDetails() {
        if (userBean != null) {
            ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(), iv_avatar);
            UIHelper.setGenderImage(userBean.getSex(), iv_gender);
            tv_name.setText(ChatHelper
                .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                    userBean.getUserId()));
            if (!TextUtils.isEmpty(userBean.getGroup())) {
                tv_group_name.setVisibility(View.VISIBLE);
                tv_group_name.setText(userBean.getGroup());
            } else {
                tv_group_name.setText("");

            }
            tv_userid.setText(userBean.getUserId());
        }
    }

    @SuppressLint("CheckResult")
    private void getUserPhotos(String username) {
        Http.getPhotos("getphoto", username)
            .compose(RxSchedulers.io_main())
            .subscribe(friendCircleEntities -> {
                    if (friendCircleEntities == null || friendCircleEntities.isEmpty()) {
                        return;
                    }
                    try {
                        List<String> imgList = new ArrayList<>();
                        boolean isSizeOver = false;
                        for (FriendCircleEntity entity : friendCircleEntities) {
                            if (isSizeOver) {
                                break;
                            }
                            if (entity != null) {
                                String nameStr = entity.getPHO_ImageName();
                                String pathStr = entity.getPHO_ImagePath();
                                if (StringUtils.isEmpty(nameStr) || nameStr.contains(".mov") || nameStr
                                    .contains(".mp4")) {
                                    continue;
                                }
                                String url;
                                String[] names = nameStr.split(";");
                                int count = Integer.parseInt(entity.getPHO_ImageNUM());
                                int i = count > 3 ? 3 : count;
                                for (int j = 0; j < i; j++) {
                                    url = pathStr.replace("C:\\HnlensWeb\\", Route.Host) + names[j];
                                    url = url.replace("\\", "/");
                                    if (imgList.size() > 2) {
                                        isSizeOver = true;
                                        break;
                                    }
                                    imgList.add(url);
                                }
                            }
                        }

                        int len = imgList.size();
                        if (len == 3) {
                            ImageHelper.loadUrlAsBitmap(imgList.get(0), iv_photo1);
                            ImageHelper.loadUrlAsBitmap(imgList.get(1), iv_photo2);
                            ImageHelper.loadUrlAsBitmap(imgList.get(2), iv_photo3);
                        } else if (len == 2) {
                            ImageHelper.loadUrlAsBitmap(imgList.get(0), iv_photo1);
                            ImageHelper.loadUrlAsBitmap(imgList.get(1), iv_photo2);
                        } else if (len == 1) {
                            ImageHelper.loadUrlAsBitmap(imgList.get(2), iv_photo3);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                throwable -> Log.e("updateCircle", throwable.getMessage()));
    }

    @Override
    public void onClick(View v) {
        String id;
        if (userBean == null) {
            return;
        } else {
            id = userBean.getUserId();
        }
        if (v.getId() == bt_add_friend.getId()) {
            if (isInvite) {
                FingerIM.I.inviteFriend(userBean.getUserId());
            } else {
                if (userBean instanceof UserBean) {
                    Intent intent = ChatActivity
                        .createChatIntent(this, (UserBean) userBean);
                    startActivity(intent);
                    finish();
                }
            }
        } else if (v.getId() == ll_phontos_root.getId()) {
            Intent i = ActivitysRouter.getInstance()
                .invoke(this, ActivityPath.PHOTOS_ACTIVITY_PATH);
            if (i != null) {
                i.putExtra(ActivityPath.USER_ID, id);
                startActivity(i);
            }
        } else if (v.getId() == fl_detail_root.getId()) {
            Intent i = ActivitysRouter.getInstance()
                .invoke(this, ActivityPath.USER_INFO_ACTIVITY_PATH);
            hasChangeInfo = true;
            if (i != null) {
                if (isSelf) {
                    i.putExtra(ActivityPath.USER_ID, id);
                } else {
                    if (userBean instanceof UserBean) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ActivityPath.USER, (UserBean) userBean);
                        i.putExtras(bundle);
                    }
                }
                startActivityForResult(i, REQUEST_CHANGE_CONFIG);
            }
        } else if (v.getId() == ll_group_root.getId()) {
            if (!isSelf) {
                if (userBean.getRelationStatus() == ERelationStatus.FRIEND.ordinal()) {
                    Intent intent = FriendAliasActivity
                        .newIntent(this, userBean.getRemarkName(), userBean.getUserId(),
                            userBean.getGroup());
                    startActivityForResult(intent, 1);
                } else {
                    T.show("你们还不是好友关系，无法修改备注");
                }
            } else {
                T.show("不能对自己进行分组");

            }
        }
    }


    private void loadUserInfo() {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        if (userId.equalsIgnoreCase(getUserId())) {//是自己的
            isSelf = true;
            isInvite = false;
            userBean = ProviderUser.selectRosterSingle(ContextHelper.getContext(), userId);
        } else {//是别人的
            userBean = ProviderUser.selectRosterSingle(this, userId);
            if (userBean != null && userBean.getRelationStatus() == ERelationStatus.FRIEND
                .ordinal()) {
                isSelf = false;
                isInvite = false;
            } else {
                isSelf = false;
                isInvite = true;
//                doSearch(userId);
            }
        }
    }

    private void updateUserInfo() {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        if (userId.equalsIgnoreCase(getUserId())) {//是自己的
            isSelf = true;
            isInvite = false;
            userBean = ProviderUser.selectRosterSingle(ContextHelper.getContext(), userId);
            showProfile();
        } else {//是别人的
            userBean = ProviderUser.selectRosterSingle(this, userId);
            if (userBean != null && userBean.getRelationStatus() == ERelationStatus.FRIEND
                .ordinal()) {
                isSelf = false;
                isInvite = false;
                showProfile();
            } else {
                isSelf = false;
                isInvite = true;
                doSearch(userId);
            }
        }
    }

    private void doSearch(String value) {
        HttpUtils.getInstance()
            .getUserInfo(value, new IDataRequestListener() {
                @Override
                public void loadFailure(String reason) {
                    System.out.println("获取用户信息失败：" + reason);
                }

                @Override
                public void loadSuccess(Object object) {
                    if (object != null && object instanceof String) {
                        String result = (String) object;
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject != null) {
                                    String json = jsonObject.optString("content");
                                    UserInfoBean bean = GsonHelper
                                        .getObject(json, UserInfoBean.class);
                                    if (bean != null) {
                                        if (!isSelf && !isInvite) {
                                            UserBean temp = new UserBean();
                                            if (userBean != null) {
                                                bean.setRemarkName(userBean.getRemarkName());
                                            }
                                            temp.setBean(bean);
                                            temp.setRelationStatus(
                                                ERelationStatus.FRIEND.ordinal());
                                            temp.setHasReaded(ESureType.YES.ordinal());
                                            temp.setNewStatus(ESureType.NO.ordinal());
                                            ProviderUser
                                                .updateRoster(ContextHelper.getContext(), temp);
                                            updateUserBean(temp);
                                            MessageManager.getInstance().updateCacheUserBean(temp);
                                        } else {
                                            bean.setRelationStatus(
                                                ERelationStatus.NO_FRIEND.ordinal());
                                            UserBean temp = new UserBean();
                                            temp.setBean(bean);
                                            userBean = temp;
                                            if (userBean != null) {
                                                setRightView();
                                                ImageHelper
                                                    .loadAvatarPrivate(userBean.getAvatarUrl(),
                                                        iv_avatar);
                                                UIHelper
                                                    .setGenderImage(userBean.getSex(), iv_gender);
                                                tv_name.setText(StringUtils
                                                    .getUserNick(userBean.getUserNick(),
                                                        userBean.getUserId()));
                                                tv_userid.setText(userBean.getUserId());
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof ResponseEvent) {
            RespMessage message = ((ResponseEvent) event).getPacket();
            if (message != null && message.response != null) {
                int code = message.response.getCode();
                if (code == Common.ADD_SUCCESS) {//添加好友成功
                    T.showShort(R.string.add_friend_seccess);
                    setButtonText(false);
                } else if (code == Common.INVITE_OK) {//发送邀请成功
                    T.showShort(R.string.add_friend_seccess);
                    setButtonText(false);
                } else if (code == Common.SEND_OK) { //发送了邀请
                    T.showShort(R.string.add_friend_send_seccess);
                    if (userBean instanceof SearchTableBean) {
                        ((SearchTableBean) userBean)
                            .setRelationStatus(ERelationStatus.INVITE.ordinal());
                        ((SearchTableBean) userBean).setTime(System.currentTimeMillis());
                        ((SearchTableBean) userBean)
                            .setNewStatus(ESureType.YES.ordinal());//新朋友
                        ((SearchTableBean) userBean)
                            .setHasReaded(ESureType.YES.ordinal());//自己发的，标记为已读
                    } else if (userBean instanceof UserInfoBean) {
                        ((UserInfoBean) userBean)
                            .setRelationStatus(ERelationStatus.INVITE.ordinal());//新朋友
                        ((UserInfoBean) userBean).setTime(System.currentTimeMillis());
                        ((UserInfoBean) userBean).setNewStatus(ESureType.YES.ordinal());//自己发的，标记为已读
                        ((UserInfoBean) userBean)
                            .setHasReaded(ESureType.YES.ordinal());//自己发的，标记为已读
                    } else if (userBean instanceof UserBean) {
                        ((UserBean) userBean)
                            .setRelationStatus(ERelationStatus.INVITE.ordinal());//新朋友
                        ((UserBean) userBean).setTime(System.currentTimeMillis());
                        ((UserBean) userBean).setNewStatus(ESureType.YES.ordinal());//自己发的，标记为已读
                        ((UserBean) userBean)
                            .setHasReaded(ESureType.YES.ordinal());//自己发的，标记为已读
                    }
                    ProviderUser.updateRoster(ContextHelper.getContext(), userBean);
                } else if (code == Common.INVITE_FAILURE) { //邀请失败
                    T.showShort(R.string.add_friend_failed);
                    if (userBean instanceof SearchTableBean) {
                        ((SearchTableBean) userBean)
                            .setRelationStatus(ERelationStatus.INVITE.ordinal());
                        ((SearchTableBean) userBean).setTime(System.currentTimeMillis());
                        ((SearchTableBean) userBean).setNewStatus(1);
                    }
                    ProviderUser.updateRoster(ContextHelper.getContext(), userBean);
                } else if (code == Common.INVITE_DUMPLICATED) {//重复邀请
                    T.showShort(R.string.not_repeated);
                }
            }
        } else if (event instanceof RosterEvent) {
            RosterMessage message = ((RosterEvent) event).getPacket();
            int code = message.message.getCode();
            if (code == Common.ADD_SUCCESS) {
                setButtonText(false);
                T.showShort(R.string.add_friend_seccess);
            }
            List<RosterItem> rosters = message.message.getItemList();
            if (rosters != null && rosters.size() > 0) {
                userBean = ProviderUser
                    .selectRosterSingle(ContextHelper.getContext(), userBean.getUserId());
                if (code != Common.ADD_SUCCESS) {
                    if (userBean.getRelationStatus() == ERelationStatus.FRIEND.ordinal()) {
                        setButtonText(false);
                    } else {
                        setButtonText(true);
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {//修改名字成功
            if (resultCode == RESULT_OK) {
                updateUserInfo();
            }
        } else if (requestCode == REQUEST_CHANGE_CONFIG) {
            updateUserInfo();
        }
    }

    private void updateUserBean(UserBean bean) {
        userBean = bean;
        showProfile();
    }

    public void updateGroupViewWidth() {
        int totalWidth = ll_group_root.getMeasuredWidth();
        int titleWidth = tv_group_title.getMeasuredWidth();
        int arrowWidth = iv_arrow.getMeasuredWidth();
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = totalWidth - titleWidth - arrowWidth;
        tv_group_name.setLayoutParams(params);
    }
}

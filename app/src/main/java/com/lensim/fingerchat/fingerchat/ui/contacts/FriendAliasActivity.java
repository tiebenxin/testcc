package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.manager.RosterManager;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.fingerchat.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by LY309313 on 2016/11/5.
 */

public class FriendAliasActivity extends BaseUserInfoActivity {


    private BaseFragment fragment;
    private String userjid;
    private List<UserBean> usernames;
    public static final int CHANGE_ALIAS = 1;
    public static final int CREATE_MUC = 2;
    public static final int CHANGE_MUC_NAME = 3;
    public static final int CHANGE_MUC_SUBJECT = 4;
    public static final int MUC_NICK = 5;
    private int type = CHANGE_ALIAS;
    private String alias;
    private String roomName;
    private String subject;
    private String oldroom;
    private String account;
    private String usernick;
    private String resultName;
    //private boolean isOperating;

    @Override
    public void initView() {
        setContentView(R.layout.activity_friend_alias);
        FGToolbar toolbar = findViewById(R.id.mAliasToolbar);
        initBackButton(toolbar, true);
        type = getIntent().getIntExtra("option_type", CHANGE_ALIAS);
        switch (type) {
            case CHANGE_ALIAS:
                toolbar.setTitleText("备注信息");
                break;
            case CREATE_MUC:
            case CHANGE_MUC_NAME:
                toolbar.setTitleText("群聊名称");
                break;
        }
        toolbar.setConfirmBt(ContextHelper.getString(R.string.sure), v -> confirm());
    }

    public static Intent newIntent(Context context, String alias, String userjid) {
        Intent intent = new Intent(context, FriendAliasActivity.class);
        intent.putExtra("alias", alias);
        intent.putExtra("userjid", userjid);
        intent.putExtra("option_type", CHANGE_ALIAS);
        return intent;
    }

    public static Intent newCreateMucIntent(Context context, ArrayList<UserBean> usernames) {
        Intent intent = new Intent(context, FriendAliasActivity.class);
        intent.putParcelableArrayListExtra("muc_members", usernames);
        intent.putExtra("option_type", CREATE_MUC);
        return intent;
    }

    public static Intent newMucNameIntent(Context context, String room, String groupName) {
        Intent intent = new Intent(context, FriendAliasActivity.class);
        intent.putExtra("muc_jid", room);
        intent.putExtra("muc_name", groupName);
        intent.putExtra("option_type", CHANGE_MUC_NAME);
        return intent;
    }

    public static Intent newMucSubjectIntent(Context context, String room, String subject) {
        Intent intent = new Intent(context, FriendAliasActivity.class);
        intent.putExtra("muc_jid", room);
        intent.putExtra("muc_subject", subject);
        intent.putExtra("option_type", CHANGE_MUC_SUBJECT);
        return intent;
    }

    public static Intent newMucNickIntent(Context context, String room, String nick) {
        Intent intent = new Intent(context, FriendAliasActivity.class);
        intent.putExtra("muc_jid", room);
        intent.putExtra("muc_nick", nick);
        intent.putExtra("option_type", MUC_NICK);
        return intent;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        account = getUserId();
        type = getIntent().getIntExtra("option_type", CHANGE_ALIAS);
        switch (type) {
            case CHANGE_ALIAS:
                alias = getIntent().getStringExtra("alias");
                userjid = getIntent().getStringExtra("userjid");
                if (savedInstanceState == null) {
                    fragment = FriendAliasFragment.newInstance(alias, userjid);
                    getSupportFragmentManager()
                        .beginTransaction().replace(R.id.mAliasFragmentComtainer, fragment)
                        .commit();
                }
                break;

            case CREATE_MUC:
                // TODO: 2017/2/28 需要检查群聊名称的有效性
                usernames = getIntent().getParcelableArrayListExtra("muc_members");

                if (savedInstanceState == null) {
                    fragment = FriendAliasFragment.newMucInstance(alias);
                    getSupportFragmentManager()
                        .beginTransaction().replace(R.id.mAliasFragmentComtainer, fragment)
                        .commit();
                }
                break;

            case CHANGE_MUC_NAME:
                // TODO: 2017/2/28 需要检查群聊名称的有效性
                oldroom = getIntent().getStringExtra("muc_jid");
                roomName = getIntent().getStringExtra("muc_name");

                break;
            case CHANGE_MUC_SUBJECT:
                // TODO: 2017/2/28 需要检查群聊名称的有效性
                oldroom = getIntent().getStringExtra("muc_jid");
                subject = getIntent().getStringExtra("muc_subject");
                if (subject.length() > 100) {
                    T.showShort(this, "公告不能超过100个字符");
                    return;
                }

                break;

            case MUC_NICK:
                oldroom = getIntent().getStringExtra("muc_jid");
                usernick = getIntent().getStringExtra("muc_nick");
                if (savedInstanceState == null) {
                    fragment = FriendAliasFragment.newMucInstance(usernick);
                    getSupportFragmentManager()
                        .beginTransaction().replace(R.id.mAliasFragmentComtainer, fragment)
                        .commit();
                }
                break;
        }

    }

    protected void confirm() {
        resultName = ((FriendAliasFragment) fragment).getResult();
        if (!StringUtils.isEmpty(resultName)) {
            switch (type) {
                case CHANGE_ALIAS:
                    if (resultName.length() > 10) {
                        T.showShort(this, "不能超过十个字符");
                        return;
                    }
                    RosterManager.getInstance()
                        .updateRoster(userjid, resultName, RosterManager.NICK);
                    break;

                case CREATE_MUC:
                    if (resultName.length() > 20) {
                        T.showShort(this, "群名不能超过20个字符");
                        return;
                    }
                    break;

                case CHANGE_MUC_NAME:
                    if (resultName.length() > 20) {
                        T.showShort(this, "群名不能超过20个字符");
                        return;
                    }

                    showProgress("请稍后...", true);

                    dismissProgress();
                    break;
                case CHANGE_MUC_SUBJECT:
                    showProgress("请稍后...", true);

                    break;

                case MUC_NICK:
                    if (StringUtils.isEmpty(resultName)) {
                        break;
                    }

                    break;
            }

        } else {
            T.showShort(this, "请输入内容");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof RosterEvent) {
            RosterMessage message = ((RosterEvent) event).getPacket();
            if (message != null && message.message != null) {
            }
        } else if (event instanceof ResponseEvent) {
            RespMessage message = ((ResponseEvent) event).getPacket();
            if (message != null && message.response != null) {
                if (message.response.getCode() == Common.UPDATE_ROSTER_SUCCESS) {//更新成功
                    if (!TextUtils.isEmpty(userjid) && !TextUtils.isEmpty(resultName)) {
                        ProviderUser.updateRosterRemarkName(ContextHelper.getContext(), userjid, resultName);
                        finish();
                    }
                } else {
                    T.show("修改失败");
                }
            }
        }
    }

    public int getTextMaxLength() {
        int len = 10;
        switch (type) {
            case CHANGE_ALIAS:
                len = 10;
                break;

            case CREATE_MUC:
                len = 20;

                break;

            case CHANGE_MUC_NAME:
                len = 20;
                break;
            case CHANGE_MUC_SUBJECT:
                len = 20;
                break;

            case MUC_NICK:
                len = 10;
                break;
        }
        return len;
    }
}

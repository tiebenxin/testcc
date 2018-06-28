package com.lensim.fingerchat.fingerchat.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.annotation.Path;
import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.Resp.Message;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ChatMessageEvent;
import com.lens.chatmodel.eventbus.MucActionEvent;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.ResponseType;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.net.network.NetworkReceiver;
import com.lens.chatmodel.net.network.NetworkUtils;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.CodeHelper;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.data.HttpChannel;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityNewMainBinding;
import com.lensim.fingerchat.fingerchat.ui.chat.FragmentTabMessage;
import com.lensim.fingerchat.fingerchat.ui.code.QRCodeScanActivity;
import com.lensim.fingerchat.fingerchat.ui.contacts.FragmentTabContacts;
import com.lensim.fingerchat.fingerchat.ui.login.PermitLoginActivity;
import com.lensim.fingerchat.fingerchat.ui.me.UserCenterFragment;
import com.lensim.fingerchat.fingerchat.ui.search.ActivitySearchContacts;
import com.lensim.fingerchat.fingerchat.ui.settings.SettingsFragment;
import com.lensim.fingerchat.fingerchat.ui.work_center.FragmentTabWorkCenter;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_SCAN_CODE;

@Path(ActivityPath.ACTIVITY_MAIN_PATH)
public class MainActivity extends BaseUserInfoActivity {

    private static final String EXTRA_PAGE = "page";
    public static final int MESSAGE = 0; // 消息
    public static final int CONTACT = 1; // 联系人
    public static final int WORK = 2; // 工作中心
    public static final int ME = 3; // 个人中心
    public static final int SETTING = 4; // 设置

    private ActivityNewMainBinding viewBinding;

    private NetworkReceiver mNetworkReceiver;


    @CurPage
    int mCurrentPager = WORK; // 当前页面,先不赋值

    @IntDef({MESSAGE, CONTACT, WORK, ME, SETTING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CurPage {

    }

    private List<PageModel> pageModels = new ArrayList<>();

    {
        pageModels.add(new PageModel(FragmentTabMessage.newInstance(), R.string.tab_message,
            R.drawable.selector_icon_message));
        pageModels.add(new PageModel(FragmentTabContacts.newInstance(), R.string.tab_contacts,
            R.drawable.selector_icon_contact));
        pageModels.add(new PageModel(FragmentTabWorkCenter.newInstance(), R.string.tab_work,
            R.drawable.selector_icon_work));
        pageModels.add(new PageModel(UserCenterFragment.newInstance(), R.string.tab_me,
            R.drawable.selector_icon_me));
        pageModels.add(new PageModel(SettingsFragment.newInstance(), R.string.setting,
            R.drawable.selector_icon_settings));
    }

    /**
     * 提供静态方法给调用者
     */
    public static void start(Context context, @CurPage int page) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.putExtra(EXTRA_PAGE, page);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_main);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUnreadCounts();
        if (getCurrentFocus() != null) {
            hideSoftKeyboard(getCurrentFocus());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpChannel.getInstance().clear();
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_SCAN_CODE:
                Bundle bundle = data.getExtras();
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if (result == null) {
                        return;
                    }
                    String[] split = result.split(CodeHelper.DEFAULT_SPLIT);
                    if (split != null && split.length > 2) {
                        int type = Integer.parseInt(split[0]);
                        String userId = split[1];
                        if (type == CodeHelper.TYPE_PRIVATE) {
                            Intent intent = FriendDetailActivity
                                .createNormalIntent(this, userId);
                            startActivity(intent);
                        } else if (type == CodeHelper.TYPE_MUC) {//邀请入群
                            ArrayList<String> users = new ArrayList<>();
                            users.add(getUserId());
                            MucManager.getInstance()
                                .mucMberOperation(users, Muc.MOption.Join, split[1]);
                        } else if (type == CodeHelper.TYPE_NET) {//扫码登录
                            Intent intent = PermitLoginActivity.newIntent(this, split[1]);
                            startActivity(intent);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_muc) {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", Constant.GROUP_SELECT_MODE_CREATE);
            toActivity(GroupSelectListActivity.class, bundle);
        } else if (item.getItemId() == R.id.action_addfriends) {
            toActivity(ActivitySearchContacts.class);
        } else if (item.getItemId() == R.id.action_scan) {
            if (!AppManager.getInstance().checkCamara(MainActivity.this)) {
                return false;
            }
            Intent intent = new Intent();
            intent.setClass(this, QRCodeScanActivity.class);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUEST_SCAN_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    @Override
    public void initView() {
        // TODO BaseActivity待重构
    }

    private void init() {
        initBundle();
        initToolBar();
        initTabs();
        initViewPage();
        loadAuthority();
        loadMucs();
        checkNetwork();
        checkSSOLoginValid();
    }

    private void checkSSOLoginValid() {
        if (SSOTokenRepository.getInstance().getSSOToken() == null
            || SSOTokenRepository.getTokenValidTime() <= System.currentTimeMillis()) {
            String userId = UserInfoRepository.getUserName();
            String password = PasswordRespository.getPassword();
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password)) {
                ssoLogin(userId, password);
            }
        }
    }

    /**
     * 单点登录 拿到Token，存SP
     **/
    private void ssoLogin(final String userid, final String pwd) {
        HttpUtils.getInstance().ssoLogin(userid, pwd)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject<SSOToken>>(false) {
                @Override
                public void onHandleSuccess(ResponseObject<SSOToken> response) {
                    if (response.code == 10) {
                        SSOToken token = response.result;
                        if (token != null) {
                            token.setUserid(userid);
                            token.setTokenValidTime(
                                System.currentTimeMillis() + token.getLifetime() * 1000);
                            SSOTokenRepository.getInstance().setSSOToken(token);
                        }
                    } else {
                        Log.e("ssoLogin", response.msg);
                    }
                }
            });
    }


    private void checkNetwork() {
        if (!NetworkUtils.isNetAvaliale()) {
            doRegisterNetReceiver(ENetStatus.ERROR_NET);
        }
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCurrentPager = bundle.getInt(EXTRA_PAGE, WORK);
        }
    }

    private void initToolBar() {
        setSupportActionBar(viewBinding.viewTitleBar);
        viewBinding.viewTitleBar.setTitleText(pageModels.get(mCurrentPager).titleRes);
    }

    /**
     * 初始化ViewPage
     */
    private void initViewPage() {
        viewBinding.viewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pageModels.get(position).fragment;
            }

            @Override
            public int getCount() {
                return pageModels.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {

            }
        });
        viewBinding.viewPage.addOnPageChangeListener(
            new TabLayout.TabLayoutOnPageChangeListener(viewBinding.bottomTabLayout));
        // 设置默认显示第几个页面
        viewBinding.viewPage.setCurrentItem(mCurrentPager, false);
    }

    /**
     * 设置添加Tab
     */
    private void initTabs() {
        for (PageModel pageModel : pageModels) {
            // 创建一个新的Tab
            TabLayout.Tab tab = viewBinding.bottomTabLayout.newTab();
            View view = getLayoutInflater().inflate(R.layout.view_table_custom, null);
            tab.setCustomView(view);

            // 初始化Tab
            ImageView ivTab = view.findViewById(R.id.iv_tab);
            ivTab.setImageResource(pageModel.iconRes);
            TextView tvTitle = view.findViewById(R.id.tv_tab);
            tvTitle.setText(pageModel.titleRes);

            // 添加Tab
            viewBinding.bottomTabLayout.addTab(tab);
        }
        viewBinding.bottomTabLayout.addOnTabSelectedListener(
            new TabLayout.ViewPagerOnTabSelectedListener(viewBinding.viewPage) {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    closeFloatMenu();
                    // 设置标题
                    int postion = tab.getPosition();
                    viewBinding.viewTitleBar
                        .setTitleText(pageModels.get(postion).titleRes);
                    viewBinding.viewPage.setCurrentItem(postion, false);
                    showToolBar(postion == ME ? false : true);
                    if (postion == MESSAGE || postion == CONTACT) {//刷新消息页面，和通讯录页面
                        pageModels.get(postion).fragment.notifyResumeData();
                    }
                }
            });
    }

    /**
     * 从网上获取用户权限
     */
    private void loadAuthority() {
        HttpUtils.getInstance().getAuthorityById(getUserId(), new IDataRequestListener() {
            @Override
            public void loadFailure(String reason) {

            }

            @Override
            public void loadSuccess(Object object) {
                if (object != null && object instanceof String) {
                    String s = (String) object;
                    if (TextUtils.isEmpty(s)) {
                        return;
                    }
                    SPHelper.saveValue(getUserId() + AppConfig.AUTHORITY_SETTED, s);
                    if (s.contains("003002")) {
                        MainActivity.this.getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    }
                }
            }
        });
    }

    /*
     * 加载所有群组信息
     * */
    private void loadMucs() {
        int count = MucInfo.selectMucCount(ContextHelper.getContext());
        if (count <= 0) {
            MucManager.getInstance().qHomeAllRoomInfo();
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof RosterEvent) {
            RosterMessage message = ((RosterEvent) event).getPacket();
            if (message != null && message.message != null) {
                pageModels.get(CONTACT).fragment.notifyRequestResult(event);
            }
        } else if (event instanceof RefreshEvent) {
            RefreshEntity entity = ((RefreshEvent) event).getEntity();
            if (entity != null && entity.getActivity() == ChatEnum.EActivityNum.MAIN.value) {
                if (entity.getFragment() == ChatEnum.EFragmentNum.TAB_MESSAGE.value) {
                    pageModels.get(MESSAGE).fragment.notifyResumeData();
                    initUnreadCounts();
                } else if (entity.getFragment() == ChatEnum.EFragmentNum.TAB_CONTACTS.value) {
                    initUnreadCounts();
                }
            }
        } else if (event instanceof ChatMessageEvent) {
            pageModels.get(MESSAGE).fragment.notifyResumeData();
            initUnreadCounts();
        } else if (event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent) event;
            if (response.getType() == ResponseType.LOGIN) {//断网重登陆
                Message msg = response.getPacket().response;
                if (msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                    if (FingerIM.I.isLoginConflicted()) {
                        FingerIM.I.setLoginConflicted(false);
                    }
                    AppManager.getInstance().setLoginStatus(true);
                    pageModels.get(MESSAGE).fragment.notifyResumeData();
                }
            }
            if (response.getType() == ResponseType.NO_LOGIN) {//账号异常退出登录
                FingerIM.I.loginError();
                AppManager.getInstance().setLoginStatus(false);
                ((FragmentTabMessage) pageModels.get(MESSAGE).fragment)
                    .notifyNetStatusChange(ENetStatus.ERROR_LOGIN);
            } else {
                RespMessage message = (response).getPacket();
                if (message != null && message.response != null) {
                    pageModels.get(CONTACT).fragment.notifyRequestResult(event);
                }
            }
        } else if (event instanceof NetStatusEvent) {
            NetStatusEvent netEvent = (NetStatusEvent) event;
            ChatEnum.ENetStatus status = netEvent.getStatus();
            if (status == ENetStatus.ERROR_NET) {
                doRegisterNetReceiver(status);
                ((FragmentTabMessage) pageModels.get(MESSAGE).fragment)
                    .notifyNetStatusChange(status);
            } else if (status == ENetStatus.ERROR_CONNECT || status == ENetStatus.SUCCESS_ON_NET
                || status == ENetStatus.SUCCESS_ON_SERVICE) {
                if (status == ENetStatus.SUCCESS_ON_SERVICE) {
                    System.out.println("sss--收到连接成功");
                }
                if (status == ENetStatus.ERROR_CONNECT && !NetworkUtils.isNetAvaliale()) {
                    doRegisterNetReceiver(ENetStatus.ERROR_NET);
                }
                ((FragmentTabMessage) pageModels.get(MESSAGE).fragment)
                    .notifyNetStatusChange(status);
            } else if (status == ENetStatus.LOGIN_CONFLICTED) {
                FingerIM.I.setLoginConflicted(true);
                System.out.println("sss-收到登录冲突消息");
                Intent intent = ActivitysRouter.getInstance()
                    .invoke(this, ActivityPath.USER_CONFLICT_ACTIVITY_PATH);
                if (intent != null) {
                    intent.putExtra(ActivityPath.CLOSE_ERROR, 0);
                    startActivity(intent);
                }
            }
        } else if (event instanceof RefreshEvent) {
            RefreshEntity entity = ((RefreshEvent) event).getEntity();
            if (entity != null) {
                int activity = entity.getActivity();
                if (activity > 0 && activity == ChatEnum.EActivityNum.MAIN.ordinal()) {
                    pageModels.get(MESSAGE).fragment.notifyResumeData();
                    initUnreadCounts();
                }
            }
        } else if (event instanceof MucActionMessageEvent) {
            MucActionMessageEvent messageEvent = (MucActionMessageEvent) event;
            MucActionMessage message = messageEvent.getPacket();
            MucAction action = message.action;
            if (action.getAction() == MOption.Join) {
                Intent intent = ChatActivity
                    .createChatIntent(MainActivity.this, action.getMucid(), action.getMucname(),
                        EChatType.GROUP.ordinal(), EChatBgId.DEFAULT.ordinal(),
                        ESureType.NO.ordinal(), ESureType.NO.ordinal());
                startActivity(intent);
            }
        }
    }

    /*
     * 断网后，注册网络状态的广播接受者
     * */

    private void doRegisterNetReceiver(ENetStatus status) {
        if (status == ENetStatus.ERROR_NET) {
            if (mNetworkReceiver == null) {
                mNetworkReceiver = new NetworkReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(mNetworkReceiver, filter);
            }
        }
    }

    @SuppressLint("CheckResult")
    public void initUnreadCounts() {
        Observable.just(0)
            .map(integer -> {
                int[] counts = new int[5];
                counts[0] = MessageManager.getInstance().getTotalUnreadMessageCount();//会话列表
                counts[1] = ProviderUser.getUnreadedRosterCount();//通讯录
                counts[2] = 0;
                counts[3] = 0;
                counts[4] = 0;
                return counts;
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ints -> {
                int tabCount = viewBinding.bottomTabLayout.getTabCount();
                for (int i = 0; i < tabCount; i++) {
                    View view = viewBinding.bottomTabLayout.getTabAt(i).getCustomView();
                    TextView tvPoint = view.findViewById(R.id.tv_point);
                    int point = ints[i];
                    if (point > 0) {
                        tvPoint.setVisibility(View.VISIBLE);
                        point = (point > 99 ? 99 : point);
                        tvPoint.setText(point + "");
                    } else {
                        tvPoint.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }

    /**
     * 是否显示toolbar
     */
    private void showToolBar(boolean isShow) {
        viewBinding.viewTitleBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private static class PageModel {

        BaseFragment fragment; // 内容
        @StringRes
        int titleRes; // 标题
        @DrawableRes
        int iconRes; // 默认图标

        public PageModel(BaseFragment fragment, int titleRes, int iconRes) {
            this.fragment = fragment;
            this.titleRes = titleRes;
            this.iconRes = iconRes;
        }
    }

    public void closeFloatMenu() {
        viewBinding.floatMenu.closeMenu();
    }

}

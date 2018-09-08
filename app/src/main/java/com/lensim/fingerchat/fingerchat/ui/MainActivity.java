package com.lensim.fingerchat.fingerchat.ui;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_SCAN_CODE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.webview.BrowserActivity;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.AckListener;
import com.fingerchat.api.listener.ConflictListener;
import com.fingerchat.api.listener.MucListener;
import com.fingerchat.api.listener.UserListener;
import com.fingerchat.api.message.ConflictMessage;
import com.fingerchat.api.message.ExcuteResultMessage;
import com.fingerchat.api.message.MucActionMessage;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.proto.message.Excute.ExcuteMessage;
import com.fingerchat.proto.message.Excute.ExcuteType;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.User;
import com.google.common.io.BaseEncoding;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.base.ChatEnvironment;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ChatMessageEvent;
import com.lens.chatmodel.eventbus.ExcuteEvent;
import com.lens.chatmodel.eventbus.MucActionMessageEvent;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.MucManager;
import com.lens.chatmodel.manager.NotifyManager;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.net.network.NetworkReceiver;
import com.lens.chatmodel.net.network.NetworkUtils;
import com.lens.chatmodel.ui.emoji.ExpressionActivity;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.message.TextPreviewActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.route.annotation.Path;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.global.FGEnvironment;
import com.lensim.fingerchat.commons.helper.CodeHelper;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.Api;
import com.lensim.fingerchat.data.HttpChannel;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.help_class.TokenHelper;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.db.DaoManager;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.SystemApi;
import com.lensim.fingerchat.fingerchat.databinding.ActivityNewMainBinding;
import com.lensim.fingerchat.fingerchat.manager.DataClearMananger;
import com.lensim.fingerchat.fingerchat.model.bean.VersionInfoBean;
import com.lensim.fingerchat.fingerchat.model.result.GetVersionInfoResult;
import com.lensim.fingerchat.fingerchat.ui.chat.FragmentTabMessage;
import com.lensim.fingerchat.fingerchat.ui.code.QRCodeScanActivity;
import com.lensim.fingerchat.fingerchat.ui.contacts.FragmentTabContacts;
import com.lensim.fingerchat.fingerchat.ui.login.LoginActivity;
import com.lensim.fingerchat.fingerchat.ui.login.PermitLoginActivity;
import com.lensim.fingerchat.fingerchat.ui.main.PageModel;
import com.lensim.fingerchat.fingerchat.ui.main.TabAdapter;
import com.lensim.fingerchat.fingerchat.ui.me.UserCenterFragment;
import com.lensim.fingerchat.fingerchat.ui.search.ActivitySearchContacts;
import com.lensim.fingerchat.fingerchat.ui.settings.SettingsFragment;
import com.lensim.fingerchat.fingerchat.ui.work_center.FragmentTabWorkCenter;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

@Path(ActivityPath.ACTIVITY_MAIN_PATH)
public class MainActivity extends BaseUserInfoActivity implements UserListener, ConflictListener {

    private static final String EXTRA_PAGE = "page";
    public static final int MESSAGE = 0; // 消息
    public static final int CONTACT = 1; // 联系人
    public static final int WORK = 2; // 工作中心
    public static final int ME = 3; // 个人中心
    public static final int SETTING = 4; // 设置

    public static final int HANDLER_EXIT = 10; // 是否退出
    public static final int HANDLER_OFFLINE = 11; // 是否加载了离线消息
    public static final int HANDLER_MUC = 12; // 是否加载了群组信息
    public static final int HANDLER_ROSTER = 13; // 是否加载了通讯录消息
    public static final int HANDLER_RESEND = 14; // 自动发送发送失败的文本消息

    private ActivityNewMainBinding viewBinding;

    private NetworkReceiver mNetworkReceiver;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                case HANDLER_EXIT:
                    isExit = false;
                    break;
                case HANDLER_OFFLINE:
                    loadOfflineMessage();
                    break;
                case HANDLER_ROSTER:
                    loadRosters();
                    break;
                case HANDLER_MUC:
                    loadMucs();
                    break;
                case HANDLER_RESEND:
                    checkAndSendFailMessage();
                    break;
            }
        }
    };


    @CurPage
    int mCurrentPager = WORK; // 当前页面,先不赋值
    private boolean isExit;
    private boolean hasOfflineMessage;
    private boolean isLoadRoster = false;
    private boolean isLoadMuc = false;
    private TabAdapter tabAdapter;

    @Override
    public void onReceivedUserinfo(UserInfoMessage message) {
        UserInfo info = createUserInfo(message.userInfo);
        if (info != null) {
            checkSSOLoginValid();
            UserInfoRepository.getInstance().setUserInfo(info);
            if (!TextUtils.isEmpty(DaoManager.getUserID())) {
                ProviderUser
                    .updateUser(ContextHelper.getContext(), info);
            }
            FGEnvironment.getInstance()
                .initUserInfo(RosterManager.getInstance().createUser(info));
        }
    }

    @Override
    public void onReceivedConflictListener(ConflictMessage message) {
        FingerIM.I.getClient().stop();
        if (Common.ACCOUNT_CELANED == message.message.getCode()) {

        } else if (Common.ACCOUNT_LOCKED == message.message.getCode()) {

        } else {
            Intent intent = ActivitysRouter.getInstance()
                .invoke(this, ActivityPath.USER_CONFLICT_ACTIVITY_PATH);
            if (intent != null) {
                intent.putExtra(ActivityPath.CLOSE_ERROR, 0);
                startActivity(intent);
                finish();
            }
        }

    }

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
        doRegisterNetReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSSOLoginValid();
        resumeData();
        checkRosters();
        registerIMListener();
        checkLogin();
        checkMucListenerRegister();
        initUnreadCounts();
        if (getCurrentFocus() != null) {
            hideSoftKeyboard(getCurrentFocus());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MessageManager.getInstance().removeAckListener();
    }

    private void resumeData() {
        pageModels.get(mCurrentPager).getFragment().notifyResumeData();
    }

    private void registerIMListener() {
        ClientConfig.I.registerListener(UserListener.class, this);
        MessageManager.getInstance().registerAckListener();
        ClientConfig.I.registerListener(ConflictListener.class, this);
    }

    private void removeIMListener() {
        ClientConfig.I.removeListener(UserListener.class, this);
        ClientConfig.I.removeListener(ConflictListener.class, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeIMListener();
        HttpChannel.getInstance().clear();
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
        ClientConfig.I.removeListener(MucListener.class, MucManager.getInstance());
        NotifyManager.getInstance().clearNotification();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return false;
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(HANDLER_EXIT, 2000);
        } else {
            finish();
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
                        } else if (type == CodeHelper.TYPE_NET) {//url
//                            Intent intent = new Intent(this, BrowserActivity.class);
//                            Uri uri = Uri.parse(split[1]);
//                            intent.setData(uri);
//                            startActivity(intent);
                            Intent intent = new Intent(this, TextPreviewActivity.class);
                            intent.putExtra("text", result);
                            startActivity(intent);
                        } else if (type == CodeHelper.TYPE_LOGIN) {//扫码登录第三方
                            Intent intent = PermitLoginActivity.newIntent(this, split[1], split[2]);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, TextPreviewActivity.class);
                            intent.putExtra("text", result);
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
        checkVersion();
        initDao();
        initBundle();
        initToolBar();
        initTabs();
        initViewPage();
//        checkNetwork();
        loadAuthority();
        loadRosters();
        loadMucs();
        loadOfflineMessage();
        checkUnreadCountIsRight();
        checkAndSendFailMessage();
        loadEmoticons();
    }

    private void initDao() {
        if (TextUtils.isEmpty(DaoManager.getUserID())) {
            String userId = AppConfig.INSTANCE.get(AppConfig.ACCOUT);
            if (!TextUtils.isEmpty(userId)) {
                DaoManager.initUserId(userId);
                System.out.println(LoginActivity.class.getSimpleName() + "--initDao");
            } else {
                //手机号登录
                String user = UserInfoRepository.getUserId();
                if (!TextUtils.isEmpty(user)) {
                    DaoManager.initUserId(user);
                    System.out.println(LoginActivity.class.getSimpleName() + "--initDao");
                } else {
                    T.show("未获取到用户信息，请重新登录");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
            if (UserInfoRepository.getInstance().getUserInfo() != null) {
                ProviderUser
                    .updateUser(ContextHelper.getContext(),
                        UserInfoRepository.getInstance().getUserInfo());
            }
        }
    }

    private void checkSSOLoginValid() {
        if (!TokenHelper.isSSOTokenValid(getUserId())) {
            String userId = getUserId();
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
                            token.setUserId(userid);
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

    private void checkLogin() {
        if (FingerIM.I.hasStarted()) {
            if (FingerIM.I.isConnected()) {
                if (!FingerIM.I.isLogin()) {
                    String userId = UserInfoRepository.getUserId();
                    String password = PasswordRespository.getPassword();
                    if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password)) {
                        FingerIM.I.login(userId, password);
                    }
                }
            }
        }
    }

    private void checkMucListenerRegister() {
        Collection<MucListener> mucListeners = ClientConfig.I.getFGlistener(MucListener.class);
        if (mucListeners == null || mucListeners.isEmpty()) {
            ClientConfig.I
                .registerListener(MucListener.class, MucManager.getInstance(getApplication()));
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
        viewBinding.viewTitleBar.setTitleText(pageModels.get(mCurrentPager).getTitleRes());
    }


    /**
     * 初始化ViewPage
     */
    private void initViewPage() {
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.setPageModels(pageModels);
        viewBinding.viewPage.setAdapter(tabAdapter);
        viewBinding.viewPage.addOnPageChangeListener(
            new TabLayout.TabLayoutOnPageChangeListener(viewBinding.bottomTabLayout));
        // 设置默认显示第几个页面
        viewBinding.viewPage.setCurrentItem(mCurrentPager, false);
    }

    /**
     * 设置添加Tab
     */
    private void initTabs() {
        if (viewBinding.bottomTabLayout.getTabCount() > 0) {
            return;
        }
        for (PageModel pageModel : pageModels) {
            // 创建一个新的Tab
            TabLayout.Tab tab = viewBinding.bottomTabLayout.newTab();
            View view = getLayoutInflater().inflate(R.layout.view_table_custom, null);
            tab.setCustomView(view);

            // 初始化Tab
            ImageView ivTab = view.findViewById(R.id.iv_tab);
            ivTab.setImageResource(pageModel.getIconRes());
            TextView tvTitle = view.findViewById(R.id.tv_tab);
            tvTitle.setText(pageModel.getTitleRes());

            // 添加Tab
            viewBinding.bottomTabLayout.addTab(tab);
        }
        viewBinding.bottomTabLayout.addOnTabSelectedListener(
            new TabLayout.ViewPagerOnTabSelectedListener(viewBinding.viewPage) {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mCurrentPager = tab.getPosition();
                    closeFloatMenu();
                    // 设置标题
                    int postion = tab.getPosition();
                    viewBinding.viewTitleBar
                        .setTitleText(pageModels.get(postion).getTitleRes());
                    viewBinding.viewPage.setCurrentItem(postion, false);
                    showToolBar(postion == ME ? false : true);
                    if (postion == MESSAGE || postion == CONTACT || postion == ME) {//刷新消息页面，和通讯录页面
                        pageModels.get(postion).getFragment().notifyResumeData();
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
            if (isLoadRoster && !isLoadMuc) {//已经下载群组信息
                if (FingerIM.I.isLogin()) {
                    mHandler.removeMessages(HANDLER_MUC);
                    MucManager.getInstance().qHomeAllRoomInfo();
                    isLoadMuc = true;
                } else {
                    mHandler.sendEmptyMessage(HANDLER_MUC);
                }
            } else {
                mHandler.sendEmptyMessage(HANDLER_MUC);
            }
        } else {
            isLoadMuc = true;
        }
    }

    //加载通讯录信息
    private void loadRosters() {
        FingerIM.I.getRosters();
        if (ProviderUser.getRosterCount() > 1) {//通讯录有数据
            isLoadRoster = true;
        }
    }

    public void checkRosters() {
        int count = ProviderUser.getRosterCount();
        if (count <= 1) {
            FingerIM.I.getRosters();
        }
    }

    private void loadOfflineMessage() {
        //等roster和muc信息加载完后再加载
        if (isLoadRoster && isLoadMuc && FingerIM.I.isLogin()) {
            mHandler.removeMessages(HANDLER_OFFLINE);
            long lastMessageTime = ProviderChat.getLastMessageTime();
            try {
                String param = "";
                if (lastMessageTime > 0) {
                    JSONObject object = new JSONObject();
                    object.put("latestMessTime", lastMessageTime);
                    param = object.toString();
                }
                ExcuteMessage message = MessageManager.getInstance().createExcuteBody(
                    ExcuteType.QUERY_OFFLINE, param);
                if (message != null) {
                    FingerIM.I.excute(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mHandler.sendEmptyMessageDelayed(HANDLER_OFFLINE, 1000);
        }
    }

    private void loadEmoticons() {
        ExcuteMessage message = MessageManager.getInstance()
            .createExcuteBody(ExcuteType.EMOTICON_QUERY, getUserId());
        if (message != null) {
            FingerIM.I.excute(message);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof RosterEvent) {
            RosterMessage message = ((RosterEvent) event).getPacket();
            if (message != null && message.message != null) {
                if (message.message.getCode() == Common.QUERY_OK) {
                    isLoadRoster = true;
                }
                pageModels.get(CONTACT).getFragment().notifyRequestResult(event);
            }

        } else if (event instanceof RefreshEvent) {
            RefreshEntity entity = ((RefreshEvent) event).getEntity();
            if (entity != null && entity.getActivity() == ChatEnum.EActivityNum.MAIN.value) {
                if (entity.getType() > 0) {
                    hasOfflineMessage = true;
                    initUnreadCounts();
                } else {
                    if (entity.getFragment() == ChatEnum.EFragmentNum.TAB_MESSAGE.value) {
                        pageModels.get(MESSAGE).getFragment().notifyResumeData();
                        initUnreadCounts();
                    } else if (entity.getFragment() == ChatEnum.EFragmentNum.TAB_CONTACTS.value) {
                        initUnreadCounts();
                    }
                }

            }

        } else if (event instanceof ChatMessageEvent) {
            pageModels.get(MESSAGE).getFragment().notifyResumeData();
            initUnreadCounts();

        } else if (event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent) event;
            //断网重登陆
            RespMessage message = (response).getPacket();
            if (message.response.getCode() == Common.LOGIN_VERYFY_PASSED) {
                if (FingerIM.I.isBannedAutoLogin()) {
                    FingerIM.I.setBannedAutoLogin(false);
                }
                AppManager.getInstance().setLoginStatus(true);
                pageModels.get(MESSAGE).getFragment().notifyResumeData();
                mHandler.sendEmptyMessageDelayed(HANDLER_OFFLINE, 500);//登陆成功，加载离线数据
                mHandler.sendEmptyMessageDelayed(HANDLER_RESEND, 500);//登陆成功，自动发送发送失败消息
                ((FragmentTabMessage) pageModels.get(MESSAGE).getFragment())
                    .notifyNetStatusChange(ENetStatus.LOGIN_SUCCESS);
            } else if (message.response.getCode() == Common.LOGIN_UNAUTHORIZED) {
                FingerIM.I.loginError();
                AppManager.getInstance().setLoginStatus(false);
                ((FragmentTabMessage) pageModels.get(MESSAGE).getFragment())
                    .notifyNetStatusChange(ENetStatus.ERROR_LOGIN);

            } else if (message.response.getCode() == Common.PASSWORD_CHANGED) {//密码被其他端修改，强制下线
                loginOut();
                toActivity(LoginActivity.class);
                finish();
            }
            pageModels.get(CONTACT).getFragment().notifyRequestResult(event);

        } else if (event instanceof NetStatusEvent) {
            NetStatusEvent netEvent = (NetStatusEvent) event;
            ChatEnum.ENetStatus status = netEvent.getStatus();

            if (status == ENetStatus.ERROR_NET || status == ENetStatus.ERROR_CONNECT ||
                status == ENetStatus.SUCCESS_ON_NET || status == ENetStatus.SUCCESS_ON_SERVICE
                || status == ENetStatus.LOGIN_SUCCESS) {
                ((FragmentTabMessage) pageModels.get(MESSAGE).getFragment())
                    .notifyNetStatusChange(status);

            } else if (status == ENetStatus.LOGIN_CONFLICTED_PSW) {
                System.out.println("sss-收到登录冲突消息");
                T.show("密码被修改，请重新登录");
                toActivity(LoginActivity.class);
                finish();
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
        } else if (event instanceof ExcuteEvent) {
            ExcuteResultMessage message = ((ExcuteEvent) event).getPacket();
            int code = message.message.getCode();
            if (code == Common.EMOTICON_QUERY_SUCCESS) {
                String result = message.message.getResult();
                if (!TextUtils.isEmpty(result)) {
                    SPHelper.remove(AppConfig.EX_KEY);
                    SPHelper.saveValue(AppConfig.EX_KEY, result);
                }
            }
        }
    }

    /*
     * 断网后，注册网络状态的广播接受者
     * */
    private void doRegisterNetReceiver() {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new NetworkReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetworkReceiver, filter);
        }
    }


    @SuppressLint("CheckResult")
    public void initUnreadCounts() {
        Observable.just(0)
            .map(integer -> {
                int[] counts = new int[5];
                counts[0] = ProviderChat.selectTotalUnreadMessageCount();//会话列表
                counts[1] = ProviderUser.getUnreadedRosterCount();//通讯录
                counts[2] = 0;
                counts[3] = 0;
                counts[4] = 0;
                return counts;
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(Observable.<int[]>empty())
            .subscribe(ints -> {
                int tabCount = viewBinding.bottomTabLayout.getTabCount();
                for (int i = 0; i < tabCount; i++) {
                    View view = viewBinding.bottomTabLayout.getTabAt(i).getCustomView();
                    TextView tvPoint = view.findViewById(R.id.tv_point);
                    int point = ints[i];
                    if (point > 0) {
                        tvPoint.setVisibility(View.VISIBLE);
                        if (point > 99) {
                            tvPoint.setText(99 + "+");
                        } else {
                            tvPoint.setText(point + "");
                        }
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


    public void closeFloatMenu() {
        viewBinding.floatMenu.closeMenu();
    }

    public void loginOut() {
        ((FragmentTabMessage) pageModels.get(MESSAGE).getFragment()).loginOut();
        if (TokenHelper.isSSOTokenValid(getUserId())) {
            HttpUtils.getInstance().ssoLoginOut(SSOTokenRepository.getToken())
                .compose(RxSchedulers.compose())
                .subscribe(new FGObserver<ResponseObject<SSOToken>>(false) {
                    @Override
                    public void onHandleSuccess(ResponseObject<SSOToken> response) {
                        if (response.code == 10) {
                            Log.e("ssoLogin", "成功");
                        } else {
                            Log.e("ssoLogin", response.msg);
                        }
                    }
                });
        }
        FingerIM.I.unbindAccount();
        AppManager.getInstance().setLoginStatus(false);
        MessageManager.getInstance().clearLoginData();
        NotifyManager.getInstance().clearNotification();

    }

    private UserInfo createUserInfo(User.UserInfo userInfo) {
        return new UserInfo(userInfo.getUserid(), userInfo.getUsernick(), userInfo.getPhoneNumber(),
            userInfo.getWorkAddress(),
            userInfo.getEmpName(), userInfo.getSex(), userInfo.getAvatar(), userInfo.getIsvalid(),
            userInfo.getJobname(),
            userInfo.getDptNo(), userInfo.getDptName(), userInfo.getEmpNo(), userInfo.getRight());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initBundle();
        if (mCurrentPager == SETTING) {
            finish();
            MainActivity.start(this, mCurrentPager);
        } else {
            viewBinding.viewPage.setCurrentItem(mCurrentPager, false);
        }

    }


    /*
    * 检查未读消息数目是否准确
    * */
    public void checkUnreadCountIsRight() {
        List<String> recentChats = ProviderChat.getAllChat();
        List<String> unreadChats = ProviderChat.getAllUnreadChat();
        int len = unreadChats.size();
        for (int i = 0; i < len; i++) {
            String chatId = unreadChats.get(i);
            if (recentChats == null || recentChats.size() <= 0) {
                ProviderChat.updateHasReaded(chatId, true);
            } else {
                if (!recentChats.contains(chatId)) {
                    ProviderChat.updateHasReaded(chatId, true);
                }
            }
        }
    }

    public void checkAndSendFailMessage() {
        List<IChatRoomModel> list = ProviderChat.getSendFailedMessage();
        if (list != null) {
            int len = list.size();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    IChatRoomModel model = list.get(i);
                    BodyEntity entity;
                    if (model.isGroupChat()) {
                        String mucName = MucInfo
                            .getMucName(ContextHelper.getContext(), model.getTo());
                        entity = MessageManager.getInstance()
                            .createBody(model.getContent(), model.isSecret(), model.getMsgType(),
                                model.getAvatarUrl(), model.getNick(), mucName);
                    } else {
                        entity = MessageManager.getInstance()
                            .createBody(model.getContent(), model.isSecret(), model.getMsgType(),
                                model.getNick());
                    }
                    model.setBody(BodyEntity.toJson(entity));
                    MessageManager.getInstance().sendMessage(model);
                }
            }
        }
    }

    private void checkVersion() {
        if (!SPHelper.getBoolean(AppConfig.VERSION_REMIND, false)) {  //wsq  用户没有点击过不再提醒
            new SystemApi().getVersionInfo(new FXRxSubscriberHelper<GetVersionInfoResult>() {
                @Override
                public void _onNext(GetVersionInfoResult getVersionInfoResult) {
                    VersionInfoBean bean = getVersionInfoResult.getContent();
                    if (bean == null) {
                        return;
                    }
                    if (!bean.getAppVersion().equals(getVersionName())) {
                        showUpdateDialog(bean);
                    }
                }
            });
        }
    }

    //获取当前版本号
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showUpdateDialog(VersionInfoBean mAppVersion) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder
            .getInstance(this);
        builder.withTitle("有新版本")
            .withMessage(mAppVersion.getAppMsg())
            .withDuration(300)
            .withIcon(R.mipmap.ic_logo)
            .withButton1Text("不再提醒")
            .withButton2Text("更新")
            .isCancelableOnTouchOutside(false)
            .setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    SPHelper.saveValue(AppConfig.VERSION_CODE, mAppVersion.getAppVersion());
                    SPHelper.saveValue(AppConfig.VERSION_REMIND, true);
                }
            })
            .setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    try {
                        Uri uri = Uri.parse(mAppVersion.getAppUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        T.show("apk下载链接异常");
                    }
                }
            })
            .show();
    }


    //清除所有应用信息，及缓存信息，图片
    private void clearApplicationData() {
        DataClearMananger.cleanApplicationData(ContextHelper.getContext(), FileUtil.FG_ROOT_FILE);
    }
}

package com.lens.chatmodel.ui.message;

import static com.lens.chatmodel.ui.video.CameraActivity.BUTTON_STATE_BOTH;
import static com.lensim.fingerchat.commons.app.AppConfig.EX_KEY;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_EX;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_IMAGE;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_TRANSFOR;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_VIDEO;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_VOTE;
import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.webview.BrowserActivity;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.AckListener;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.api.message.ExcuteResultMessage;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.proto.message.Excute.ExcuteMessage;
import com.fingerchat.proto.message.Excute.ExcuteType;
import com.fingerchat.proto.message.Muc.MucItem;
import com.fingerchat.proto.message.MucChat.RoomMessage;
import com.fingerchat.proto.message.PrivateChat.PrivateMessage;
import com.fingerchat.proto.message.ReadAck.ReadedMessageList;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EFragmentNum;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESCrollType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.ChatEnum.ETransforModel;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.base.ChatEnvironment;
import com.lens.chatmodel.bean.CardEntity;
import com.lens.chatmodel.bean.DefaultEmojiconDatas;
import com.lens.chatmodel.bean.Emojicon;
import com.lens.chatmodel.bean.EmojiconDefaultGroupData;
import com.lens.chatmodel.bean.EmojiconGroupEntity;
import com.lens.chatmodel.bean.ImageEventBean;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.VideoEventBean;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.bean.body.MapBody;
import com.lens.chatmodel.bean.body.PushBody;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.bean.body.VoiceUploadEntity;
import com.lens.chatmodel.bean.body.VoteBody;
import com.lens.chatmodel.bean.body.VoteEntity;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lens.chatmodel.bean.transfor.MultiMessageEntity;
import com.lens.chatmodel.controller.ControllerChatAttachBottom;
import com.lens.chatmodel.controller.ControllerNewMessage;
import com.lens.chatmodel.controller.cell.FactoryChatCell;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ChatMessageEvent;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.ExcuteEvent;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IChatBottomAttachListener;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.manager.NotifyManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.emoji.ExpressionActivity;
import com.lens.chatmodel.ui.group.GroupOperationActivity;
import com.lens.chatmodel.ui.group.GroupRemindSelectActivity;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lens.chatmodel.ui.multi.ActivityMultiMsgDetail;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.chatmodel.ui.video.CameraActivity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.CustomContextMenu;
import com.lens.chatmodel.view.chat.ChatMessageList;
import com.lens.chatmodel.view.emoji.ChatInputMenu;
import com.lens.chatmodel.view.emoji.ChatInputMenu.ChatInputMenuListener;
import com.lens.chatmodel.view.emoji.EmotionKeyboard;
import com.lens.chatmodel.view.voice_recorder_view.VoiceRecorderView;
import com.lens.chatmodel.view.voice_recorder_view.VoiceRecorderView.EaseVoiceRecorderCallback;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.global.CommonEnum.ELogType;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.map.MapPickerActivity;
import com.lensim.fingerchat.commons.map.ShowLocationActivity;
import com.lensim.fingerchat.commons.map.bean.MapInfoEntity;
import com.lensim.fingerchat.commons.permission.EPermission;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.ThreadUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.components.pulltorefresh.XCPullToLoadMoreListView;
import com.lensim.fingerchat.data.Api;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.data.help_class.TokenHelper;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.data.me.content.StoreManager;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.data.work_center.OAToken;
import com.lensim.fingerchat.data.work_center.OATokenRepository;
import com.lensim.fingerchat.data.work_center.SignInJsonRet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/5.
 */

public class ChatActivity extends BaseUserInfoActivity implements AckListener, IChatEventListener {

    public final static int MAP_FOR_CHAT = 3990;
    public final static int INPUTTING = 1 << 1;
    public final static int RECORDING = 1 << 2;
    public final static int INPUT_AND_RECORDING = 1 << 3;
    public final static int READ = 1 << 4;//服务器已读
    public final static int MIN_UNREAD_COUNT = 15;
    public final static int MIN_TEXT = 1 << 10;

    private int CAMARA_PERMISSON_REQUEST_CODE = 1;
    private int LOCATION_PERMISSON_REQUEST_CODE = 2;


    private String userId;
    private ChatInputMenu mInputMenu;
    private List<EmojiconGroupEntity> emojiconGroupList;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, IChatRoomModel> uploadMap = new HashMap<>();
    private MessageAdapter mAdapter;

    private FGToolbar toolbar;
    private int mCurrrentPage = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INPUT_AND_RECORDING) {
                if (firstInputingTime > 0
                    && System.currentTimeMillis() - firstInputingTime >= 300) {
                    stopInpputTimer();
                    updateToolBarTitle(null);
                }
            } else if (msg.what == INPUTTING) {
                ChatActivity.this.sendMessage(EMessageType.INPUTING);
            } else if (msg.what == RECORDING) {
                ChatActivity.this.sendMessage(EMessageType.RECORDING);
            } else if (msg.what == READ) {
                ChatActivity.this.sendMessage();
            }
        }
    };
    private CustomContextMenu mCustomContextMenu;
    private ControllerChatAttachBottom viewAttachBottom;
    private File mTmpFile;
    private VoiceRecorderView viewRecorder;
    private ChatMessageList chatMessageList;
    private XCPullToLoadMoreListView mListView;
    private static boolean isFirstUpdate;
    private boolean isSecret = false;
    private long shakeDate;
    private UserBean userBean;//对方信息bean
    private ControllerNewMessage viewNewMessage;
    private int unreadCount;//未读消息数
    private Timer timer;
    private TimerTask timerTask;
    private long firstInputingTime;

    private ArrayList<UserBean> atUsers = new ArrayList<>();
    private int topFlag;
    private int noDisturb;
    private RelativeLayout mChatContentView;
    private TextView tv_success;
    private boolean hasDraft;
    private String mucUserNick;
    private boolean isSendingHypertext = false;//是否正在发送富文本
    private List<String> sendTexts;
    private int textPosition;
    private boolean isInbottom = true;

    public static Intent createChatIntent(Context context, String user, String nick, int chatType,
                                          int backId, int disturb, int top) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userId", user);
        intent.putExtra("nick", nick);
        intent.putExtra("chat_type", chatType);
        intent.putExtra("background", backId);
        intent.putExtra("top_flag", top);
        intent.putExtra("disturb", disturb);
        return intent;
    }

    public static Intent createChatIntent(Context context, String user, int backId, int disturb,
                                          int top) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userId", user);
        intent.putExtra("top_flag", top);
        intent.putExtra("disturb", disturb);
        intent.putExtra("background", backId);
        return intent;
    }

    public static Intent createChatIntent(Context context, UserBean bean) {
        Intent intent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", bean);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent createUpdataChatIntent(Context context, String user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userId", user);
        isFirstUpdate = true;
        return intent;
    }

    private void initIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int backgroundId = intent.getIntExtra("background", 0);
        topFlag = intent.getIntExtra("top_flag", ESureType.NO.ordinal());
        noDisturb = intent.getIntExtra("disturb", ESureType.NO.ordinal());
        if (noDisturb < 0) {
            noDisturb = ESureType.NO.ordinal();
        }
        if (bundle != null) {
            userBean = bundle.getParcelable("user");
            if (userBean != null) {
                userId = userBean.getUserId();
                userBean.setBgId(backgroundId);
            } else {
                userId = intent.getStringExtra("userId");
                if (!ChatHelper.isSystemUser(userId)) {
                    userBean = (UserBean) ProviderUser
                        .selectRosterSingle(ContextHelper.getContext(), userId);
                    if (userBean == null) {
                        String nick = intent.getStringExtra("nick");
                        int chatType = intent.getIntExtra("chat_type", 1);
                        if (chatType == EChatType.GROUP.ordinal()) {
                            mucUserNick = MucUser
                                .getMucUserNick(ContextHelper.getContext(), userId, getUserId());
                        }
                        userBean = new UserBean();
                        userBean.setUserId(userId);
                        if (!TextUtils.isEmpty(nick)) {
                            userBean.setRemarkName(nick);
                            userBean.setChatType(chatType <= 0 ? 0 : chatType);
                            userBean.setBgId(backgroundId <= 0 ? 0 : backgroundId);
                        } else {
                            MucItem item = MucInfo
                                .selectMucInfoSingle(ContextHelper.getContext(), userId);
                            if (item != null) {
                                userBean.setRemarkName(item.getMucname());
                                userBean.setChatType(EChatType.GROUP.ordinal());
                                userBean
                                    .setBgId(TextUtils.isEmpty(item.getPConfig().getChatBg()) ? 0
                                        : Integer.parseInt(item.getPConfig().getChatBg()));
                            } else {
                                userBean.setChatType(chatType <= 0 ? 0 : chatType);
                                userBean.setBgId(backgroundId <= 0 ? 0 : backgroundId);
                            }
                        }

                    }
                } else {
                    userBean = new UserBean();
                    userBean.setUserId(userId);
                    userBean.setRemarkName(userId);
                    userBean.setChatType(1);
                    userBean.setBgId(0);
                }
            }
        } else {
            userId = intent.getStringExtra("userId");
            userBean = (UserBean) ProviderUser
                .selectRosterSingle(ContextHelper.getContext(), userId);
            userBean.setBgId(backgroundId);
        }

        if (TextUtils.isEmpty(userId) || userBean == null) {
            finish();
        }
    }

    private void initMucAcode() {
        if (ChatHelper.isGroupChat(userBean.getChatType())) {
            AppManager.getInstance().initMucAcode(userBean.getUserId());
        }
    }

    @RequiresApi(api = VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        setContentView(R.layout.activity_chat2);
        initIntent();
        initMucAcode();
        NotifyManager.getInstance().removeMessageNotification(getUserId(), userId);
        toolbar = findViewById(R.id.viewTitleBar);
        initToolbar();
        chatMessageList = findViewById(R.id.message_list);
        mListView = chatMessageList.getListView();

        mChatContentView = findViewById(R.id.mChatContentView);
        updateChatBackGround();
        mInputMenu = findViewById(R.id.input_menu);
        mCustomContextMenu = findViewById(R.id.customContextMenu);
        viewRecorder = findViewById(R.id.voice_recorder);

        tv_success = findViewById(R.id.tv_success);

        viewNewMessage = new ControllerNewMessage(findViewById(R.id.viewNewMessage));
        viewNewMessage.setClickListener(() -> {
            int position = mAdapter.getTotalItemsCount() - unreadCount;
            if (position >= 0) {
                scrollChatToPostion(position);
            } else {
                scrollChatToPostion(0);
            }
            viewNewMessage.setVisible(false);

        });

        viewAttachBottom = new ControllerChatAttachBottom(this,
            findViewById(R.id.viewAttachBottom));
        viewAttachBottom.setOnClickListener(new IChatBottomAttachListener() {
            @Override
            public void clickForword(ETransforModel model) {//转发
                if (mAdapter.getSelectedIds() == null) {
                    return;
                }
                ArrayList<String> ids = new ArrayList<>();
                ids.addAll(mAdapter.getSelectedIds());
                if (ids != null && !ids.isEmpty()) {
                    mInputMenu.setVisibility(View.VISIBLE);
                    viewAttachBottom.setVisible(false);
                    mAdapter.hideBottomMenu();
                    transforMessage(ids, model);
                }
            }

            @Override
            public void clickCollect() {//收藏
                T.show(ContextHelper.getString(R.string.no_surport_function));
                mInputMenu.setVisibility(View.VISIBLE);
                viewAttachBottom.setVisible(false);
                mAdapter.hideBottomMenu();
                return;
//                if (mAdapter.getSelectedIds() != null && mAdapter.getSelectedIds().size() > 0) {
//                    mInputMenu.setVisibility(View.VISIBLE);
//                    viewAttachBottom.setVisible(false);
//                    mAdapter.hideBottomMenu();
//                }
            }

            @Override
            public void clickDele() {
                if (mAdapter.getSelectedIds() != null && mAdapter.getSelectedIds().size() > 0) {
                    List<String> ids = mAdapter.getSelectedIds();
                    for (int i = 0; i < ids.size(); i++) {
                        ProviderChat.delePrivateMessage(ContextHelper.getContext(), ids.get(i));
                    }
                    mHandler.postDelayed(() -> {
                        loadChat(0);
                        mInputMenu.setVisibility(View.VISIBLE);
                        viewAttachBottom.setVisible(false);
                        mAdapter.hideBottomMenu();
                    }, 300);
                }
            }

            @Override
            public void clickAttach() {
                if (mAdapter.getSelectedIds() != null && mAdapter.getSelectedIds().size() > 0) {
                    ArrayList<String> ids = mAdapter.getSelectedIds();
                    attachMessage(ids);
                }
            }
        });

        registerExtendMenuItem();

        EmotionKeyboard emotionKeyboard = EmotionKeyboard.with(this)
            .bindToContent(mChatContentView);
        emojiconGroupList = new ArrayList<EmojiconGroupEntity>();
        emojiconGroupList.add(
            new EmojiconGroupEntity(R.drawable.ee_1,
                Arrays.asList(DefaultEmojiconDatas.getData())));
        emojiconGroupList.add(
            new EmojiconGroupEntity(R.drawable.love,
                Arrays.asList(DefaultEmojiconDatas.getData1())));
        emojiconGroupList.add(EmojiconDefaultGroupData.getData());
        emojiconGroupList.add(EmojiconDefaultGroupData.getCustomData());
        mInputMenu.init(emotionKeyboard, emojiconGroupList);

        mListView.setOnRefreshListener(() -> {
            mCurrrentPage++;
            loadChat(mCurrrentPage);
            mListView.onRefreshComplete();
        });

        chatMessageList.getListView().getListView().setOnTouchListener((v, event) -> {
            hideInput();
            return false;
        });

        chatMessageList.getListView().getListView().setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE://滚动结束
                        System.out.println("onScrollStateChanged=" + SCROLL_STATE_IDLE);
                        mHandler.sendEmptyMessageDelayed(READ, 500);
                        break;
                    case SCROLL_STATE_FLING://快速滚动
                        System.out.println("onScrollStateChanged=" + SCROLL_STATE_FLING);
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL://手动滚动
                        System.out.println("onScrollStateChanged=" + SCROLL_STATE_TOUCH_SCROLL);
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                System.out.println("firstVisibleItem=" + firstVisibleItem + "--visibleItemCount="
                    + visibleItemCount + "--totalItemCount=" + totalItemCount);
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    isInbottom = true;
                } else {
                    isInbottom = false;
                }
                checkMessageReaded(firstVisibleItem, visibleItemCount);
            }
        });
        mInputMenu.setChatInputMenuListener(new ChatInputMenuListener() {
            @Override
            public void onSendMessage(String content) {
                if (mHandler != null) {
                    mHandler.removeMessages(INPUTTING);
                }
                if (content.startsWith("@000")) {
                    String[] arr = content.split("#");
                    int count = Integer.valueOf(arr[1]);
                    int time;
                    if (arr.length == 3 && !TextUtils.isEmpty(arr[2])) {
                        time = Integer.valueOf(arr[2]);
                    } else {
                        time = 300;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < count; i++) {
                                System.out.println("测试数据" + "--当前位置=" + i + "--总count=" + count);
                                int position = i + 1;
                                sendTextMessage("测试数据" + position, EMessageType.TEXT);
                                try {
                                    Thread.sleep(time);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                } else {
                    sendTextMessage(content, EMessageType.TEXT);
                }
                //清空atUser
                atUsers.clear();
            }

            @Override
            public void onBigExpressionClicked(
                Emojicon emojicon) {
                if (isSecret) {//密信不能发表情
                    T.show("动态表情不能发送密信");
                    return;
                }
                if (emojicon.getIdentityCode()
                    .equals("add_ex")) {
                    startActivityForResult(
                        new Intent(ChatActivity.this,
                            ExpressionActivity.class),
                        AppConfig.REQUEST_EX);
                } else {
                    IChatRoomModel messagePrivate;
                    if (!emojicon.getIdentityCode()
                        .contains("http")) {
                        messagePrivate = createMessage(
                            emojicon.getIdentityCode(),
                            EMessageType.FACE);
                    } else {
                        messagePrivate = createMessage(
                            emojicon.getIdentityCode(),
                            EMessageType.FACE);
                    }

                    addMessage(messagePrivate);
                    saveMessage(messagePrivate);
                    sendUploadMessage(messagePrivate);
                    scrollChat(ESCrollType.BOTTOM);
                }
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v,
                                                  MotionEvent event) {//录音
                return doRecording(v, event);
            }

            @Override
            public void onPrivateCall() {//@功能
                if (hasDraft) {
                    hasDraft = false;
                    return;
                }
                if (userBean != null
                    && userBean.getChatType()
                    == EChatType.GROUP.ordinal()) {
                    Intent intent = new Intent(
                        ContextHelper.getContext(),
                        GroupRemindSelectActivity.class);
                    intent.putExtra("mucId",
                        userBean.getUserId());
                    startActivityForResult(intent,
                        AppConfig.REQUEST_USER);
                }
            }

            @Override
            public void onSecretCall() {//@功能
                if (userBean != null
                    && userBean.getChatType()
                    == EChatType.GROUP.ordinal()) {

                }
            }

            @Override
            public void onFocusChange(boolean hasFocus) {
                if (hasFocus) {
                    chatMessageList.scrollDown();
                }
            }

            @Override
            public void onEditTextInputting() {//正在输入
//                if (mHandler != null && userBean.getChatType() != EChatType.GROUP.ordinal()) {
//                    mHandler.sendEmptyMessageDelayed(INPUTTING, 300);
//                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initIntent();
        initToolbar();
        updateChatBackGround();
        initAdapter();
        loadChat(0);
        scrollChat(ESCrollType.BOTTOM);
    }

    private void updateChatBackGround() {
        if (userBean == null) {
            return;
        }
        mChatContentView.setBackground(ChatHelper.getChatBackGround(userBean.getBgId()));
    }

    private void initToolbar() {
        if (!hasInitToolBar()) {
            initBackButton(toolbar, true);
        } else {
            setHasBackButton(true);
        }
        EChatType type = EChatType.fromInt(userBean.getChatType());
        if (type == null) {
            return;
        }
        toolbar.setTitleText(ChatHelper
            .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                userBean.getUserId()));
        if (!ChatHelper.isSystemUser(userBean.getUserId())) {//非系统用户
            toolbar.setBtImageDrawable(getDrawableId(), v -> {
//                if (userBean.getChatType() == EChatType.GROUP.ordinal()) {
//                    MucMemberItem memberItem = MucUser
//                        .selectUserById(ContextHelper.getContext(), userId, getUserId());
//                    if (memberItem == null) {
//                        T.show("你已经不是该群成员，不能查看该群信息");
//                        return;
//                    }
//                }
                Bundle bundle = new Bundle();
                bundle.putString("mucId", userId);
                bundle.putString("mucName", ChatHelper
                    .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                        userBean.getUserId()));
                bundle.putInt("chat_type", userBean.getChatType());
                toActivityForResult(GroupOperationActivity.class, bundle,
                    AppConfig.REQUEST_CHANGE_CONFIG);
            });
            toolbar.setBtMessageDrawable(R.drawable.selector_secret, v -> {
                toolbar.resetSecretSelected();
                isSecret = toolbar.getSecretSelected();
                mInputMenu.setSecretChat(isSecret);//更新UI
            });
        }

    }

    private int getDrawableId() {
        if (userBean.getChatType() == EChatType.GROUP.ordinal()) {
            return R.drawable.group_of_icon;
        } else {
            return R.drawable.ic_single;
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        String draft = SPHelper.getString(getUserId() + "&" + userBean.getUserId(), "");
        if (!TextUtils.isEmpty(draft)) {
            if (draft.startsWith("@")) {
                hasDraft = true;
            }
            mInputMenu.setEmojicon(SmileUtils.getSmiledText(this, draft, TDevice.sp2px(14)));
        }

    }

    private void initInputTimer() {
        timer = new Timer();
    }

    class InputTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(INPUT_AND_RECORDING);
        }
    }

    private void transforMessage(IChatRoomModel model) {
        String content = MessageManager.getInstance().getMessageBodyJson(model);
        if (!TextUtils.isEmpty(content)) {
            Intent intent = TransforMsgActivity
                .newPureIntent(this, content, model.getMsgType().value,
                    userBean.getChatType(), userId);
            startActivityForResult(intent, REQUEST_TRANSFOR);
        } else {
            T.show("数据异常");
        }
    }

    private void transforMessage(ArrayList<String> list, ETransforModel model) {
        Intent intent = TransforMsgActivity
            .newMultiMultiIntent(this, list, userId, model.ordinal(), userBean.getChatType(),
                userBean.getBgId());
        startActivityForResult(intent, REQUEST_TRANSFOR);
    }

    private void attachMessage(ArrayList<String> list) {
        Intent intent = AttachMessageActivity
            .newIntent(this, list);
        startActivity(intent);
    }

    //录音,语音
    private boolean doRecording(View v, MotionEvent event) {
        int CamaraPermissonRequestCode = 4;

        if (Build.VERSION.SDK_INT >= 23) {
            int CamaraPermisson = ContextCompat
                .checkSelfPermission(ChatActivity.this, permission.RECORD_AUDIO);
            if (CamaraPermisson != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{permission.RECORD_AUDIO}
                    , CamaraPermissonRequestCode);
            } else {
                viewRecorder.setVisibility(View.VISIBLE);
                viewRecorder.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {
                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        viewRecorder.setVisibility(View.INVISIBLE);
                        if (mHandler != null) {
                            mHandler.removeMessages(RECORDING);
                        }
                        L.d("录音完成", voiceFilePath);
                        VoiceUploadEntity entity = new VoiceUploadEntity();
                        entity.setVoiceUrl(voiceFilePath);
                        entity.setTimeLength(voiceTimeLength);
                        IChatRoomModel message = createMessage(VoiceUploadEntity.toJson(entity),
                            EMessageType.VOICE);
                        addMessage(message);
                        uploadVoice(message);
                        saveMessage(message);
                    }

                    @Override
                    public void onVoiceRecording() {
//                        if (mHandler != null && userBean.getChatType() != EChatType.GROUP
//                            .ordinal()) {
//                            mHandler.sendEmptyMessageDelayed(RECORDING, 300);
//                        }
                    }
                });
            }
        } else {
            viewRecorder.setVisibility(View.VISIBLE);
            viewRecorder.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {
                @Override
                public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                    viewRecorder.setVisibility(View.INVISIBLE);
                    L.d("录音完成", voiceFilePath);
                    VoiceUploadEntity entity = new VoiceUploadEntity();
                    entity.setVoiceUrl(voiceFilePath);
                    entity.setTimeLength(voiceTimeLength);
                    IChatRoomModel message = createMessage(VoiceUploadEntity.toJson(entity),
                        EMessageType.VOICE);
                    addMessage(message);
                    uploadVoice(message);
                    saveMessage(message);
                }

                @Override
                public void onVoiceRecording() {
//                    if (mHandler != null && userBean.getChatType() != EChatType.GROUP.ordinal()) {
//                        mHandler.sendEmptyMessageDelayed(RECORDING, 300);
//                    }
                }
            });
        }

        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter == null) {
            mCurrrentPage = 0;
            initAdapter();
        }
        unreadCount = ProviderChat
            .selectUnreadMessageCountOfUser(ContextHelper.getContext(), userId);
        if (unreadCount >= MIN_UNREAD_COUNT) {
            viewNewMessage.setCount(unreadCount);
            viewNewMessage.setVisible(true);
            mAdapter.setUnreadCount(unreadCount);
        } else {
            viewNewMessage.setVisible(false);
            mAdapter.setUnreadCount(0);

        }
        if (mAdapter.getTotalItemsCount() <= 0 || isFirstUpdate) {
            loadChat(mCurrrentPage);
        }
        if (isInbottom) {
            scrollChat(ESCrollType.BOTTOM);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        ClientConfig.I.registerListener(AckListener.class, this);
        if (!TextUtils.isEmpty(userId)) {
            MessageManager.getInstance().setCurrentChat(userId);
        }

        if (MessageManager.getInstance().isMessageChange()) {
            loadChat(0);
            MessageManager.getInstance().setMessageChange(false);
            initToolbar();
        }
        checkAndLoadEmoticon();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            L.d("按下了删除键");
            String text = mInputMenu.getText();
            L.d("输入框文字", text + "==");
            if (!TextUtils.isEmpty(text)) {
                int i = text.lastIndexOf('@');
                if (i != -1) {
                    String name = text.substring(i + 1);
                    for (UserBean bean : atUsers) {
                        if (bean.getRemarkName().equalsIgnoreCase(name)) {
                            String newstr = text.substring(0, i);
                            mInputMenu.setPreStr(newstr);
                            mInputMenu.setEmojicon(newstr);
                            atUsers.remove(bean);
                        }
                    }
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewAttachBottom.isVisible()) {
                isSelectedChat(false);
                mAdapter.hideBottomMenu();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //是否正在选择消息
    public void isSelectedChat(boolean show) {
        if (show) {
            viewAttachBottom.setVisible(true);
            mInputMenu.setVisibility(View.GONE);
        } else {
            viewAttachBottom.setVisible(false);
            mInputMenu.setVisibility(View.VISIBLE);
        }
    }

    private void initAdapter() {
        mAdapter = new MessageAdapter(this, this);
        if (mCustomContextMenu != null) {
            mAdapter.setCustomContextMenu(mCustomContextMenu);
        }
        mAdapter.setViewFactory(new FactoryChatCell(mAdapter, this));

        mAdapter.setBottomShowListener(show -> isSelectedChat(show));
        mAdapter.setUser(userId);
        mAdapter.initUserBean(getUserInfo(), userBean);
        mAdapter.setChatType(userBean.getChatType());
        mAdapter.setListView(chatMessageList.getListView());
        chatMessageList.setAdapter(mAdapter);
    }

    private void loadChat(int page) {
        if (page != mCurrrentPage) {
            mCurrrentPage = page;
        }
        List<IChatRoomModel> messages;
        if (unreadCount > 20 && page == 0) {
            messages = ProviderChat
                .selectMsgAsPage(ContextHelper.getContext(), userId, page, unreadCount + 20,
                    ChatHelper.isGroupChat(userBean.getChatType()));
        } else {
            messages = ProviderChat
                .selectMsgAsPage(ContextHelper.getContext(), userId, page, 20,
                    ChatHelper.isGroupChat(userBean.getChatType()));
        }
        if (messages != null) {
            mAdapter.setData(messages, mCurrrentPage);
        }
    }


    //重新发送
    private void doResendMessage(IChatRoomModel model) {
        if (model == null) {
            return;
        }
        if (model.getSendType() == ESendType.FILE_SUCCESS) {//文件已经上传成功
            sendUploadMessage(model);
        } else {
            if (model.getMsgType() == EMessageType.IMAGE) {
                reUploadImage(model);
            } else if (model.getMsgType() == EMessageType.FACE) {
                reUploadImage(model);
            } else if (model.getMsgType() == EMessageType.VOICE) {
                uploadVoice(model);
            } else if (model.getMsgType() == EMessageType.VIDEO) {
                uploadVideoImage(model);
            } else {
                sendUploadMessage(model);
            }

        }
    }

    private void registerExtendMenuItem() {
        mInputMenu
            .registerExtendMenuItem(ContextHelper.getString(R.string.picture),
                R.drawable.chat_pictures, 1, (itemId, view) -> {
                    ChatEnvironment.getInstance().getPermissionExecutor()
                        .checkPermission(ChatActivity.this, EPermission.STORAGE,
                            (permission, isGranted, withAsk) -> {
                                if (permission == EPermission.STORAGE && isGranted) {
                                    Intent intent = new Intent(ChatActivity.this,
                                        MultiImageSelectorActivity.class);
                                    intent
                                        .putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA,
                                            false);
                                    intent
                                        .putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT,
                                            9);
                                    intent
                                        .putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
                                            MultiImageSelectorActivity.MODE_MULTI);
                                    startActivityForResult(intent, REQUEST_IMAGE);
                                }

                            });
                });
        mInputMenu.registerExtendMenuItem(ContextHelper.getString(R.string.take_photo),
            R.drawable.chat_photo,
            2, (itemId, view) -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    int CamaraPermisson = ContextCompat
                        .checkSelfPermission(ChatActivity.this, permission.CAMERA);
                    int ReCordPermisson = ContextCompat
                        .checkSelfPermission(ChatActivity.this, permission.RECORD_AUDIO);
                    if (CamaraPermisson != PackageManager.PERMISSION_GRANTED
                        || ReCordPermisson != PackageManager.PERMISSION_GRANTED) {
                        //如果设置中权限是禁止的咋返回false;如果是提示咋返回的是true
                        //在禁止的情况下，动态请求权限是无效果的
                        boolean isCameraBaned = ActivityCompat
                            .shouldShowRequestPermissionRationale(ChatActivity.this,
                                permission.CAMERA);
                        if (isCameraBaned) {
                            ActivityCompat.requestPermissions(ChatActivity.this,
                                new String[]{permission.CAMERA, permission.RECORD_AUDIO},
                                CAMARA_PERMISSON_REQUEST_CODE);
                        } else {
                            T.show("相机权限被禁止，请将其设置为允许或者提示,才能使用该功能");
                        }
                    } else {
                        toCamera();
                    }
                } else {
                    toCamera();
                }
            });

        mInputMenu
            .registerExtendMenuItem(ContextHelper.getString(R.string.shake), R.drawable.chat_jitter,
                3, (itemId, view) -> {
                    if (isSecret) {
                        T.showShort(R.string.secret_no_support);
                        return;
                    }
                    if (System.currentTimeMillis() - shakeDate < 2 * 1000) {
                        showToast("间隔过短");
                    } else {
                        String shakeContent = ContextHelper.getString(R.string.shake_content);
                        sendMessage(shakeContent, EMessageType.NOTICE);
                        shakeDate = System.currentTimeMillis();

                    }
                });

        mInputMenu.registerExtendMenuItem(ContextHelper.getString(R.string.calling_card),
            R.drawable.share_card, 4, (itemId, view) -> {
                if (isSecret) {
                    T.showShort(R.string.secret_no_support);
                    return;
                }
                Intent intent = TransforMsgActivity
                    .newBusinessCardIntent(ChatActivity.this, userId,
                        ChatHelper
                            .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                                userBean.getUserId()), userBean.getChatType());
                startActivity(intent);
            });
        mInputMenu.registerExtendMenuItem(ContextHelper.getString(R.string.pop_menu_collect),
            R.drawable.collection, 5, (itemId, view) -> {
                if (isSecret) {
                    T.showShort(R.string.secret_no_support);
                    return;
                }
//                showToast("此功能暂未开放");
                Intent intent = ActivitysRouter.getInstance().invoke(ChatActivity.this,
                    ActivityPath.COLLECTION_ACTIVITY_PATH);
                intent.putExtra("activity", ChatActivity.class.getSimpleName());
                intent.putExtra("USERID", userId);
                startActivityForResult(intent, AppConfig.REQUEST_COLLECTION);
            });

        mInputMenu
            .registerExtendMenuItem(ContextHelper.getString(R.string.position), R.drawable.position,
                6, (itemId, view) -> {
                    if (Build.VERSION.SDK_INT >= 23) {
                        int loacationPermisson = ContextCompat
                            .checkSelfPermission(ChatActivity.this,
                                permission.ACCESS_FINE_LOCATION);
                        if (loacationPermisson != PackageManager.PERMISSION_GRANTED) {
                            //如果设置中权限是禁止的咋返回false;如果是提示咋返回的是true
                            //在禁止的情况下，动态请求权限是无效果的
                            boolean isCameraBaned = ActivityCompat
                                .shouldShowRequestPermissionRationale(ChatActivity.this,
                                    permission.ACCESS_FINE_LOCATION);
                            if (isCameraBaned) {
                                ActivityCompat.requestPermissions(ChatActivity.this,
                                    new String[]{permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSON_REQUEST_CODE);
                            } else {
                                T.show("位置权限被禁止，请将其设置为允许或者提示,才能使用该功能");
                            }
                        } else {
                            toMap();
                        }
                    } else {
                        toMap();
                    }
                });

        if (userBean.getChatType() == EChatType.GROUP.ordinal()) {
            mInputMenu.registerExtendMenuItem("投票", R.drawable.vote, 7,
                (itemId, view) -> toVote());
        }

    }

    //投票
    private void toVote() {
        if (isSecret) {
            T.showShort(R.string.secret_no_support);
            return;
        }
        Intent intent = new Intent(this, BrowserActivity.class);
        String url = String.format(Api.URL_VOTE, getUserId(), getUserNick(), userId);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        intent.putExtra("title", "投票");
        intent.putExtra("user", userId);
        startActivityForResult(intent, REQUEST_VOTE);
    }

    private void toVote(String voteid) {
        Intent intent = new Intent(this, BrowserActivity.class);
        String url = String.format(Api.URL_VOTE_TO, getUserId(), getUserNick(), userId, voteid);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        intent.putExtra("title", "投票");
        intent.putExtra("user", userId);
        startActivity(intent);
    }

    private void toCamera() {
        CameraActivity
            .start(ChatActivity.this, REQUEST_VIDEO, BUTTON_STATE_BOTH);
    }

    private void toMap() {
        if (isSecret) {
            T.showShort(R.string.secret_no_support);
            return;
        }
        MapPickerActivity
            .openActivity(ChatActivity.this, MAP_FOR_CHAT, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i("onActivityResult");
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                final List<ImageBean> beans = data.getExtras()
                    .getParcelableArrayList(MultiImageSelectorActivity.EXTRA_RESULT);
                if (beans == null || beans.size() <= 0) {
                    return;
                }
                //默认不使用原图
                boolean useOrigin = data
                    .getBooleanExtra(MultiImageSelectorActivity.EXTRA_RESULT_ORIGIN, false);

                List<String> urls = new ArrayList<>();
                for (int i = 0; i < beans.size(); i++) {
                    ImageBean bean = beans.get(i);
                    String path = bean.path;
                    if (TextUtils.isEmpty(path)) {
                        return;
                    }
                    urls.add(path);
                    EUploadFileType type = ContextHelper.configFileType(path);
                    if (type != null && getMessageType(type) != null) {
                        ImageUploadEntity entity = ImageUploadEntity
                            .createEntity(path, bean.size, "", "");
                        IChatRoomModel message = createMessage(ImageUploadEntity.toJson(entity),
                            getMessageType(type));
                        uploadMap.put(i, message);
                        addMessage(message);
                        saveMessage(message);
                    }
                }
                uploadImage(urls, useOrigin, 0);
            }
        } else if (requestCode == REQUEST_VIDEO) {
            if (resultCode == 102) {
                String videoPath = data.getStringExtra("videoPath");
                String imagePath = data.getStringExtra("framePicPath");
                int timeLength = data.getIntExtra("videoDuration", 0);
                String defaultSize = 640 + "x" + 480;
                VideoUploadEntity entity = VideoUploadEntity
                    .createEntity(videoPath, imagePath, defaultSize, timeLength);
                L.i("录制视频成功", videoPath);
                if (!TextUtils.isEmpty(VideoUploadEntity.toJson(entity))) {
                    IChatRoomModel message = createMessage(VideoUploadEntity.toJson(entity),
                        getMessageType(EUploadFileType.VIDEO));
                    addMessage(message);
                    uploadVideoImage(message);
                    saveMessage(message);
                }
            } else if (resultCode == 101) {
                String imagePath = data.getStringExtra("imagePath");
                ImageUploadEntity entity = ImageUploadEntity
                    .createEntity(imagePath, "", "", "");
                if (entity != null) {
                    IChatRoomModel message = createMessage(ImageUploadEntity.toJson(entity),
                        EMessageType.IMAGE);
                    uploadMap.put(0, message);
                    addMessage(message);
                    saveMessage(message);
                    uploadImageSingle(imagePath);
                }
            }
        } else if (requestCode == MAP_FOR_CHAT) {
            if (resultCode == RESULT_OK && data != null) {
                MapInfoEntity mapInfoEntity = data.getParcelableExtra("position");
                if (mapInfoEntity != null) {
                    MapBody body = new MapBody();
                    body.setLocationAddress(mapInfoEntity.getStreet());
                    body.setLocationName(mapInfoEntity.getAddressName());
                    body.setLatitude(mapInfoEntity.getLatitude());
                    body.setLongitude(mapInfoEntity.getLongitude());
                    body.setSecret(isSecret ? 1 : 0);
                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                        String mucUserNick = MucUser
                            .getMucUserNick(ContextHelper.getContext(), userBean.getUserId(),
                                getUserId());
                        body.setMucNickName(
                            ChatHelper.getUserRemarkName(mucUserNick, getUserNick(), getUserId()));
                        body.setSenderAvatar(getUserAvatar());
                        body.setGroupName(userBean.getUserNick());
                    } else {
                        body.setMucNickName(getUserNick());
                    }
                    sendMessage(body.toJson(), EMessageType.MAP);
                }
            }
        } else if (requestCode == REQUEST_EX) {
            if (resultCode == RESULT_OK && data != null) {
                boolean exChanged = data.getBooleanExtra("ex_changed", false);
                if (exChanged) {
                    String exData = SPHelper.getString(AppConfig.EX_KEY, "");
                    resetExpression(exData);
                }
            }
        } else if (requestCode == AppConfig.REQUEST_VOTE) {
            if (resultCode == RESULT_OK && data != null) {
                String result = data.getStringExtra("vote_message");
                if (result != null && !TextUtils.isEmpty(result)) {
                    VoteBody body = GsonHelper.getObject(result, VoteBody.class);
                    body.setSecret(isSecret ? 1 : 0);
                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                        String mucUserNick = MucUser
                            .getMucUserNick(ContextHelper.getContext(), userBean.getUserId(),
                                getUserId());
                        body.setMucNickName(
                            ChatHelper.getUserRemarkName(mucUserNick, getUserNick(), getUserId()));
                        body.setSenderAvatar(getUserAvatar());
                        body.setGroupName(userBean.getUserNick());
                    } else {
                        body.setMucNickName(getUserNick());
                    }
                    sendMessage(body.toJson(), EMessageType.VOTE);
                }
            }
        } else if (requestCode == AppConfig.REQUEST_COLLECTION) {
            if (resultCode == RESULT_OK) {
                String content = data.getStringExtra("content");
                String type = data.getStringExtra("type");
                if (!TextUtils.isEmpty(content)) {
                    sendMessage(content, ChatHelper.getMessageTypeByString(type));
                }
            }
        } else if (requestCode == AppConfig.REQUEST_USER) {//@功能
            if (resultCode == RESULT_OK || data != null) {
                UserBean userBean = data.getParcelableExtra("remindBean");
                if (!atUsers.contains(userBean)) {
                    atUsers.add(userBean);
                    String text = mInputMenu.getText();
                    String userNick = ChatHelper
                        .getUserNick(userBean.getUserNick(), userBean.getUserId());
                    mInputMenu.setEmojicon(text + userNick + " ");
                }
            }
        } else if (requestCode == AppConfig.REQUEST_CHANGE_CONFIG) {//更新聊天配置信息
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    int chatBg = data.getIntExtra("chat_bg", 0);
                    userBean.setBgId(chatBg);
                    updateChatBackGround();
                }
            } else {//刷新数据
                loadChat(0);
                scrollChat(ESCrollType.BOTTOM);
            }
        } else if (requestCode == REQUEST_TRANSFOR) {
            if (resultCode == RESULT_OK) {
                showTransforSuccess(true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showTransforSuccess(false);
                    }
                }, 1200);
            }
        } /*else if (requestCode == REQUEST_PREVIEW_SECRET) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }*/
    }


    private EMessageType getMessageType(EUploadFileType t) {
        switch (t) {
            case JPG:
                return EMessageType.IMAGE;
            case GIF:
                return EMessageType.FACE;
            case VOICE:
                return EMessageType.VOICE;
            case VIDEO:
                return EMessageType.VIDEO;
        }
        return null;
    }

    //批量上传图片
    private void uploadImage(List<String> images, boolean isOrigin, final int position) {
        int size = images.size();
        if (position >= images.size()) {
            uploadMap.clear();
            return;
        }
        String currentUrl = images.get(position);//当前上传url
        boolean isGif = ContextHelper.isGif(currentUrl);
        HttpUtils.getInstance()
            .uploadFileProgress(currentUrl, isGif ? EUploadFileType.GIF : EUploadFileType.JPG,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        System.out.println("上传成功-" + position);
                        if (result != null && result instanceof ImageUploadEntity) {
                            if (uploadMap != null && uploadMap.size() > 0) {
                                ImageUploadEntity entity = (ImageUploadEntity) result;

                                IChatRoomModel message = uploadMap.get(position);
                                if (message != null) {
                                    if (!TextUtils.isEmpty(ImageUploadEntity.toJson(entity))) {
                                        ProviderChat
                                            .updateMessageAfterUpload(ContextHelper.getContext(),
                                                message.getMsgId(),
                                                ImageUploadEntity.toJson(entity),
                                                ESendType.FILE_SUCCESS);
                                        message.setSendType(ESendType.FILE_SUCCESS);
                                        BodyEntity bodyEntity;
                                        if (ChatHelper.isGroupChat(userBean.getChatType())) {
                                            bodyEntity = MessageManager.getInstance()
                                                .createBody(ImageUploadEntity.toJson(entity),
                                                    isSecret, message.getMsgType(), getUserAvatar(),
                                                    getUserNick(), userBean.getRemarkName());
                                        } else {
                                            bodyEntity = MessageManager.getInstance()
                                                .createBody(ImageUploadEntity.toJson(entity),
                                                    isSecret, message.getMsgType(), getUserNick());
                                        }
                                        message.setUploadUrl(BodyEntity.toJson(bodyEntity));

                                    }
                                    sendUploadMessage(message);
                                    uploadMap.remove(position);
                                }
                            }
                            if (position < size - 1) {  //发送成功后，继续发送后续图片
                                uploadImage(images, isOrigin, position + 1);
                            }
                        } else {
                            uploadImageFailed();
                        }

                    }

                    @Override
                    public void onFailed() {
                        System.out.println("上传失败");
                        uploadImageFailed();
                    }

                    @Override
                    public void onProgress(int progress) {
                        System.out.println("上传进度" + progress);
                        if (uploadMap != null && uploadMap.size() > 0) {
                            IChatRoomModel message = uploadMap.get(position);
                            updateUploadProgress(message, progress);
                        }
                    }

                });
    }

    //单张图片
    private void uploadImageSingle(String filePath) {
        HttpUtils.getInstance()
            .uploadFileProgress(filePath, EUploadFileType.JPG, new IUploadListener() {
                @Override
                public void onSuccess(Object result) {
                    System.out.println("上传成功");
                    if (result != null && result instanceof ImageUploadEntity) {
                        if (uploadMap != null && uploadMap.size() > 0) {
                            ImageUploadEntity entity = (ImageUploadEntity) result;
                            IChatRoomModel message = uploadMap.get(0);
                            if (!TextUtils.isEmpty(ImageUploadEntity.toJson(entity))) {
                                message.setSendType(ESendType.FILE_SUCCESS);
                                ProviderChat.updateMessageAfterUpload(ContextHelper.getContext(),
                                    message.getMsgId(), ImageUploadEntity.toJson(entity),
                                    ESendType.FILE_SUCCESS);
                                BodyEntity bodyEntity;
                                if (ChatHelper.isGroupChat(userBean.getChatType())) {
                                    bodyEntity = MessageManager.getInstance()
                                        .createBody(ImageUploadEntity.toJson(entity), isSecret,
                                            message.getMsgType(), getUserAvatar(), getUserNick(),
                                            userBean.getRemarkName());
                                } else {
                                    bodyEntity = MessageManager.getInstance()
                                        .createBody(ImageUploadEntity.toJson(entity), isSecret,
                                            message.getMsgType(), getUserNick());
                                }
                                message.setUploadUrl(BodyEntity.toJson(bodyEntity));
                            }
                            sendUploadMessage(message);
                        }
                    } else {
                        uploadImageFailed();
                    }
                }

                @Override
                public void onFailed() {
                    System.out.println("上传失败");
                    uploadImageFailed();
                }

                @Override
                public void onProgress(int progress) {
                    System.out.println("上传进度" + progress);
                    if (uploadMap != null && uploadMap.size() > 0) {
                        IChatRoomModel message = uploadMap.get(0);
                        updateUploadProgress(message, progress);
                    }
                }
            });
    }


    //上传语音
    private void uploadVoice(IChatRoomModel message) {
        if (message == null) {
            return;
        }
        String json = message.getContent();//当前上传url
        if (TextUtils.isEmpty(json)) {
            return;
        }
        HttpUtils.getInstance()
            .uploadFileProgress(json, EUploadFileType.VOICE,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        System.out.println("语音上传成功");
                        if (result != null && result instanceof VoiceUploadEntity) {
                            VoiceUploadEntity entity = (VoiceUploadEntity) result;
                            entity.setTimeLength(message.getTimeLength());

                            if (message != null) {
                                if (!TextUtils.isEmpty(VoiceUploadEntity.toJson(entity))) {
                                    ProviderChat
                                        .updateMessageAfterUpload(ContextHelper.getContext(),
                                            message.getMsgId(), VoiceUploadEntity.toJson(entity),
                                            ESendType.FILE_SUCCESS);
                                    message.setSendType(ESendType.FILE_SUCCESS);
                                    BodyEntity bodyEntity;
                                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                                        bodyEntity = MessageManager.getInstance()
                                            .createBody(VoiceUploadEntity.toJson(entity), isSecret,
                                                message.getMsgType(), getUserAvatar(),
                                                getUserNick(), userBean.getRemarkName());
                                    } else {
                                        bodyEntity = MessageManager.getInstance()
                                            .createBody(VoiceUploadEntity.toJson(entity), isSecret,
                                                message.getMsgType(), getUserNick());
                                    }

                                    message.setUploadUrl(BodyEntity.toJson(bodyEntity));
                                }
                                sendUploadMessage(message);
                            }
                        }

                    }

                    @Override
                    public void onFailed() {
                        System.out.println("上传失败");
                        updateError(message);
                    }

                    @Override
                    public void onProgress(int progress) {
                        System.out.println("上传进度" + progress);
                        updateUploadProgress(message, progress);
                    }
                });
    }

    //上传视频图片
    private void uploadVideoImage(IChatRoomModel model) {
        VideoUploadEntity entity = VideoUploadEntity.fromJson(model.getContent());
        if (entity == null) {
            updateError(model);
            return;
        }
        HttpUtils.getInstance()
            .uploadFileProgress(entity.getImageUrl(), EUploadFileType.JPG,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        System.out.println("上传成功");
                        if (result != null && result instanceof ImageUploadEntity) {
                            ImageUploadEntity entity = (ImageUploadEntity) result;
                            if (entity != null) {
                                uploadVideo(model, entity);
                            } else {
                                updateError(model);
                            }
                        } else {
                            updateError(model);
                        }
                    }

                    @Override
                    public void onFailed() {
                        System.out.println("上传失败");
                        updateError(model);
                    }

                    @Override
                    public void onProgress(int progress) {
                        updateUploadProgress(model, 3);
                    }
                });
    }

    //上传视频
    private void uploadVideo(IChatRoomModel message, ImageUploadEntity image) {
        if (message == null) {
            return;
        }
        String json = message.getContent();//当前上传url
        VideoUploadEntity entitys = VideoUploadEntity.fromJson(json);
        if (entitys == null) {
            return;
        }
        HttpUtils.getInstance()
            .uploadFileProgress(entitys.getVideoUrl(), EUploadFileType.VIDEO,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        System.out.println("视频上传成功");
                        if (result != null && result instanceof VideoUploadEntity) {
                            VideoUploadEntity entity = (VideoUploadEntity) result;
                            entity.setImageUrl(image.getOriginalUrl());
                            entity.setImageSize(image.getOriginalSize());
                            if (message != null) {
                                if (!TextUtils.isEmpty(VideoUploadEntity.toJson(entity))) {
                                    ProviderChat
                                        .updateMessageAfterUpload(ContextHelper.getContext(),
                                            message.getMsgId(), VideoUploadEntity.toJson(entity),
                                            ESendType.FILE_SUCCESS);
                                    message.setSendType(ESendType.FILE_SUCCESS);
                                    BodyEntity bodyEntity;
                                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                                        bodyEntity = MessageManager.getInstance()
                                            .createBody(VideoUploadEntity.toJson(entity), isSecret,
                                                message.getMsgType(), getUserAvatar(),
                                                getUserNick(), userBean.getRemarkName());
                                    } else {
                                        bodyEntity = MessageManager.getInstance()
                                            .createBody(VideoUploadEntity.toJson(entity), isSecret,
                                                message.getMsgType(), getUserNick());
                                    }
                                    message.setUploadUrl(BodyEntity.toJson(bodyEntity));
                                }
                                sendUploadMessage(message);
                            }
                        }

                    }

                    @Override
                    public void onFailed() {
                        System.out.println("上传失败");
                        updateError(message);
                    }

                    @Override
                    public void onProgress(int progress) {
                        System.out.println("上传进度" + progress);
                        if (progress <= 3) {  //上传图片完成进度为3
                            progress = 5;
                        }
                        updateUploadProgress(message, progress);

                    }
                });
    }

    //重新上传图片
    private void reUploadImage(IChatRoomModel message) {

        String json = message.getContent();//当前上传url
        ImageUploadEntity entity = ImageUploadEntity.fromJson(json);
        if (entity == null) {
            return;
        }
        HttpUtils.getInstance()
            .uploadImageProgress(entity.getOriginalUrl(), EUploadFileType.JPG,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        System.out.println("上传成功-" + result);
                        if (result != null && result instanceof ImageUploadEntity) {
                            ImageUploadEntity entity = (ImageUploadEntity) result;
                            if (message != null) {
                                BodyEntity bodyEntity;
                                if (ChatHelper.isGroupChat(userBean.getChatType())) {
                                    bodyEntity = MessageManager.getInstance()
                                        .createBody(ImageUploadEntity.toJson(entity), isSecret,
                                            message.getMsgType(), getUserAvatar(), getUserNick(),
                                            userBean.getRemarkName());
                                } else {
                                    bodyEntity = MessageManager.getInstance()
                                        .createBody(ImageUploadEntity.toJson(entity), isSecret,
                                            message.getMsgType(), getUserNick());
                                }
                                message.setUploadUrl(BodyEntity.toJson(bodyEntity));
                                message.setSendType(ESendType.FILE_SUCCESS);
                                ProviderChat.updateMessageAfterUpload(ContextHelper.getContext(),
                                    message.getMsgId(), ImageUploadEntity.toJson(entity),
                                    ESendType.FILE_SUCCESS);
                                sendUploadMessage(message);
                            }

                        } else {
                            updateError(message);
                        }

                    }

                    @Override
                    public void onFailed() {
                        System.out.println("上传失败");
                        updateError(message);

                    }

                    @Override
                    public void onProgress(int progress) {
                        System.out.println("上传进度" + progress);
                        updateUploadProgress(message, progress);
                    }
                });
    }


    private void uploadImageFailed() {
        if (uploadMap != null && uploadMap.size() > 0) {
            Set<Map.Entry<Integer, IChatRoomModel>> entrySet = uploadMap.entrySet();
            Iterator<Map.Entry<Integer, IChatRoomModel>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, IChatRoomModel> m = iterator.next();
                IChatRoomModel message = m.getValue();
                updateError(message);
            }
        }
    }

    private void updateError(IChatRoomModel message) {
        ProviderChat
            .updateSendStatus(ContextHelper.getContext(), message.getMsgId(),
                ESendType.ERROR);
        mHandler.postDelayed(() -> mAdapter.onChange(), 100);
    }

    private void updateUploadProgress(IChatRoomModel message, int progress) {
        mHandler.postDelayed(() -> {
            int itemIndex = mAdapter.getMessagePostion(message);
            int visiblePosition = chatMessageList.getListView().getListView()
                .getFirstVisiblePosition();
            if (itemIndex - visiblePosition >= 0) {
                View view = chatMessageList.getListView().getListView()
                    .getChildAt(itemIndex - visiblePosition);
                if (view == null) {
                    return;
                }
                mAdapter.updateItemSendType(view, itemIndex, ESendType.SENDING, progress);
            }
        }, 100);
    }

    private IChatRoomModel createMessage(String content, EMessageType type) {
        MessageBean message = new MessageBean();
        if (type == EMessageType.INPUTING || type == EMessageType.RECORDING) {
            message.setMessageType(type);
            message.setMsgId(UUID.randomUUID().toString());
            message.setContent("");
            message.setAvatarUrl(getUserAvatar());
            message.setTime(System.currentTimeMillis());
            message.setFrom(getUserId());
            message.setTo(userBean.getUserId());
            message.setSecret(false);
            message.setSendType(ESendType.SENDING);
            message.setIncoming(false);
            message.setNick(getUserNick());
            if (userBean.getChatType() == EChatType.PRIVATE.ordinal()) {
                message.setGroupChat(false);
            } else {
                message.setGroupChat(true);
            }
            message.setHasReaded(1);
            message.setServerReaded(1);
        } else {
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(userId) && !TextUtils
                .isEmpty(getUserId())) {
                if (type == EMessageType.MAP || type == EMessageType.VOTE) {
                    message.setContent(content);
                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                        String muckNick = MucUser
                            .getMucUserNick(ContextHelper.getContext(), userBean.getUserId(),
                                getUserId());
                        message.setGroupChat(true);
                        message.setNick(muckNick);
                    } else {
                        message.setGroupChat(false);
                        message.setNick(getUserNick());
                    }
                } else {
                    BodyEntity entity;
                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                        String muckNick = MucUser
                            .getMucUserNick(ContextHelper.getContext(), userBean.getUserId(),
                                getUserId());
                        entity = MessageManager.getInstance()
                            .createBody(content, isSecret, type, getUserAvatar(),
                                muckNick, userBean.getRemarkName());
                        message.setGroupChat(true);
                        message.setNick(muckNick);
                    } else {
                        entity = MessageManager.getInstance()
                            .createBody(content, isSecret, type, getUserNick());
                        message.setGroupChat(false);
                        message.setNick(getUserNick());
                    }
                    message.setContent(BodyEntity.toJson(entity));

                }
                message.setMessageType(type);
                message.setMsgId(UUID.randomUUID().toString());
                message.setAvatarUrl(getUserAvatar());
                message.setTime(System.currentTimeMillis());
                message.setFrom(getUserId());
                message.setTo(userId);
                message.setSecret(isSecret);
                message.setSendType(ESendType.SENDING);
                message.setIncoming(false);
                message.setHasReaded(1);
                message.setServerReaded(1);
            }
        }
        return message;
    }

    //发送atAll消息，群聊
    private void sendMessage(String content, EMessageType type, boolean isAtAll) {
        if (isAtAll) {
            String msgId = UUID.randomUUID().toString();
            IChatRoomModel message = null;
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(userId) && !TextUtils
                .isEmpty(getUserId())) {
                String muckNick = MucUser
                    .getMucUserNick(ContextHelper.getContext(), userBean.getMucId(),
                        getUserId());
                BodyEntity entity = MessageManager.getInstance()
                    .createBody(content, isSecret, type, getUserAvatar(), muckNick,
                        userBean.getRemarkName());
                RoomMessage.Builder builder = RoomMessage.newBuilder();
                builder.setId(msgId);
                builder.setMucid(userId);
                builder.setUsername(getUserId());
                builder.setTime(System.currentTimeMillis());
                builder.setContent(BodyEntity.toJson(entity));
                builder.setType(ChatHelper.getMessageType(type));
                builder.setAtAll(ESureType.YES.value);
                message = createMessageBean(builder.build(), checkIsSecret());
            }
            addMessage(message);
            saveMessage(message);
            MessageManager.getInstance().sendMessage(message);
            doSendSuccess(msgId, System.currentTimeMillis());
            scrollChat(ESCrollType.BOTTOM);
        }
    }

    //发送无需上传文件的消息
    private void sendTextMessage(String content, EMessageType type) {
        int totalSize = content.length();
        if (totalSize <= MIN_TEXT) {
            isSendingHypertext = false;
            IChatRoomModel message = createMessage(content, type);
            addMessage(message);
            saveMessage(message);
            MessageManager.getInstance().sendMessage(message);
            scrollChat(ESCrollType.BOTTOM);
        } else {
            System.out.println("totalSize=" + totalSize);
            isSendingHypertext = true;
            int per = totalSize / MIN_TEXT;
            if (per > 10) {
                T.show("文本长度不能超过10240");
                return;
            }
            if (totalSize > per * MIN_TEXT) {
                per = per + 1;
            }
            sendTexts = new ArrayList<>();
            for (int i = 0; i < per; i++) {
                if (i < per - 1) {
                    sendTexts.add(content.substring(i * MIN_TEXT, (i + 1) * MIN_TEXT));
                } else {
                    sendTexts.add(content.substring(i * MIN_TEXT, totalSize));
                }
            }
            sendHypertext(sendTexts, 0);
        }
    }

    //分段发送富文本
    private void sendHypertext(List<String> list, int position) {
        if (position == list.size() - 1) {
            isSendingHypertext = false;
        }
        textPosition = position;
        IChatRoomModel message = createMessage(list.get(position), EMessageType.TEXT);
        addMessage(message);
        saveMessage(message);
        MessageManager.getInstance().sendMessage(message);
        scrollChat(ESCrollType.BOTTOM);
    }

    //发送无需上传文件的消息
    private void sendMessage(String content, EMessageType type) {
        IChatRoomModel message = createMessage(content, type);
        addMessage(message);
        saveMessage(message);
        MessageManager.getInstance().sendMessage(message);
        scrollChat(ESCrollType.BOTTOM);
    }

    //发送无内容消息的消息,无需保存本地
    private void sendMessage(EMessageType type) {
        IChatRoomModel message = createMessage("", type);
        MessageManager.getInstance().sendMessage(message);
        scrollChat(ESCrollType.BOTTOM);
    }

    //发送已读
    private void sendMessage() {
        Map<String, IChatRoomModel> map = MessageManager.getInstance().getSequenceReadedChatMap();
        if (map != null && map.size() > 0) {
            MessageManager.getInstance().sendReadMessage();
        }
    }


    //检查是否是密聊，临时逻辑
    private boolean checkIsSecret() {
        return isSecret;
    }


    //发送需要上传文件的消息
    private void sendUploadMessage(IChatRoomModel message) {
        MessageManager.getInstance().sendMessage(message);
        scrollChat(ESCrollType.BOTTOM);
    }

    private MessageBean createMessageBean(Object message, boolean isSecret) {
        MessageBean bean = null;
        if (message instanceof PrivateMessage) {
            PrivateMessage privateMessage = (PrivateMessage) message;
            PrivateMessage.Builder builder = privateMessage.toBuilder();
            bean = new MessageBean();
            bean.setMessageType(ChatHelper.getMessageType(builder.getType()));
            bean.setMsgId(builder.getId());
            bean.setContent(builder.getContent());
            bean.setAvatarUrl(builder.getAvatar());
            bean.setCancel(builder.getCancel());
            bean.setCode(builder.getCode());
            bean.setTime(builder.getTime());
            bean.setFrom(builder.getFrom());
            bean.setTo(builder.getTo());
            bean.setSecret(isSecret);
            bean.setSendType(ESendType.SENDING);
            bean.setIncoming(false);
            bean.setGroupChat(false);
            bean.setNick(getUserNick());
            bean.setHasReaded(1);//本地已读
            bean.setServerReaded(1);//服务器未读
        } else if (message instanceof RoomMessage) {
            RoomMessage roomMessage = (RoomMessage) message;
            RoomMessage.Builder builder = roomMessage.toBuilder();
            bean = new MessageBean();
            bean.setMessageType(ChatHelper.getMessageType(builder.getType()));
            bean.setMsgId(builder.getId());
            bean.setContent(builder.getContent());
            bean.setCancel(builder.getCancel());
            bean.setCode(builder.getCode());
            bean.setTime(builder.getTime());
            bean.setFrom(builder.getUsername());
            bean.setTo(builder.getMucid());
            bean.setAvatarUrl(getUserAvatar());
            bean.setSecret(isSecret);
            bean.setSendType(ESendType.SENDING);
            bean.setGroupChat(true);
            bean.setIncoming(false);
            bean.setHasReaded(1);
            bean.setServerReaded(1);//服务器未读
        }
        return bean;
    }

    private void saveMessage(IChatRoomModel message) {
        ProviderChat.insertMessageAsyn(ContextHelper.getContext(), message);
        if (message.isGroupChat()) {
            MessageManager.getInstance()
                .saveRecent(message, userBean.getAvatarUrl(), ChatHelper
                        .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                            userBean.getUserId()), noDisturb, userBean.getBgId(),
                    topFlag > 0 ? topFlag : ESureType.NO.ordinal(), false);

        } else {
            MessageManager.getInstance()
                .saveRecent(message, userBean.getAvatarUrl(), ChatHelper
                        .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                            userBean.getUserId()), noDisturb, userBean.getBgId(),
                    topFlag > 0 ? topFlag : ESureType.NO.ordinal(),
                    false);
        }
        notifyRecentMessage();
    }

    private void notifyRecentMessage() {
        RefreshEntity entity = new RefreshEntity();
        entity.setActivity(EActivityNum.MAIN.value);
        entity.setFragment(EFragmentNum.TAB_MESSAGE.value);
        RefreshEvent event = (RefreshEvent) EventFactory.INSTANCE
            .create(EventEnum.MAIN_REFRESH, entity);
        EventBus.getDefault().post(event);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event != null) {
            if (event instanceof ChatMessageEvent) {
                ChatMessageEvent chatEvent = (ChatMessageEvent) event;
                if (chatEvent.getType() == ChatMessageEvent.RECEIVE) {
                    IChatRoomModel message = chatEvent.getPacket();
                    if (message != null && !TextUtils.isEmpty(message.getMsgId())) {
                        L.d("收到消息:" + message.getContent());
                        if (isDoingMessage(message.getMsgType())) {
                            startInputTimer();//开启定时器
                            mHandler.sendEmptyMessage(INPUT_AND_RECORDING);
                            updateToolBarTitle(message);
                        } else {
                            loadChat(0);
                            scrollChat(ESCrollType.BOTTOM);
                        }
                    }
                } else if (chatEvent.getType() == ChatMessageEvent.ERROR) {//接受到错误消息
                    doSendError(chatEvent.getMsgId());
                } else if (chatEvent.getType() == ChatMessageEvent.CANCEL) {
                    System.out.println(ChatActivity.class.getSimpleName() + ":cancel");
                    loadChat(0);
                    scrollChat(ESCrollType.BOTTOM);
                }
            } else if (event instanceof RefreshEvent) {
                RefreshEntity entity = ((RefreshEvent) event).getEntity();
                if (entity != null) {
                    int activity = entity.getActivity();
                    if (activity > 0 && activity == EActivityNum.CHAT.ordinal()) {
                        loadChat(0);
                        scrollChat(ESCrollType.BOTTOM);
                    } else if (activity > 0 && activity == EActivityNum.ATALL.ordinal()) {
                        //发送@All消息
                        sendMessage(
                            "@所有人\n" + MucInfo.selectByMucId(getApplicationContext(), userId)
                                .getSubject(), EMessageType.TEXT, true);
                    }
                }
            } else if (event instanceof ExcuteEvent) {
                ExcuteResultMessage message = ((ExcuteEvent) event).getPacket();
                int code = message.message.getCode();
                if (code == Common.EMOTICON_SAVE_SUCCESS) {
                    String result = message.message.getResult();
                    if (!TextUtils.isEmpty(result)) {
                        saveEmoticon(result);
                        T.show("保存成功");
                    } else {
                        T.show("保存失败");
                    }
                } else if (code == Common.EMOTICON_SAVE_ERROR) {
                    T.show("保存失败");
                } else if (code == Common.EMOTICON_QUERY_SUCCESS) {
                    String result = message.message.getResult();
                    if (!TextUtils.isEmpty(result)) {
                        saveEmoticon(result);
                    }
                }
            } else if (event instanceof ResponseEvent) {
                RespMessage message = ((ResponseEvent) event).getPacket();
                int code = message.response.getCode();
                if (code == Common.INVITE_DUMPLICATED) {
                    T.show("已发送");
                }
            }
        }

    }

    private void addMessage(IChatRoomModel message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (message.getTo().equals(userBean.getUserId())) {
                    if (mAdapter != null) {
                        mAdapter.addMessage(message);
                        mAdapter.notifyDataSetChanged();
                        if (mListView.getLastVisiblePosition()
                            == mAdapter.getTotalItemsCount() - 2) {
                            scrollChat(ESCrollType.BOTTOM);
                        }
                    }
                }
            }
        });
    }

    /*
    * s是否是正在输入或者录音的消息
    * */
    private boolean isDoingMessage(EMessageType type) {
        if (type == EMessageType.INPUTING || type == EMessageType.RECORDING) {
            return true;
        } else {
            return false;
        }
    }

    private void updateToolBarTitle(IChatRoomModel model) {
        if (model != null) {
            if (model.getMsgType() == EMessageType.INPUTING) {
                toolbar.setTitleText("对方正在输入...");
            } else if (model.getMsgType() == EMessageType.RECORDING) {
                toolbar.setTitleText("对方正在录音...");
            }
        } else {
            toolbar.setTitleText(
                ChatHelper.getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                    userBean.getUserId()));
        }

    }

    private void startInputTimer() {
        firstInputingTime = System.currentTimeMillis();
        if (timer == null) {
            initInputTimer();
        }
        timer.schedule(timerTask = new InputTimerTask(), 50, 300);
    }

    private void stopInpputTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        //TimerTask是一次性的
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }


    private boolean isMapValid(Map<String, IChatRoomModel> map) {
        if (map == null || map.size() <= 0) {
            return false;
        }
        return true;
    }

    private boolean isReadMapValid(Map<String, ReadedMessageList> map) {
        if (map == null || map.size() <= 0) {
            return false;
        }
        return true;
    }


    private void doSendError(String msgId) {
        Map<String, IChatRoomModel> sequenceMap = MessageManager.getInstance().getSequenceMap();
//        if (isMapValid(sequenceMap) && sequenceMap.get(msgId) != null) {
        ProviderChat.updateSendStatus(ContextHelper.getContext(),
            msgId, ESendType.ERROR);
        sequenceMap.remove(msgId);
        mAdapter.onChange();
//        }
    }

    private void doSendSuccess(String msgId, long time) {
        Map<String, IChatRoomModel> sequenceMap = MessageManager.getInstance().getSequenceMap();
        if (isMapValid(sequenceMap) && sequenceMap.get(msgId) != null) {
            System.out.println(
                "成功消息id:" + msgId + "--" + sequenceMap.get(msgId)
                    .getContent());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("发送进度:" + 100);
                    if (sequenceMap.get(msgId) != null) {
                        ProviderChat.updateSendSuccess(ContextHelper.getContext(),
                            sequenceMap.get(msgId).getMsgId(), ESendType.MSG_SUCCESS, time);
                        sequenceMap.remove(msgId);
                        mAdapter.onChange();
                    }

                    if (isSendingHypertext) {
                        sendHypertext(sendTexts, ++textPosition);
                    }
                }
            }, 200);
        }
    }


    private void doReadMessageSendSuccess(String msgId) {
        Map<String, ReadedMessageList> sequenceMap = MessageManager.getInstance()
            .getSequenceReadedMessageMap();
        if (isReadMapValid(sequenceMap) && sequenceMap.get(msgId) != null) {
            System.out.println("成功已读消息id:" + msgId + "--" + sequenceMap.get(msgId).getId());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sequenceMap.get(msgId) != null) {
                        ProviderChat.updateServerReaded(sequenceMap.get(msgId).getId());
                        sequenceMap.remove(msgId);
                        mAdapter.onChange();
                    }
                }
            }, 200);
        }
    }


    private void scrollChat(ESCrollType type) {
        switch (type) {
            case CURRENT:
                break;
            case BOTTOM:
//                if (!isInbottom) {
//                    return;
//                }
                if (mListView.getLastVisiblePosition() > 0
                    && mListView.getLastVisiblePosition() >= mAdapter.getTotalItemsCount() - 2) {
                    return;
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatMessageList.requestFocusFromTouch();
                        chatMessageList.scrollDown();
                    }
                }, 100);
                break;
            case TOP:
                chatMessageList.scrollUp();
                break;
        }

    }

    private void scrollChatToPostion(int position) {
        if (position > mAdapter.getTotalItemsCount()) {
            return;
        } else {
            chatMessageList.scrollToPostion(position);
        }
    }

//    private boolean checkAt(String msg) {
//        if (msg.contains("@" + AppConfig.INSTANCE.get(AppConfig.ACCOUT))) {
//            return true;
//        }
//        return false;
//    }

    public void resetExpression(String date) {
        //mInputMenu.removeEmojiconGroup(3);
        emojiconGroupList.remove(3);
        emojiconGroupList.add(EmojiconDefaultGroupData.getCustomData(date));
        mInputMenu.addEmojiconGroup(emojiconGroupList);
    }

    /*
    * 保存草稿
    * */
    private void saveDraft() {
        String draft = mInputMenu.getText();
        if (!TextUtils.isEmpty(draft)) {
            SPHelper.saveValue(getUserId() + "&" + userBean.getUserId(), draft);
        } else {
            SPHelper.remove(getUserId() + "&" + userBean.getUserId());
        }
    }

    private void hideInput() {
        mInputMenu.removeFocus();
        mInputMenu.hideKeyboard();
        mInputMenu.hideExtendMenuContainer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraft();
        hideInput();
        ClientConfig.I.removeListener(AckListener.class, this);

    }

    @Override
    protected void onStop() {
        super.onStop();
//        ProviderChat.markReaded(this, userId, true);
        ProviderChat.updateHasReaded(userId, true);
        MessageManager.getInstance().setCurrentChat("");
        notifyRecentMessage();
        NotifyManager.getInstance().removeMessageNotification(getUserId(), userId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ClientConfig.I.removeListener(AckListener.class, this);
        MessageManager.getInstance().setCurrentChat("");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageManager.getInstance().clearSequenceMap();
        stopInpputTimer();
    }

    @Override
    public void onAck(AckMessage message) {
        if (message != null) {
            //成功逻辑
            String msgId = message.ack.getIdList().get(0);
            long time = message.ack.getTime();
            doSendSuccess(msgId, time);
//            doReadMessageSendSuccess(msgId);
        }
    }

    @Override
    public void onEvent(ECellEventType type, Object o1, Object o2) {
        IChatRoomModel model = null;
        if (o1 == null) {
            return;
        }
        if (o1 instanceof IChatRoomModel) {
            model = (IChatRoomModel) o1;
        }
        if (model == null) {
            return;
        }

        switch (type) {
            case RESEND_EVENT:
                doResendMessage(model);
                break;
            case TEXT_CLICK:
                Intent intentText = new Intent(this, TextPreviewActivity.class);
                intentText.putExtra("text", model.getContent());
                intentText
                    .putExtra(TextPreviewActivity.PERVIEW_TYPE, TextPreviewActivity.TEXT_PERVIEW);
                startActivity(intentText);
                break;
            case IMAGE_CLICK:
                if (o2 != null && o2 instanceof ImageEventBean) {
                    ImageEventBean bean = (ImageEventBean) o2;
                    Intent intent = GalleryAnimationActivity
                        .newIntent(bean.getUrls(), bean.getMsgIds(), bean.getRects(),
                            bean.getBooleanLists(),
                            bean.getPosition(), "");
                    startActivity(intent);
                }

                break;
            case VIDEO_CLICK:
                if (o2 != null && o2 instanceof VideoEventBean) {
                    VideoEventBean bean = (VideoEventBean) o2;
                    Intent intent = LookUpVideoActivity
                        .newIntent(this, bean.getRect(), bean.getVideoPath(), "chat");
                    startActivity(intent);

                }
                break;
            case CARD_CLICK:
                CardEntity entity = CardEntity.fromJson(model.getContent());
                if (entity == null) {
                    return;
                }
                Intent intentCard = FriendDetailActivity
                    .createNormalIntent(this, entity.getFriendId());
                startActivity(intentCard);
                break;
            case MAP_CLICK:
                MapInfoEntity mAddressInfo = MapInfoEntity.fromJson(model.getContent());
                if (mAddressInfo == null) {
                    return;
                }
                ShowLocationActivity
                    .openActivity(this, ShowLocationActivity.SHOW_ADDDRESS, mAddressInfo);
                break;
            case MULTI_CLICK:
                MultiMessageEntity multiEntity = new MultiMessageEntity(model.getBody());
                if (multiEntity == null) {
                    break;
                }
                Intent intentMulti = new Intent(this, ActivityMultiMsgDetail.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", multiEntity);
                intentMulti.putExtras(bundle);
                startActivity(intentMulti);
                break;
            case SECRET:
                Intent intentSecret = new Intent(this, SecretActivity.class);
                intentSecret.putExtra("type", model.getMsgType().value);
                intentSecret.putExtra("msgId", model.getMsgId());
                intentSecret.putExtra("content", model.getBody());
                startActivity(intentSecret);
                break;
            case VOTE_CLICK:
                if (o2 != null && o2 instanceof VoteEntity) {
                    VoteEntity voteEntity = (VoteEntity) o2;
                    toVote(voteEntity.getVoteId());
                }
                toVote();
                break;
            case ADD_EX:
                if (o2 instanceof String) {
                    String data = (String) o2;
                    if (!TextUtils.isEmpty(data)) {
                        addEmoticon(data);
                    }
                }
                break;
            case TRANSFER_MSG:
                if (!AuthorityManager.getInstance().copyInside()) {
                    T.showShort(this, "请申请权限");
                    break;
                }
                boolean isHexMeet = false;
                if (isHexMeet) {
                    T.showShort(this, "视频会议不能转发");
                } else {
                    transforMessage(model);
                }
                break;
            case COLLECT_MSG:
                collectMessage(model);
                break;
            case COPY:
                copyText(model);
                break;
            case CANCEL:
                MessageManager.getInstance().sendCancelMessage(model);
                break;
            case AVATAR:
                Intent intent;
                if (model.isGroupChat()) {
                    intent = FriendDetailActivity.createNormalIntent(this, model.getFrom());
                } else {
                    if (model.isIncoming()) {
                        if (ChatHelper.isSystemUser(model.getTo())) {
                            return;
                        }
                        intent = FriendDetailActivity.createNormalIntent(this, model.getTo());
                    } else {
                        intent = FriendDetailActivity.createNormalIntent(this, model.getFrom());
                    }
                }
                startActivity(intent);
                break;
            case CLOCK_CLICK:
                Intent intentClock = ActivitysRouter.getInstance()
                    .invoke(this, ActivityPath.CLOCK_DETAIL_ACTIVITY_PATH);
                intentClock.putExtra("content", (SignInJsonRet) o2);
                startActivity(intentClock);
                break;
            case OA_CLICK:
                if (o2 != null && o2 instanceof PushBody) {
                    PushBody body = (PushBody) o2;
                    if (TokenHelper.isOATokenValid(getUserId())) {
                        toOpenUrl(body.getActionUrl(), body.getTitle(),
                            OATokenRepository.getToken());
                    } else {
                        getOAToken(body);
                    }
                }
                break;
            case READED:
                if (model != null) {
                    MessageDetailActivity.startActivity(this, (MessageBean) model);
                }
                break;
        }
    }

    private void collectMessage(IChatRoomModel message) {
        if (!CollectionManager.getInstance().checkItemExistByID(message.getMsgId())) {
            FavJson store = new FavJson();

            if (message.isIncoming()) {
                store.setProviderJid(message.getTo());
                store.setProviderNick(message.getNick());
                if (message.isGroupChat()) {
                    store.setFavCreaterAvatar("");
                    String roomName = message.getGroupName();
                    store.setProviderNick(
                        (TextUtils.isEmpty(roomName) ? "" : roomName + "/") + message.getNick());
                } else {
                    store.setFavCreaterAvatar(getUserAvatar());
                }
                //store.setFavContent(message.getContent());
                store.setFavUrl(message.getContent());
            } else {
                if (message.getMsgType() == EMessageType.IMAGE
                    || message.getMsgType() == EMessageType.VIDEO
                    || message.getMsgType() == EMessageType.VOICE) {
                    // store.setFavContent(message.getUploadUrl());
                } else {
                    if (message.getMsgType() == EMessageType.TEXT) {
                        //store.setFavContent(message.getContent());
                    } else {
                        //store.setFavContent(message.getBody());
                    }
                }
                store.setFavUrl(message.getUploadUrl());
                store.setProviderJid(message.getFrom());
                store.setProviderNick(message.getNick());
                store.setFavCreaterAvatar(getUserAvatar());
                store.setProviderNick(getUserNick());

            }
            String content = null;
            try {
                content = getFavContent(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            store.setFavContent(TextUtils.isEmpty(content) ? "" : content);
            store.setFavMsgId(message.getMsgId());
            store.setFavProvider(getUserId());
            store.setFavType(ChatHelper.getFavType(message.getMsgType()));
            store.setFavCreater(UserInfoRepository.getUserName());
            store.setFavTime(TimeUtils.getDate());
            store.setFavDes("");
            store.setFavId(System.currentTimeMillis());
            StoreManager.getInstance().upload(store);
        } else {
            T.show("已收藏，请勿重复");
        }
    }

    private void copyText(IChatRoomModel model) {
        if (AuthorityManager.getInstance().copyOutside()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(
                Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(null, model.getContent()));
            uploadCopyLog(model.getContent());
        } else {
            if (AuthorityManager.getInstance().copyInside()) {
                AuthorityManager.getInstance().copy(model.getContent());
                uploadCopyLog(model.getContent());
            } else {
                T.show("请申请权限");
            }
        }
    }

    private void showTransforSuccess(boolean var) {
        tv_success.setVisibility(var ? View.VISIBLE : View.GONE);
    }

    private void getOAToken(PushBody body) {
        /**
         * 单点登录 拿到Token，存SP
         **/
        String fgToken = SSOTokenRepository.getToken();
        if (TextUtils.isEmpty(fgToken)) {
            T.show("请退出登录，获取飞鸽Token");
            return;
        }
        HttpUtils.getInstance().getOAToken(fgToken)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject<OAToken>>(false) {
                @Override
                public void onHandleSuccess(ResponseObject<OAToken> response) {
                    if (response.code == 10) {
                        OAToken token = response.result;
                        if (token != null) {
                            String oaToken = token.oaToken;
                            if (!TextUtils.isEmpty(oaToken)) {
                                toOpenUrl(body.getActionUrl(), body.getTitle(), oaToken);
                            } else {
                                T.show("获取OAToken失败");
                            }
                        } else {
                            T.show("获取OAToken失败");
                        }
                    } else {
                        T.show("获取OAToken失败");
                    }
                }
            });


    }

    private void toOpenUrl(String url, String title, String token) {
        Intent intent = new Intent(this, BrowserActivity.class);
        StringBuilder builder = new StringBuilder(url);
        if (!url.contains("?")) {
            builder.append("?").append("&token=")
                .append(token);
        } else {
            builder.append("&token=")
                .append(token);
        }
        Uri uri = Uri.parse(builder.toString());
        intent.setData(uri);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private String getFavContent(IChatRoomModel message) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        if (message.getMsgType() == EMessageType.TEXT) {
            jsonObject.put("userHeadImageStr", message.getAvatarUrl());
            jsonObject.put("signContent", "");
            jsonObject.put("messageType", "1");
            jsonObject.put("type", message.isGroupChat() ? "1" : "0");
            jsonObject.put("userName", message.getFrom());
            jsonObject.put("recordTime", TimeUtils.getDate());
            jsonObject.put("content", message.getContent());

        } else if (message.getMsgType() == EMessageType.IMAGE) {

            JSONObject json = new JSONObject(message.getContent());
            //JSONObject json1 = new JSONObject(json.getString("body"));
            jsonObject.put("OriginalSzie", json.optString("OriginalSzie"));
            jsonObject.put("userHeadImageStr", message.getAvatarUrl());
            jsonObject.put("OriginalUrl", json.optString("OriginalUrl"));
            jsonObject.put("signContent", "");
            jsonObject.put("messageType", "2");
            jsonObject.put("type", message.isGroupChat() ? "1" : "0");
            jsonObject.put("ThumbnailUrl", json.optString("ThumbnailUrl"));
            jsonObject.put("ThumbnailSize", json.optString("ThumbnailSize"));
            jsonObject.put("recordTime", TimeUtils.getDate());

        } else if (message.getMsgType() == EMessageType.VIDEO) {
            JSONObject json = new JSONObject(message.getContent());
            jsonObject.put("ImageSize", json.optString("ImageSize"));
            jsonObject.put("userHeadImageStr", message.getAvatarUrl());
            jsonObject.put("signContent", "");
            jsonObject.put("messageType", "4");
            jsonObject.put("ImageUrl", json.optString("ImageUrl"));
            jsonObject.put("type", message.isGroupChat() ? "1" : "0");
            if (json.has("mucNickName")) {
                jsonObject.put("userName", json.optString("mucNickName"));
            } else {
                jsonObject.put("userName", message.getFrom());
            }
            jsonObject.put("VideoUrl", json.optString("VideoUrl"));
            jsonObject.put("recordTime", TimeUtils.getDate());
        } else if (message.getMsgType() == EMessageType.VOICE) {
            JSONObject object = new JSONObject(message.getBody());
            jsonObject.put("userHeadImageStr", message.getAvatarUrl());
            jsonObject.put("signContent", "");
            jsonObject.put("messageType", "3");
            jsonObject.put("voiceLenth", object.optString("timeLength"));
            jsonObject.put("type", message.isGroupChat() ? "1" : "0");
            if (object.has("mucNickName")) {
                jsonObject.put("userName", object.optString("mucNickName"));
            } else {
                jsonObject.put("userName", message.getFrom());
            }
            jsonObject.put("recordTime", TimeUtils.getDate());
            if (message.isIncoming()) {
                jsonObject.put("content", object.optString("body"));
            } else {
                JSONObject j = new JSONObject(message.getUploadUrl());
                jsonObject.put("content", j.optString("VoiceUrl"));
            }

        }
        return jsonObject.toString();
    }

    private void uploadCopyLog(String content) {
        String copyText = FileUtil
            .uploadUserOption(content, getUserId(), "a_copy_text");
        HttpUtils.getInstance().uploadLogger(copyText,
            ELogType.COPY, new IDataRequestListener() {
                @Override
                public void loadFailure(String reason) {
                    L.d("上传失败");
                }

                @Override
                public void loadSuccess(Object object) {
                    L.d("上传成功");
                }
            });
    }

    private void addEmoticon(String json) {
        String emoJson = SPHelper.getString(EX_KEY);
        if (!TextUtils.isEmpty(emoJson)) {
            if (!emoJson.contains(json)) {
                ExcuteMessage message = MessageManager.getInstance()
                    .createExcuteBody(ExcuteType.EMOTICON_SAVE, json);
                if (message != null) {
                    FingerIM.I.excute(message);
                }
            } else {
                T.show("已添加，请勿重复添加");
            }
        }

    }

    private void saveEmoticon(String json) {
        SPHelper.remove(AppConfig.EX_KEY);
        SPHelper.saveValue(AppConfig.EX_KEY, json);
        resetExpression(json);
    }

    private void checkAndLoadEmoticon() {
        String emoticon = SPHelper.getString(EX_KEY);
        if (TextUtils.isEmpty(emoticon)) {
            ExcuteMessage message = MessageManager.getInstance()
                .createExcuteBody(ExcuteType.EMOTICON_QUERY, getUserId());
            if (message != null) {
                FingerIM.I.excute(message);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMARA_PERMISSON_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toCamera();
            } else {
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat
                    .shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    T.show("相机权限已被禁止");
                }
            }
        } else if (requestCode == LOCATION_PERMISSON_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toMap();
            } else {
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat
                    .shouldShowRequestPermissionRationale(this, permission.ACCESS_FINE_LOCATION)) {
                    T.show("位置权限已被禁止");
                }
            }
        }
    }

    private void checkMessageReaded(int firstVisibleItem, int visibleItemCount) {
        if (mAdapter != null) {
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                IChatRoomModel model = mAdapter.getItem(i);
                if (model.isIncoming() && model.getMsgType() == EMessageType.TEXT
                    && model.getServerReaded() == 1) {
                    Map<String, IChatRoomModel> map = MessageManager.getInstance()
                        .getSequenceReadedChatMap();
                    if (!map.containsKey(model.getMsgId())) {
                        map.put(model.getMsgId(), model);
                    }
                }
            }
            mHandler.sendEmptyMessageDelayed(READ, 500);
        }
    }
}

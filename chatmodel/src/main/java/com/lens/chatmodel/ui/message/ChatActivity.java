package com.lens.chatmodel.ui.message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import android.widget.TextView;
import com.example.webview.BrowserActivity;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.AckListener;
import com.fingerchat.api.message.AckMessage;
import com.fingerchat.proto.message.Muc.MucItem;
import com.fingerchat.proto.message.MucChat.RoomMessage;
import com.fingerchat.proto.message.PrivateChat.PrivateMessage;
import com.lens.chatmodel.ChatEnum;
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
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ChatMessageEvent;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.helper.ChatHelper;
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
import com.lens.chatmodel.view.ChatMessageList;
import com.lens.chatmodel.view.CustomContextMenu;
import com.lens.chatmodel.view.emoji.ChatInputMenu;
import com.lens.chatmodel.view.emoji.ChatInputMenu.ChatInputMenuListener;
import com.lens.chatmodel.view.emoji.EmotionKeyboard;
import com.lens.chatmodel.view.voice_recorder_view.VoiceRecorderView;
import com.lens.chatmodel.view.voice_recorder_view.VoiceRecorderView.EaseVoiceRecorderCallback;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
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
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.components.pulltorefresh.XCPullToLoadMoreListView;
import com.lensim.fingerchat.data.Api;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.data.me.content.StoreManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_EX;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_IMAGE;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_TRANSFOR;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_VIDEO;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_VOTE;
import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

/**
 * Created by LL130386 on 2017/12/5.
 */

public class ChatActivity extends BaseUserInfoActivity implements AckListener, IChatEventListener {

    public final static int MAP_FOR_CHAT = 3990;
    public final static int INPUT_AND_RECORDING = 1 << 3;
    public final static int INPUTTING = 1 << 1;
    public final static int RECORDING = 1 << 2;
    public final static int MIN_UNREAD_COUNT = 15;
    private String userId;
    private ChatInputMenu mInputMenu;
    private List<EmojiconGroupEntity> emojiconGroupList;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, IChatRoomModel> uploadMap = new HashMap<>();
    private MessageAdapter mAdapter;

    private FGToolbar toolbar;
    private int mCurrrentPage = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
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
                        int chatType = intent.getIntExtra("chat_type", -1);
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
                    userBean.setChatType(0);
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
            scrollChatToPostion(mAdapter.getTotalItemsCount() - unreadCount - 1);
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
            public void clickCollect() {
                if (mAdapter.getSelectedIds() != null && mAdapter.getSelectedIds().size() > 0) {
                    mInputMenu.setVisibility(View.VISIBLE);
                    viewAttachBottom.setVisible(false);
                    mAdapter.hideBottomMenu();
                }
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
        mInputMenu.setChatInputMenuListener(new ChatInputMenuListener() {
            @Override
            public void onSendMessage(String content) {
                if (mHandler != null) {
                    mHandler.removeMessages(INPUTTING);
                }
                sendMessage(content, EMessageType.TEXT);
                //清空atUser
                atUsers.clear();
            }

            @Override
            public void onBigExpressionClicked(
                Emojicon emojicon) {
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

    private void setChatBackGround(int chagBg) {
        mChatContentView.setBackground(ChatHelper.getChatBackGround(chagBg));
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
                .checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO);
            if (CamaraPermisson != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO}
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
        scrollChat(ESCrollType.BOTTOM);

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
        mAdapter.setViewFactory(new FactoryChatCell(this, mAdapter, this));

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
        List<IChatRoomModel> messages = ProviderChat
            .selectMsgAsPage(ContextHelper.getContext(), userId, page, 20,
                ChatHelper.isGroupChat(userBean.getChatType()));
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
        mInputMenu
            .registerExtendMenuItem(ContextHelper.getString(R.string.take_photo),
                R.drawable.chat_photo,
                2, (itemId, view) -> {
                    ChatEnvironment.getInstance().getPermissionExecutor()
                        .checkPermission(ChatActivity.this,
                            EPermission.CAMERA, (permission, isGranted, withAsk) -> {
                                if (permission == EPermission.CAMERA && isGranted) {
                                    CameraActivity.start(ChatActivity.this, REQUEST_VIDEO);
                                }
                            });
                });

        mInputMenu
            .registerExtendMenuItem(ContextHelper.getString(R.string.shake), R.drawable.chat_jitter,
                3, (itemId, view) -> {
                    if (System.currentTimeMillis() - shakeDate < 2 * 60 * 1000) {
                        showToast("间隔过短");
                    } else {
                        sendMessage("发送一条抖动消息", EMessageType.TEXT);
                        shakeDate = System.currentTimeMillis();

                    }
                });

        mInputMenu.registerExtendMenuItem(ContextHelper.getString(R.string.calling_card),
            R.drawable.share_card, 4, (itemId, view) -> {
                Intent intent = TransforMsgActivity
                    .newBusinessCardIntent(ChatActivity.this, userId,
                        ChatHelper
                            .getUserRemarkName(userBean.getRemarkName(), userBean.getUserNick(),
                                userBean.getUserId()), userBean.getChatType());
                startActivity(intent);
            });
        mInputMenu.registerExtendMenuItem(ContextHelper.getString(R.string.pop_menu_collect),
            R.drawable.collection, 5, (itemId, view) -> {
                Intent intent = ActivitysRouter.getInstance().invoke(ChatActivity.this,
                    ActivityPath.COLLECTION_ACTIVITY_PATH);
                intent.putExtra("activity", ChatActivity.class.getSimpleName());
                intent.putExtra("USERID", userId);
                startActivityForResult(intent, AppConfig.REQUEST_COLLECTION);
            });

        mInputMenu
            .registerExtendMenuItem(ContextHelper.getString(R.string.position), R.drawable.position,
                6, (itemId, view) -> ChatEnvironment.getInstance()
                    .getPermissionExecutor()
                    .checkPermission(ChatActivity.this,
                        EPermission.CAMERA, (permission, isGranted, withAsk) -> {
                            if (permission == EPermission.CAMERA && isGranted) {
                                MapPickerActivity
                                    .openActivity(ChatActivity.this, MAP_FOR_CHAT, null);
                            }
                        }));

        if (userBean.getChatType() == EChatType.GROUP.ordinal()) {
            mInputMenu.registerExtendMenuItem("投票", R.drawable.vote, 7,
                (itemId, view) -> toVote());
        }

    }

    //投票
    private void toVote() {
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
                    urls.add(bean.path);
                    EUploadFileType type = ContextHelper.configFileType(bean.path);
                    if (type != null && getMessageType(type) != null) {
                        ImageUploadEntity entity = ImageUploadEntity
                            .createEntity(bean.path, bean.size, "", "");
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
                    body.setSecret(isSecret);
                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                        body.setMucNickName(getUserNick());
                        body.setSenderAvatar(getUserAvatar());
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
                    body.setSecret(isSecret);
                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                        body.setMucNickName(getUserNick());
                        body.setSenderAvatar(getUserAvatar());
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
                    mInputMenu.setEmojicon(text + userBean.getRemarkName() + " ");
                }
            }
        } else if (requestCode == AppConfig.REQUEST_CHANGE_CONFIG) {//更新聊天配置信息
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    int chatBg = data.getIntExtra("chat_bg", 0);
                    userBean.setBgId(chatBg);
                    updateChatBackGround();
                } else {//刷新数据
                    loadChat(0);
                    scrollChat(ESCrollType.BOTTOM);
                }
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
        }
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
                                                    getUserNick());
                                        } else {
                                            bodyEntity = MessageManager.getInstance()
                                                .createBody(ImageUploadEntity.toJson(entity),
                                                    isSecret,
                                                    message.getMsgType());
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
                                            message.getMsgType(), getUserAvatar(), getUserNick());
                                } else {
                                    bodyEntity = MessageManager.getInstance()
                                        .createBody(ImageUploadEntity.toJson(entity), isSecret,
                                            message.getMsgType());
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
                                                getUserNick());
                                    } else {
                                        bodyEntity = MessageManager.getInstance()
                                            .createBody(VoiceUploadEntity.toJson(entity), isSecret,
                                                message.getMsgType());
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
                                                getUserNick());
                                    } else {
                                        bodyEntity = MessageManager.getInstance()
                                            .createBody(VideoUploadEntity.toJson(entity), isSecret,
                                                message.getMsgType());
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
                                            message.getMsgType(), getUserAvatar(), getUserNick());
                                } else {
                                    bodyEntity = MessageManager.getInstance()
                                        .createBody(ImageUploadEntity.toJson(entity), isSecret,
                                            message.getMsgType());
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
        } else {
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(userId) && !TextUtils
                .isEmpty(getUserId())) {
                if (type == EMessageType.MAP || type == EMessageType.VOTE) {
                    message.setContent(content);
                } else {
                    BodyEntity entity;
                    if (ChatHelper.isGroupChat(userBean.getChatType())) {
                        entity = MessageManager.getInstance()
                            .createBody(content, isSecret, type, getUserAvatar(), getUserNick());
                    } else {
                        entity = MessageManager.getInstance()
                            .createBody(content, isSecret, type);
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
                message.setNick(getUserNick());
                if (userBean.getChatType() == EChatType.PRIVATE.ordinal()) {
                    message.setGroupChat(false);
                } else {
                    message.setGroupChat(true);
                }
            }
        }
        return message;
    }

    //发送atAll消息

    private void sendMessage(String content, EMessageType type, boolean isAtAll) {
        if (isAtAll) {
            String msgId = UUID.randomUUID().toString();
            IChatRoomModel message = null;
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(userId) && !TextUtils
                .isEmpty(getUserId())) {
                BodyEntity entity = MessageManager.getInstance()
                    .createBody(content, isSecret, type);
                RoomMessage.Builder builder = RoomMessage.newBuilder();
                builder.setId(msgId);
                builder.setMucid(userId);
                builder.setUsername(getUserId());
                builder.setTime(System.currentTimeMillis());
                builder.setContent(BodyEntity.toJson(entity));
                builder.setType(ChatHelper.getMessageType(type));
                builder.setAtAll(ChatEnum.ESureType.YES.value);
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
        }
        return bean;
    }

    private void saveMessage(IChatRoomModel message) {
        ProviderChat.insertPrivateMessage(ContextHelper.getContext(), message);
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
//                            addMessage(message);
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
                                .getSubject(), ChatEnum.EMessageType.TEXT, true);
                    }
                }
            }
        }

    }

    private void addMessage(IChatRoomModel message) {
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
                }
            }, 200);
        }
    }


    private void scrollChat(ESCrollType type) {
        switch (type) {
            case CURRENT:
                break;
            case BOTTOM:
                if (mListView.getLastVisiblePosition() > 0
                    && mListView.getLastVisiblePosition() >= mAdapter.getTotalItemsCount() - 2) {
                    return;
                }
                chatMessageList.scrollDown();
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

    public void resetExpression(String data) {
        //mInputMenu.removeEmojiconGroup(3);
        emojiconGroupList.remove(3);
        emojiconGroupList.add(EmojiconDefaultGroupData.getCustomData(data));
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProviderChat.markReaded(this, userId, true);
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
        ClientConfig.I.removeListener(AckListener.class, this);
        stopInpputTimer();
    }

    @Override
    public void onAck(AckMessage message) {
        if (message != null) {
            //成功逻辑
            String msgId = message.ack.getIdList().get(0);
            long time = message.ack.getTime();
            doSendSuccess(msgId, time);
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
                            bean.getPosition());
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
                ChatHelper.destroySecretMsg(model);
                MessageManager.getInstance()
                    .destryMessageContent(ContextHelper.getContext(), model.getMsgId(),
                        model.getBody());
                startActivity(intentSecret);
                mAdapter.notifyDataSetChanged();
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
                        resetExpression(data);
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
                        intent = FriendDetailActivity.createNormalIntent(this, model.getTo());
                    } else {
                        intent = FriendDetailActivity.createNormalIntent(this, model.getFrom());
                    }
                }
                startActivity(intent);
                break;
        }

    }

    private void collectMessage(IChatRoomModel message) {
        if (!CollectionManager.getInstance().checkDuplicateByID(message.getMsgId())) {
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
                store.setFavContent(message.getContent());
                store.setFavUrl(message.getContent());
            } else {
                if (message.getMsgType() == EMessageType.IMAGE
                    || message.getMsgType() == EMessageType.VIDEO
                    || message.getMsgType() == EMessageType.VOICE) {
                    store.setFavContent(message.getUploadUrl());
                } else {
                    if (message.getMsgType() == EMessageType.TEXT) {
                        store.setFavContent(message.getContent());
                    } else {
                        store.setFavContent(message.getBody());
                    }
                }
                store.setFavUrl(message.getUploadUrl());
                store.setProviderJid(message.getFrom());
                store.setProviderNick(message.getNick());
                store.setFavCreaterAvatar(getUserAvatar());
                store.setProviderNick(getUserNick());

            }
            store.setFavMsgId(message.getMsgId());
            store.setFavProvider(getUserId());
            store.setFavType(ChatHelper.getFavType(message.getMsgType()));
            store.setFavCreater(UserInfoRepository.getUserName());
            store.setFavTime(TimeUtils.getDate());
            store.setFavDes("");
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
        } else {
            if (AuthorityManager.getInstance().copyInside()) {
                AuthorityManager.getInstance().copy(model.getContent());
            } else {
                T.show("请申请权限");
            }
        }
    }

    private void showTransforSuccess(boolean var) {
        tv_success.setVisibility(var ? View.VISIBLE : View.GONE);
    }
}

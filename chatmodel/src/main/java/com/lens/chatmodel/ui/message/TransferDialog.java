package com.lens.chatmodel.ui.message;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.fingerchat.proto.message.BaseChat.MessageType;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ETransforModel;
import com.lens.chatmodel.ChatEnum.ETransforType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.interf.IEventClickListener;
import com.lens.chatmodel.ui.contacts.OnlyAvatarAdapter;
import com.lens.chatmodel.utils.SmileUtils;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import com.lensim.fingerchat.components.dialog.BaseDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY309313 on 2017/2/9.
 */

public class TransferDialog extends BaseDialog {


    private Context mContext;
    private TextView mTransforName;
    private TextView mTransforContent;
    private EditText mTransforMessage;//留言
    private TextView mTransforCancel;
    private TextView mTransforConfirm;

    private List<UserBean> users;
    private List<IChatRoomModel> messageModels;

    private int carbonType;
    private int carbonMode;
    private RecyclerView mTransforMultiUser;
    private LinearLayout mTransforSingleUser;
    private ImageView mTransforImg;
    private String user;
    private String content;
    private int messageType;


    private IEventClickListener listener;
    private AvatarImageView iv_avatar;
    private boolean isGroupChat = false;
    private ImageView iv_arrow;
    private RelativeLayout rlContent;
    private RecyclerView recyclerView;
    boolean isViewMember = false;
    private LinearLayout ll_member;

    public TransferDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    public TransferDialog(Context context, List<UserBean> users, IEventClickListener l) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.users = users;
        listener = l;
    }


    @Override
    public void initView() {
        setContentView(R.layout.dialog_transfer);
        iv_avatar = findViewById(R.id.iv_avatar);
        mTransforName = findViewById(R.id.mTransforName);
        mTransforContent = findViewById(R.id.mTransforContent);
        mTransforMessage = findViewById(R.id.mTransforMessage);
        mTransforCancel = findViewById(R.id.mTransforCancel);
        mTransforConfirm = findViewById(R.id.mTransforConfirm);
        mTransforMultiUser = findViewById(R.id.mTransforMultiUser);
        mTransforSingleUser = findViewById(R.id.mTransforSingleUser);
        mTransforImg = findViewById(R.id.mTransforImg);
        iv_arrow = findViewById(R.id.iv_arrow);
        rlContent = findViewById(R.id.rl_content);
        recyclerView = findViewById(R.id.recyclerView);
        ll_member = findViewById(R.id.ll_member);

        mTransforConfirm.setText("发送");

    }

    //检查选中人是否是群聊
    private void checkIsGroup() {
        if (!TextUtils.isEmpty(user)) {
            IChatUser bean = ProviderUser.selectRosterSingle(ContextHelper.getContext(), user);
            if (bean == null) {
                isGroupChat = true;
            } else {
                isGroupChat = false;
            }
        }
    }

    private void animArrow(boolean isViewMember) {
        ObjectAnimator animator;
        if (isViewMember) {
            animator = ObjectAnimator.ofFloat(iv_arrow, "rotation", 0f, 180f);
        } else {
            animator = ObjectAnimator.ofFloat(iv_arrow, "rotation", 180f, 0f);
        }
        animator.setDuration(500);
        animator.start();
    }

    private void showContent(boolean b) {
        if (b) {
            rlContent.setVisibility(View.VISIBLE);
            mTransforMessage.setVisibility(View.VISIBLE);
            ll_member.setVisibility(View.GONE);
        } else {
            rlContent.setVisibility(View.GONE);
            mTransforMessage.setVisibility(View.GONE);
            ll_member.setVisibility(View.VISIBLE);
            updateMemberViewHeight();
        }
        animArrow(isViewMember);
    }

    //设置member展示高度
    private void updateMemberViewHeight() {
        int screenHeight = (int) TDevice.getScreenHeight();
        ll_member.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int listHeight = ll_member.getMeasuredHeight();
        if (listHeight > screenHeight / 2) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = screenHeight / 2;
            ll_member.setLayoutParams(params);
        }
    }

    private void initAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(ContextHelper.getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        UserBean bean = users.get(0);
        List<MucMemberItem> memberItems = MucInfo
            .selectMucMemberItem(ContextHelper.getContext(), bean.getUserId());
        MemberInfoAdapter adapter = new MemberInfoAdapter(ContextHelper.getContext(), memberItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initdata() {
        checkIsGroup();
        if (isGroupChat) {
            initAdapter();
            iv_arrow.setVisibility(View.VISIBLE);
            initAdapter();
            showContent(true);
            iv_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isViewMember = !isViewMember;
                    showContent(!isViewMember);
                }
            });
        } else {
            iv_arrow.setVisibility(View.GONE);
        }

        if (carbonType == ETransforType.CARD_MSG.ordinal()) {
            if (isGroupChat) {
                mTransforName.setText(MucInfo.getMucName(ContextHelper.getContext(), user));
                iv_avatar.setDrawText(
                    MucInfo.selectMucUserNick(ContextHelper.getContext(), user));
            } else {
                IChatUser bean = ProviderUser.selectRosterSingle(ContextHelper.getContext(), user);
                if (bean != null) {
                    iv_avatar.setChatType(true);
                    mTransforName.setText(bean.getUserNick());
                    ImageHelper.loadAvatarPrivate(bean.getAvatarUrl(), iv_avatar);
                }
            }
        } else {
            if (users.size() > 1) {
                mTransforSingleUser.setVisibility(View.GONE);
                mTransforMultiUser.setVisibility(View.VISIBLE);
                mTransforMultiUser.setHasFixedSize(true);
                GridLayoutManager manager = new GridLayoutManager(mContext, 5);
                manager.offsetChildrenVertical(20);
                mTransforMultiUser.setLayoutManager(manager);
                OnlyAvatarAdapter adapter = new OnlyAvatarAdapter(mContext, users);
                mTransforMultiUser.setAdapter(adapter);
            } else {
                mTransforMultiUser.setVisibility(View.GONE);
                mTransforSingleUser.setVisibility(View.VISIBLE);
                UserBean user = users.get(0);
                if (ChatHelper.isGroupChat(user.getChatType())) {
                    mTransforName.setText(user.getMucName());
                    iv_avatar.setDrawText(
                        MucInfo.selectMucUserNick(ContextHelper.getContext(), user.getUserId()));
                } else {
                    mTransforName.setText(user.getUserNick());
                    iv_avatar.setChatType(true);
                    ImageHelper.loadAvatarPrivate(user.getAvatarUrl(), iv_avatar);
                }
            }
        }

        if (carbonType == ETransforType.CARD_MSG.ordinal()) {
            bindCardData();
        } else if (carbonType != ETransforType.PURE_MSG.ordinal()) {
            bindMultiData();
        } else {
            bindData();
        }

    }

    private void bindCardData() {
        String des;
        if (users.size() == 1) {
            IChatUser roster = users.get(0);
            des = getContext().getString(R.string.personal_card,
                TextUtils.isEmpty(roster.getUserNick()) ? roster.getUserId()
                    : roster.getUserNick());
        } else {
            des = getContext().getString(R.string.personal_card, "选中多人");
        }

        mTransforContent.setText(des);
    }

    private void bindData() {
        EMessageType type = EMessageType.fromInt(messageType);
        BodyEntity entity = new BodyEntity(content);
        switch (type) {
            case TEXT:
                mTransforContent.setText(SmileUtils.getSmiledText(mContext, content, (int) TDevice
                    .dpToPixel(12 + 10)));
                break;
            case IMAGE:
                ImageUploadEntity image;
                if (content.contains("body")) {
                    image = ImageUploadEntity.fromJson(entity.getBody());
                } else {
                    image = ImageUploadEntity.fromJson(content);
                }
                if (image == null) {
                    return;
                }
                mTransforContent.setVisibility(View.GONE);
                mTransforImg.setVisibility(View.VISIBLE);
                ImageHelper.loadImage(image.getOriginalUrl(), mTransforImg);
                break;
            case FACE:
                mTransforContent.setVisibility(View.GONE);
                mTransforImg.setVisibility(View.VISIBLE);
                ImageUploadEntity face = ImageUploadEntity.fromJson(entity.getBody());
                if (face == null) {
                    return;
                }
                if (ContextHelper.isGif(face.getOriginalUrl())) {
                    ImageHelper.loadGif(face.getOriginalUrl(), mTransforImg);
                } else {
                    ImageHelper.loadImage(face.getOriginalUrl(), mTransforImg);
                }
            case VIDEO:
            case VOICE:
            case CONTACT:
            case MAP:
            case VOTE:
            case NOTICE:
            case CARD:
            case MULTIPLE:
                mTransforContent.setText(ChatHelper.getHint(type, content, false));
                break;
        }
    }

    private void bindMultiData() {
        if (messageModels != null && !messageModels.isEmpty()) {
            if (messageModels.size() == 1) {
                IChatRoomModel model = messageModels.get(0);
                EMessageType type = model.getMsgType();
                switch (type) {
                    case TEXT:
                        mTransforContent.setText(model.getContent());
                        break;
                    case IMAGE:
                        mTransforContent.setVisibility(View.GONE);
                        mTransforImg.setVisibility(View.VISIBLE);
                        ImageUploadEntity image = ImageUploadEntity.fromJson(model.getContent());
                        if (image != null) {
                            ImageHelper.loadImage(image.getOriginalUrl(), mTransforImg);
                        }
                        break;
                    case FACE:
                        mTransforContent.setVisibility(View.GONE);
                        mTransforImg.setVisibility(View.VISIBLE);
                        ImageUploadEntity face = ImageUploadEntity.fromJson(model.getContent());
                        if (face == null) {
                            return;
                        }
                        if (ContextHelper.isGif(face.getOriginalUrl())) {
                            ImageHelper.loadGif(face.getOriginalUrl(), mTransforImg);
                        } else {
                            ImageHelper.loadImage(face.getOriginalUrl(), mTransforImg);
                        }
                    case VIDEO:
                    case VOICE:
                    case CONTACT:
                    case MAP:
                    case VOTE:
                    case NOTICE:
                    case CARD:
                    case MULTIPLE:

                        mTransforContent
                            .setText(ChatHelper.getHint(type, model.getContent(), false));
                        break;
                }
            } else {
                if (carbonMode == ETransforModel.MODE_ONE_BY_ONE.ordinal()) {
                    mTransforContent.setText("[逐条转发]" + messageModels.size() + "条消息");
                } else {
                    String name;
                    if (isGroupChat) {
                        name = MucInfo.getMucName(ContextHelper.getContext(), user);
                    } else {
                        name = ProviderUser.getRosterNick(ContextHelper.getContext(), user);
                    }
                    mTransforContent.setText("[合并转发]" + name + "的聊天记录");
                }
            }
        }
    }

    @Override
    public void initEvent() {
        mTransforCancel.setOnClickListener(this);
        mTransforConfirm.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int i = view.getId();
        if (i == R.id.mTransforCancel) {
            users.clear();
            ETransforType type = ETransforType.fromInt(carbonType);
            listener.onEvent(type.ordinal(), users);
            dismiss();
        } else if (i == R.id.mTransforConfirm) {
            ETransforType type = ETransforType.fromInt(carbonType);
            listener.onEvent(type.ordinal(), users);
            dismiss();

        }
    }

    public int getCarbonType() {
        return carbonType;
    }

    public void setCarbonType(int carbonType) {
        this.carbonType = carbonType;
    }

    public List<IChatRoomModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(List<IChatRoomModel> messages) {
        this.messageModels = messages;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getCarbonMode() {
        return carbonMode;
    }

    public void setCarbonMode(int carbonMode) {
        this.carbonMode = carbonMode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    /*
    * 获取留言信息
    * */
    public String getEditMessage() {
        if (mTransforMessage != null && mTransforMessage.getText() != null) {
            return mTransforMessage.getText().toString();
        }
        return "";
    }

    public void clearEditMessage() {
        mTransforMessage.setText("");
    }

}

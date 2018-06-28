package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fingerchat.proto.message.Resp.Message;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.view.CustomContextMenu;
import com.lens.chatmodel.view.CustomContextMenu.OnMenuListener;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.Date;
import java.util.List;

/**
 * Created by LL130386 on 2018/1/3.
 */

public abstract class ChatCellBase implements View.OnLongClickListener {

    private final Context mContext;
    public IChatRoomModel mChatRoomModel;
    private final EChatCellLayout mCellLayout;
    public final IChatEventListener mEventListener;
    private final View viewControl;

    public TextView tv_time;
    public TextView tv_notify;
    public RelativeLayout rl_root;
    public LinearLayout ll_root;
    public ImageView iv_avatar;
    public View bubbleLayout;
    public LinearLayout ll_load;
    public ProgressBar progressBar;
    public TextView tv_percetage;
    public ImageView iv_status;
    public ImageView iv_secret;
    public TextView tv_name;
    public FrameLayout fl_check;
    public ImageView iv_check;

    public OnMenuListener mMenuListener;
    private CustomContextMenu mCustomContextMenu;
    public final MessageAdapter mAdapter;
    private boolean isProgressShow;
    private boolean isErrorShow;
    private final int currentPosition;
    private int unreadPostion;
    private TextView tv_new;


    protected ChatCellBase(Context context, EChatCellLayout cellLayout,
        IChatEventListener listener, MessageAdapter adapter, int postion) {
        super();
        mContext = context;
        mCellLayout = cellLayout;
        mEventListener = listener;
        viewControl = LayoutInflater.from(context).inflate(mCellLayout.LayoutId, null);
        mAdapter = adapter;
        currentPosition = postion;
        initView();
        initListener();
    }

    public void initView() {
        if (getView() == null) {
            return;
        }
        tv_time = getView().findViewById(R.id.tv_time);
        tv_notify = getView().findViewById(R.id.tv_notify);
        rl_root = getView().findViewById(R.id.rl_root);
        ll_root = getView().findViewById(R.id.ll_root);
        iv_avatar = getView().findViewById(R.id.iv_avatar);
        bubbleLayout = getView().findViewById(R.id.bubble);
        ll_load = getView().findViewById(R.id.ll_loading);
        progressBar = getView().findViewById(R.id.progress_bar);
        tv_percetage = getView().findViewById(R.id.tv_percentage);
        iv_status = getView().findViewById(R.id.iv_status);
        iv_secret = getView().findViewById(R.id.iv_secret);
        tv_name = getView().findViewById(R.id.tv_name);
        fl_check = getView().findViewById(R.id.fl_check);
        iv_check = getView().findViewById(R.id.iv_checkbox);
        tv_new = getView().findViewById(R.id.tv_new);
    }

    private void initListener() {
        if (fl_check != null) {
            fl_check.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mChatRoomModel != null) {
                        List<String> selectedChats = ((MessageAdapter) mAdapter)
                            .getSelectedIds();
                        if (selectedChats != null) {
                            if (selectedChats.contains(mChatRoomModel.getMsgId())) {
                                selectedChats.remove(mChatRoomModel.getMsgId());
                            } else {
                                selectedChats.add(mChatRoomModel.getMsgId());
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        if (bubbleLayout != null) {
            bubbleLayout.setOnLongClickListener(this);

            bubbleLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBubbleClick();
                }
            });
        }

        if (iv_status != null) {
            iv_status.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mEventListener != null && mChatRoomModel != null) {
                        mEventListener.onEvent(ECellEventType.RESEND_EVENT, mChatRoomModel, null);
                        showProgress(true);
                    }
                }
            });
        }

        if (iv_avatar != null) {
            iv_avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEventListener != null && mChatRoomModel != null) {
                        mEventListener.onEvent(ECellEventType.AVATAR, mChatRoomModel, null);
                    }
                }
            });
        }


    }

    public void setModel(IChatRoomModel model) {
        mChatRoomModel = model;
        showData();
    }


    public View getView() {
        return viewControl;
    }


    @Override
    public boolean onLongClick(View v) {
        if (mChatRoomModel.getMsgType() != EMessageType.VOTE) {
            showMenu(this, mChatRoomModel.isIncoming(), mMenuListener);
        }
        return true;
    }


    public void showData() {
        setName();
        if (ChatHelper.isSystemUser(mChatRoomModel.getTo())) {
            ImageHelper.loadDrawableImage(R.drawable.ic_contact_server, iv_avatar);
        } else {
            ImageHelper.loadAvatarPrivate(mChatRoomModel.getAvatarUrl(), iv_avatar);
        }
        updateSendState();
        setCheckView();
        setTime();
        showNew();

    }

    private void showNew() {
        if (tv_new != null) {
            if (unreadPostion > 0 && currentPosition == unreadPostion) {
                tv_new.setVisibility(View.VISIBLE);
                tv_new.setText(ContextHelper.getString(R.string.downstairs_new_message));
            } else {
                tv_new.setVisibility(View.GONE);
            }
        }
    }

    private void setTime() {
        if (tv_time != null) {
            // 两条消息时间离得如果稍长，显示时间
            if (currentPosition == 0) {
                tv_time.setText(
                    StringUtils.friendly_time4(new Date(mChatRoomModel.getTime())));
                tv_time.setVisibility(View.VISIBLE);
            } else {
                IChatRoomModel prevMessage = mAdapter.getItem(currentPosition - 1);
                if (prevMessage != null && (mChatRoomModel.getTime() - prevMessage.getTime()
                    < 60 * 1000 * 5)) {
                    tv_time.setVisibility(View.GONE);
                } else {
                    tv_time.setText(
                        StringUtils.friendly_time4(new Date(mChatRoomModel.getTime())));
                    tv_time.setVisibility(View.VISIBLE);
                }
            }

        }

    }

    private void setCheckView() {
        if (mAdapter instanceof MessageAdapter) {

            if (fl_check != null) {
                if (mChatRoomModel.getMsgType() == EMessageType.MULTIPLE
                    || mChatRoomModel.getMsgType() == EMessageType.VOTE) {
                    if (mAdapter.isShowCheckBox()) {
                        fl_check.setVisibility(View.INVISIBLE);
                    } else {
                        fl_check.setVisibility(View.GONE);
                    }
                    return;
                }
                if (mAdapter.isShowCheckBox()) {
                    fl_check.setVisibility(View.VISIBLE);
                    List<String> selectedChats = mAdapter.getSelectedIds();
                    if (selectedChats.contains(mChatRoomModel.getMsgId())) {
                        iv_check.setImageResource(R.drawable.click_check_box);
                    } else {
                        iv_check.setImageResource(R.drawable.check_box);

                    }
                } else {
                    fl_check.setVisibility(View.GONE);
                }
            }
        }
    }

    public void updateSendState() {
        if (!mChatRoomModel.isIncoming()) {
            System.out.println("更新发送状态：" + mChatRoomModel.getSendType().value);
            if (mChatRoomModel.getSendType() == ESendType.SENDING) {
                showProgress(true);
                if (iv_status != null) {
                    iv_status.setVisibility(View.GONE);
                }
                setErrorShow(false);
                updateProgress(mChatRoomModel.getUploadProgress());
            } else if (mChatRoomModel.getSendType() == ESendType.MSG_SUCCESS) {
                showProgress(false);
                if (iv_status != null) {
                    iv_status.setVisibility(View.GONE);

                }
                setErrorShow(false);
            } else if (mChatRoomModel.getSendType() == ESendType.FILE_SUCCESS) {
                showProgress(false);
                if (iv_status != null) {
                    iv_status.setVisibility(View.VISIBLE);
                }
                setErrorShow(true);
            } else if (mChatRoomModel.getSendType() == ESendType.ERROR) {
                showProgress(false);
                if (iv_status != null) {
                    iv_status.setVisibility(View.VISIBLE);
                    iv_status.requestFocus();
                }
                setErrorShow(true);
            }
        } else {
            showProgress(false);
        }
    }

    public void setSecretShow(boolean isShow, View view) {
        if (iv_secret == null) {
            return;
        }
        if (!isErrorShowing() && !isProgressShowing() && isShow /*&& view != null*/) {
//            view.setBackground(ContextHelper.getDrawable(R.drawable.ic_secret_bubble_send));
            iv_secret.setVisibility(View.VISIBLE);
        } else {
            iv_secret.setVisibility(View.GONE);

        }
    }


    public void updateProgress(int progress) {
        if (tv_percetage == null) {
            return;
        }
        if (progress < 100) {
            showProgress(true);
            tv_percetage.setText(progress + "%");
        } else {
            showProgress(false);
        }
    }

    public void showProgress(boolean flag) {
        if (flag) {
            if (ll_load != null) {
                ll_load.setVisibility(View.VISIBLE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (tv_percetage != null) {
                tv_percetage.setVisibility(View.VISIBLE);
            }
            setProgressShow(true);
        } else {
            if (ll_load != null) {
                ll_load.setVisibility(View.GONE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (tv_percetage != null) {
                tv_percetage.setVisibility(View.GONE);
            }
            setProgressShow(false);
        }
    }

    public boolean isProgressShowing() {
        return isProgressShow;
    }

    public void setProgressShow(boolean b) {
        isProgressShow = b;
    }

    public boolean isErrorShowing() {
        return isErrorShow;
    }

    public void setErrorShow(boolean b) {
        isErrorShow = b;
    }

    public void isSecret(boolean flag) {
        if (iv_secret != null) {
            iv_secret.setVisibility(flag ? View.VISIBLE : View.GONE);
        }
    }

    public void setName() {
        if (tv_name == null) {
            return;
        }
        if (mChatRoomModel.isIncoming()) {
            if (mChatRoomModel.isGroupChat()) {
                tv_name
                    .setText(!TextUtils.isEmpty(mChatRoomModel.getNick()) ? mChatRoomModel.getNick()
                        : mChatRoomModel.getFrom());
            } else {
                tv_name
                    .setText(!TextUtils.isEmpty(mChatRoomModel.getNick()) ? mChatRoomModel.getNick()
                        : mChatRoomModel.getTo());
            }
        } else {
            if (mChatRoomModel.isGroupChat()) {
                tv_name
                    .setText(!TextUtils.isEmpty(mChatRoomModel.getNick()) ? mChatRoomModel.getNick()
                        : mChatRoomModel.getFrom());
            } else {
                tv_name
                    .setText(!TextUtils.isEmpty(mChatRoomModel.getNick()) ? mChatRoomModel.getNick()
                        : mChatRoomModel.getFrom());
            }

        }

    }

    public void showMenu(ChatCellBase v, boolean isIncoming,
        OnMenuListener listener) {
        if (mMenuListener == null || getCustomContextMenu() == null) {
            return;
        }
        // 1是 incoming
        boolean copy = false;
        boolean transmit = true;
        boolean collection = true;
        boolean cancle = true;
        boolean addex = false;
        boolean more = true;
        if (isIncoming) {
            cancle = false;
        }
        if (v instanceof ChatCellText) {
            copy = true;
        } else if (v instanceof ChatCellImage) {
            addex = true;
        } else if (v instanceof ChatCellVideo) {

        } else if (v instanceof ChatCellVoice) {

        } else if (v instanceof ChatCellMulti) {
            transmit = false;
            collection = false;
        } else if (v instanceof ChatCellEmoticon) {
            addex = true;
        } else if (v instanceof ChatCellBusinessCard) {
            collection = false;
        } else if (v instanceof ChatCellMap) {
            collection = false;
        }

        getCustomContextMenu().setCanCopy(copy).setCanTransmit(transmit).setCanCancle(cancle)
            .setCanCollection(collection).setCanMore(more).setCanAddEx(addex)
            .bindData(mChatRoomModel)
            .show(v.getView(), listener);

    }


    public void showCheckBox(boolean flag) {
        if (flag) {
            fl_check.setVisibility(View.VISIBLE);
        } else {
            fl_check.setVisibility(View.GONE);
        }
    }

    public void onBubbleClick() {

    }


    public void setCustomContextMenu(CustomContextMenu view) {
        mCustomContextMenu = view;
    }

    public CustomContextMenu getCustomContextMenu() {
        return mCustomContextMenu;
    }


    public void setMenuListener(OnMenuListener listener) {
        mMenuListener = listener;
    }

    public void setFirstUnreadPosition(int position) {
        unreadPostion = position;
        System.out.println("unreadPostion = " + unreadPostion);
    }

}

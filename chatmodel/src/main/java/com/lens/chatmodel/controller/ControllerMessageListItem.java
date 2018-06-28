package com.lens.chatmodel.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.helper.MsgTagHandler;
import com.lens.chatmodel.interf.IChatItemClickListener;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import java.sql.Date;

/**
 * Created by LL130386 on 2017/11/25.
 */

public class ControllerMessageListItem extends ControllerBaseItem<RecentMessage> {

    private AvatarImageView iv_avatar;
    private TextView tv_unread;
    private TextView tv_name;
    private LinearLayout ll_identify;
    private TextView tv_time;
    private TextView tv_msg;
    private ImageView iv_identify;
    private TextView tv_identify;
    private IChatItemClickListener listener;
    private RecentMessage model;
    private String userId;
    private ImageView iv_disturb;
    private LinearLayout ll_root;
    private TextView tv_sender;

    public ControllerMessageListItem(Context context) {
        this(context, R.layout.item_message_list);
    }


    private ControllerMessageListItem(Context context, int viewId) {
        super(context, viewId);
    }

    @Override
    protected void loadControls() {
        super.loadControls();
        ll_root = getView().findViewById(R.id.ll_root);
        iv_avatar = getView().findViewById(R.id.iv_avatar);
        tv_unread = getView().findViewById(R.id.tv_unread);
        tv_name = getView().findViewById(R.id.tv_name);

        ll_identify = getView().findViewById(R.id.ll_identify);
        iv_identify = getView().findViewById(R.id.iv_identify);
        tv_identify = getView().findViewById(R.id.tv_identify);
        tv_time = getView().findViewById(R.id.tv_time);
        tv_msg = getView().findViewById(R.id.tv_msg);
        iv_disturb = getView().findViewById(R.id.iv_disturb);
        tv_sender = getView().findViewById(R.id.tv_sender);
    }

    @Override
    protected void initControls() {
        super.initControls();

        getView().setOnClickListener(view -> {
            if (listener != null && model != null) {
                listener.click(model);
            }
        });

        getView().setOnLongClickListener(view -> {
            if (listener != null && model != null) {
                listener.onLongClick(model);
            }
            return true;
        });
    }

    public void setOnClickListener(IChatItemClickListener l) {
        listener = l;
    }

    @RequiresApi(api = VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    public void showData(RecentMessage o) {
        super.showData(o);

        model = o;
        if (model != null) {
            int factor = SPHelper.getInt("font_size", 1) * 2;
            if (factor <= 2) {
                factor = 2;
            } else if (factor > 4) {
                factor = 4;
            }
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 12);
            tv_msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 10);
            tv_time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 10);

            if (model.getChatType() == EChatType.PRIVATE.ordinal()) {
                tv_sender.setVisibility(View.GONE);

                tv_name
                    .setText(
                        TextUtils.isEmpty(model.getNick()) ? model.getChatId() : model.getNick());
                if (ChatHelper.isSystemUser(model.getChatId())) {
                    ImageHelper.loadDrawableImage(R.drawable.ic_contact_server, iv_avatar);
                } else {
                    ImageHelper.loadAvatarPrivate(model.getAvatarUrl(), iv_avatar);
                }
                iv_avatar.setChatType(true);
            } else {
                if (model.getUserId().equals(userId)) {
                    tv_sender.setVisibility(View.GONE);
                } else {
                    if (model.getMsgType() != EMessageType.ACTION && !TextUtils
                        .isEmpty(model.getMsg())) {
                        tv_sender.setVisibility(View.VISIBLE);
                        tv_sender
                            .setText(
                                StringUtils.getUserNick(model.getNick(), model.getUserId()) + ":");
                    } else {
                        tv_sender.setVisibility(View.GONE);
                    }
                }

                tv_name
                    .setText(
                        TextUtils.isEmpty(model.getGroupName()) ? model.getChatId()
                            : model.getGroupName());
                iv_avatar
                    .setDrawText(MucInfo.selectMucUserNick(mContext, model.getChatId()));

            }
            String draft = "";
            if (!TextUtils.isEmpty(userId)) {
                draft = SPHelper.getString(userId + "&" + model.getChatId());
            }
            if (!TextUtils.isEmpty(draft)) {//有草稿
                Spannable smiledText = SmileUtils
                    .getSmiledText(mContext, "[草稿]", draft, TDevice.sp2px(12 + factor));
                tv_msg.setText(SpannableUtil.traftText(smiledText), BufferType.SPANNABLE);
            } else {
                ///为action的消息做解析
                if (model.getMsgType().value == ChatEnum.EMessageType.ACTION.value) {
                    tv_msg.setMovementMethod(LinkMovementMethod.getInstance());
                    tv_msg.setText(Html.fromHtml(model.getHint(), null,
                        new MsgTagHandler(ContextHelper.getContext(), false, "")));
                } else {
                    Spannable smiledText = SmileUtils
                        .getSmiledText(mContext, model.getHint(), TDevice.sp2px(12 + factor));
                    tv_msg.setText(smiledText, BufferType.SPANNABLE);
                }
            }
            ll_identify.setVisibility(View.INVISIBLE);

            tv_time.setText(StringUtils.friendly_time3(new Date(model.getTime())));

            if (model.getUnreadCount() > 0) {
                tv_unread.setVisibility(View.VISIBLE);
                tv_unread.setText(model.getUnreadCount() + "");
            } else {
                tv_unread.setVisibility(View.GONE);
            }
            iv_disturb.setVisibility(
                model.getNotDisturb() == ESureType.YES.ordinal() ? View.VISIBLE : View.GONE);
            ll_root.setBackgroundColor(
                model.getTopFlag() == ESureType.YES.ordinal() ? ContextHelper
                    .getColor(R.color.top_color) : ContextHelper.getColor(R.color.white));

        }
    }

    /*
    * 当前登录UserId
    * */
    public void setUserId(String value) {
        userId = value;
    }

}

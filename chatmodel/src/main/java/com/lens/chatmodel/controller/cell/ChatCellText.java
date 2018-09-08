package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Browser;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableClickable;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.List;
import java.util.regex.Matcher;


/**
 * Created by LL130386 on 2018/1/3.
 * 文字
 */

public class ChatCellText extends ChatCellBase {

    private TextView tv_msg;
    private String content;
    private SpannableStringBuilder builder;
    private Spannable span;
    private int textSize;
    private int color;
    private long lastClick;
    private SpannableString subjectSpanText;
    private TextView tv_read;

    protected ChatCellText(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int position) {
        super(cellLayout, listener, adapter, position);
        loadControls();
    }


    private void loadControls() {

        tv_msg = getView().findViewById(R.id.tv_msg);

        tv_read = getView().findViewById(R.id.tv_read);

        textSize = SPHelper.getInt("font_size", 1) * 2 + 14;

        tv_msg.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showMenu(ChatCellText.this, mChatRoomModel.isIncoming(), mMenuListener);
                return true;
            }
        });

    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            if (mChatRoomModel.isIncoming()) {
                color = SPHelper.getInt("font_receive_color", Color.BLACK);
            } else {
                color = SPHelper.getInt("font_send_color", Color.WHITE);
            }

            content = mChatRoomModel.getContent();
            initText(tv_msg, content);
            setSecretShow(mChatRoomModel.isSecret(), tv_msg);
            intReadedContent();
            tv_msg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final long time = System.currentTimeMillis();
                    if (time - lastClick < 500) {
                        if (mEventListener != null) {
                            mEventListener.onEvent(ECellEventType.TEXT_CLICK, mChatRoomModel, null);
                        }
                    }
                    lastClick = time;

                }
            });
        }
    }


    private void initText(TextView tv_msg, String content) {
        if (!TextUtils.isEmpty(content)) {
            Matcher matcher = StringUtils.URL1.matcher(content);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (matcher.find()) {
                String substring = content.substring(matcher.start(), matcher.end());
                builder.append(content.substring(0, matcher.start()));
                builder.append(setClickableSpan(substring));
                builder.append(content.substring(matcher.end()));
                span = SpannableUtil.getAtText(SmileUtils
                    .getSmiledText(ContextHelper.getContext(), builder,
                        (int) TDevice.dpToPixel(textSize + 10)));
            } else {
                span = SmileUtils
                    .getSmiledText(ContextHelper.getContext(), content,
                        (int) TDevice.dpToPixel(textSize + 10));
            }
            tv_msg.setText(span);
            tv_msg.setTextColor(color);
            tv_msg.setTextSize(textSize);
        }
    }

    public SpannableString setClickableSpan(String textStr) {
        subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable() {
                                    @Override
                                    public void onClick(View widget) {
                                        // TODO: 2016/8/14 此处应该是跳转至相册的操作
                                        L.i("ChatRowText:" + "setClickableSpan--onclick");
                                        Uri uri = Uri.parse(textStr);
                                        Context context = widget.getContext();
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                                        context.startActivity(intent);
                                    }
                                }, 0, subjectSpanText.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    private void intReadedContent() {
        if (mChatRoomModel.isIncoming()) {
            return;
        }
        int serverReaded = mChatRoomModel.getServerReaded();
        String content = "";
        tv_read.setVisibility(View.VISIBLE);
        if (serverReaded == 0) {//已读
            if (mChatRoomModel.isGroupChat()) {
                List<String> readedUsers = mChatRoomModel.getReadedUserList();
                int totalMemberCount = MucInfo.getMucMemberCount(mChatRoomModel.getTo());
                if (readedUsers != null) {
                    int size = readedUsers.size();
                    if (size > 0) {
                        if (size < totalMemberCount) {
                            content = size + "人已读";
                            tv_read.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mEventListener
                                        .onEvent(ECellEventType.READED, mChatRoomModel, null);

                                }
                            });
                            tv_read.setTextColor(ContextHelper.getColor(R.color.blue));
                        } else if (size == totalMemberCount) {
                            content = "全部已读";
                            tv_read.setTextColor(ContextHelper.getColor(R.color.divider));
                        }
                    }
                }
            } else {
                content = "已读";
                tv_read.setTextColor(ContextHelper.getColor(R.color.divider));

            }
        } else {//未读
            tv_read.setTextColor(ContextHelper.getColor(R.color.blue));
            content = "未读";
        }
        tv_read.setText(content);

    }
}

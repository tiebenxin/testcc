package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
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
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.regex.Matcher;


/**
 * Created by LL130386 on 2018/1/3.
 * 文字
 */

public class ChatCellText extends ChatCellBase {


    private TextView tv_msg;

    private String content;
    private SpannableStringBuilder builder;
    private final Context mContext;
    private Spannable span;
    private int textSize;
    private int color;
    private long lastClick;

    protected ChatCellText(Context context,
        EChatCellLayout cellLayout, IChatEventListener listener, MessageAdapter adapter,
        int position) {
        super(context, cellLayout, listener, adapter, position);
        mContext = context;

        loadControls();
    }


    private void loadControls() {

        tv_msg = getView().findViewById(R.id.tv_msg);

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
//            builder.append(setClickableSpan(substring));
                builder.append(content.substring(matcher.end()));
                span = SpannableUtil.getAtText(SmileUtils
                    .getSmiledText(mContext, builder, (int) TDevice.dpToPixel(textSize + 10)));
            } else {
                span = SmileUtils
                    .getSmiledText(mContext, content, (int) TDevice.dpToPixel(textSize + 10));
            }
            tv_msg.setText(span);
            tv_msg.setTextColor(color);
            tv_msg.setTextSize(textSize);
        }
    }

}

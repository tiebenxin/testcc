package com.lens.chatmodel.controller.cell;

import android.graphics.Color;
import android.text.Spannable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.body.PushBody;
import com.lens.chatmodel.bean.body.PushEntity;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by LL130386 on 2018/1/3.
 * 系统推送消息
 */

public class ChatCellOA extends ChatCellBase {

    private TextView tv_title;
    private TextView tv_content;
    private PushEntity entity;
    private PushBody body;
    private ImageView iv_icon;


    protected ChatCellOA(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int position) {
        super(cellLayout, listener, adapter, position);
        loadControls();
    }


    private void loadControls() {
        tv_title = getView().findViewById(R.id.tv_title);
        tv_content = getView().findViewById(R.id.tv_content);
        iv_icon = getView().findViewById(R.id.iv_oa_secre);
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            entity = GsonHelper.getObject(mChatRoomModel.getBody(), PushEntity.class);
            if (entity != null) {
                if (entity.getType().equalsIgnoreCase("OA")) {
                    body = entity.getBody();
                    int level = body.getLevel();
                    Spannable spannable = null;
                    switch (level) {
                        case 1:
                            spannable = SpannableUtil.generateFCSpannable("[紧急]", Color.RED);
                            break;
                        case 2:
                            spannable = SpannableUtil.generateFCSpannable("[急]", Color.RED);
                            break;
                        case 3:
                            spannable = SpannableUtil.generateFCSpannable("[一般]", Color.RED);
                            break;
                    }
                    if (spannable != null) {
                        tv_title.setText(spannable);
                        tv_title.append(body.getTitle());
                    } else {
                        tv_title.setText(body.getTitle());
                    }

                    tv_content.setText(body.getContent());
                    iv_icon.setVisibility(View.VISIBLE);
                } else {//非OA消息
                    String json = mChatRoomModel.getBody();
                    try {
                        JSONObject object = new JSONObject(json);
                        if (object != null) {
                            String content = object.optString("body");
                            String from = object.optString("from");
                            tv_title.setText(from + "消息");
                            tv_content.setText(content);
                            iv_icon.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onBubbleClick() {
        if (entity != null && entity.getType().equalsIgnoreCase("OA")) {
            if (mEventListener != null) {
                mEventListener.onEvent(ECellEventType.OA_CLICK, mChatRoomModel, body);
            }
        }
    }
}

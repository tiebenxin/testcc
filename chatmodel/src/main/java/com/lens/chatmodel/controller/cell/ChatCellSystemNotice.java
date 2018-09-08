package com.lens.chatmodel.controller.cell;

import android.text.TextUtils;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.interf.IChatEventListener;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by LL130386 on 2018/1/3.
 * 系统推送消息
 */

public class ChatCellSystemNotice extends ChatCellBase {

    private TextView tv_title;
    private TextView tv_content;


    protected ChatCellSystemNotice(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int position) {
        super(cellLayout, listener, adapter, position);
        loadControls();
    }


    private void loadControls() {
        tv_title = getView().findViewById(R.id.tv_title);
        tv_content = getView().findViewById(R.id.tv_content);
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            String json = mChatRoomModel.getBody();//系统push消息，可能是json，可能不是
            try {
                JSONObject object = new JSONObject(json);
                if (object != null) {
                    String sbody = object.optString("body");
                    if (!TextUtils.isEmpty(sbody) && sbody.contains("content")) {
                        JSONObject body = new JSONObject(sbody);
                        if (body != null) {
                            String content = body.optString("content");
                            String title = body.optString("title");
                            if (!TextUtils.isEmpty(title)) {
                                tv_title.setText(title);
                            } else {
                                tv_title.setText("系统消息");
                            }
                            tv_content.setText(content);
                        }
                    }
                }
            } catch (JSONException e) {
                tv_title.setText("系统消息");
                tv_content.setText(mChatRoomModel.getContent());
            }
        }
    }

    @Override
    public void onBubbleClick() {

    }

}

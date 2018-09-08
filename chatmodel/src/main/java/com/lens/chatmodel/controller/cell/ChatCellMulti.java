package com.lens.chatmodel.controller.cell;

import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.transfor.BaseTransforEntity;
import com.lens.chatmodel.bean.transfor.MultiMessageEntity;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import java.util.List;


/**
 * Created by LL130386 on 2018/1/3.
 * 合并转发消息
 */

public class ChatCellMulti extends ChatCellBase {


    private TextView tv_msg;
    private TextView tv_title;

    protected ChatCellMulti(EChatCellLayout cellLayout, IChatEventListener listener,
        MessageAdapter adapter, int position) {
        super(cellLayout, listener, adapter, position);
        loadControls();
    }


    private void loadControls() {
        tv_msg = getView().findViewById(R.id.tv_msg);
        tv_title = getView().findViewById(R.id.tv_title);
    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            MultiMessageEntity entity = new MultiMessageEntity(mChatRoomModel.getBody());
            if (entity != null) {
                tv_title.setText(entity.getTransitionTitle());
                List<BaseTransforEntity> beans = entity.getBody();
                tv_msg.setText(getContent(beans));
            }
        }
    }

    private String getContent(List<BaseTransforEntity> beans) {
        if (beans != null && !beans.isEmpty()) {
            StringBuffer stringBuffer = new StringBuffer();
            int len = beans.size();
            for (int i = 0; i < len; i++) {
                BaseTransforEntity bean = beans.get(i);
                if (bean != null) {
                    int value = bean.getMessageType();
                    EMessageType msgType = EMessageType.fromInt(value);
                    if (i > 0) {
                        stringBuffer.append("\n");
                    }
                    switch (msgType) {
                        case TEXT:
                            stringBuffer.append(ChatHelper
                                .getUserNick(bean.getSenderUserName(), bean.getSenderUserid()))
                                .append(":").append(bean.getBody());
                            break;
                        case VOICE:
                        case VIDEO:
                        case IMAGE:
                        case FACE:
                        case CONTACT:
                        case MAP:
                        case VOTE:
                        case NOTICE:
                        case CARD:
                        case MULTIPLE:
                            stringBuffer.append(ChatHelper
                                .getUserNick(bean.getSenderUserName(), bean.getSenderUserid()))
                                .append(":")
                                .append(ChatHelper.getHint(msgType, bean.getBody(), false));
                            break;
                        default:
                            stringBuffer.append(ChatHelper
                                .getUserNick(bean.getSenderUserName(), bean.getSenderUserid()))
                                .append(":").append(bean.getBody());
                            break;

                    }
                }
            }
            return stringBuffer.toString();
        }
        return "";
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        mEventListener.onEvent(ECellEventType.MULTI_CLICK, mChatRoomModel, null);

    }
}

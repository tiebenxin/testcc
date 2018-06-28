package com.lens.chatmodel.controller.cell;

import android.content.Context;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.interf.IChatEventListener;

/**
 * Created by LL130386 on 2018/1/3.
 * 聊天消息item工厂
 */

public class FactoryChatCell {

    private final Context mContext;
    private IChatEventListener mListener;
    MessageAdapter mAdapter;

    public FactoryChatCell(Context context, MessageAdapter adapter,
        IChatEventListener listener) {
        mContext = context;
        mListener = listener;
        mAdapter = adapter;
    }

    public void setListener(IChatEventListener listener) {
        mListener = listener;
    }

    public ChatCellBase createController(ChatEnum.EChatCellLayout cellLayout, int position) {

        ChatCellBase result = null;

        switch (cellLayout) {
            case TEXT_RECEIVED:
            case TEXT_SEND:
                result = new ChatCellText(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case IMAGE_RECEIVED:
            case IMAGE_SEND:
                result = new ChatCellImage(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case VOICE_RECEIVED:
            case VOICE_SEND:
                result = new ChatCellVoice(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case VIDEO_RECEIVED:
            case VIDEO_SEND:
                result = new ChatCellVideo(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case MAP_RECEIVED:
            case MAP_SEND:
                result = new ChatCellMap(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case VOTE_RECEIVED:
            case VOTE_SEND:
                result = new ChatCellVote(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case EMOTICON_RECEIVED:
            case EMOTICON_SEND:
                result = new ChatCellEmoticon(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case BUSINESS_CARD_RECEIVED:
            case BUSINESS_CARD_SEND:
                result = new ChatCellBusinessCard(mContext, cellLayout, mListener, mAdapter,
                    position);
                break;
            case WORK_LOGIN_RECEIVED:
            case WORK_LOGIN_SEND:
                result = new ChatCellWorkLogin(mContext, cellLayout, mListener, mAdapter,
                    position);
                break;
            case MULTI_RECEIVED:
            case MULTI_SEND:
                result = new ChatCellMulti(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case CHAT_ACTION:
                result = new ChatCellAction(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case SECRET:
                result = new ChatCellSecret(mContext, cellLayout, mListener, mAdapter, position);
                break;
//            case NOTICE:
//                result = new ChatCellOA(mContext, cellLayout, mListener, mAdapter, position);
//                break;
            case OA:
                result = new ChatCellOA(mContext, cellLayout, mListener, mAdapter, position);
                break;
            case SYSTEM:
                result = new ChatCellSystemNotice(mContext, cellLayout, mListener, mAdapter, position);
                break;
            default:
                result = new ChatCellText(mContext, cellLayout, mListener, mAdapter, position);
                break;
        }

        return result;
    }

}

package com.lens.chatmodel.controller.cell;

import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.interf.IChatEventListener;

/**
 * Created by LL130386 on 2018/1/3.
 * 聊天消息item工厂
 */

public class FactoryChatCell {

    private IChatEventListener mListener;
    MessageAdapter mAdapter;

    public FactoryChatCell(MessageAdapter adapter, IChatEventListener listener) {
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
                result = new ChatCellText(cellLayout, mListener, mAdapter, position);
                break;
            case IMAGE_RECEIVED:
            case IMAGE_SEND:
                result = new ChatCellImage(cellLayout, mListener, mAdapter, position);
                break;
            case VOICE_RECEIVED:
            case VOICE_SEND:
                result = new ChatCellVoice(cellLayout, mListener, mAdapter, position);
                break;
            case VIDEO_RECEIVED:
            case VIDEO_SEND:
                result = new ChatCellVideo(cellLayout, mListener, mAdapter, position);
                break;
            case MAP_RECEIVED:
            case MAP_SEND:
                result = new ChatCellMap(cellLayout, mListener, mAdapter, position);
                break;
            case VOTE_RECEIVED:
            case VOTE_SEND:
                result = new ChatCellVote(cellLayout, mListener, mAdapter, position);
                break;
            case EMOTICON_RECEIVED:
            case EMOTICON_SEND:
                result = new ChatCellEmoticon(cellLayout, mListener, mAdapter, position);
                break;
            case BUSINESS_CARD_RECEIVED:
            case BUSINESS_CARD_SEND:
                result = new ChatCellBusinessCard(cellLayout, mListener, mAdapter,
                    position);
                break;
            case WORK_LOGIN_RECEIVED:
            case WORK_LOGIN_SEND:
                result = new ChatCellWorkLogin(cellLayout, mListener, mAdapter,
                    position);
                break;
            case MULTI_RECEIVED:
            case MULTI_SEND:
                result = new ChatCellMulti(cellLayout, mListener, mAdapter, position);
                break;
            case CHAT_ACTION:
                result = new ChatCellAction(cellLayout, mListener, mAdapter, position);
                break;
            case SECRET:
                result = new ChatCellSecret(cellLayout, mListener, mAdapter, position);
                break;
            case OA:
                result = new ChatCellOA(cellLayout, mListener, mAdapter, position);
                break;
            case SYSTEM:
                result = new ChatCellSystemNotice(cellLayout, mListener, mAdapter, position);
                break;
            case NOTICE:
                result = new ChatCellNotice(cellLayout, mListener, mAdapter, position);
                break;
            default:
                result = new ChatCellText(cellLayout, mListener, mAdapter, position);
                break;
        }

        return result;
    }

}

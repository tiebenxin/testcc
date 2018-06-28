package com.lens.chatmodel.controller.multi;

import android.content.Context;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.interf.IChatEventListener;

/**
 * Created by LL130386 on 2018/2/1.
 * 合并消息item工厂
 */

public class FactoryMultiCell {

    private final Context mContext;
    private IChatEventListener mListener;

    public FactoryMultiCell(Context context, IChatEventListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setListener(IChatEventListener listener) {
        mListener = listener;
    }

    public MultiCellBase createController(EMultiCellLayout cellLayout) {

        MultiCellBase result = null;

        switch (cellLayout) {
            case TEXT:
                result = new MultiCellText(mContext, cellLayout, mListener);
                break;
            case IMAGE:
                result = new MultiCellImage(mContext, cellLayout, mListener);
                break;
            case VOICE:
                result = new MultiCellVoice(mContext, cellLayout, mListener);
                break;
            case VIDEO:
                result = new MultiCellVideo(mContext, cellLayout, mListener);
                break;
            case MAP:
                result = new MultiCellMap(mContext, cellLayout, mListener);
                break;
            case VOTE:
                result = new MultiCellText(mContext, cellLayout, mListener);
                break;
            case EMOTICON:
                result = new MultiCellEmoticon(mContext, cellLayout, mListener);
                break;
            case BUSINESS_CARD://名片
                result = new MultiCellCard(mContext, cellLayout, mListener);
                break;
            case WORK_LOGIN://签到
                result = new MultiCellText(mContext, cellLayout, mListener);
                break;
            case MULTI:
                result = new MultiCellText(mContext, cellLayout, mListener);
                break;
            default:
                result = new MultiCellText(mContext, cellLayout, mListener);
                break;

        }

        return result;
    }

}

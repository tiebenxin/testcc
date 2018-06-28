package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.body.MapBody;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lensim.fingerchat.commons.helper.GsonHelper;


/**
 * Created by LL130386 on 2018/1/3.
 * 地图
 */

public class ChatCellMap extends ChatCellBase {


    private TextView tv_address;

    private String content;
    private final Context mContext;
    private MapBody mAddressInfo;
    private LinearLayout ll_map;

    protected ChatCellMap(Context context,
        EChatCellLayout cellLayout, IChatEventListener listener, MessageAdapter adapter,
        int position) {
        super(context, cellLayout, listener, adapter, position);
        mContext = context;
        loadControls();
    }


    private void loadControls() {

        tv_address = getView().findViewById(R.id.tv_map_address);
        ll_map = getView().findViewById(R.id.ll_map);


    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            content = mChatRoomModel.getContent();
            mAddressInfo = GsonHelper.getObject(mChatRoomModel.getBody(), MapBody.class);
            if (mAddressInfo != null) {
                tv_address
                    .setText(mAddressInfo.getLocationAddress() + mAddressInfo.getLocationName());
            }

            if (mChatRoomModel.isIncoming()) {
                showProgress(false);
            }
            setSecretShow(mChatRoomModel.isSecret(), ll_map);


        }
    }

    @Override
    public void onBubbleClick() {
        if (mEventListener != null && mChatRoomModel != null) {
            mEventListener.onEvent(ECellEventType.MAP_CLICK, mChatRoomModel, null);
        }
    }

}

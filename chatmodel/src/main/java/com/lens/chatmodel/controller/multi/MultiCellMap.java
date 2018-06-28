package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.MapBody;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.ui.message.AttachMessageActivity;
import com.lens.chatmodel.ui.multi.ActivityMultiMsgDetail;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.map.ShowLocationActivity;
import com.lensim.fingerchat.commons.map.bean.MapInfoEntity;

/**
 * Created by LL130386 on 2018/2/1.
 * 地图转发消息
 */

public class MultiCellMap extends MultiCellBase {

    private TextView tv_address;
    private MapBody mAddressInfo;
    private LinearLayout ll_map;

    protected MultiCellMap(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super(context, cellLayout, listener);
        loadControls();

    }

    private void loadControls() {
        tv_address = getView().findViewById(R.id.tv_map_address);
        ll_map = getView().findViewById(R.id.ll_map);

    }

    @Override
    public void showData() {
        super.showData();
        if (mEntity != null) {
            mAddressInfo = GsonHelper.getObject(mEntity.getBody(), MapBody.class);
            if (mAddressInfo != null) {
                tv_address
                    .setText(mAddressInfo.getLocationAddress() + mAddressInfo.getLocationName());
            }
        }
    }

    @Override
    public void onBubbleClick() {
        if (mAddressInfo == null) {
            return;
        }
        MapInfoEntity map = new MapInfoEntity();
        map.setAddressName(mAddressInfo.getLocationAddress());
        map.setStreet(mAddressInfo.getLocationName());
        map.setLatitude(mAddressInfo.getLatitude());
        map.setLongitude(mAddressInfo.getLongitude());
        if (mContext instanceof AttachMessageActivity) {
            ShowLocationActivity
                .openActivity((AttachMessageActivity) mContext, ShowLocationActivity.SHOW_ADDDRESS,
                    map);
        } else if (mContext instanceof ActivityMultiMsgDetail) {
            ShowLocationActivity
                .openActivity((ActivityMultiMsgDetail) mContext, ShowLocationActivity.SHOW_ADDDRESS,
                    map);
        }

    }
}

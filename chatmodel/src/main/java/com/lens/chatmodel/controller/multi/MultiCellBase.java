package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.transfor.BaseTransforEntity;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.Date;

/**
 * Created by LL130386 on 2017/12/6.
 * 合并转发消息base类
 */

public abstract class MultiCellBase implements View.OnLongClickListener,OnClickListener {

    public final Context mContext;
    BaseTransforEntity mEntity;
    public final IChatEventListener mEventListener;
    private final View viewControl;

    public TextView tv_time;
    public ImageView iv_avatar;
    public TextView tv_name;


    MultiCellBase(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super();
        mContext = context;
        mEventListener = listener;
        viewControl = LayoutInflater.from(context).inflate(cellLayout.LayoutId, null);
        viewControl.setTag(this);
        viewControl.setOnLongClickListener(this);
        initView();
        initListener();
    }

    public void initView() {
        if (getView() == null) {
            return;
        }
        tv_time = getView().findViewById(R.id.tv_time);
        iv_avatar = getView().findViewById(R.id.iv_avatar);
        tv_name = getView().findViewById(R.id.tv_name);

    }

    public void isShowAvatar(boolean b) {
        iv_avatar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    private void initListener() {
        viewControl.setOnClickListener(this);

    }

    public void setModel(BaseTransforEntity e) {
        mEntity = e;
        showData();
    }


    public View getView() {
        return viewControl;
    }


    @Override
    public boolean onLongClick(View v) {
        return true;
    }


    public void showData() {
        setName();
        setTime();
        ImageHelper.loadAvatarPrivate(mEntity.getFriendHeader(), iv_avatar);
    }


    public void setName() {
        tv_name.setText(mEntity.getSenderUserName());
    }

    public void setTime() {
        tv_time.setText(StringUtils.friendly_time3(new Date(mEntity.getInsertTime())));
    }

    @Override
    public void onClick(View v) {
        onBubbleClick();
    }

    public void onBubbleClick() {

    }

}

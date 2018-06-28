package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.view.CustomShapeTransformation;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;


/**
 * Created by LL130386 on 2018/1/3.
 * 外出考勤打卡
 */

public class ChatCellWorkLogin extends ChatCellBase {


    private ImageView iv_content;

    private String content;
    private final Context mContext;

    protected ChatCellWorkLogin(Context context,
        EChatCellLayout cellLayout, IChatEventListener listener, MessageAdapter adapter,
        int position) {
        super(context, cellLayout, listener, adapter, position);
        mContext = context;

        loadControls();
    }


    private void loadControls() {

        iv_content = getView().findViewById(R.id.iv_content);


    }

    @Override
    public void showData() {
        super.showData();
        if (mChatRoomModel != null) {
            content = mChatRoomModel.getContent();
            loadImage(content, mChatRoomModel.isIncoming() ? R.drawable.finger_chatfrom_bg :
                R.drawable.finger_chatto_bg);

        }
    }

    private void loadImage(String url, int res) {
        Glide.with(ContextHelper.getContext())
            .load(url)
            .error(R.drawable.ease_default_image)
            .override(DensityUtil.dip2px(ContextHelper.getContext(), 100),
                DensityUtil.dip2px(ContextHelper.getContext(), 60))//复写尺寸
            .transform(new CustomShapeTransformation(ContextHelper.getContext(), res, false))//自定义裁剪
            .into(iv_content);
    }

}

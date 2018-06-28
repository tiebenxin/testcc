package com.lens.chatmodel.controller.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.lens.chatmodel.ChatEnum.ECellEventType;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.bean.ImageEventBean;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IBooleanListener;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.view.CustomShapeTransformation;
import com.lens.chatmodel.view.ImageLinearLayout;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.components.pulltorefresh.XCPullToLoadMoreListView;
import com.lensim.fingerchat.data.bean.LongImageBean;
import java.util.ArrayList;


/**
 * Created by LL130386 on 2018/1/3.
 * 图片
 */

public class ChatCellImage extends ChatCellBase implements IBooleanListener {


    private ImageView iv_content;
    private final Context mContext;
    private boolean isLongImage;

    protected ChatCellImage(Context context,
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
            ImageUploadEntity entity = ImageUploadEntity.fromJson(mChatRoomModel.getContent());
            if (mChatRoomModel.isIncoming()) {
                if (!TextUtils.isEmpty(mChatRoomModel.getContent())) {
                    if (entity != null) {
                        loadImage(entity.getThumbnailUrl(), R.drawable.finger_chatfrom_bg);
                    } else {
                        loadImage("", R.drawable.finger_chatfrom_bg);
                    }
                } else {
                    loadImage("", R.drawable.finger_chatfrom_bg);
                }
            } else {
                if (entity != null) {
                    loadImage(entity.getOriginalUrl(), R.drawable.finger_chatto_bg);
                } else {
                    loadImage("", R.drawable.finger_chatto_bg);
                }
            }

            bindData();
            setSecretShow(mChatRoomModel.isSecret(), null);

        }

    }

    private void bindData() {
        ((ImageLinearLayout) getView()).setImageView(iv_content);
        ((ImageLinearLayout) getView()).setMsgId(mChatRoomModel.getMsgId());
        ((ImageLinearLayout) getView()).setLongImage(isLongImage);
    }

    @Override
    public void onBubbleClick() {
        super.onBubbleClick();
        if (mChatRoomModel != null && mEventListener != null) {
            ArrayList<String> uris = new ArrayList<>();
            ArrayList<String> msgIds = new ArrayList<>();
            ProviderChat.selectAllImageMessage(ContextHelper.getContext(), mAdapter.getUser(), uris,
                msgIds);
            if (uris.size() <= 0 || msgIds.size() <= 0) {
                return;
            }
            XCPullToLoadMoreListView listView = mAdapter.getListView();
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            int LastVisiblePosition = listView.getLastVisiblePosition();
            ArrayList<AnimationRect> animationRectArrayList = new ArrayList<>();
            ArrayList<LongImageBean> booleanLists = new ArrayList<>();
            for (int i = firstVisiblePosition; i <= LastVisiblePosition; i++) {
                if (mAdapter.getItemViewType(i) != 2
                    && mAdapter.getItemViewType(i) != 3) { //这里返回-1，所以没取到值
                    continue;
                }

                View view = listView.getListView().getChildAt(i - firstVisiblePosition);
                if (view instanceof ImageLinearLayout) {
                    ImageLinearLayout cell = (ImageLinearLayout) view;
                    ImageView imageView = cell.getImageView();
                    if (imageView.getVisibility() == View.VISIBLE) {
                        AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                        if (rect == null) {
                            continue;
                        }
                        rect.setMsgId(cell.getMsgId());
                        animationRectArrayList.add(rect);
                        LongImageBean longImageBean = new LongImageBean();
                        longImageBean.setId(cell.getMsgId());
                        longImageBean.setLongImage(cell.isLongImage());
                        booleanLists.add(longImageBean);
                    }
                }
            }

            if (msgIds.indexOf(getMsgId()) >= 0) {
                ImageEventBean bean = new ImageEventBean();
                bean.setUrls(uris);
                bean.setMsgIds(msgIds);
                bean.setRects(animationRectArrayList);
                bean.setBooleanLists(booleanLists);
                bean.setPosition(msgIds.indexOf(getMsgId()));
                mEventListener.onEvent(ECellEventType.IMAGE_CLICK, mChatRoomModel, bean);
            } else {

            }

        }
    }

    private void loadImage(String url, final int res) {
        ImageHelper.loadMessageImage(url, iv_content,
            new CustomShapeTransformation(ContextHelper.getContext(), res, false, this));
    }

    public ImageView getImageView() {
        return iv_content;
    }

    public String getMsgId() {
        return mChatRoomModel.getMsgId();
    }

    @Override
    public void onResult(boolean result) {
        isLongImage = result;
    }

    private boolean isLongImage() {
        return isLongImage;
    }
}

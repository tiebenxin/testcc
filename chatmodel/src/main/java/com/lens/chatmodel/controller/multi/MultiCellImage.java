package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/2/1.
 * 合并转发image
 */

public class MultiCellImage extends MultiCellBase {

    private ImageView iv_content;
    private LinearLayout ll_right;
    private ImageUploadEntity entity;

    MultiCellImage(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super(context, cellLayout, listener);
        loadControls();

    }

    private void loadControls() {
        iv_content = getView().findViewById(R.id.iv_content);
        ll_right = getView().findViewById(R.id.ll_right);
    }

    @Override
    public void showData() {
        super.showData();
        if (mEntity != null) {
            entity = ImageUploadEntity.fromJson(mEntity.getBody());
            assert entity != null;
            String size = entity.getOriginalSize();
            List<Integer> arr = ImageHelper.getOverrideImageSize(size);
            if (arr != null && arr.size() == 2) {
                ImageHelper.loadImageOverrideSize(entity.getOriginalUrl(), iv_content, arr.get(0),
                    arr.get(1));
            } else {
                ImageHelper.loadImage(entity.getOriginalUrl(), iv_content);

            }

        }
        updateWidth();
    }

    private void updateWidth() {
        float screenWidth = TDevice.getScreenWidth();
        int avatarWidth = iv_avatar.getMeasuredWidth();
        LayoutParams avatarParams = (LayoutParams) iv_avatar.getLayoutParams();
        int avatarMargin = avatarParams.leftMargin + avatarParams.rightMargin;
        LayoutParams rightParams = (LayoutParams) ll_right.getLayoutParams();
        int rightMargin = rightParams.rightMargin + rightParams.leftMargin;
        int width = (int) (screenWidth - avatarMargin - avatarWidth - rightMargin);
        ll_right.setMinimumWidth(width);
    }

    @Override
    public void onBubbleClick() {
        ArrayList<AnimationRect> animationRectArrayList = new ArrayList<AnimationRect>();
        AnimationRect rect = AnimationRect.buildFromImageView(iv_content);
        if (rect != null) {
            rect.setUri(mEntity.getBody());
        }
        animationRectArrayList.add(rect);
        ArrayList<String> urls = new ArrayList<>();
        urls.add(mEntity.getBody());
        Intent intent = GalleryAnimationActivity
            .newIntent(urls, null, animationRectArrayList, null, 0,"");
        mContext.startActivity(intent);
    }
}

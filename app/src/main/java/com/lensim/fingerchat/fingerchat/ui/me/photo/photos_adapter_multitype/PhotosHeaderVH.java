package com.lensim.fingerchat.fingerchat.ui.me.photo.photos_adapter_multitype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lens.chatmodel.ui.video.CameraActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.components.adapter.multitype.ItemViewBinder;
import com.lensim.fingerchat.components.dialog.BaseDialog;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.StatuActivity;
import com.lensim.fingerchat.fingerchat.ui.me.photo.PhotosActivity;

/**
 * date on 2017/12/20
 * author ll147996
 * describe
 */

public class PhotosHeaderVH extends ItemViewBinder<String, PhotosHeaderVH.ViewHolder> {

    private Context context;

    public PhotosHeaderVH(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_photo_head, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull String item) {
        holder.videoImage.setOnClickListener(v -> showPopupWindow());
    }

    //照片或是视频
    private void showPopupWindow() {
        PhotoChooseDialog dialog = new PhotoChooseDialog((Activity) context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout videoImage;

        public ViewHolder(View itemView) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.rv_header);
        }
    }



    /**
     * date on 2018/3/12
     * author ll147996
     * describe 发朋友圈：两种选择——图片、视频
     */

    public class PhotoChooseDialog extends BaseDialog {

        private Activity context;
        private TextView mPhoto;
        private TextView mVideo;

        private PhotoChooseDialog(Activity context) {
            super(context);
            this.context = context;
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override public void initView() {
            setContentView(R.layout.pop_publish_circle_menu);
            mPhoto = findViewById(R.id.pop_publish_photo);
            mVideo = findViewById(R.id.pop_publish_video);
        }

        @Override public void initEvent() {
            mPhoto.setOnClickListener(this);
            mVideo.setOnClickListener(this);
        }

        @Override public void processClick(View view) {
            switch (view.getId()) {
                case R.id.pop_publish_photo:
                    Intent intent = new Intent(context, StatuActivity.class);
                    context.startActivityForResult(intent, PhotosActivity.PHOTOS_REQUEST_NEW_STATUS);
                    break;
                case R.id.pop_publish_video:
                    if(AppManager.getInstance().checkCamaraAndAudioPermisson(context)) {
                        CameraActivity.start(context, AppConfig.REQUEST_VIDEO, CameraActivity.BUTTON_STATE_ONLY_RECORDER);
                    }
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

}

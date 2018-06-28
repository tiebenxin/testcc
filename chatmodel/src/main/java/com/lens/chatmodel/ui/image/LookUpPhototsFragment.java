package com.lens.chatmodel.ui.image;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.fingerchat.proto.message.BaseChat.MessageType;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.ui.message.TransforMsgActivity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lens.chatmodel.view.photoview.PhotoViewAttacher;
import com.lens.chatmodel.view.photoview.ZoomImageView;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.T;

public class LookUpPhototsFragment extends Fragment {

    private ZoomImageView photoView;
    private ImageView iv_play;
    private String imgURL;

    public static LookUpPhototsFragment newInstance(String path) {
        LookUpPhototsFragment fragment = new LookUpPhototsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_general_layout, container, false);

        photoView = view.findViewById(R.id.animation);
        iv_play = view.findViewById(R.id.iv_play);

        String path = getArguments().getString("path");

        photoView.setIsOrigin(false);//不让拉升
        loadImage(path);

        imgURL = path;

        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (getActivity() instanceof LookUpPhotosActivity) {
                    ((LookUpPhotosActivity) getActivity()).onPhotoTap();
                }
            }
        });

        photoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof LookUpPhotosActivity) {
                    ((LookUpPhotosActivity) getActivity()).onPhotoTap();
                }
            }
        });

//        setLongClick();

        checkIsVideo(path);

        return view;
    }

    private void setLongClick() {
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!AuthorityManager.getInstance().copyPicOutsize()) {
                    T.show("没有相应权限");
                    return false;
                }
                final String[] menus = new String[]{getString(R.string.dialog_menu_send_to_friend),
                    getString(R.string.pop_menu_collect),
                    getString(R.string.pop_menu_copy_to_local)};
                new AlertDialog.Builder(getActivity())
                    .setItems(menus, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!AuthorityManager.getInstance().copyPicOutsize()) {
                                T.show("没有相关权限");
                                return;
                            }
                            switch (which) {
                                case 0:
                                    transfer(imgURL);
                                    break;
                                case 1:
//                                    if (getActivity() instanceof LookUpPhotosActivity) {
//                                        LookUpPhotosActivity parentActivity = (LookUpPhotosActivity) getActivity();
//                                        parentActivity.store();
//                                    }
                                    break;
                                case 2:
                                    save(imgURL);
                                    break;
                            }
                        }
                    }).show();
                return true;
            }
        });
    }

    private void checkIsVideo(final String path) {
        if (getActivity() instanceof PhotoPreviewActivity) {
            if (ContextHelper.isVideo(path)) {
                iv_play.setVisibility(View.VISIBLE);
                iv_play.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimationRect rect = AnimationRect.buildFromImageView(photoView);
                        Intent intent = LookUpVideoActivity
                            .newIntent(getActivity(), rect, path, "gallery");
                        intent.putExtra("isSilent", false);
                        getActivity().startActivity(intent);
                    }
                });
            } else {
                iv_play.setVisibility(View.GONE);
            }
        } else {
            iv_play.setVisibility(View.GONE);
        }
    }

    private void loadImage(String path) {
        if (ContextHelper.isGif(path)) {
            photoView.setIsGif(true);
            ImageHelper.loadGif(path, photoView);
        } else {
            photoView.setIsGif(false);
            ImageHelper.loadImage(path, photoView);
        }
    }

    public void setImagePath(String filepath) {
        photoView.setIsOrigin(true);
        loadImage(filepath);

    }

    /*
    * 注意修改chatType参数
    * */
    public void transfer(String imgURL) {
        ImageUploadEntity entity = ImageUploadEntity.createEntity(imgURL);
        Intent intent = TransforMsgActivity
            .newPureIntent(getActivity(), ImageUploadEntity.toJson(entity),
                EMessageType.IMAGE.value, EChatType.PRIVATE.ordinal(), "");
        getActivity().startActivity(intent);
    }

    public void save(String imgURL) {
        Drawable drawable = photoView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            String filepath = FileUtil.saveToPicDir(bitmap);
            if (filepath != null) {
                T.show("保存成功");
//        String copyText = FileUtil.uploadUserOption(imgURL, "", "a_save_pic");
//        LensImUtil.uploadLog(getActivity(), copyText, new IDataRequestListener() {
//          @Override
//          public void loadFailure(String reason) {
//            L.i("上传失败:失败码" + reason);
//          }
//
//          @Override
//          public void loadSuccess(Object object) {
//            if (object instanceof String) {
//              L.i("上传成功:" + (String) object);
//            }
//          }
//        });

            } else {
                T.show("保存失败");
            }
        } else {
            T.show("此类型无法保存");
        }
    }
}

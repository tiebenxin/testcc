package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lens.core.componet.log.DLog;
import com.lens.core.componet.net.exeception.ApiException;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.CommonEnum;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.adapter.BaseListAdapter;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.CirclesFriendsApi;
import com.lensim.fingerchat.fingerchat.databinding.ActivityStatuBinding;
import com.lensim.fingerchat.fingerchat.model.requestbody.SendPhotosRequestBody;
import com.lensim.fingerchat.fingerchat.ui.work_center.sign.ImagePagerOptActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY309313 on 2016/8/5.
 */

public class StatuActivity extends FGActivity {

    public final static String TEXT = "OnlyText";
    private GridAdapter adapter;
    private boolean isOnlyText = false;
    private List<String> pictures = new ArrayList<>();

    ActivityStatuBinding ui;

    private String mind;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_statu);
        initBackButton(ui.statuToolbar, true);
        ui.statuToolbar.setConfirmBt((view) -> confirm());
        initListener();
    }


    private void confirm() {
        // 需要拿到所有的图片传上去，然后将评论传上去
        mind = ui.statuInputMind.getText().toString().trim();
        if (isOnlyText) {
            if (TextUtils.isDigitsOnly(mind)) {
                T.show("内容不能为空");
                return;
            } else {
                sendText();
            }
        } else {
            List<String> uris = adapter.getItems();
            if ((uris == null || uris.size() == 0)) {
                T.show("请选择图片");
                return;
            } else {
                sendImage(uris, 0);
            }
        }
        showProgress("正在上传...", false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        List<String> path = new ArrayList<>();
        adapter = new GridAdapter(this, path);
        ui.statuImgContainer.setAdapter(adapter);
        isOnlyText = getIntent().getBooleanExtra(TEXT, false);
        if (isOnlyText) {
            ui.statuImgContainer.setVisibility(View.GONE);
        }
    }


    public void initListener() {
        ui.statuInputMind.setOnLongClickListener(v -> {
            if (!AuthorityManager.getInstance().copyOutside()) {
                ClipboardManager clipboard = (ClipboardManager) StatuActivity.this
                    .getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText(
                    null, AuthorityManager.getInstance().getCopyContent()));
            }
            return false;
        });

    }

    private class GridAdapter extends BaseListAdapter<String> {

        private Context mContext;
        private LayoutInflater inflater;
        //最多9张图片
        private final static int MAX_COUNT = 9;

        private GridAdapter(Context ctx, List<String> list) {
            super(ctx);
            mContext = ctx;
            items = list;
            inflater = LayoutInflater.from(mContext);
        }


        @Override
        public int getCount() {
            return items.size() + 1;
        }

        @Override
        public String getItem(int position) {
            if (position == items.size()) {
                return null;
            }
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_statu_img, parent, false);
            ImageView view = convertView.findViewById(R.id.statu_img);
            ImageButton ibDelete = convertView.findViewById(R.id.ib_delete);
            Log.e("位置：", position + "");
            Log.e("数量：", items.size() + "");
            String uri = getItem(position);
            if (!StringUtils.isEmpty(uri)) {
                if (ContextHelper.isGif(uri)) {
                    Glide.with(StatuActivity.this)
                        .load(uri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//原型
                        .thumbnail(0.1f)
                        .into(view);
                } else {
                    Glide.with(StatuActivity.this)
                        .load(uri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//原型
                        .into(view);
                }
            }

            ibDelete.setOnClickListener(v -> {
                items.remove(position);
                notifyDataSetChanged();
            });

            if (position == items.size()) {
                ibDelete.setVisibility(View.GONE);
                view.setImageResource(R.drawable.icon_addpic_unfocused);
                if (position == MAX_COUNT) {
                    view.setVisibility(View.GONE);
                    ibDelete.setVisibility(View.GONE);
                } else {
                    view.setOnClickListener(v -> {
                        Intent intent = new Intent(StatuActivity.this,
                            MultiImageSelectorActivity.class);
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT,
                            MAX_COUNT - pictures.size());
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
                            MultiImageSelectorActivity.MODE_MULTI);
                        startActivityForResult(intent, AppConfig.REQUEST_IMAGE);
                    });
                }

            } else {
                view.setOnClickListener(v -> {
                    Intent intent = new Intent(StatuActivity.this, ImagePagerOptActivity.class);
                    intent.putStringArrayListExtra(ImagePagerOptActivity.INTENT_IMGURLS,
                        new ArrayList<>(items));
                    intent.putExtra(ImagePagerOptActivity.INTENT_POSITION, position);
                    StatuActivity.this.startActivityForResult(intent, AppConfig.REQUEST_IMGS);
                });

            }
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == AppConfig.REQUEST_IMAGE) {
            if (pictures == null || pictures.isEmpty()) {
                photoUrl = new StringBuilder();
                photoFilenames = new StringBuilder();
            }
            // 获取返回的图片列表
            List<ImageBean> path = data.getParcelableArrayListExtra(
                MultiImageSelectorActivity.EXTRA_RESULT);
            // 处理你自己的逻辑 ....
            Log.e("选择了几张图片:", path.size() + "");
            pictures.addAll(addAllImageBean(path));
            adapter.setItems(pictures);

            for (ImageBean imageBean : path) {
                photoFilenames.append((photoFilenames.length() == 0 ? "" : ",") + imageBean.name);
            }
            uploadList = pictures;

        } else if (requestCode == AppConfig.REQUEST_IMGS) {
            List<String> path = data.getStringArrayListExtra("imgs");
            if (path != null) {
                pictures.clear();
                pictures.addAll(path);
                adapter.setItems(pictures);
            }
        }
    }

    private List<String> addAllImageBean(List<ImageBean> path) {
        List<String> list = new ArrayList<>();
        for (ImageBean bean : path) {
            list.add(bean.path);
        }
        return list;
    }


    private List<String> uploadList; // 剩余上传的图片
    private StringBuilder photoUrl;
    private StringBuilder photoFilenames;

    /**
     * 发送图片
     *
     * @param images
     * @param position
     */
    private void sendImage(List<String> images, final int position) {
        if (position >= images.size()) {
            SendPhotosRequestBody sendPhotosRequestBody
                = new SendPhotosRequestBody.Builder()
                .creatorUserId(UserInfoRepository.getUserId())
                .creatorUserName(CyptoUtils.encrypt(UserInfoRepository.getUserName()))
                .photoContent(CyptoUtils.encrypt(mind))
                .photoFileNum(images.size())
                .photoFilenames(photoFilenames.toString())
                .photoUrl(photoUrl.toString())
                .build();

            new CirclesFriendsApi().sendPhoto(sendPhotosRequestBody, new FXRxSubscriberHelper<BaseResponse>() {
                @Override
                public void _onNext(BaseResponse baseResponse) {
                    dismissProgress();
                    T.show("发布成功");
                    dismissProgress();
                    Intent intent = new Intent();
                    intent.putExtra("statu_result", true);
                    StatuActivity.this.setResult(RESULT_OK, intent);
                    StatuActivity.this.finish();
                }

                @Override
                public void _onError(ApiException error) {
                    super._onError(error);
                    dismissProgress();
                }
            });
            return;
        }
        String currentUrl = images.get(position);//当前上传url
        boolean isGif = ContextHelper.isGif(currentUrl);
        HttpUtils.getInstance()
            .uploadFileProgress(currentUrl, isGif ? CommonEnum.EUploadFileType.GIF : CommonEnum.EUploadFileType.JPG,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        DLog.i("上传成功-" + position);
                        if (result != null && result instanceof ImageUploadEntity) {
                            if (uploadList != null && uploadList.size() > 0) {
                                ImageUploadEntity entity = (ImageUploadEntity) result;
                                photoUrl.append((photoUrl.length() == 0 ? "" : ",") + entity.getOriginalUrl());
                            }
                            sendImage(images, position + 1);
                        } else {
                            dismissProgress();
                            // 上传失败重置上传数据
                            T.show("上传失败");
                            photoUrl.delete(0, photoUrl.length());
                            uploadList = pictures;
                        }
                    }

                    @Override
                    public void onFailed() {
                        dismissProgress();
                        T.show("上传失败");
                        photoUrl.delete(0, photoUrl.length());
                        uploadList = pictures;
                    }

                    @Override
                    public void onProgress(int progress) {
                        DLog.d("上传进度" + progress);
                    }

                });
    }

    /**
     * 发送文字
     */
    private void sendText() {
        new CirclesFriendsApi().pulishTalkText(UserInfoRepository.getUserId(),
            CyptoUtils.encrypt(UserInfoRepository.getUserName()),CyptoUtils.encrypt(mind), new FXRxSubscriberHelper<BaseResponse>() {
                @Override
                public void _onNext(BaseResponse baseResponse) {
                    dismissProgress();
                    T.show("发布成功");
                    L.d("上传成功");
                    dismissProgress();
                    Intent intent = new Intent();
                    intent.putExtra("statu_result", true);
                    StatuActivity.this.setResult(RESULT_OK, intent);
                    StatuActivity.this.finish();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    dismissProgress();
                }
            });
    }

}

package com.lensim.fingerchat.fingerchat.ui.me.collection;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lens.chatmodel.ui.message.TransforMsgActivity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.JsonUtils;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil;
import com.lensim.fingerchat.components.widget.CircleProgress;
import com.lensim.fingerchat.components.widget.circle_friends.MultiImageView;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavContent;
import com.lensim.fingerchat.data.me.content.VideoFavContent;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityCollectionDetailBinding;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.utils.GlideCircleTransform;
import com.lensim.fingerchat.fingerchat.ui.me.utils.SpliceUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;

public class CollectionDetailActivity extends BaseActivity {

    public final static int REQUEST_FOR_DETAIL = 331;
    public final static String ITEM_POSITION = "position";
    public final static String UNIQUE_ID = "unique_id";
    public final static String ITEM_TYPE = "typeItem";
    public final static String ITEM_DATA = "ItemData";
    public final static String AVATAR_URL = "avatar";
    public final static String NAME = "name";
    public final static String CREATE_TIME = "createTime";
    public final static String DES = "des";

    private final static String SPLIT = ",";
    private long mUniqueID = 0L;
    private String imgUrl = null;
    private int positionInParent = 0;
    private ArrayList<String> groupLabels;
    private boolean isMarkChanged = false;
    ActivityCollectionDetailBinding ui;
    private String data;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_collection_detail);
        initBackButton(ui.mDetailToolBar, true);
        ui.mDetailToolBar.setTitleText("详情");
        groupLabels = new ArrayList<>();
        UIHelper.setTextSize2(14, ui.tvFriendDetailName);
        UIHelper.setTextSize2(10, ui.collectCreateTime, ui.txtMyMark);
        ui.llAddMark.setOnClickListener(v -> {
            Intent intent = CollectionMarkActivity.newIntent(this, groupLabels);
            startActivityForResult(intent, 0);
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //1.text 文字 2.image 图片 3.video 视频 4.gif 大表情
        int type = getIntent().getIntExtra(ITEM_TYPE, 1);
        positionInParent = getIntent().getIntExtra(ITEM_POSITION, 0);
        data = getIntent().getStringExtra(ITEM_DATA);
        mUniqueID = getIntent().getLongExtra(UNIQUE_ID, 0L);
        String avatar_url = getIntent().getStringExtra(AVATAR_URL);
        String name = getIntent().getStringExtra(NAME);
        String des = getIntent().getStringExtra(DES);
        long timeStamp = getIntent().getLongExtra(CREATE_TIME, new Date().getTime());

        if (!TextUtils.isEmpty(avatar_url)) {
            Glide.with(CollectionDetailActivity.this).load(avatar_url).centerCrop()
                .transform(new GlideCircleTransform(CollectionDetailActivity.this))
                .into(ui.ivFriendsDetailAvater);
        }

        if (!TextUtils.isEmpty(name)) {
            ui.tvFriendDetailName.setText(name);
        }

        if (!TextUtils.isEmpty(des)) {
            ui.txtMyMark.setText(des);

            ui.txtMyMark.setTextColor(ContextCompat.getColor(this, R.color.primary));

            String[] array = des.split(SPLIT);
            List<String> listA = Arrays.asList(array);
            groupLabels = new ArrayList<>(listA);
        }

        if (timeStamp != 0) {
            ui.collectCreateTime.setText("收藏于" + TimeUtils.secondToTime(timeStamp + ""));
        }

        //1.text 文字 2.image 图片 3.video 视频 4.gif 大表情
        switch (type) {
            case 1:
                ui.viewStubCollect.getViewStub().setLayoutResource(R.layout.viewstub_collect_text);
                ui.viewStubCollect.getViewStub().inflate();
                TextView text = findViewById(R.id.simple_text);
                int textSize = SPSaveHelper.getIntValue("font_size", 1) * 4 + 12;
                Spannable span = SpannableUtil.getAtText(SmileUtils
                    .getSmiledText(CollectionDetailActivity.this, data,
                        (int) TDevice.dpToPixel(textSize + 10)));
                text.setText(span);
                UIHelper.setTextSize2(14, text);
                final String content = data;
                text.setOnLongClickListener(v -> {
                    //复制
                    showCopyDialog(content);
                    return true;
                });
                break;
            case 4:
                ui.viewStubCollect.getViewStub().setLayoutResource(R.layout.viewstub_big_ex);
                ui.viewStubCollect.getViewStub().inflate();
                final ImageView multiImageView = findViewById(R.id.multiImagView);
                final List<String> photos = new ArrayList<>();
                photos.add(data);
                if (photos.size() > 0) {
                    imgUrl = data;
                    Glide.with(ContextHelper.getContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.ease_default_expression)
                        .override(DensityUtil.dip2px(ContextHelper.getContext(), 200),
                            DensityUtil.dip2px(ContextHelper.getContext(), 120))
                        .into(multiImageView);
                } else {
                    multiImageView.setVisibility(View.GONE);
                }
                break;
            case 3:
                VideoFavContent videoFavContent = new Gson().fromJson(data, VideoFavContent.class);
                ui.viewStubCollect.getViewStub().setLayoutResource(R.layout.viewstub_videobody);
                View rootView = ui.viewStubCollect.getViewStub().inflate();
                ImageView videothumbnial = rootView.findViewById(R.id.video_override);
                LinearLayout ll_loading = rootView.findViewById(R.id.ll_loading);
                FrameLayout layoutPlayer = rootView.findViewById(R.id.container_video_play);
                CircleProgress progressBar = rootView.findViewById(R.id.progress_bar);

                ll_loading.setVisibility(View.GONE);
                String videoPath = FileCache.getInstance()
                    .getVideoPath(videoFavContent.getVideoUrl());

                if (!StringUtils.isEmpty(videoPath) && FileUtil.checkFilePathExists(videoPath)) {
                    videothumbnial.setVisibility(View.VISIBLE);
                    Glide.with(CollectionDetailActivity.this)
                        .load(videoFavContent.getImageUrl())
                        .centerCrop()
                        .into(videothumbnial);
                    layoutPlayer.setOnClickListener(v -> toPlayer(videothumbnial, videoPath));
                } else {
                    ll_loading.setVisibility(View.VISIBLE);
                    ProgressManager.getInstance()
                        .addResponseListener(videoFavContent.getVideoUrl(), new ProgressListener() {
                            @Override
                            public void onError(long id, Exception e) {
                            }

                            @Override
                            public void onProgress(ProgressInfo progressInfo) {
                                progressBar.setPercent(progressInfo.getPercent());
                            }
                        });

                    Http.downloadFileWithDynamicUrlAsync(videoFavContent.getVideoUrl())
                        .compose(RxSchedulers.compose())
                        .subscribe(responseBody -> {
                            ll_loading.setVisibility(View.GONE);
                            byte[] videoBytes = responseBody.bytes();
                            boolean b = false;
                            try {
                                b = FileCache.getInstance()
                                    .saveVideo(videoFavContent.getVideoUrl(), videoBytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (b) {
                                videothumbnial.setVisibility(View.VISIBLE);
                                videothumbnial.setAlpha(1f);
                                layoutPlayer.setVisibility(View.VISIBLE);
                                Glide.with(CollectionDetailActivity.this)
                                    .load(videoFavContent.getVideoUrl().replace(".mp4", ".jpg"))
                                    .centerCrop().into(videothumbnial);
                                layoutPlayer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        toPlayer(videothumbnial, videoPath);
                                    }
                                });
                            }
                        });
                }
                break;

            case 2:
                ui.viewStubCollect.getViewStub().setLayoutResource(R.layout.viewstub_imgbody);
                ui.viewStubCollect.getViewStub().inflate();
                final MultiImageView exImageView = findViewById(R.id.multiImagView);
                FavContent favContent = JsonUtils.fromJson(data, FavContent.class);
                final List<String> exPhotos = new ArrayList<>();
                exPhotos.add(favContent.getOriginalUrl());
                if (exPhotos.size() > 0) {
                    imgUrl = favContent.getOriginalUrl();
                    exImageView.setVisibility(View.VISIBLE);
                    exImageView.setList(exPhotos);
                    exImageView.setOnItemClickListener((view, position) -> {
                        //imagesize是作为loading时的图片size
                        ArrayList<AnimationRect> animationRectArrayList = new ArrayList<AnimationRect>();
                        SparseArray<ImageView> imageviews = exImageView.getImageviews();
                        for (int i = 0; i < imageviews.size(); i++) {
                            ImageView imageView = imageviews.get(i);
                            if (imageView.getVisibility() == View.VISIBLE) {
                                AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                                if (rect == null) {
                                    L.d("根本没有取到iamgeview的信息");
                                } else {
                                    if (i < exPhotos.size()) {
                                        rect.setUri(exPhotos.get(i));
                                    }
                                }
                                animationRectArrayList.add(rect);
                            }
                        }
                        ArrayList<String> urls = new ArrayList<>(exPhotos);
                        Intent intent = GalleryAnimationActivity
                            .newIntent(SpliceUrl.getUrls(urls), null, animationRectArrayList, null,
                                position);
                        startActivity(intent);
                    });
                    exImageView.setOnItemLongClickListener((view, position) -> showForwardDialog());
                } else {
                    exImageView.setVisibility(View.GONE);
                }

                break;
        }
    }

    private void showCopyDialog(final String content) {
        new AlertDialog.Builder(CollectionDetailActivity.this)
            .setItems(new String[]{getResources().getString(R.string.text_copy)},
                (dialog, which) -> {
                    if (!AuthorityManager.getInstance().copyPicOutsize()) {
                        T.showShort(CollectionDetailActivity.this, "没有相关权限");
                        return;
                    }
                    if (AuthorityManager.getInstance().copyOutside()) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(
                            Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText(null, content));
                    } else {
                        if (AuthorityManager.getInstance().copyInside()) {
                            AuthorityManager.getInstance().copy(content);
                        } else {
                            SecureUtil.showToast("请申请权限");
                        }
                    }
                    String copyText = FileUtil.uploadUserOption(content, null, "a_copy_text");
                    Http.uploadLog(copyText)
                        .compose(RxSchedulers.io_main())
                        .subscribe(responseBody -> L.i("上传成功:" + responseBody.string()),
                            throwable -> L.i("上传失败:" + throwable.getMessage()));
                }).show();
    }

    /***
     *  去播放视频
     *
     */
    private void toPlayer(ImageView imageView, String videoPath) {
        imageView.setAlpha(1f);
        AnimationRect rect = AnimationRect.buildFromImageView(imageView);
        Intent intent = LookUpVideoActivity
            .newIntent(CollectionDetailActivity.this, rect, videoPath, "circle");
        intent.putExtra("type", "chat");
        startActivity(intent);
    }


    public void saveMyLabel() {
        if (null == groupLabels || groupLabels.size() < 1) {
            CollectionManager.getInstance().updateItemDES(mUniqueID, "");
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = groupLabels.size(); i < len; i++) {
                if (i == len - 1) {
                    sb.append(groupLabels.get(i));
                } else {
                    sb.append(groupLabels.get(i) + SPLIT);
                }
            }
            CollectionManager.getInstance().updateItemDES(mUniqueID, sb.toString().trim());
        }
        isMarkChanged = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StringBuffer sb = new StringBuffer();
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> user_labels = data.getStringArrayListExtra("user_labels");
            groupLabels.clear();
            if (user_labels != null && !user_labels.isEmpty()) {
                groupLabels.addAll(user_labels);
                int len = groupLabels.size();
                for (int i = 0; i < len; i++) {
                    if (i == len - 1) {
                        sb.append(groupLabels.get(i));
                    } else {
                        sb.append(groupLabels.get(i) + SPLIT);
                    }
                }
                ui.txtMyMark.setText(sb.toString());
                ui.txtMyMark.setTextColor(ContextCompat.getColor(this, R.color.primary));
            } else {
                ui.txtMyMark.setText(getString(R.string.hint_add_mark));
                ui.txtMyMark.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
            }
            saveMyLabel();
        }
    }

    //左上角返回键
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            myReturn();
        }
        return super.onOptionsItemSelected(item);
    }

    //返回键
    @Override
    public void onBackPressed() {
        myReturn();
    }

    public void myReturn() {
        Intent intent = new Intent();
        intent.putExtra("position", positionInParent);
        intent.putExtra("isMarkChanged", isMarkChanged);
        if (groupLabels.size() > 0) {
            intent.putExtra("Item_callback", ui.txtMyMark.getText());
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public void showForwardDialog() {
        new AlertDialog.Builder(CollectionDetailActivity.this)
            .setItems(new String[]{getResources().getString(R.string.dialog_menu_send_to_friend)},
                (dialog, which) -> {
                    if (!AuthorityManager.getInstance().copyPicOutsize()) {
                        T.showShort(CollectionDetailActivity.this, "没有相关权限");
                    } else {
                        BodyEntity entity = new BodyEntity(data);
                        Intent intent = TransforMsgActivity
                            .newPureIntent(CollectionDetailActivity.this, BodyEntity.toJson(entity),
                                Content.MSG_TYPE_PIC, 0, "");
                        CollectionDetailActivity.this.startActivity(intent);
                    }

                }).show();
    }
}


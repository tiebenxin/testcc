package com.lensim.fingerchat.fingerchat.ui.me.collection;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.cache.FileManager;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lens.chatmodel.ui.message.TransforMsgActivity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.global.CommonEnum.ELogType;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
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
import com.lensim.fingerchat.data.CollectionApi;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.bean.AddFavoryRequestBody;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavContent;
import com.lensim.fingerchat.data.me.content.VideoFavContent;
import com.lensim.fingerchat.data.me.content.VoiceFavContent;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityCollectionDetailBinding;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.utils.GlideCircleTransform;
import com.lensim.fingerchat.fingerchat.ui.me.utils.SpliceUrl;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import org.json.JSONException;
import org.json.JSONObject;

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
    public final static String CREATOR = "creator";
    public final static String PROVIDER_ID = "provider_id";
    public final static String MSG_ID = "msg_id";
    private final static String SPLIT = ",";
    private long mUniqueID = 0L;
    private String imgUrl = null;
    private int positionInParent = 0;
    private ArrayList<String> groupLabels;
    private boolean isMarkChanged = false;
    ActivityCollectionDetailBinding ui;
    private String tData;
    MediaPlayer mediaPlayer = null;
    private AnimationDrawable voiceAnimation;
    public static boolean isPlaying = false;
    private String creator;
    private String name;
    private String provideId;
    private int type;
    private String msgId;
    private String videoPath;
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
        type = getIntent().getIntExtra(ITEM_TYPE, 1);
        positionInParent = getIntent().getIntExtra(ITEM_POSITION, 0);
        tData = getIntent().getStringExtra(ITEM_DATA);
        mUniqueID = getIntent().getLongExtra(UNIQUE_ID, 0L);
        String avatar_url = getIntent().getStringExtra(AVATAR_URL);
        creator = getIntent().getStringExtra(CREATOR);
        name = getIntent().getStringExtra(NAME);
        provideId = getIntent().getStringExtra(PROVIDER_ID);
        msgId = getIntent().getStringExtra(MSG_ID);
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
                String textData = null;
                try {
                    JSONObject jsonObject = new JSONObject(tData);
                    if (jsonObject.has("content")){
                        textData = jsonObject.getString("content");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Spannable span = SpannableUtil.getAtText(SmileUtils
                    .getSmiledText(CollectionDetailActivity.this, TextUtils.isEmpty(textData)? tData
                            :textData,
                        (int) TDevice.dpToPixel(textSize + 10)));
                text.setText(span);
                UIHelper.setTextSize2(14, text);
                final String content = tData;
                text.setOnLongClickListener(v -> {
                    //复制
                    showCopyDialog(content);
                    return true;
                });
                break;
            case 3:
                /*ui.viewStubCollect.getViewStub().setLayoutResource(R.layout.viewstub_big_ex);
                ui.viewStubCollect.getViewStub().inflate();
                final ImageView multiImageView = findViewById(R.id.multiImagView);
                final List<String> photos = new ArrayList<>();
                photos.add(tData);
                if (photos.size() > 0) {
                    GifFavContent favContent = JsonUtils.fromJson(tData, GifFavContent.class);
                    imgUrl = favContent.getBody();
                    Emojicon emojicon;
                    if (ChatEnvironment.getInstance().getEmojiconInfoProvider() != null) {
                        emojicon = ChatEnvironment.getInstance().getEmojiconInfoProvider()
                            .getEmojiconInfo(imgUrl);
                        if (emojicon != null) {
                            if (emojicon.getBigIcon() != 0) {
                                ImageHelper
                                    .loadImageOverrideSize(emojicon.getBigIcon(), multiImageView,
                                        DensityUtil.dip2px(ContextHelper.getContext(), 200),
                                        DensityUtil.dip2px(ContextHelper.getContext(), 120));
                            } else if (emojicon.getBigIconPath() != null) {
                                Glide.with(ContextHelper.getContext())
                                    .load(emojicon.getBigIconPath())
                                    .asGif()
                                    .placeholder(R.drawable.ease_default_expression)
                                    .into(multiImageView);
                            } else {
                                multiImageView.setImageResource(R.drawable.ease_default_expression);
                            }
                        } else {
                            ImageUploadEntity entity = ImageUploadEntity
                                .fromJson(imgUrl);
                            if (entity != null) {
                                Glide.with(ContextHelper.getContext()).load(entity.getOriginalUrl()).asGif().into(multiImageView);
                            } else {
                                multiImageView.setImageResource(R.drawable.ease_default_expression);
                            }
                        }
                    } else {
                        multiImageView.setVisibility(View.GONE);
                    }
                }*/
                ui.viewStubCollect.getViewStub().setLayoutResource(R.layout.viewstub_collect_voice);
                ui.viewStubCollect.getViewStub().inflate();
                RelativeLayout ll_root = findViewById(R.id.bubble);
                ImageView iv_voice = findViewById(R.id.iv_voice);
                TextView tv_length = findViewById(R.id.tv_length);

                VoiceFavContent voiceFavContent = new Gson().fromJson(tData, VoiceFavContent.class);
                tv_length.setText(voiceFavContent.getVoiceLenth());
                String voicePath = FileCache.getInstance().getVoicePath(voiceFavContent.getContent());
                ll_root.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isPlaying){
                            playVoice(TextUtils.isEmpty(voicePath)?voiceFavContent.getContent():voicePath,iv_voice);
                            }else {
                                stopPlayVoice();
                            }
                        }
                    });

                break;
            case 4:
                VideoFavContent videoFavContent = new Gson().fromJson(tData, VideoFavContent.class);
                ui.viewStubCollect.getViewStub().setLayoutResource(R.layout.viewstub_videobody);
                View rootView = ui.viewStubCollect.getViewStub().inflate();
                ImageView videothumbnial = rootView.findViewById(R.id.video_override);
                LinearLayout ll_loading = rootView.findViewById(R.id.ll_loading);
                FrameLayout layoutPlayer = rootView.findViewById(R.id.container_video_play);
                CircleProgress progressBar = rootView.findViewById(R.id.progress_bar);

                ll_loading.setVisibility(View.GONE);

                if (!ChatHelper.checkHttpUrl(videoFavContent.getVideoUrl())){
                    videoPath = videoFavContent.getVideoUrl();
                }else {
                    videoPath = FileCache.getInstance()
                        .getVideoPath(videoFavContent.getVideoUrl());
                }
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
                FavContent favContent = JsonUtils.fromJson(tData, FavContent.class);
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
                                position,"");
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

                    String cyContent = "";
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("content")){
                            cyContent = jsonObject.optString("content");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (!AuthorityManager.getInstance().copyPicOutsize()) {
                        T.showShort(CollectionDetailActivity.this, "没有相关权限");
                        return;
                    }
                    if (AuthorityManager.getInstance().copyOutside()) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(
                            Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText(null, cyContent));
                    } else {
                        if (AuthorityManager.getInstance().copyInside()) {
                            AuthorityManager.getInstance().copy(content);
                        } else {
                            SecureUtil.showToast("请申请权限");
                        }
                    }

                    String copyText = FileUtil.uploadUserOption(cyContent, UserInfoRepository.getUserId(), "a_copy_text");
                    HttpUtils.getInstance().uploadLogger(copyText,
                        ELogType.COPY, new IDataRequestListener() {
                            @Override
                            public void loadFailure(String reason) {
                                L.d("上传失败");
                            }

                            @Override
                            public void loadSuccess(Object object) {
                                L.d("上传成功");
                            }
                        });
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
            CollectionManager.getInstance().updateItemDES(msgId, "");
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = groupLabels.size(); i < len; i++) {
                if (i == len - 1) {
                    sb.append(groupLabels.get(i));
                } else {
                    sb.append(groupLabels.get(i) + SPLIT);
                }
            }
            CollectionManager.getInstance().updateItemDES(msgId, sb.toString().trim());
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

            AddFavoryRequestBody body = new AddFavoryRequestBody();
            body.setCreator(creator);
            body.setFromNickname(name);
            body.setMsgType(type);
            body.setFrom(provideId);
            body.setMsgContent(tData);
            body.setProvider(name);
            body.setMsgId(msgId);
            body.setTags(sb.toString());
            new CollectionApi().favoritesUpdate(body, new FXRxSubscriberHelper<BaseResponse>() {
                @Override
                public void _onNext(BaseResponse baseResponse) {
                    saveMyLabel();
                }
            });

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
                        BodyEntity entity = new BodyEntity(tData);
                        Intent intent = TransforMsgActivity
                            .newPureIntent(CollectionDetailActivity.this, /*BodyEntity.toJson(entity)*/getTransContent(tData),
                                Content.MSG_TYPE_PIC, 0, "");
                        CollectionDetailActivity.this.startActivity(intent);
                    }

                }).show();
    }

    public void playVoice(String filePath,ImageView imageView) {
        if (!(new File(filePath).exists())) {
            FileManager.getInstance()
                .downloadFile(filePath, EUploadFileType.VOICE, new IProgressListener() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            playVoice(filePath,imageView);
                        }

                        @Override
                        public void progress(int progress) {

                        }

                        @Override
                        public void onFailed() {
                            T.showShort(ContextHelper.getContext(), "下载失败");
                            imageView.setImageResource(com.lens.chatmodel.R.drawable.voice_error);
                        }
                    }
                );
            return;
        }

        final AudioManager audioManager = (AudioManager) this
            .getSystemService(Context.AUDIO_SERVICE);

        Observable.just(filePath)
            .map(new Function<String, String>() {
                @Override
                public String apply(@NonNull String s) throws Exception {
                    File file = FileCache.getInstance().decryptVoice(s);
                    return file.getPath();
                }
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(@NonNull final String path) throws Exception {
                    mediaPlayer = new MediaPlayer();
                    if (SettingsManager.chatsVoiceByOuter()) {//是否开启了扬声器播放语音
                        audioManager.setMode(AudioManager.MODE_NORMAL);
                        audioManager.setSpeakerphoneOn(true);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    } else {
                        audioManager.setSpeakerphoneOn(false);// 关闭扬声器
                        // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
                        audioManager.setMode(AudioManager.MODE_IN_CALL);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                    }
                    try {
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mediaPlayer.release();
                                mediaPlayer = null;
                                File f = new File(path);
                                if (f.exists()) {
                                    f.delete();
                                }
                                stopPlayVoice(); // stop animation
                            }

                        });
                        isPlaying = true;
                        mediaPlayer.start();
                        showAnimation(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    }
    private void showAnimation(ImageView voiceIconView) {
        // play voice, and start animation

            voiceAnimation = (AnimationDrawable) this.getResources()
                .getDrawable(com.lens.chatmodel.R.drawable.lens_voice_from);
        voiceIconView.setImageDrawable(voiceAnimation);
        voiceAnimation.start();
    }
    public void stopPlayVoice() {
        voiceAnimation.stop();
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
      /*  playMsgId = null;

        if (message.getPlayStatus() != EPlayType.PALYED) {
            message.setPlayStatus(EPlayType.PALYED);
            adapter.notifyDataSetChanged();
            ProviderChat.updatePlayStatus(activity, message.getMsgId(), EPlayType.PALYED);

        }*/
    }

    private String getTransContent(String tData){
        String data = "";
        try {
            JSONObject json = new JSONObject(tData);
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            if (null == json) {
                return data;
            }
            jsonObject.put("OriginalUrl",json.optString("OriginalUrl"));
            jsonObject.put("OriginalSzie",json.optString("OriginalSzie"));
            jsonObject.put("ThumbnailUrl",json.optString("ThumbnailUrl"));
            jsonObject.put("ThumbnailSize",json.optString("ThumbnailSize"));
            jsonObject1.put("body",jsonObject.toString());
            jsonObject1.put("secret",0);
            jsonObject1.put("bubbleWidth",0);
            jsonObject1.put("bubbleHeight",0);
           data = jsonObject1.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

}


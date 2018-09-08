package com.lensim.fingerchat.fingerchat.ui.me.photo;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.core.componet.log.DLog;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.CommonEnum;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.CirclesFriendsApi;
import com.lensim.fingerchat.fingerchat.databinding.ActivityVideoStatuBinding;
import com.lensim.fingerchat.fingerchat.model.requestbody.SendPhotosRequestBody;
import java.io.File;

/**
 * Created by ll147996 on 2017/12/15.
 * 录制视频
 */

public class VideoStatuActivity extends FGActivity implements TextureView.SurfaceTextureListener,
    MediaPlayer.OnCompletionListener {

    public static final String PATH = "path";
    public static final String PATH_IMAGE = "path_image";

    private MediaPlayer mediaPlayer;
    private String path;
    private String imagePath;
    private ActivityVideoStatuBinding ui;

    private String videoUrl;
    private String content;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_video_statu);
        ui.statuToolbar.setTitleText("发表状态");
        initBackButton(ui.statuToolbar, true);
        mediaPlayer = new MediaPlayer();
        initListener();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        path = getIntent().getStringExtra(PATH);
        imagePath = getIntent().getStringExtra(PATH_IMAGE);
        L.d("需要播放的视频:" + path);
        //先解密再播放
//        FileCache.getInstance().decrypt(path);
    }

    public void initListener() {
        ui.statuToolbar.setConfirmBt(v -> confirm());
        ui.mVideoStatuView.setSurfaceTextureListener(this);
//        ui.mVideoStatuView.setOnClickListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }


    @SuppressLint("CheckResult")
    protected void confirm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        showProgress("正在发送", false);
        content = ui.mVideoStatuText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            content = " ";
        }

        HttpUtils.getInstance()
            .uploadFileProgress(path, CommonEnum.EUploadFileType.VIDEO,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        DLog.d("上传成功");
                        if (result != null && result instanceof VideoUploadEntity) {
                            VideoUploadEntity entity = (VideoUploadEntity) result;
                            if (entity != null) {
                                videoUrl = entity.getVideoUrl();
                                uploadImage();
                            } else {
                                T.show("上传失败");
                            }
                        } else {
                            T.show("上传失败");
                        }
                    }

                    @Override
                    public void onFailed() {
                        T.show("上传失败");
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
    }


    @Override
    protected void onStop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(0, 0);
            // 设置需要播放的视频
            mediaPlayer.setDataSource(path);
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        prepare(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void uploadImage() {
        HttpUtils.getInstance()
            .uploadFileProgress(imagePath, CommonEnum.EUploadFileType.JPG,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        DLog.i("图片上传成功");
                        if (result != null && result instanceof ImageUploadEntity) {
                            ImageUploadEntity entity = (ImageUploadEntity) result;
                            sendPhotos(content, videoUrl, entity.getOriginalUrl());
                        } else {
                            T.show("上传失败");

                        }
                    }

                    @Override
                    public void onFailed() {
                        dismissProgress();
                        T.show("上传失败");
                    }

                    @Override
                    public void onProgress(int progress) {
                        DLog.d("上传进度" + progress);
                    }

                });
    }


    private void sendPhotos(String content, String videoUrl, String imageUrl) {
        SendPhotosRequestBody sendPhotosRequestBody
            = new SendPhotosRequestBody.Builder()
            .creatorUserId(UserInfoRepository.getUserId())
            .creatorUserName(CyptoUtils.encrypt(UserInfoRepository.getUserName()))
            .photoContent(CyptoUtils.encrypt(content))
            .photoFileNum(0)
            .photoFilenames(new File(path).getName())
            .photoUrl(videoUrl + "," + imageUrl)
            .build();
        new CirclesFriendsApi().sendPhoto(sendPhotosRequestBody, new FXRxSubscriberHelper<BaseResponse>() {
            @Override
            public void _onNext(BaseResponse baseResponse) {
                dismissProgress();
                FileUtil.deleteFileWithPath(path);
                T.show("发表成功");
                Intent intent = new Intent();
                intent.putExtra("statu_result", true);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                dismissProgress();
                FileUtil.deleteFileWithPath(path);
                T.show("发表失败");
                finish();
            }
        });
    }
}

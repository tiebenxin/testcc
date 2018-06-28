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
import com.lens.chatmodel.helper.FileCache;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityVideoStatuBinding;
import java.io.File;

/**
 * Created by ll147996 on 2017/12/15.
 * 录制视频
 */

public class VideoStatuActivity extends FGActivity implements TextureView.SurfaceTextureListener,
    MediaPlayer.OnCompletionListener {

    public static final String PATH = "path";

    private MediaPlayer mediaPlayer;
    private String path;
    private ActivityVideoStatuBinding ui;

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
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        showProgress("正在发送",false);
        String text = ui.mVideoStatuText.getText().toString();
        if(TextUtils.isEmpty(text)){
            text = " ";
        }

        //已经解密
        Http.sendVideoAndText(UserInfoRepository.getUserName(), text, new File(path))
            .compose(RxSchedulers.io_main())
            .subscribe(responseBody -> {
                    dismissProgress();
                    FileUtil.deleteFileWithPath(path);
                    T.show("发表成功");
                    Intent intent = new Intent();
                    intent.putExtra("statu_result",true);
                    setResult(RESULT_OK,intent);
                    finish();
                },
                throwable -> {
                    dismissProgress();
                    FileUtil.deleteFileWithPath(path);
                    T.show("发表失败");
                    finish();
                });
    }


    @Override
    protected void onStop() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(0,0);
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
}

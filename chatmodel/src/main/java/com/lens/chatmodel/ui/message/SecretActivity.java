package com.lens.chatmodel.ui.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.cache.FileManager;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.bean.body.VoiceUploadEntity;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.zhy.android.percent.support.PercentFrameLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by LY309313 on 2016/9/26.
 */

public class SecretActivity extends FGActivity implements TextureView.SurfaceTextureListener,
    OnClickListener {

    private AnimationDrawable voiceAnimation;
    private MediaPlayer mediaPlayer;
    private byte[] datas;
    private String url;
    private int type;
    private boolean isPlaying;
    private File temp;
    private FileDescriptor fd;
    private ArrayList<String> list;
    private String msgId;
    private FGToolbar toolbar;
    private TextView tv_text;
    private ImageView iv_voice;
    private ImageView iv_img;
    private PercentFrameLayout fl_video_root;
    private TextureView tv_video;
    private Button bt_retry;
    private ProgressBar progressBar;
    private String voicePath;

    @Override
    public void initView() {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_secret);
        loadView();
        mediaPlayer = new MediaPlayer();
    }

    private void loadView() {
        toolbar = findViewById(R.id.viewTitleBar);
        tv_text = findViewById(R.id.tv_content);
        iv_voice = findViewById(R.id.iv_voice);
        iv_img = findViewById(R.id.iv_img);
        fl_video_root = findViewById(R.id.fl_video_root);
        tv_video = findViewById(R.id.tv_video);
        bt_retry = findViewById(R.id.bt_retry);
        progressBar = findViewById(R.id.progress);

        toolbar.setTitleText("密信");
        initBackButton(toolbar, true);
        tv_video.setSurfaceTextureListener(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();

        String content = intent.getStringExtra("content");
        BodyEntity bodyEntity = new BodyEntity(content);
        if (TextUtils.isEmpty(bodyEntity.getBody())) {
            return;
        }
        L.i("SecretActivity", "传过来的内容为:" + content);
        type = intent.getIntExtra("type", -1);
        msgId = intent.getStringExtra("msgId");
        EMessageType e = EMessageType.fromInt(type);
        if (e != null) {
            switch (e) {
                case TEXT:
                    iv_img.setVisibility(View.GONE);
                    fl_video_root.setVisibility(View.GONE);
                    iv_voice.setVisibility(View.GONE);
                    tv_text.setVisibility(View.VISIBLE);
                    CharSequence smiledText = SmileUtils
                        .getSmiledText(this, bodyEntity.getBody(),
                            ((int) TDevice.dpToPixel(34)) + 10);
                    tv_text.setText(smiledText);
                    break;
                case IMAGE:
                    iv_img.setVisibility(View.VISIBLE);
                    fl_video_root.setVisibility(View.GONE);
                    iv_voice.setVisibility(View.GONE);
                    tv_text.setVisibility(View.GONE);
                    ImageUploadEntity imageUploadEntity = ImageUploadEntity
                        .fromJson(bodyEntity.getBody());
                    if (imageUploadEntity == null) {
                        return;
                    }
                    Glide.with(this)
                        .load(imageUploadEntity.getOriginalUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ease_default_image)
                        .error(R.drawable.default_error)
                        .into(iv_img);

                    iv_img.setOnClickListener(v -> {
                        AnimationRect rect = AnimationRect.buildFromImageView(iv_img);
                        if (rect != null) {
                            rect.setMsgId(msgId);

                            ArrayList<String> list = new ArrayList<>();
                            ArrayList<AnimationRect> animlist = new ArrayList<>();
                            animlist.add(rect);
                            list.add(url);
                            final ArrayList<String> msgIds = new ArrayList<>();
                            if (!TextUtils.isEmpty(msgId)) {
                                msgIds.add(msgId);
                            }
                            Intent intent1 = GalleryAnimationActivity
                                .newIntent(list, msgIds, animlist, null, 0);
                            startActivity(intent1);
                        } else {
                            T.showShort(SecretActivity.this, "图片异常");
                        }

                    });
                    break;
                case VIDEO:
                    iv_img.setVisibility(View.GONE);
                    fl_video_root.setVisibility(View.GONE);
                    iv_voice.setVisibility(View.GONE);
                    tv_text.setVisibility(View.GONE);
                    showProgress("正在加载视频..", false);
                    VideoUploadEntity videoEntity = VideoUploadEntity
                        .fromJson(bodyEntity.getBody());
                    if (videoEntity == null) {
                        return;
                    }
                    FileManager.getInstance().downloadFile(videoEntity.getVideoUrl(),
                        EUploadFileType.VIDEO, new IProgressListener() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                dismissProgress();
                                datas = bytes;
                                if (fd == null) {
                                    fd = getFd("fingerchatvideotemp", "mp4", bytes);
                                }
                                fl_video_root.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void progress(int progress) {

                            }

                            @Override
                            public void onFailed() {
                                dismissProgressDelay(1000);
                                resetProgressText("下载失败");
                                bt_retry.setVisibility(View.VISIBLE);
                            }

                        }

                    );
                    break;
                case VOICE:
                    iv_img.setVisibility(View.GONE);
                    fl_video_root.setVisibility(View.GONE);
                    iv_voice.setVisibility(View.GONE);
                    tv_text.setVisibility(View.GONE);
                    showProgress("正在下载语音", false);
                    if (bodyEntity == null || TextUtils.isEmpty(bodyEntity.getBody())) {
                        return;
                    }
                    String[] split = bodyEntity.getBody().split("@");
                    String Url = split[0];
                    L.d("音频下载路径", Url);
                    FileManager.getInstance()
                        .downloadFile(Url, EUploadFileType.VOICE, new IProgressListener() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    dismissProgress();
                                    iv_voice.setVisibility(View.VISIBLE);
//                                    datas = bytes;
                                    playVoice(Url);
                                }

                                @Override
                                public void progress(int progress) {

                                }

                                @Override
                                public void onFailed() {
                                    dismissProgressDelay(1000);
                                    resetProgressText("下载失败");
                                    iv_voice.setImageResource(R.drawable.voice_error);
                                }
                            }
                        );
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (temp != null && temp.exists()) {
            temp.delete();
        }
    }

    public void initListener() {
        iv_voice.setOnClickListener(this);
        bt_retry.setOnClickListener(this);
    }


    public void stopPlayVoice() {
        voiceAnimation.stop();
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
    }

    public void playVoice(String url) {
//        if (fd == null) {
//            fd = getFd("fingerchatvoicetemp", "mp3", bytes);
//        }
        getVoicePath(url);
        if (TextUtils.isEmpty(voicePath)) {
            return;
        }

        Observable.just(voicePath)
            .map(new Function<String, String>() {
                @Override
                public String apply(@NonNull String s) throws Exception {
                    File file = FileCache.getInstance().decryptVoice(s);//解密
                    return file.getPath();
                }
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(@NonNull final String path) throws Exception {
                    mediaPlayer = new MediaPlayer();
                    AudioManager audioManager = (AudioManager) getSystemService(
                        Context.AUDIO_SERVICE);

                    if (SettingsManager.chatsVoiceByOuter()) {
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
                        showAnimation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    // show the voice playing animation
    @SuppressWarnings("deprecation")
    private void showAnimation() {
        // play voice, and start animation

        voiceAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.vioce_play);
        iv_voice.setImageDrawable(voiceAnimation);
        voiceAnimation.start();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            mediaPlayer.setDataSource(fd);
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (Exception e) {
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        prepare(new Surface(surfaceTexture));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    private FileDescriptor getFd(String prefix, String suffix, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        FileInputStream fis = null;
        try {
            if (temp == null) {
                temp = File.createTempFile(prefix, suffix, getCacheDir());
                temp.deleteOnExit();

                FileOutputStream fos = new FileOutputStream(temp);
                fos.write(bytes);
                fos.close();
            }

            fis = new FileInputStream(temp);
            return fis.getFD();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onStop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onStop();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_retry) {
            initData(null);

        } else if (i == R.id.iv_voice) {
            if (datas == null || datas.length == 0) {
                initData(null);
            } else {
                if (!isPlaying) {
                    // playVoice(datas);
                }
            }

        }
    }

    private void getVoicePath(String url) {
        if (!TextUtils.isEmpty(url)) {
            File file = new File(url);
            if (file.exists()) {
                voicePath = url.split("@")[0];
            } else {
                this.voicePath = FileCache.getInstance().getVoicePath(url.split("@")[0]);
            }
        }
    }
}

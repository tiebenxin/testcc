package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.transfor.VoiceBody;
import com.lens.chatmodel.cache.FileManager;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;

/**
 * Created by LL130386 on 2018/2/1.
 */

public class MultiCellVoice extends MultiCellBase {

    private ImageView iv_voice;
    private LinearLayout ll_voice;
    private TextView tv_length;
    private VoiceBody body;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private AnimationDrawable voiceAnimation;
    private String voicePath;
    private boolean isLocalPath;
    private boolean hasDownloaded;

    protected MultiCellVoice(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super(context, cellLayout, listener);
        loadControls();
    }

    private void loadControls() {
        iv_voice = getView().findViewById(R.id.iv_voice);
        tv_length = getView().findViewById(R.id.tv_length);
        ll_voice = getView().findViewById(R.id.ll_voice);

        ll_voice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    playVoice();
                }
            }
        });
    }

    public void playVoice() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        AudioManager audioManager = (AudioManager) ContextHelper.getContext()
            .getSystemService(Context.AUDIO_SERVICE);
        Observable.just(voicePath)
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


    private void getVoicePath(String url) {
        if (!TextUtils.isEmpty(url)) {
            File file = new File(url);
            if (file.exists()) {
                voicePath = url.split("@")[0];
                isLocalPath = true;
            } else {
                this.voicePath = FileCache.getInstance().getVoicePath(url.split("@")[0]);
                isLocalPath = false;
            }
        }
    }

    public void stopPlayVoice() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;

    }

    private void showAnimation() {
        // play voice, and start animation
        voiceAnimation = (AnimationDrawable) ContextHelper.getContext().getResources()
            .getDrawable(R.drawable.lens_voice_from);
        iv_voice.setImageDrawable(voiceAnimation);
        voiceAnimation.start();
    }

    @Override
    public void showData() {
        super.showData();
        if (mEntity != null) {
            if (!TextUtils.isEmpty(mEntity.getBody())) {
                body = GsonHelper.getObject(mEntity.getBody(), VoiceBody.class);
                if (body != null) {
                    tv_length.setText(
                        ChatHelper.getTimeLength(body.getTimeLength(), EMessageType.VOICE));
                    getVoicePath(body.getBody());

                    if (!isLocalPath && !hasDownloaded) {
                        downloadVoice();
                    }
                }
            }
        }
    }


    private void downloadVoice() {
        FileManager.getInstance()
            .downloadFile(body.getBody(), EUploadFileType.VOICE, new IProgressListener() {
                @Override
                public void onSuccess(byte[] bytes) {
                    hasDownloaded = true;
                    getVoicePath(body.getBody());
                }

                @Override
                public void progress(int progress) {
                }

                @Override
                public void onFailed() {
                    hasDownloaded = false;
                }
            });
    }

}

package com.lens.chatmodel.controller.cell;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.ChatEnum.ESendType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.T;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;


/**
 * 语音row播放点击事件监听
 */
public class VoicePlayClickListener implements View.OnClickListener {

    private static final String TAG = "VoicePlayClickListener";
    IChatRoomModel message;
    ImageView voiceIconView;
    String voicePath;
    private AnimationDrawable voiceAnimation;
    MediaPlayer mediaPlayer = null;
    ImageView iv_read_status;
    Context activity;

    private BaseAdapter adapter;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;
    public static String playMsgId;

    public VoicePlayClickListener(IChatRoomModel message, ImageView v,
        ImageView iv_read_status,
        BaseAdapter adapter, Context context) {
        this.message = message;
        getVoicePath(message);
        L.i("文件播放路径:" + voicePath);
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        voiceIconView = v;
        this.activity = context;
    }

    private void getVoicePath(IChatRoomModel model) {
        String uri = model.getContent();
        if (!message.isIncoming() && !checkHttpUrl(uri)) {
            this.voicePath = uri.split("@")[0];
        } else {
            this.voicePath = FileCache.getInstance().getVoicePath(uri.split("@")[0]);
        }
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        playMsgId = null;

        if (message.getPlayStatus() != EPlayType.PALYED) {
            message.setPlayStatus(EPlayType.PALYED);
            adapter.notifyDataSetChanged();
            ProviderChat.updatePlayStatus(activity, message.getMsgId(), EPlayType.PALYED);

        }
    }

    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            T.showShort(ContextHelper.getContext(), "文件不存在或已被删除");
            return;
        }

        final AudioManager audioManager = (AudioManager) activity
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
                        currentPlayListener = VoicePlayClickListener.this;
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
        if (!message.isIncoming()) {
            voiceAnimation = (AnimationDrawable) activity.getResources()
                .getDrawable(R.drawable.lens_voice_to);
        } else {
            voiceAnimation = (AnimationDrawable) activity.getResources()
                .getDrawable(R.drawable.lens_voice_from);
        }
        voiceIconView.setImageDrawable(voiceAnimation);
        voiceAnimation.start();
    }


    @Override
    public void onClick(View v) {
        String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
        if (isPlaying) {
            if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }

        if (!message.isIncoming()) {
            // for sent msg, we will try to play the voice file directly
            L.d("播放路径", voicePath);
            playVoice(voicePath);

        } else {
            if (!TextUtils.isEmpty(message.getContent())) {
                File file = new File(voicePath);
                if (file.exists() && file.isFile()) {
                    playVoice(voicePath);
                } else {
                    T.showShort(ContextHelper.getContext(), "文件不存在或已被删除");
                    L.e(TAG, "file not exist");
                }


            } else if (message.getSendType() != ESendType.MSG_SUCCESS) {
                Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //检查是否是网络路径
    private boolean checkHttpUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("http") || url.contains("https")) {
                return true;
            }
        }
        return false;
    }
}

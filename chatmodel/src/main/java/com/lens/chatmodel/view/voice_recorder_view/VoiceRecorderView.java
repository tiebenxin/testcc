package com.lens.chatmodel.view.voice_recorder_view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.lens.chatmodel.R;
import com.lens.chatmodel.interf.ChatRowVoicePlayClickListener;


/**
 * 按住说话录制控件
 */
@SuppressWarnings("ALL")
public class VoiceRecorderView extends RelativeLayout {

    private static final int MIN_SECONDS = 1;

    protected Context context;
    protected LayoutInflater inflater;
    protected Drawable[] micImages;
    protected VoiceRecorder voiceRecorder;

    protected PowerManager.WakeLock wakeLock;
    protected ImageView micImage;
    protected TextView recordingHint;
    EaseVoiceRecorderCallback callback;

    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            if (msg.what >= 14) {
                return;
            }
            micImage.setImageDrawable(micImages[msg.what]);
            if (callback != null) {//正在录音回调
                callback.onVoiceRecording();
            }
        }
    };

    public VoiceRecorderView(Context context) {
        super(context);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.lens_widget_voice_recorder, this);

        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);

        voiceRecorder = new VoiceRecorder(micImageHandler);

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{

            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_01),

            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_01),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_02),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_03),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_04),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_05),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_06),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_07),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_08),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_09),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_10),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_11),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_12),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_13),
            ContextCompat.getDrawable(context, R.drawable.ease_record_animate_14),
        };

        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK, "lens");
    }

    /**
     * 长按说话按钮touch事件
     */
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event,
        EaseVoiceRecorderCallback recorderCallback) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    if (ChatRowVoicePlayClickListener.isPlaying) {
                        ChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
                    }
                    v.setPressed(true);
                    startRecording();
                    callback = recorderCallback;
                } catch (Exception e) {
                    v.setPressed(false);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    showReleaseToCancelHint();
                } else {
                    showMoveUpToCancelHint();
                }
                return true;
            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                if (event.getY() < 0) {
                    // discard the recorded audio.
                    discardRecording();
                } else {
                    // stop recording and send voice file
                    try {
                        int length = stopRecoding();
                        if (length >= MIN_SECONDS) {
                            if (recorderCallback != null) {
                                recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                            }
                        } else if (length == -1) {
                            Toast.makeText(context, R.string.The_recording_time_is_too_short,
                                Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, R.string.The_recording_time_is_too_short,
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            default:
                discardRecording();
                return false;
        }
    }

    public interface EaseVoiceRecorderCallback {

        /**
         * 录音完毕
         *
         * @param voiceFilePath 录音完毕后的文件路径
         * @param voiceTimeLength 录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);

        void onVoiceRecording();
    }

    public void startRecording() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT)
                .show();
            return;
        }
        try {
            wakeLock.acquire();
            this.setVisibility(View.VISIBLE);
            recordingHint.setText(context.getString(R.string.move_up_to_cancel));
            recordingHint.setBackgroundColor(Color.TRANSPARENT);
            voiceRecorder.startRecording(context);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
            if (voiceRecorder != null) {
                voiceRecorder.discardRecording();
            }
            this.setVisibility(View.INVISIBLE);
            Toast.makeText(context, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void showReleaseToCancelHint() {
        recordingHint.setText(context.getString(R.string.release_to_cancel));
        recordingHint.setBackgroundResource(R.drawable.ease_recording_text_hint_bg);
    }

    public void showMoveUpToCancelHint() {
        recordingHint.setText(context.getString(R.string.move_up_to_cancel));
        recordingHint.setBackgroundColor(Color.TRANSPARENT);
    }

    public void discardRecording() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                this.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    public int stopRecoding() {
        this.setVisibility(View.INVISIBLE);
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        return voiceRecorder.stopRecoding();
    }

    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
    }

    public String getVoiceFileName() {
        return voiceRecorder.getVoiceFileName();
    }

    public boolean isRecording() {
        return voiceRecorder.isRecording();
    }

}

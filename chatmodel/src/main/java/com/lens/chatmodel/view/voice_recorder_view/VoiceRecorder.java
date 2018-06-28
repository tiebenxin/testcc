package com.lens.chatmodel.view.voice_recorder_view;


import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

import com.czt.mp3recorder.MP3Recorder;
import com.lens.chatmodel.helper.FileCache;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.io.File;
import java.io.IOException;
import java.util.Date;


public class VoiceRecorder {

    MP3Recorder recorder;

    static final String PREFIX = "voice";
    //static final String EXTENSION = ".mp3";

    private long startTime;
    private String voiceFilePath = null;
    private String voiceFileName = null;
    private File file;
    private Handler handler;

    public VoiceRecorder(Handler handler) {
        this.handler = handler;
    }

    /**
     * start recording to the file
     */
    public String startRecording(Context appContext) {
        file = null;
        try {
            // need to create recorder every time, otherwise, will got exception
            // from setOutputFile when try to reuse
            if (recorder != null && recorder.isRecording()) {
                recorder.stop();
                recorder = null;
            }

            voiceFileName = getVoiceFileName(TDevice.getUdid());
            //声音文件存放路径：/sdcard/lensim/用户名/voice/send
            File voiceFileFolder = FileUtil.getDiskCacheDirs(ContextHelper.getContext(), PREFIX);
            if (!voiceFileFolder.exists()) {
                voiceFileFolder.mkdirs();
            }
            file = new File(voiceFileFolder, voiceFileName);
            voiceFilePath = file.getPath();
            L.d("录音文件存放路径" + voiceFilePath);
            //  file = new File(voiceFilePath);
            recorder = new MP3Recorder(file);
            recorder.start();
        } catch (IOException e) {
            L.e("voice" + "prepare() failed");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (recorder.isRecording()) {

                        android.os.Message msg = handler.obtainMessage();
                        msg.what = recorder.getRealVolume() * 13 / 18000;
                        if (msg.what > 14) {
                            msg.what = 14;
                        }
                        handler.sendMessage(msg);
                        SystemClock.sleep(100);
                    }
                } catch (Exception e) {
                    // from the crash report website, found one NPE crash from
                    // one android 4.0.4 htc phone
                    // maybe handler is null for some reason
                    L.e("voice" + e.toString());
                }
            }
        }).start();
        startTime = new Date().getTime();
        L.d("voice" + "start voice recording to file:" + file.getAbsolutePath());
        return file == null ? null : file.getAbsolutePath();
    }

    /**
     * stop the recoding
     *
     * @return seconds of the voice recorded
     */

    public void discardRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder = null;
                if (file != null && file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            } catch (IllegalStateException e) {
            } catch (RuntimeException e) {
            }

        }
    }

    public int stopRecoding() {
        if (recorder != null) {
            recorder.stop();
            recorder = null;

            if (file == null || !file.exists() || !file.isFile()) {
                return -1;
            }
            if (file.length() == 0) {
                file.delete();
                return -1;
            }
            FileCache.getInstance().encryptVoice(file.getAbsolutePath());
            int seconds = (int) (new Date().getTime() - startTime) / 1000;
//            int seconds = (int) (new Date().getTime() - startTime);
            L.d("voice" +
                "voice recording finished. seconds:" + seconds + " file length:" + file.length());
            return seconds;
        }
        return 0;
    }


    private String getVoiceFileName(String uid) {
        // return uid + EXTENSION;
        return uid;
    }

    public boolean isRecording() {
        return recorder.isRecording();
    }


    public String getVoiceFilePath() {
        return voiceFilePath;
    }

    public String getVoiceFileName() {
        return voiceFileName;
    }
}

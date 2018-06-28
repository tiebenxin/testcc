package com.lensim.fingerchat.hexmeet.activity;

import static android.media.AudioManager.STREAM_MUSIC;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import com.lensim.fingerchat.hexmeet.utils.CallRecordManager;
import com.lensim.fingerchat.hexmeet.utils.Convertor;
import com.lensim.fingerchat.hexmeet.utils.DialOutRetryHandler;
import com.lensim.fingerchat.hexmeet.utils.SoundPlayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConnectActivity extends FullscreenActivity {

    private ImageView background;
    private ImageView background_wrapper;
    private View endcall_btn;
    private ImageView conn_to_avatar;
    private TextView conn_to_text;
    private TextView endcall_txt;

    boolean isVideoCall = true;
    private String sipNumber;
    boolean isFromDialing = false;
    private DialOutRetryHandler.DialOutRetryListener mRetryListener;
    private MediaPlayer mRingerPlayer;
    private String mRingSoundFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Bundle bundle = this.getIntent().getExtras();
            isVideoCall = bundle.getBoolean("isVideoCall", false);
            sipNumber = bundle.getString("sipNumber");
            isFromDialing = bundle.getBoolean("isFromDialing", false);
            mRingSoundFile = getFilesDir().getAbsolutePath() + "/ringtone.wav";
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_connect);

        background = (ImageView) findViewById(R.id.background);
        background_wrapper = (ImageView) findViewById(R.id.background_wrapper);
        endcall_btn = findViewById(R.id.endcall_btn);
        conn_to_avatar = (ImageView) findViewById(R.id.conn_to_avatar);
        conn_to_text = (TextView) findViewById(R.id.conn_to_text);
        endcall_txt = (TextView) findViewById(R.id.endcall_txt);

        RestContact callTo = App.getContact(sipNumber);
        if (callTo != null) {
            String imageUrl = Convertor.getAvatarUrl(callTo);
            findViewById(R.id.bkg_avatar_circle).setVisibility(View.VISIBLE);
            conn_to_avatar.setVisibility(View.VISIBLE);
            conn_to_text.setText(callTo.getName());
            AvatarLoader.load(imageUrl, conn_to_avatar, ConnectActivity.this, new Runnable() {
                @Override
                public void run() {
                    background.setImageDrawable(conn_to_avatar.getDrawable());
                }
            });
        } else {
            conn_to_avatar.setVisibility(View.GONE);
            findViewById(R.id.bkg_avatar_circle).setVisibility(View.GONE);
            RestMeeting meeting = App.getMeeting(sipNumber);
            conn_to_text.setText(meeting != null ? meeting.getName() : sipNumber);
            background_wrapper.setVisibility(View.INVISIBLE);
            conn_to_text.setTextColor(Color.parseColor("#919191"));
            endcall_txt.setTextColor(Color.parseColor("#919191"));
        }

        startRinging();

        setupEvent();

        mRetryListener = new DialOutRetryHandler.DialOutRetryListener() {
            @Override
            public void callRetryState(DialOutRetryHandler.RetryState state) {
                if (state == DialOutRetryHandler.RetryState.E_RETRY_SUCCEED) {
                    ConnectActivity.this.finish();
                } else if (state == DialOutRetryHandler.RetryState.E_RETRY_FAILED) {
                    if (isFromDialing || sipNumber.length() <= 4) {
                        RestCallRow_ call = new RestCallRow_();
                        call.setIsVideoCall(isVideoCall);
                        call.setPeerSipNum(sipNumber);
                        call.setIsOutgoing(true);
                        call.setStartTime(System.currentTimeMillis());
                        call.setDuration(0L);
                        CallRecordManager.insert(call);
                    }
                    ConnectActivity.this.finish();
                }
            }
        };
        DialOutRetryHandler.addListener(mRetryListener);
    }

    @Override
    protected void onDestroy() {
        DialOutRetryHandler.removeListener(mRetryListener);
        stopRinging();
        super.onDestroy();
    }

    private void startRinging() {
        try {
            copyIfNotExist(R.raw.ringtone, mRingSoundFile);
            if (mRingerPlayer == null) {
                mRingerPlayer = new MediaPlayer();
                mRingerPlayer.setAudioStreamType(STREAM_MUSIC);
                try {
                    String ringtone = mRingSoundFile;
                    if (ringtone.startsWith("content://")) {
                        mRingerPlayer.setDataSource(this, Uri.parse(ringtone));
                    } else {
                        FileInputStream fis = new FileInputStream(ringtone);
                        mRingerPlayer.setDataSource(fis.getFD());
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mRingerPlayer.prepare();
                mRingerPlayer.setLooping(true);
                mRingerPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRinging() {
        if (mRingerPlayer != null) {
            mRingerPlayer.stop();
            mRingerPlayer.release();
            mRingerPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        SoundPlayer.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void setupEvent() {
        endcall_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ConnectActivity.this.endCall();
            }
        });
    }

    protected void endCall() {
        DialOutRetryHandler.getInstance().cancel();
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            endCall();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void copyIfNotExist(int ressourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(ressourceId, lFileToCopy.getName());
        }
    }

    public void copyFromPackage(int ressourceId, String target) throws IOException {
        FileOutputStream lOutputStream = this.openFileOutput(target, 0);
        InputStream lInputStream = getResources().openRawResource(ressourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }

}

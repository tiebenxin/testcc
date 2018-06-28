package com.lensim.fingerchat.hexmeet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hexmeet.sdk.HexmeetCallState;
import com.hexmeet.sdk.HexmeetReason;
import com.hexmeet.sdk.HexmeetRegistrationState;
import com.hexmeet.sdk.HexmeetSdkListener;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import com.lensim.fingerchat.hexmeet.utils.CallRecordManager;
import com.lensim.fingerchat.hexmeet.utils.Convertor;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import com.lensim.fingerchat.hexmeet.utils.SoundPlayer;

public class DialInNotifyActivity extends Activity {

    private String mCallUri;
    private String mCallNum;
    private HexmeetSdkListener mListener;
    private boolean isVideoCall = true;
    private boolean accepted = false;
    private boolean rejected = false;
    public static boolean showNewMissedCallFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow()
            .addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setFinishOnTouchOutside(false);
        SoundPlayer.stop();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScreenUtil.initStatusBar(this);
        this.setContentView(R.layout.activity_dial_in_notify);

        accepted = false;
        rejected = false;

        Bundle bundle = this.getIntent().getExtras();

        try {
            mCallUri = bundle.getString("callName");
        } catch (Exception e) {
            mCallUri = "";
            e.printStackTrace();

        }

        try {
            mCallNum = bundle.getString("callNum");
        } catch (Exception e) {
            mCallNum = "";
        }

        TextView hint = (TextView) findViewById(R.id.hint);
        hint.setText(mCallUri + getString(R.string.invite_you_to_call));

        RestContact caller = App.getContact(mCallNum);
        if (caller != null) {
            ImageView avatar = (ImageView) findViewById(R.id.avatar);
            String imageUrl = Convertor.getAvatarUrl(caller);
            AvatarLoader.load(imageUrl, avatar);
        }

        LinearLayout background = (LinearLayout) findViewById(R.id.background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accepted = true;
                Intent intent = new Intent(App.getContext(), HexMeetListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(intent);
            }

        });

        Button reject = (Button) findViewById(R.id.reject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejected = true;
                if (mCallNum.length() <= 4) {
                    RestCallRow_ call = new RestCallRow_();
                    call.setIsVideoCall(isVideoCall);
                    call.setPeerSipNum(mCallNum);
                    call.setIsOutgoing(false);
                    call.setStartTime(System.currentTimeMillis());
                    call.setDuration(1L);
                    CallRecordManager.insert(call);
                }
                App.getHexmeetSdkInstance().hangupCall();
                finish();
            }
        });

        mListener = new HexmeetSdkListener() {
            @Override
            public void globalState() {

            }

            @Override
            public void registrationState(HexmeetRegistrationState hexmeetRegistrationState) {

            }

            @Override
            public void callState(HexmeetCallState state, HexmeetReason reason) {
                if (!App.getHexmeetSdkInstance().hasOngoingCall()) {
                    finish();
                    return;
                }
            }
        };
        App.getHexmeetSdkInstance().addHexmeetSdkListener(mListener);
        isVideoCall = App.getHexmeetSdkInstance().isCurrentCallVideoEnabled();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("DialInNotifyActivity", "onKeyDown for KeyEvent.KEYCODE_BACK. Ignore it.");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getHexmeetSdkInstance().removeHexmeetSdkListener(mListener);

        // finished by far end
        if (!accepted && !rejected) {
            if (mCallNum.length() <= 4) {
                RestCallRow_ call = new RestCallRow_();
                call.setIsVideoCall(isVideoCall);
                call.setPeerSipNum(mCallNum);
                call.setIsOutgoing(false);
                call.setStartTime(System.currentTimeMillis());
                call.setDuration(0L);
                CallRecordManager.insert(call);
                showNewMissedCallFlag = true;
            }
        }
    }
}

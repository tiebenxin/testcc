package com.lensim.fingerchat.hexmeet.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hexmeet.sdk.HexmeetAudioRouteEventType;
import com.hexmeet.sdk.HexmeetAudioRouteType;
import com.hexmeet.sdk.HexmeetCallState;
import com.hexmeet.sdk.HexmeetCameraOperationType;
import com.hexmeet.sdk.HexmeetReason;
import com.hexmeet.sdk.HexmeetRegistrationState;
import com.hexmeet.sdk.HexmeetSdkListener;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestParticipant;
import com.lensim.fingerchat.hexmeet.app.ConferenceSimplifiedStat;
import com.lensim.fingerchat.hexmeet.app.FloatingService;
import com.lensim.fingerchat.hexmeet.conf.ConferenceParticipantList;
import com.lensim.fingerchat.hexmeet.conf.ConferenceParticipantList.ParticipantCountScanner;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import com.lensim.fingerchat.hexmeet.utils.CallRecordManager;
import com.lensim.fingerchat.hexmeet.utils.Convertor;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import com.lensim.fingerchat.hexmeet.widget.HangupPopupMenu;
import com.lensim.fingerchat.hexmeet.widget.MenuItem;
import com.lensim.fingerchat.hexmeet.widget.VideoBoxGroup;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class Conversation extends FullScreenWithAECActivity {

    private Context context;

    // this surface is used to capture image from camera
    private SurfaceView mDummyPreviewView;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;
    private SurfaceView mContentView;

    private long starttime;

    static final int FAR_AWAY = 2000;
    private boolean isVideoCall = true;

    private RelativeLayout rootLayout;
    private ImageButton endcall_btn;
    private ImageButton call_statistics_btn;
    private TextView participant_count;

    private View layout_video_mute;
    private ImageButton video_mute_btn;

    private View layout_camera_switch;
    private ImageButton camera_switch_btn;

    private ImageButton mic_mute_btn;

    private View layout_speaker_earphone;
    private ImageView speaker_earphone;
    private View audio_only_use_earphone;
    private TextView audio_only_use_earphone_text;
    private View mute_on_prompt;

    private ImageButton local_float_button;
    private View local_toolbar_button;
    private ImageView local_btn_img;

    private ImageButton remote_float_button;
    private View remote_toolbar_button;
    private ImageView remote_btn_img;

    private View toolbar;
    private View toolbar_top;
    private View toolbar_bottom;
    private Chronometer timer_chronometer;

    private LinearLayout participants_btn;
    private ImageView audio_only_pic;

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private SVCGestureListener mGestureListener = new SVCGestureListener();

    private boolean isLocalVideoMuted = false;
    private boolean isSpeakerOn = true;

    boolean mIsCallConnected = false;
    private SignalIntensityScanner signalIntensityScanner;
    private ParticipantCountScanner participantCountScanner;
    private Handler contentCheckHandler;

    private HexmeetSdkListener mListener;

    public static boolean m_isForground = false;

    private OrientationEventListener orientationListener;
    private boolean isCallRecordSaved = false;

    private String peerSipNumber = "";
    private String peerSipNumberWithoutPassword = "";

    private HandlerThread handlerThread = new HandlerThread("conversation_worker");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsCallConnected = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        context = this;

        try {
            Bundle bundle = this.getIntent().getExtras();
            isVideoCall = bundle.getBoolean("isVideoCall");
            starttime = bundle.getLong("starttime");

            if (App.getIncomingCallRecord() != null && !App.getIncomingCallRecord().getIsOutgoing()) {
                CallRecordManager.delete(App.getIncomingCallRecord());
                App.setIncomingCallRecord(null);
            }
        } catch (Exception e) {
        }

        initAudioMode();

        audio_only_use_earphone = findViewById(R.id.audio_only_use_earphone);
        audio_only_use_earphone_text = (TextView) findViewById(R.id.audio_only_use_earphone_text);
        mute_on_prompt = findViewById(R.id.mute_on_prompt);

        mic_mute_btn = (ImageButton) findViewById(R.id.mic_mute_btn);
        boolean isMicphoneMuted = App.getHexmeetSdkInstance().isMicMuted();
        mic_mute_btn.setImageResource(isMicphoneMuted ? R.drawable.btn_mute : R.drawable.btn_unmute);
        mute_on_prompt.setVisibility(isMicphoneMuted ? View.VISIBLE : View.GONE);

        layout_speaker_earphone = findViewById(R.id.layout_speaker_earphone);
        speaker_earphone = (ImageButton) findViewById(R.id.speaker_earphone);

        local_float_button = (ImageButton) findViewById(R.id.local_float_button);
        local_toolbar_button = findViewById(R.id.local_button);
        local_btn_img = (ImageView) findViewById(R.id.local_btn_img);

        remote_float_button = (ImageButton) findViewById(R.id.remote_float_button);
        remote_toolbar_button = findViewById(R.id.remote_button);
        remote_btn_img = (ImageView) findViewById(R.id.remote_btn_img);

        rootLayout = (RelativeLayout) findViewById(R.id.root);
        endcall_btn = (ImageButton) findViewById(R.id.endcall_btn);
        call_statistics_btn = (ImageButton) findViewById(R.id.call_statistics_btn);
        participant_count = (TextView) findViewById(R.id.participant_count);
        camera_switch_btn = (ImageButton) findViewById(R.id.camera_switch_btn);

        video_mute_btn = (ImageButton) findViewById(R.id.video_mute_btn);
        isLocalVideoMuted = App.getHexmeetSdkInstance().isLocalVideoMuted();
        video_mute_btn.setImageResource(isLocalVideoMuted ? R.drawable.btn_camera_off
            : R.drawable.btn_camera_on);

        participants_btn = (LinearLayout) findViewById(R.id.participants_btn);

        layout_video_mute = findViewById(R.id.layout_video_mute);
        layout_camera_switch = findViewById(R.id.layout_camera_switch);

        toolbar = findViewById(R.id.include1);
        toolbar_top = findViewById(R.id.include2);
        toolbar_bottom = findViewById(R.id.include3);

        toolbar.setVisibility(isVideoCall ? View.INVISIBLE : View.VISIBLE);
        toolbar_top.setVisibility(isVideoCall ? View.INVISIBLE : View.VISIBLE);
        toolbar_bottom.setVisibility(isVideoCall ? View.INVISIBLE : View.VISIBLE);
        local_float_button.setVisibility(View.INVISIBLE);
        remote_float_button.setVisibility(View.INVISIBLE);

        layout_speaker_earphone.setVisibility(isVideoCall ? View.GONE : View.VISIBLE);

        audio_only_pic = (ImageView) findViewById(R.id.audio_only_pic);

        setupEvent();

        mGestureDetector = new GestureDetector(this, mGestureListener);
        mGestureDetector.setIsLongpressEnabled(true);
        mGestureDetector.setOnDoubleTapListener(mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(this, mGestureListener);

        timer_chronometer = (Chronometer) findViewById(isVideoCall ? R.id.timer_chrono : R.id.timer_chronometer);
        timer_chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
                long time = SystemClock.elapsedRealtime() - ch.getBase();
                long hour = time / 3600000;
                long m = time % 3600000 / 60000;
                long s = time % 60000 / 1000;
                ch.setText(String.format("%02d:%02d:%02d", hour, m, s));
            }
        });
        timer_chronometer.setBase(starttime);
        timer_chronometer.start();

        RestContact callTo = null;
        TextView call_title = (TextView) findViewById(R.id.call_title);
        if (App.getHexmeetSdkInstance().hasOngoingCall()) {
            peerSipNumber = App.getHexmeetSdkInstance().getFarendInfo().getCallNumber();
            if (peerSipNumber != null) {
                peerSipNumberWithoutPassword = peerSipNumber;
                if (peerSipNumber.contains("*")) {
                    peerSipNumberWithoutPassword = peerSipNumber.split("\\*")[0];
                }
            }
            callTo = App.getContact(peerSipNumberWithoutPassword);
            if (callTo != null) {
                call_title.setText(callTo.getName());
            } else {
                RestMeeting meeting = App.getMeeting(peerSipNumberWithoutPassword);
                if (meeting != null) {
                    call_title.setText(meeting.getName());
                } else {
                    call_title.setText(peerSipNumberWithoutPassword);
                }
            }

            int len = peerSipNumberWithoutPassword.length();
            if (len >= 1 && len <= 4) {
                participants_btn.setVisibility(View.GONE);
            }
        }

        signalIntensityScanner = new SignalIntensityScanner();
        participantCountScanner = new ParticipantCountScanner();

        layout_video_mute.setVisibility(isVideoCall ? View.VISIBLE : View.GONE);
        layout_camera_switch.setVisibility(isVideoCall ? View.VISIBLE : View.GONE);

        mDummyPreviewView = (SurfaceView) findViewById(R.id.dummyPreviewView);
        mLocalView = getMain().getLocalVideoSurfaceView();
        mRemoteView = getMain().getRemoteVideoSurfaceView();
        mContentView = getMain().getContentVideoSurfaceView();
        if (isVideoCall) {
            state = State.INVISIBLE;
            signalIntensityScanner.sendEmptyMessageDelayed(1, 5000);
            mDummyPreviewView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    App.getHexmeetSdkInstance().setDummyPrevieView(mDummyPreviewView);
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    App.getHexmeetSdkInstance().setDummyPrevieView(null);
                }
            });

            mLocalView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    App.getHexmeetSdkInstance().setLocalVideoView(mLocalView);
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    App.getHexmeetSdkInstance().setLocalVideoView(null);
                }
            });

            mContentView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    App.getHexmeetSdkInstance().setContentVideoView(mContentView);
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    App.getHexmeetSdkInstance().setContentVideoView(null);
                }
            });

            audio_only_pic.setVisibility(View.GONE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            state = State.VISIBLE;
            signalIntensityScanner.sendEmptyMessageDelayed(2, 5000);
            int peerSipLen = peerSipNumberWithoutPassword.length();
            if (peerSipLen > 4) {
                participantCountScanner.sendEmptyMessageDelayed(1, 1000);
            }

            findViewById(R.id.video_mute_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.endcall_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.speaker_earphone_txt).setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            toolbar.setLayoutParams(params);

            call_title.setVisibility(View.INVISIBLE);
            findViewById(R.id.timer_chrono).setVisibility(View.INVISIBLE);
            findViewById(R.id.timer_chronometer).setVisibility(View.VISIBLE);
            if (peerSipLen >= 1 && peerSipLen <= 4) {
                findViewById(R.id.background_wrapper).setVisibility(View.VISIBLE);
            }

            audio_only_pic.setVisibility(View.VISIBLE);
            RestMeeting meeting = App.getMeeting(peerSipNumberWithoutPassword);
            final ImageView conn_to_avatar = (ImageView) findViewById(R.id.conn_to_avatar);
            TextView conn_to_text = (TextView) findViewById(R.id.conn_to_text);
            TextView meeting_name = (TextView) findViewById(R.id.meeting_name);
            if (callTo != null) {
                findViewById(R.id.avatar_layout).setVisibility(View.VISIBLE);
                String imageUrl = Convertor.getAvatarUrl(callTo);
                AvatarLoader.load(imageUrl, conn_to_avatar, Conversation.this, new Runnable() {
                    @Override
                    public void run() {
                        audio_only_pic.setImageDrawable(conn_to_avatar.getDrawable());
                    }
                });
                conn_to_text.setText(callTo.getName());
            } else if (meeting != null) {
                meeting_name.setVisibility(View.VISIBLE);
                meeting_name.setText(meeting.getName());
            } else {
                if (peerSipLen >= 1 && peerSipLen <= 4) {
                    findViewById(R.id.avatar_layout).setVisibility(View.VISIBLE);
                    AvatarLoader.load((String) null, conn_to_avatar);

                    audio_only_pic.setImageResource(R.drawable.icon_contact);
                    conn_to_text.setText(peerSipNumberWithoutPassword);
                } else {
                    meeting_name.setVisibility(View.VISIBLE);
                    meeting_name.setText(peerSipNumberWithoutPassword);
                }
            }
            mic_mute_btn.setAlpha(1.0f);
        }

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
                    if (!isCallRecordSaved) {
                        isCallRecordSaved = true;
                    }
                    Conversation.this.finish();
                    return;
                }

                if (state == HexmeetCallState.Connected) {
                    return;
                }

                if (state == HexmeetCallState.StreamsRunning) {
                    checkContentComing();
                    // For enable AEC when reinvite happens on some devices like vivo
                    if (isSpeakerOn) {
                        App.getHexmeetSdkInstance().setAudioRoute(HexmeetAudioRouteEventType.UI_SPEAKERLABEL_EVENT, App.getHexmeetSdkInstance().EVENT_STOP);
                        App.getHexmeetSdkInstance().setAudioRoute(HexmeetAudioRouteEventType.UI_SPEAKERLABEL_EVENT, App.getHexmeetSdkInstance().EVENT_START);
                    }
                }
            }
        };

        App.getHexmeetSdkInstance().addHexmeetSdkListener(mListener);
        //check ear phone
        if (isEarphoneOn()) {
            isSpeakerOn = false;
        } else {
            isSpeakerOn = true;
        }

        if (isVideoCall && orientationListener == null) {
            orientationListener = new OrientationEventListener(Conversation.this) {
                private int oldDirection = 0;
                private int oldCameraDirection = -1;

                @Override
                public void onOrientationChanged(int orientation) {
                    if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                        return;
                    }

                    int direction = 0;
                    if ((orientation >= 0 && orientation <= 40)
                        || (orientation >= 320 && orientation <= 360)) {
                        direction = 0;
                    } else if (orientation >= 50 && orientation <= 130) {
                        direction = 90;
                    } else if (orientation >= 140 && orientation <= 220) {
                        direction = 180;
                    } else if (orientation >= 230 && orientation <= 310) {
                        direction = 270;
                    } else {
                        return;
                    }

                    if (Math.abs(oldDirection - orientation) >= 50) {
                        if (direction != oldDirection) {
                            onNewDirection(direction);
                            oldDirection = direction;
                        }
                    }
                }

                private void onNewDirection(final int direction) {
                    if (direction != oldCameraDirection) {
                        int orient = getScreenRotationOnPhone();
                        if (orient == Surface.ROTATION_270 && direction == 0) {
                            App.getHexmeetSdkInstance().setDeviceRotation(270);
                        } else {
                            App.getHexmeetSdkInstance().setDeviceRotation(direction);
                        }
                        oldCameraDirection = direction;
                    }
                }
            };

            orientationListener.enable();
        }

        contentCheckHandler = new Handler();
        contentCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkContentComing();
            }
        }, 5000);

        handlerThread.start();

        // if call is hung up before listener registered. finish self.
        if (!App.getHexmeetSdkInstance().hasOngoingCall()) {
            finish();
        }
    }

    private boolean isEarphoneOn() {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        return am.isWiredHeadsetOn();
    }

    @Override
    public void updateSpeakerStatus(HexmeetAudioRouteType audioRoute) {
        if (audioRoute == HexmeetAudioRouteType.ROUTE_TO_SPEAKER) {
            speaker_earphone.setImageResource(R.drawable.btn_handsfree_on);
            isSpeakerOn = true;
        } else if (audioRoute == HexmeetAudioRouteType.ROUTE_TO_RECEIVER) {
            speaker_earphone.setImageResource(R.drawable.btn_handsfree_off);
            isSpeakerOn = false;
        } else { // bluetooth or wiredHeadset
            speaker_earphone.setImageResource(R.drawable.btn_handsfree_off);
            isSpeakerOn = false;
        }

        if (!isVideoCall) {
            if (isSpeakerOn) {
                audio_only_use_earphone.setVisibility(View.GONE);
            } else {
                audio_only_use_earphone.setVisibility(View.VISIBLE);
                if (isHeadsetPlugIn()) {
                    audio_only_use_earphone_text.setText(R.string.audio_only_using_earphone);
                } else {
                    audio_only_use_earphone_text.setText(R.string.audio_only_use_earphone);
                }
            }
        }
        App.setSpeakerOn(isSpeakerOn);
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent(Conversation.this, FloatingService.class);
        stopService(intent);

        super.onStart();

        // frontend, start video
        if (isVideoCall) {
            if (App.getHexmeetSdkInstance().hasOngoingTelephoneCall()) {
                video_mute_btn.setImageResource(R.drawable.btn_camera_off);
                if (!App.getHexmeetSdkInstance().isLocalVideoMuted()) {
                    App.getHexmeetSdkInstance().muteLocalVideo(true);
                }
            } else {
                if (App.IsUserMuteVideo()) {
                    video_mute_btn.setImageResource(R.drawable.btn_camera_off);
                    if (!App.getHexmeetSdkInstance().isLocalVideoMuted()) {
                        App.getHexmeetSdkInstance().muteLocalVideo(true);
                    }
                } else {
                    video_mute_btn.setImageResource(R.drawable.btn_camera_on);
                    if (App.getHexmeetSdkInstance().isLocalVideoMuted()) {
                        App.getHexmeetSdkInstance().muteLocalVideo(false);
                    }
                }
            }
        }

        HexmeetAudioRouteType audioRoute = App.getHexmeetSdkInstance().getCurrentAudioRoute();
        updateSpeakerStatus(audioRoute);

        keepSilent();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!App.isForground()) {
            // backend, mute video
            if (isVideoCall) {
                video_mute_btn.setImageResource(R.drawable.btn_camera_off);
                if (!App.getHexmeetSdkInstance().isLocalVideoMuted()) {
                    App.getHexmeetSdkInstance().muteLocalVideo(true);
                } else {

                }
            }

            Intent intent = new Intent(Conversation.this, FloatingService.class);
            intent.putExtra("isVideoCall", isVideoCall);
            intent.putExtra("starttime", starttime);
            startService(intent);
        }
    }

    private void checkContentComing() {
        if (isContentComing()) {
            if (getMain().isReceivingContent()) {
                return;
            }

            getMain().onContentIncoming();
            placeFloatButtons();
        } else {
            if (!getMain().isReceivingContent()) {
                return;
            }

            getMain().onContentClosed();
            remote_toolbar_button.setVisibility(View.GONE);
            placeFloatButtons();
        }
    }

    private boolean isContentComing() {
        return App.getHexmeetSdkInstance().isReceivingContent();
    }

    @Override
    protected void onPause() {
        participantCountScanner.removeMessages(1);

        super.onPause();
    }

    @Override
    protected void onResume() {
        if (state == State.VISIBLE) {
            boolean isMicphoneMuted = App.getHexmeetSdkInstance().isMicMuted();
            mic_mute_btn.setImageResource(isMicphoneMuted ? R.drawable.btn_mute : R.drawable.btn_unmute);
            mute_on_prompt.setVisibility(isMicphoneMuted ? View.VISIBLE : View.GONE);

            isLocalVideoMuted = App.getHexmeetSdkInstance().isLocalVideoMuted();
            video_mute_btn.setImageResource(isLocalVideoMuted ? R.drawable.btn_camera_off
                : R.drawable.btn_camera_on);
        }

        mRemoteView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                synchronized (Conversation.class) {
                    App.getHexmeetSdkInstance().setRemoteVideoView(mRemoteView);
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                synchronized (Conversation.class) {
                    App.getHexmeetSdkInstance().setRemoteVideoView(null);
                }
            }
        });

        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        signalIntensityScanner.removeMessages(1);
        signalIntensityScanner.removeMessages(2);
        participantCountScanner.removeMessages(1);

        if (orientationListener != null) {
            orientationListener.disable();
            orientationListener = null;
        }

        Intent intent = new Intent(Conversation.this, FloatingService.class);
        stopService(intent);

        handlerThread.quit();

        App.getHexmeetSdkInstance().removeHexmeetSdkListener(mListener);

        state = State.INVISIBLE;

        // specific code for Samsung 8.9 pad: SHV-140S
        mIsCallConnected = false;

        uninitAudioMode();

        super.onDestroy();
    }

    private void displayHangupMenu() {
        HangupPopupMenu popuMenu = new HangupPopupMenu(Conversation.this, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        popuMenu.addItem(new MenuItem(Conversation.this, getString(R.string.end_call)));
        popuMenu.addItem(new MenuItem(Conversation.this, getString(R.string.cancel), Color.parseColor("#de3939"), 0));
        popuMenu.setItemOnClickListener(new HangupPopupMenu.OnItemOnClickListener() {
            @Override
            public void onItemClick(MenuItem item, int position) {
                if (position == 0) {
                    Conversation.this.endCall();
                }
            }
        });

        popuMenu.show(endcall_btn);
    }

    /**
     * 默认一进来静音
     */
    private void keepSilent() {
        mic_mute_btn.setImageResource(R.drawable.btn_mute);
        mute_on_prompt.setVisibility(View.VISIBLE);
        App.getHexmeetSdkInstance().muteMic(true);
        if (isVideoCall) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mute_on_prompt.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.topMargin = ScreenUtil.dp_to_px(65);
            mute_on_prompt.setLayoutParams(params);
            mute_on_prompt.setVisibility(View.VISIBLE);
        }
    }

    protected void setupEvent() {
        participants_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Conversation.this, ConferenceParticipantList.class);
                i.putExtra("isVideoCall", isVideoCall);
                Conversation.this.startActivity(i);
            }
        });

        speaker_earphone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = App.getHexmeetSdkInstance().EVENT_START;
                if (isSpeakerOn) {
                    value = App.getHexmeetSdkInstance().EVENT_STOP;
                }
                HexmeetAudioRouteType audioRoute = App.getHexmeetSdkInstance().setAudioRoute(HexmeetAudioRouteEventType.UI_SPEAKERLABEL_EVENT, value);
                updateSpeakerStatus(audioRoute);
                App.setSpeakerOn(isSpeakerOn);
            }
        });

        mic_mute_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMicphoneMuted = !App.getHexmeetSdkInstance().isMicMuted();
                mic_mute_btn.setImageResource(isMicphoneMuted ? R.drawable.btn_mute : R.drawable.btn_unmute);
                mute_on_prompt.setVisibility(isMicphoneMuted ? View.VISIBLE : View.GONE);
                App.getHexmeetSdkInstance().muteMic(isMicphoneMuted);

                if (isVideoCall) {
                    if (isMicphoneMuted) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mute_on_prompt.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params.topMargin = ScreenUtil.dp_to_px(65);
                        mute_on_prompt.setLayoutParams(params);
                        mute_on_prompt.setVisibility(View.VISIBLE);
                    } else {
                        mute_on_prompt.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        camera_switch_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    App.getHexmeetSdkInstance().switchCamera(HexmeetCameraOperationType.Alternate);
                    //CallManager.getInstance().updateCall();

                    // previous call will cause graph reconstruction -> regive preview
                    // window
                    if (mLocalView != null) {
                        App.getHexmeetSdkInstance().setLocalVideoView(mLocalView);
                    }
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                }
            }
        });

        video_mute_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isLocalVideoMuted = !isLocalVideoMuted;

                if (isLocalVideoMuted) {
                    video_mute_btn.setImageResource(R.drawable.btn_camera_off);

                    if (!App.getHexmeetSdkInstance().isLocalVideoMuted()) {
                        App.getHexmeetSdkInstance().muteLocalVideo(true);
                        App.setUserMuteVideo(true);
                    }

                } else {
                    video_mute_btn.setImageResource(R.drawable.btn_camera_on);

                    if (App.getHexmeetSdkInstance().isLocalVideoMuted()) {
                        App.getHexmeetSdkInstance().muteLocalVideo(false);
                        App.setUserMuteVideo(false);
                    }
                }

            }
        });

        endcall_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // displayHangupMenu();
                Conversation.this.endCall();
            }
        });

        call_statistics_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Conversation.this, ConferenceSimplifiedStat.class);
                intent.putExtra("isVideoCall", isVideoCall);
                Conversation.this.startActivity(intent);

                if (isVideoCall) {
                    toolbar.setVisibility(View.INVISIBLE);
                    toolbar_top.setVisibility(View.INVISIBLE);
                    toolbar_bottom.setVisibility(View.INVISIBLE);
                    local_float_button.setVisibility(View.INVISIBLE);
                    remote_float_button.setVisibility(View.INVISIBLE);
                    mute_on_prompt.setVisibility(View.INVISIBLE);
                    state = State.INVISIBLE;
                }
            }
        });

        local_float_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().setLocalVisible(false);
                local_toolbar_button.setVisibility(View.VISIBLE);
                String imageUrl = Convertor.getLogUserAvatarUrl();
                AvatarLoader.load(imageUrl, local_btn_img);

                placeFloatButtons();
            }
        });

        local_btn_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().setLocalVisible(true);
                local_toolbar_button.setVisibility(View.GONE);

                placeFloatButtons();
            }
        });

        remote_float_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().setRemoteShrunkVisible(false);
                remote_toolbar_button.setVisibility(View.VISIBLE);

                placeFloatButtons();
            }
        });

        remote_btn_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().setRemoteShrunkVisible(true);
                remote_toolbar_button.setVisibility(View.GONE);

                placeFloatButtons();
            }
        });

        placeFloatButtonsInit();
    }

    private void placeFloatButtonsInit() {
        local_float_button.setVisibility(View.GONE);
        remote_float_button.setVisibility(View.GONE);

        setAsGroundButton(local_float_button);
        setAsLeftButton(remote_float_button);
    }

    private void placeFloatButtons() {
        if (Conversation.this.state != State.VISIBLE) {
            return;
        }

        local_float_button.setVisibility(isVideoCall && getMain().isLocalVisible() ? View.VISIBLE : View.GONE);
        remote_float_button.setVisibility(isVideoCall && getMain().isRemoteShrunkVisible() ? View.VISIBLE : View.GONE);

        if (getMain().isLocalVisible()) {
            setAsGroundButton(local_float_button);
            if (getMain().isRemoteShrunkVisible()) {
                setAsLeftButton(remote_float_button);
            }
        } else {
            if (getMain().isRemoteShrunkVisible()) {
                setAsGroundButton(remote_float_button);
            }
        }
    }

    private int w = VideoBoxGroup.little_box_width;
    private int h = VideoBoxGroup.little_box_height;
    private int offset = VideoBoxGroup.little_box_offset + ScreenUtil.dp_to_px(13);
    private int gap = VideoBoxGroup.little_boxes_gap;

    private void setAsGroundButton(ImageButton button) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) button.getLayoutParams();
        p.leftMargin = ScreenUtil.getWidth(context) - w - offset;
        p.topMargin = ScreenUtil.getHeight(context) - h - offset;
        button.setLayoutParams(p);
    }

    private void setAsLeftButton(ImageButton button) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) button.getLayoutParams();
        p.leftMargin = ScreenUtil.getWidth(context) - w - w - offset - gap;
        p.topMargin = ScreenUtil.getHeight(context) - h - offset;
        button.setLayoutParams(p);
    }

    protected void endCall() {
        App.getHexmeetSdkInstance().hangupCall();
        finish();
    }

    protected void pressBack() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayHangupMenu();
                }
            });

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    VideoBoxGroup getMain() {
        return (VideoBoxGroup) findViewById(R.id.main_frame);
    }

    public class SVCGestureListener implements OnGestureListener, OnDoubleTapListener,
        OnScaleGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return getMain().onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!isVideoCall) {
                return true;
            }

            if (state == State.VISIBLE) {
                toolbar.setVisibility(View.INVISIBLE);
                toolbar_top.setVisibility(View.INVISIBLE);
                toolbar_bottom.setVisibility(View.INVISIBLE);
                local_float_button.setVisibility(View.INVISIBLE);
                remote_float_button.setVisibility(View.INVISIBLE);
                mute_on_prompt.setVisibility(View.INVISIBLE);
                state = State.INVISIBLE;
            } else {
                if (App.getHexmeetSdkInstance().hasOngoingCall()) {
                    float level = App.getHexmeetSdkInstance().getCurrentCallQuality();
                    if (level < 1) {
                        call_statistics_btn.setImageResource(R.drawable.image_signal1);
                    } else if (level >= 1 && level < 2) {
                        call_statistics_btn.setImageResource(R.drawable.image_signal2);
                    } else if (level >= 2 && level < 3) {
                        call_statistics_btn.setImageResource(R.drawable.image_signal3);
                    } else if (level >= 3 && level < 4) {
                        call_statistics_btn.setImageResource(R.drawable.image_signal4);
                    } else if (level >= 4) {
                        call_statistics_btn.setImageResource(R.drawable.image_signal5);
                    }
                }

                toolbar.setVisibility(View.VISIBLE);
                toolbar_top.setVisibility(View.VISIBLE);
                toolbar_bottom.setVisibility(View.VISIBLE);
                toolbar.bringToFront();
                toolbar_top.bringToFront();
                toolbar_bottom.bringToFront();
                rootLayout.invalidate();

                if (App.getHexmeetSdkInstance().isMicMuted()) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mute_on_prompt.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.topMargin = ScreenUtil.dp_to_px(65);
                    mute_on_prompt.setLayoutParams(params);
                    mute_on_prompt.setVisibility(View.VISIBLE);
                }

                Animation am = new AlphaAnimation(0, 1);
                am.setDuration(500);
                am.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        state = State.VISIBLE;
                        invisibleLater();
                        signalIntensityScanner.sendEmptyMessageDelayed(2, 1000);
                        if (peerSipNumberWithoutPassword.length() > 4) {
                            participantCountScanner.sendEmptyMessageDelayed(1, 1000);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                });

                toolbar.setAnimation(am);
                toolbar_top.setAnimation(am);
                toolbar_bottom.setAnimation(am);
                mute_on_prompt.setAnimation(am);
                if (getMain().isLocalVisible() && !getMain().isFullScreen()) {
                    local_float_button.setVisibility(View.VISIBLE);
                    local_float_button.bringToFront();
                    local_float_button.setAnimation(am);
                } else {
                    local_float_button.setVisibility(View.INVISIBLE);
                }
                if (getMain().isRemoteShrunkVisible() && !getMain().isFullScreen()) {
                    remote_float_button.setVisibility(View.VISIBLE);
                    remote_float_button.bringToFront();
                    remote_float_button.setAnimation(am);
                } else {
                    remote_float_button.setVisibility(View.INVISIBLE);
                }
                am.startNow();
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return getMain().onScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    public class SignalIntensityScanner extends Handler {

        public SignalIntensityScanner() {
        }

        public SignalIntensityScanner(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (App.getHexmeetSdkInstance().hasOngoingCall()) {
                if (!App.isScreenLocked() && App.isForground()) {
                    float level = App.getHexmeetSdkInstance().getCurrentCallQuality();
                    if (level < 3) {
                        showWeakSignalWarning();
                    }

                    if (state == State.VISIBLE) {
                        if (level < 1) {
                            call_statistics_btn.setImageResource(R.drawable.image_signal1);
                        } else if (level >= 1 && level < 2) {
                            call_statistics_btn.setImageResource(R.drawable.image_signal2);
                        } else if (level >= 2 && level < 3) {
                            call_statistics_btn.setImageResource(R.drawable.image_signal3);
                        } else if (level >= 3 && level < 4) {
                            call_statistics_btn.setImageResource(R.drawable.image_signal4);
                        } else if (level >= 4) {
                            call_statistics_btn.setImageResource(R.drawable.image_signal5);
                        }

                        if (msg.what == 2) {
                            // scan signal intensity only when title bar is visible
                            signalIntensityScanner.sendEmptyMessageDelayed(2, 2000);
                            return;
                        }
                    }
                }

                if (msg.what == 1) {
                    // scan signal intensity in background all along with Conversation
                    signalIntensityScanner.sendEmptyMessageDelayed(1, 5000);
                }
            }
        }
    }

    public class ParticipantCountScanner extends Handler {

        public ParticipantCountScanner() {
        }

        public ParticipantCountScanner(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (state == State.VISIBLE) {
                if (!App.isScreenLocked() && App.isForground() && App.getHexmeetSdkInstance().hasOngoingCall()) {
                    try {
                        ApiClient.getParticipants(Integer.parseInt(peerSipNumberWithoutPassword),
                            new retrofit2.Callback<List<RestParticipant>>() {
                                @Override
                                public void onResponse(
                                    Call<List<RestParticipant>> call, Response<List<RestParticipant>> resp) {
                                    participant_count.setText(resp.body().size() + "");
                                }

                                @Override
                                public void onFailure(Call<List<RestParticipant>> call, Throwable e) {
                                }
                            });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (peerSipNumberWithoutPassword.length() > 4) {
                    participantCountScanner.sendEmptyMessageDelayed(1, 2000);
                }
            }
        }
    }

    private long lastShow = 0;

    private void showWeakSignalWarning() {
        if (System.currentTimeMillis() - lastShow > 120000) {
            lastShow = System.currentTimeMillis();
            Toast t = Toast.makeText(Conversation.this, R.string.network_instability, Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout layout = (LinearLayout) t.getView();
            layout.setBackgroundColor(Color.parseColor("#66000000"));
            t.show();
        }
    }

    private static enum State {
        VISIBLE, ANIMATIONING, INVISIBLE,
    }

    private static final Handler INVISIBLE_ME_HANDLER = new Handler();
    public static final long MILLISECOND_TO_INVISIBLE = 15000;
    private State state = State.INVISIBLE;
    private Runnable invisibleTask;

    private boolean invisibleLater() {
        if (state != State.VISIBLE) {
            return false;
        }

        cancelCurrentInvisibleTask();
        invisibleTask = new InvisibleTask();
        INVISIBLE_ME_HANDLER.postDelayed(invisibleTask, MILLISECOND_TO_INVISIBLE);
        return true;
    }

    private void cancelCurrentInvisibleTask() {
        if (invisibleTask != null) {
            INVISIBLE_ME_HANDLER.removeCallbacks(invisibleTask);
            invisibleTask = null;
        }
    }

    private class InvisibleTask implements Runnable {

        @Override
        public void run() {
            boolean visible = State.VISIBLE == state;
            boolean cancelled = this != invisibleTask;
            if (visible && !cancelled) {
                toolbar.setVisibility(View.INVISIBLE);
                toolbar_top.setVisibility(View.INVISIBLE);
                toolbar_bottom.setVisibility(View.INVISIBLE);
                local_float_button.setVisibility(View.INVISIBLE);
                remote_float_button.setVisibility(View.INVISIBLE);
                mute_on_prompt.setVisibility(View.INVISIBLE);
                state = State.INVISIBLE;
            }
        }
    }


    private int getScreenRotationOnPhone() {
        int surfaceOrentation = Surface.ROTATION_0;
        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                System.out.println("SCREEN_ORIENTATION_PORTRAIT");
                break;

            case Surface.ROTATION_90:
                System.out.println("SCREEN_ORIENTATION_LANDSCAPE");
                surfaceOrentation = Surface.ROTATION_90;
                break;

            case Surface.ROTATION_180:
                System.out.println("SCREEN_ORIENTATION_REVERSE_PORTRAIT");
                break;

            case Surface.ROTATION_270:
                surfaceOrentation = Surface.ROTATION_270;
                System.out.println("SCREEN_ORIENTATION_REVERSE_LANDSCAPE");
                break;
        }
        return surfaceOrentation;
    }
}

package com.lensim.fingerchat.hexmeet;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;


import android.util.Log;
import com.hexmeet.sdk.HexmeetCallState;
import com.hexmeet.sdk.HexmeetException;
import com.hexmeet.sdk.HexmeetReason;
import com.hexmeet.sdk.HexmeetRegistrationState;
import com.hexmeet.sdk.HexmeetSdkBuilder;
import com.hexmeet.sdk.HexmeetSdkListener;
import com.hexmeet.sdk.IHexmeetSdkApi;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.hexmeet.activity.HexMeetListActivity;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import com.lensim.fingerchat.hexmeet.utils.DialOutRetryHandler;
import com.lensim.fingerchat.hexmeet.utils.NetworkStatusCallback;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.SSLCertificateHandler;
import com.lensim.fingerchat.hexmeet.utils.SipRegisterUtil;
import com.lensim.fingerchat.hexmeet.utils.SoundPlayer;
import com.lensim.fingerchat.hexmeet.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class App {

    private static Context context;
    private HexmeetSdkBuilder mSdkBuilder;
    private HexmeetSdkListener mSdkListener;
    private static boolean m_isSDKinited = false;
    private static ScheduledExecutorService serviceRegisterSip = Executors
        .newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> registerSipTask = null;

    public static IHexmeetSdkApi mHexmeetSdkInstance;

    public static IHexmeetSdkApi getHexmeetSdkInstance() {
        return mHexmeetSdkInstance;
    }

    public static enum appState {
        APP_NO_CALL,
        APP_CALL_COMMING,
        APP_CALL_ESTABLISHED;
    }

    public static appState getCurrentAppState() {
        appState ret = appState.APP_NO_CALL;

        if (mHexmeetSdkInstance.isIncomingCallReceived()) {
            ret = appState.APP_CALL_COMMING;
        } else if (mHexmeetSdkInstance.hasOngoingCall()) {
            ret = appState.APP_CALL_ESTABLISHED;
        }
        return ret;
    }

    public static Context getContext() {
        return context = ContextHelper.getContext();
    }

    public static boolean isEnVersion() {
        return !Locale.getDefault().getLanguage().equals("zh");
    }

    private static boolean isForground = true;

    public static boolean isForground() {
        return isForground;
    }

    private static boolean isScreenLocked = false;

    public static boolean isScreenLocked() {
        return isScreenLocked;
    }

    public static void setScreenLocked(boolean locked) {
        App.isScreenLocked = locked;
    }

    private static boolean isUserMuteVideo = false;

    public static synchronized boolean IsUserMuteVideo() {
        return isUserMuteVideo;
    }

    public static synchronized void setUserMuteVideo(boolean isMute) {
        isUserMuteVideo = isMute;
    }


    public static Bitmap blurredBackground;

    private static Map<String, RestContact> map_sipNum_contact = new ConcurrentHashMap<String, RestContact>();

    public static void addContact(String sipNum, RestContact contact) {
        try {
            map_sipNum_contact.put(sipNum, contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearContacts() {
        map_sipNum_contact.clear();
    }

    public static RestContact getContact(String sipNum) {
        return map_sipNum_contact.get(sipNum);
    }

    public static void removeContact(String sipNum) {
        if (sipNum != null) {
            map_sipNum_contact.remove(sipNum);
        }
    }

    private static Map<String, RestMeeting> map_sipNum_meeting = new ConcurrentHashMap<String, RestMeeting>();

    public static void addMeeting(String sipNum, RestMeeting meeting) {
        try {
            map_sipNum_meeting.put(sipNum, meeting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearMeetings() {
        map_sipNum_meeting.clear();
    }

    public static RestMeeting getMeeting(String sipNum) {
        return map_sipNum_meeting.get(sipNum);
    }

    private static boolean networkConnected = true;

    public static synchronized boolean isNetworkConnected() {
        return networkConnected;
    }

    public static synchronized void setNetworkConnected(boolean networkConnected) {
        App.networkConnected = networkConnected;
        try {
            mHexmeetSdkInstance.updateNetworkReachability();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static NetworkStatusCallback networkStatusCallback = null;

    public static NetworkStatusCallback getNetworkStatusCallback() {
        return networkStatusCallback;
    }

    public static void setNetworkStatusCallback(NetworkStatusCallback networkStatusCallback) {
        App.networkStatusCallback = networkStatusCallback;
    }

    private static boolean isSpeakerOn = true;

    public static synchronized void setSpeakerOn(boolean yesno) {
        isSpeakerOn = yesno;
    }

    public static synchronized boolean isSpeakerOn() {
        return isSpeakerOn;
    }

    private static boolean isLocalVideoEnabled = true;

    public static synchronized void setLocalVideoEnabled(boolean yesno) {
        isLocalVideoEnabled = yesno;
    }

    public static synchronized boolean isLocalVideoEnabled() {
        return isLocalVideoEnabled;
    }

    private static RestCallRow_ incomingCallRecord;

    public static RestCallRow_ getIncomingCallRecord() {
        return incomingCallRecord;
    }

    public static void setIncomingCallRecord(RestCallRow_ r) {
        incomingCallRecord = r;
    }


    public void init() {
        context = ContextHelper.getContext();
        Boolean isDefaultProcess = false;
        String processName = getProcessName(context, android.os.Process.myPid());
        if (processName != null) {
            isDefaultProcess = processName.equals(context.getApplicationInfo().processName);
        }

        SSLCertificateHandler.nuke();
        SoundPlayer.init(context);

        if (isDefaultProcess && !m_isSDKinited) {
            initSDK();
            m_isSDKinited = true;

            App.setNetworkStatusCallback(new NetworkStatusCallback() {
                @Override
                public void onConnected() {
                    App.setNetworkConnected(true);

                    NetworkUtil.setSdkCallRate(App.getContext());
                    ApiClient.reset();

                    RuntimeData.registerSip();
                }

                @Override
                public void onDisconnected() {
                    App.setNetworkConnected(false);
                }

                @Override
                public void onChanged() {
                    App.setNetworkConnected(true);
                    NetworkUtil.setSdkCallRate(App.getContext());
                    ApiClient.reset();

                    RuntimeData.registerSip();
                }
            });
        }

    }

    private void initSDK() {
        mSdkBuilder = new HexmeetSdkBuilder();
        mSdkBuilder.prepareEssentialFileRes(R.raw.hexmeet_default,
            R.raw.hexmeet_factory,
            R.raw.rootca,
            R.raw.background,
            R.raw.background_calling);

        try {
            mHexmeetSdkInstance = mSdkBuilder.buildHexmeetApiInstance(context);
        } catch (HexmeetException e) {
            e.printStackTrace();
            return;
        }

        String patha = context.getFilesDir().getAbsolutePath();
        mHexmeetSdkInstance.enableLogCollection(true);
        mSdkListener = new HexmeetSdkListener() {

            @Override
            public void globalState() {

            }

            @Override
            public void registrationState(HexmeetRegistrationState state) {
                if (state == HexmeetRegistrationState.RegistrationFailed
                    || state == HexmeetRegistrationState.RegistrationNone) {
                    if (isNetworkConnected()) {
                        scheduleRegisterSip();
                    } else {

                    }
                }
            }

            @Override
            public void callState(HexmeetCallState state, HexmeetReason reason) {

                DialOutRetryHandler.getInstance().iterator(state, reason);

                if (!mHexmeetSdkInstance.hasOngoingCall()) {
                    // cancel incoming call window
                    return;
                }

                if (state == HexmeetCallState.Connected) {
                    App.setUserMuteVideo(false);
                    if (HexMeetListActivity.getInstance() != null) {
                        HexMeetListActivity.getInstance().showVideoWindow();
                    } else {
                        Intent intent = new Intent(App.getContext(), HexMeetListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.getContext().startActivity(intent);
                    }
                    return;
                }
            }
        };
        mHexmeetSdkInstance.addHexmeetSdkListener(mSdkListener);
        mHexmeetSdkInstance.setUserAgent("HexMeet EasyVideo Android", "V" + Utils.getVersion());
        //mHexmeetSdkInstance.setDeviceRotation(270);
    }


    public void onExit() {
//    super.onTerminate();
        if (m_isSDKinited == true) {
            mHexmeetSdkInstance.hangupCall();
            mHexmeetSdkInstance.removeHexmeetSdkListener(mSdkListener);
            mSdkBuilder.destroyHexmeetApiInstance();
            m_isSDKinited = false;
        }

    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static void scheduleRegisterSip() {
        try {
            if (registerSipTask != null) {
                registerSipTask.cancel(true);
                registerSipTask = null;
            }

            registerSipTask = serviceRegisterSip.scheduleAtFixedRate(new Runnable() {
                int i = 0;

                @Override
                public void run() {
                    HexmeetRegistrationState registerStatus = SipRegisterUtil
                        .getSipRegisterStatus();
                    if (registerStatus == HexmeetRegistrationState.RegistrationOk
                        || registerStatus == HexmeetRegistrationState.RegistrationNone) {
                        registerSipTask.cancel(true);
                        registerSipTask = null;
                        return;
                    }

                    if (!App.isNetworkConnected()) {
                        registerSipTask.cancel(true);
                        registerSipTask = null;
                        return;
                    }

                    if (i >= 30) {
                        registerSipTask.cancel(true);
                        registerSipTask = null;
                        return;
                    }
                    RuntimeData.registerSip();
                    i++;
                }
            }, 5, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopServiceTask() {
        if (registerSipTask != null) {
            registerSipTask.cancel(true);
            registerSipTask = null;
        }
    }
}

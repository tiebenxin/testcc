package com.lensim.fingerchat.hexmeet.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.hexmeet.sdk.HexmeetAudioRouteEventType;
import com.hexmeet.sdk.HexmeetAudioRouteType;
import com.lensim.fingerchat.hexmeet.App;

@SuppressLint("DefaultLocale")
@SuppressWarnings({"deprecation", "unused"})
public class FullScreenWithAECActivity extends FullscreenActivity {

    private static final String tag = "FullScreenWithAECActivity";
    private boolean oldSpeakerState = false;

    private static final String BLUETOOTH_CONNECT_ACTION = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";
    private HeadsetPlugReceiver headsetPlugReceiver = new HeadsetPlugReceiver();

    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    private int value = App.getHexmeetSdkInstance().EVENT_STOP;

    private class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            HexmeetAudioRouteEventType event = null;
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                event = HexmeetAudioRouteEventType.HEADSET_PLUG_EVENT;
                if (intent.getIntExtra("state", 0) != 1) {
                    // plug out
                    value = App.getHexmeetSdkInstance().EVENT_STOP;
                } else { // plug in
                    value = App.getHexmeetSdkInstance().EVENT_START;
                }
                if (isInitialStickyBroadcast()) {
                    return;
                }
            } else if (intent.getAction().equals(BLUETOOTH_CONNECT_ACTION)) {
                event = HexmeetAudioRouteEventType.BLUETOOTH_CONNECTION_EVENT;
                int state = intent
                    .getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    value = App.getHexmeetSdkInstance().EVENT_START;
                } else {
                    value = App.getHexmeetSdkInstance().EVENT_STOP;
                }
            }
            if (event != null) {
                HexmeetAudioRouteType audioRoute = App.getHexmeetSdkInstance()
                    .setAudioRoute(event, value);
                updateSpeakerStatus(audioRoute);
            }
        }
    }

    public void updateSpeakerStatus(HexmeetAudioRouteType audioRoute) {
    }

    public void initAudioMode() {
        intentFilter.addAction(BLUETOOTH_CONNECT_ACTION);
        registerReceiver(headsetPlugReceiver, intentFilter);
        App.getHexmeetSdkInstance().setAudioRoute(HexmeetAudioRouteEventType.CONVERSATION_EVENT,
            App.getHexmeetSdkInstance().EVENT_START);
    }

    public void uninitAudioMode() {
        unregisterReceiver(headsetPlugReceiver);
        App.getHexmeetSdkInstance().setAudioRoute(HexmeetAudioRouteEventType.CONVERSATION_EVENT,
            App.getHexmeetSdkInstance().EVENT_STOP);

    }

    public boolean isHeadsetPlugIn() {
        return value == App.getHexmeetSdkInstance().EVENT_START;
    }
}

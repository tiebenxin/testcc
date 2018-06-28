package com.lensim.fingerchat.hexmeet.utils;

import com.hexmeet.sdk.HexmeetCallState;
import com.hexmeet.sdk.HexmeetCameraOperationType;
import com.hexmeet.sdk.HexmeetException;
import com.hexmeet.sdk.HexmeetReason;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.OfflineMessage;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestResult;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialOutRetryHandler {

  static public enum RetryState {
    E_RETRY_INIT, E_RETRY_FIRST_DIALING, E_RETRY_DIALING, E_RETRY_SUCCEED, E_RETRY_FAILED;
  }

  static public class DialOutRetryListener {

    public void callRetryState(RetryState state) {

    }
  }

  static private CopyOnWriteArrayList<DialOutRetryListener> listeners = new CopyOnWriteArrayList<DialOutRetryListener>();

  static public void addListener(DialOutRetryListener listenr) {
    if (listenr != null && !listeners.contains(listenr)) {
      listeners.add(listenr);
    }
  }

  static public void removeListener(DialOutRetryListener listenr) {
    if (listenr != null && listeners.contains(listenr)) {
      listeners.remove(listenr);
    }
  }

  static private void callListeners(RetryState state) {
    for (DialOutRetryListener listener : listeners) {
      listener.callRetryState(state);
    }
  }

  static private DialOutRetryHandler instance = null;

  static public synchronized DialOutRetryHandler getInstance() {
    if (instance == null) {
      instance = new DialOutRetryHandler();
    }
    return instance;
  }

  static private final int RETRY_TIME_OUT = 100 * 1000; //seconds
  static private final int RETRY_INTERVAL = 2 * 1000; //seconds

  private RetryState state;
  private int retryCount;
  private long startTime;
  private long lastTime;

  private String callNum;
  private String display;
  private RestContact restContact = null;
  private boolean isVideoCall;
  private Timer mTimer;
  private HexmeetCallState mlcState;
  private HexmeetReason mReason;
  private String mMessage;
  private boolean hasRinging;
  private boolean hasNotSendNotify;

  private ScheduledExecutorService confMissedInMcu_service = null;
  private boolean haveRetriedCallInCaseConfMissedInMcu = false;

  public DialOutRetryHandler() {
    init();
  }

  public void cleanCall() {
    mReason = null;
    mlcState = null;
    mMessage = null;
    hasRinging = false;
  }

  public synchronized void init() {
    state = RetryState.E_RETRY_INIT;
    retryCount = 0;
    startTime = lastTime = System.currentTimeMillis();
    callNum = "";
    display = "";
    isVideoCall = false;
    hasNotSendNotify = true;
    cleanCall();
    callListeners(state);
  }

  private void outgoingCall(final String callNum, final boolean isVideoCall) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          App.getHexmeetSdkInstance().placeCall(callNum, isVideoCall);
        } catch (HexmeetException e) {
//						Toast t = new Toast(App.getContext());
//						t.setDuration(Toast.LENGTH_SHORT);
//						t.setText("sdk没有激活");
//						t.show();
        }

      }
    }).start();
  }

  public synchronized void startDialing(String callNum, RestContact restContact, boolean isVideoCall) {
    App.getHexmeetSdkInstance().switchCamera(HexmeetCameraOperationType.FrontCamera);
    haveRetriedCallInCaseConfMissedInMcu = false;
    this.restContact = restContact;
    switch (state) {
      case E_RETRY_INIT:
        state = RetryState.E_RETRY_FIRST_DIALING;
        break;
      default:
        return;
    }

    TimerTask lTask = new TimerTask() {
      @Override
      public void run() {
        if (System.currentTimeMillis() - lastTime >= RETRY_INTERVAL) {
          DialOutRetryHandler.this.iterator(mlcState, mReason);
        }
      }
    };

    /*use schedule instead of scheduleAtFixedRate to avoid iterate from being call in burst after cpu wake up*/
    mTimer = new Timer("DialOutRetryHandler scheduler");
    mTimer.schedule(lTask, 0, 500);

    this.callNum = callNum;
    this.display = display;
    this.isVideoCall = isVideoCall;
    startTime = lastTime = System.currentTimeMillis();
    callListeners(state);
    App.setLocalVideoEnabled(isVideoCall);
    outgoingCall(callNum, isVideoCall);
  }

  private void dialing() {
    switch (state) {
      case E_RETRY_FIRST_DIALING:
      case E_RETRY_DIALING:
        state = RetryState.E_RETRY_DIALING;
        break;
      default:
        return;
    }

    long currentTime = System.currentTimeMillis();
    lastTime = currentTime;
    App.getHexmeetSdkInstance().hangupCall();
    retryCount++;

    callListeners(state);
    cleanCall();
    outgoingCall(callNum, isVideoCall);
  }

  public synchronized void iterator(HexmeetCallState lcState, HexmeetReason reason) {
    switch (state) {
      case E_RETRY_FIRST_DIALING:
      case E_RETRY_DIALING:
        break;
      default:
        return;
    }

    long currentTime = System.currentTimeMillis();

    if (hasNotSendNotify && (currentTime - startTime > RETRY_INTERVAL) && (hasRinging == false)) {
      this.sendNotify();
    }

    if (lcState == null) {
      return;
    }

    if (lcState == HexmeetCallState.OutgoingRinging) {
      hasRinging = true;
    }

    if (lcState == HexmeetCallState.Connected ||
        lcState == HexmeetCallState.Error ||
        lcState == HexmeetCallState.CallEnd ||
        lcState == HexmeetCallState.OutgoingProgress) {
      if (lcState != HexmeetCallState.CallEnd) {

      }
    } else {

      return;
    }

    mlcState = lcState;
    mReason = reason;

    if (lcState == HexmeetCallState.Connected) {
      state = RetryState.E_RETRY_SUCCEED;
      callListeners(state);

      destory();
      return;
    } else if (lcState == HexmeetCallState.Error || lcState == HexmeetCallState.CallEnd) {
      if (reason == HexmeetReason.Busy) {

          cancel();
        //notify user the remote is busy
        Utils.showToastInNewThread(App.getContext(), App.getContext().getResources().getString(R.string.remote_is_busy));
        return;
      } else if (reason == HexmeetReason.Declined) {

        //Call Canceled by remote or time out
        cancel();
        return;
      } else if (reason == HexmeetReason.Unknown) {
        if (confMissedInMcu_service == null) {
          confMissedInMcu_service = Executors.newSingleThreadScheduledExecutor();
          confMissedInMcu_service.schedule(new Runnable() {
            @Override
            public void run() {
              haveRetriedCallInCaseConfMissedInMcu = true;
              dialing();
            }
          }, 2, TimeUnit.SECONDS);
          return;
        }

        if (haveRetriedCallInCaseConfMissedInMcu) {
          haveRetriedCallInCaseConfMissedInMcu = false;
          cancel();
          //notify user the server decline call
          Utils.showToastInNewThread(App.getContext(), App.getContext().getResources().getString(R.string.reject));
        }
        return;
      }

      if (currentTime - startTime > RETRY_TIME_OUT) {

        cancel();
        return;
      }
    }

    if ((currentTime - lastTime > RETRY_INTERVAL) && (hasRinging == false)) {

      dialing();
      return;
    }
  }

  public synchronized void cancel() {
    switch (state) {
      case E_RETRY_INIT:
      case E_RETRY_FIRST_DIALING:
      case E_RETRY_DIALING:
        state = RetryState.E_RETRY_FAILED;
        break;
      default:
    }

    App.getHexmeetSdkInstance().hangupCall();

    callListeners(state);
    destory();
  }

  public synchronized void destory() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
    if (confMissedInMcu_service != null) {
      confMissedInMcu_service.shutdownNow();
      confMissedInMcu_service = null;
    }
  }

  public synchronized void sendNotify() {
    if (this.restContact != null) {
      OfflineMessage offlineMessage = new OfflineMessage();
      offlineMessage.setReceiverUserId(restContact.getUserId());
      offlineMessage.setLaunchTime(System.currentTimeMillis());
      offlineMessage.setVideoCall(isVideoCall);

      ApiClient.pushOfflineMessage(offlineMessage, new Callback<RestResult>() {
        @Override
        public void onResponse(Call<RestResult> call, Response<RestResult> response) {
          if (response.isSuccessful()) {

          } else {

          }
        }

        @Override
        public void onFailure(Call<RestResult> call, Throwable e) {
        }
      });
      hasNotSendNotify = false;
    }
  }
}
package com.lensim.fingerchat.hexmeet.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.utils.NetworkStatusCallback;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;

public class NetworkStateService extends Service {

  private ConnectivityManager connectivityManager;
  private NetworkInfo info;
  private static NetworkStatusCallback networkStatusCallback = null;

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context context, Intent intent) {
      String action = intent.getAction();
      networkStatusCallback = App.getNetworkStatusCallback();

      if (NetworkUtil.isNetConnected(context)) {
        if (networkStatusCallback != null) {
          networkStatusCallback.onConnected();
        }
      } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
          // network onChanged
          String name = info.getTypeName();

          if (networkStatusCallback != null) {
            networkStatusCallback.onChanged();
          }
        } else {
          // network onDisconnected
          if (networkStatusCallback != null) {
            networkStatusCallback.onDisconnected();
          }
        }
      }
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    IntentFilter mFilter = new IntentFilter();
    mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(mReceiver, mFilter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mReceiver);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }
}

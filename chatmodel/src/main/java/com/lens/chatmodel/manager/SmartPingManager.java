package com.lens.chatmodel.manager;


import android.util.Log;

import com.fingerchat.api.Manager;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.api.util.thread.ExecutorManager;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.NetWorkUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by LY309313 on 2017/4/18.
 */

public class SmartPingManager extends Manager {

  private static final String TAG = "SmartPingManager";

  private static final Map<Connection, SmartPingManager> INSTANCES = new WeakHashMap<>();

  /**
   * 最大心跳间隔
   */
  private static final int MAX_INTERVAL = 270;

  /**
   * 短心跳，重连到网络时，用短间隔探测网络的稳定性
   */
  private static final int MIN_INTERVAL = 60;
  /**
   * 当前心跳区间的最大值
   */
  private int curMaxInterval = MAX_INTERVAL;
  /**
   * 当前心跳区间的最小值
   */
  private int curMinInterval = MIN_INTERVAL;
  /**
   * 心跳连续失败次数最大值，用来向下探测
   */
  private int maxFailedCount = 5;
  /**
   * 短心跳成功最大次数，达到3次就认为当前网络是稳定的网络状态
   */
  private int maxSuccessCount = 3;
  /**
   * 当前短心跳成功次数
   */
  private int curSuccessCount;
  /**
   * 当前网络类型
   */
  private volatile String networkTag;
  /**
   * 各种网络类型对应的心跳
   */
  private Map<String, Heartbeat> heartbeatMap = new HashMap<>();

  /**
   * 心跳
   */
  private class Heartbeat {

    AtomicInteger heartbeatStabledSuccessCount = new AtomicInteger(0); // 心跳连续成功次数

    AtomicInteger heartbeatFailedCount = new AtomicInteger(0); // 心跳连续失败次数

    int successHeart;

    int failedHeart;
    //当前正在使用的心跳间隔，默认60秒
    int curInterval = 60;

    AtomicBoolean stabled = new AtomicBoolean(false);

  }

  /**
   * 获取一个单例
   */
  public synchronized static SmartPingManager getInstanceFor(Connection connection) {
    SmartPingManager pingManager = INSTANCES.get(connection);
    if (pingManager == null) {
      pingManager = new SmartPingManager(connection);
      INSTANCES.put(connection, pingManager);
    }
    return pingManager;
  }

  /**
   * 初始的心跳间隔
   */
  private static int defaultPingInterval = 60;


  private final ScheduledExecutorService executorService;

  /**
   * 心跳的时间间隔，单位为秒
   */
  private int pingInterval = defaultPingInterval;

  private ScheduledFuture<?> nextAutomaticPing;


  public static void init(int interval) {
    defaultPingInterval = interval;
  }


  public SmartPingManager(Connection connection) {
    super(connection);
    executorService = ExecutorManager.INSTANCE.getTimerThread();

    networkTag = NetWorkUtil.GetNetworkType(ContextHelper.getContext());
    Log.i(TAG, "对应的网络类型:" + networkTag);
//        connection.registerIQRequestHandler(new AbstractIqRequestHandler(Ping.ELEMENT, Ping.NAMESPACE, IQ.Type.get, IQRequestHandler.Mode.async) {
//            @Override
//            public IQ handleIQRequest(IQ iqRequest) {
//                Ping ping = (Ping) iqRequest;
//                return ping.getPong();
//            }
//        });
//        connection.addConnectionListener(new AbstractConnectionClosedListener() {
//            @Override
//            public void authenticated(XMPPConnection connection, boolean resumed) {
//                maybeSchedulePingServerTask();
//            }
//            @Override
//            public void connectionTerminated() {
//                maybeStopPingServerTask();
//            }
//        });
    // maybeSchedulePingServerTask();
  }


  public boolean ping() {
    final Connection connection = connection();
    // Packet collector for IQs needs an connection that was at least authenticated once,
    // otherwise the client JID will be null causing an NPE
    if (!connection.isConnected()) {
      return false;
    }
    connection.send(Packet.HB_PACKET);
    return true;
  }


  private void maybeSchedulePingServerTask() {
    maybeSchedulePingServerTask(0);
  }

  /**
   * Cancels any existing periodic ping task if there is one and schedules a new ping task if
   * pingInterval is greater then zero.
   *
   * @param delta the delta to the last received stanza in seconds
   */
  private synchronized void maybeSchedulePingServerTask(int delta) {
    maybeStopPingServerTask();
    if (pingInterval > 0) {
      int nextPingIn = pingInterval - delta;
      Log.i("SmartManager", "下一次ping的时间: " + nextPingIn + " seconds (pingInterval="
          + pingInterval + ", delta=" + delta + ")");
      nextAutomaticPing = executorService
          .schedule(pingServerRunnable, nextPingIn, TimeUnit.SECONDS);
    }
  }

  public void maybeStopPingServerTask() {
    if (nextAutomaticPing != null) {
      nextAutomaticPing.cancel(true);
      nextAutomaticPing = null;
    }
  }

  public synchronized void pingServerIfNecessary() {
    final int DELTA = 1000; // 1 seconds
    final int TRIES = 3; // 3 tries
    final Connection connection = connection();
    if (connection == null) {
      // connection has been collected by GC
      // which means we can stop the thread by breaking the loop
      return;
    }
    if (pingInterval <= 0) {
      // Ping has been disabled
      return;
    }
    //上次接收消息包的时间
    long lastStanzaReceived = connection.getLastReadTime();
    if (lastStanzaReceived > 0) {
      long now = System.currentTimeMillis();
      // Delta since the last stanza was received
      int deltaInSeconds = (int) ((now - lastStanzaReceived) / 1000);
      // If the delta is small then the ping interval, then we can defer the ping
      if (deltaInSeconds < pingInterval) {
        //这里是否算是一次成功的心跳
        //结论:这里不算一次成功的心跳，因为并没有发生，如果一直出现这种情况，则代表永远不会发送心跳，无伤
        maybeSchedulePingServerTask(deltaInSeconds);
        return;
      }
    }
    if (connection.isConnected()) {
      boolean res = false;

      for (int i = 0; i < TRIES; i++) {
        if (i != 0) {
          try {
            Thread.sleep(DELTA);
          } catch (InterruptedException e) {
            // We received an interrupt
            // This only happens if we should stop pinging
            return;
          }
        }
//                try {
        res = ping();
//                }
//                catch (SmackException e) {
//                    L.w("ping服务器的时候发生SmackError错误"+e.getMessage());
//                    res = false;
//                }
        // stop when we receive a pong back
        if (res) {
          break;
        }
      }
      if (!res) {
        adjustHeart(false);
//                for (PingFailedListener l : pingFailedListeners) {
//                    //心跳失败，如果凑满五次，则往下调整
//                    l.pingFailed();
//                }
      } else {
        // 这是一次成功的心跳
        adjustHeart(true);
        //  maybeSchedulePingServerTask();
      }
    } else {
      Log.w(TAG, "XMPPConnection 没有连接上");
    }
  }

  private final Runnable pingServerRunnable = new Runnable() {
    public void run() {
      Log.i(TAG, "ServerPingTask run()");
      pingServerIfNecessary();
    }
  };

  @Override
  protected void finalize() throws Throwable {
    Log.i(TAG, "finalizing PingManager: Shutting down executor service");
    try {
      executorService.shutdown();
    } catch (Throwable t) {
      Log.e(TAG, "finalize() threw throwable" + t);
    } finally {
      super.finalize();
    }
  }


  private void adjustHeart(boolean success) {
    Heartbeat heartbeat = getHeartbeat();
    if (success) {
      onSuccess(heartbeat);
    } else {
      onFailed(heartbeat);
    }
    Log.i(TAG,
        "after success is [" + success + "] adjusted,heartbeat.curHeart:" + heartbeat.curInterval
            + ",networkTag:" + networkTag);
  }

  /**
   * ping服务器成功，调整心跳间隔
   */
  private void onSuccess(Heartbeat heartbeat) {
    //1、先确定短间隔是否已经到达三次，如果三次都达不到就判定当前网络不好，不要调整稳定间隔
    if (curSuccessCount < maxSuccessCount) {
      Log.i(TAG, "还没有达到稳定态");
      curSuccessCount++;
    } else {
      //2、短间隔到达三次，开始上调间隔
      //2.1 将失败次数归零
      int anInt = SPHelper.getInt(networkTag, -1);
      if (anInt != -1) {
        heartbeat.stabled.set(true);
        heartbeat.curInterval = anInt;
      }
      heartbeat.heartbeatFailedCount.set(0);
      if (heartbeat.stabled.get()) {
        //如果是已经为稳定状态，则不要继续提升，之记录成功次数
        int count = heartbeat.heartbeatStabledSuccessCount.incrementAndGet();
        // TODO: 2017/4/19 如果稳定次数达到一定次数，断开重连 ，可以每个星期重新计算稳定间隔
      } else {
        //如果还没有达到稳定态，则判断是否达到最大值，达到就设置为稳定态，没达到就继续往上调整
        heartbeat.curInterval += 30;
      }
      //如果超过了最大值，则稳定
      if (heartbeat.curInterval > MAX_INTERVAL) {
        heartbeat.curInterval = MAX_INTERVAL;
        heartbeat.stabled.set(true);
        SPHelper.saveValue(networkTag, heartbeat.curInterval);
      }
      pingInterval = heartbeat.curInterval;
    }
    Log.i(TAG, "当前心跳:" + pingInterval);
    maybeSchedulePingServerTask();

  }

  private void onFailed(Heartbeat heartbeat) {
    heartbeat.heartbeatStabledSuccessCount.set(0);
    int count = heartbeat.heartbeatFailedCount.incrementAndGet();
    //判断失败次数
    if (count >= maxFailedCount) {
      //如果失败次数到达五次，那么往下调整一级，并将这个心跳置为稳定
      heartbeat.stabled.set(true);
      heartbeat.curInterval -= 30;
      SPHelper.saveValue(networkTag, heartbeat.curInterval);
    }

    curSuccessCount = 0;
    pingInterval = MIN_INTERVAL;
  }

  /**
   * 获取网络类型对应的心跳区间
   */
  private Heartbeat getHeartbeat() {
    Heartbeat heartbeat = heartbeatMap.get(networkTag);
    if (heartbeat == null) {
      heartbeat = new Heartbeat();
      heartbeatMap.put(networkTag, heartbeat);
    }
    return heartbeat;
  }

}

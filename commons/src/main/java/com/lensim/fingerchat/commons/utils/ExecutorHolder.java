package com.lensim.fingerchat.commons.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LL130386 on 2017/8/23.
 */

public class ExecutorHolder {

  private final static int CORE_POOL_SIZE = 5;

  public static final ExecutorService EXECUTOR;

  private static ExecutorService CHAT_UPDATES_EXECUTOR;
  private static ExecutorService RECENT_EXECUTOR;
  private static ExecutorService NOTIFICATION_EXECUTOR;
  private static ExecutorService PROFILE_EXECUTOR;
  private static ScheduledExecutorService ONLINE_EXECUTOR;
  private static ExecutorService MOMENT_EXECUTOR;

  static {
    int corePoolSize = getCorePoolSize();
    EXECUTOR = new ThreadPoolExecutor(corePoolSize, getPoolSize(corePoolSize),
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
  }

  private static int getCorePoolSize() {
    int cores = DeviceHelper.getNumCores();
    return cores > CORE_POOL_SIZE ? cores : CORE_POOL_SIZE;
  }

  private static int getPoolSize(int corePoolSize) {
    return corePoolSize * 3;
  }

  public static synchronized ExecutorService getMomentExecutor() {
    if (MOMENT_EXECUTOR == null) {
      MOMENT_EXECUTOR = Executors.newSingleThreadExecutor();
    }
    return MOMENT_EXECUTOR;
  }

  public static synchronized ExecutorService getChatUpdatesExecutor() {
    if (CHAT_UPDATES_EXECUTOR == null) {
      CHAT_UPDATES_EXECUTOR = Executors.newSingleThreadExecutor();
    }
    return CHAT_UPDATES_EXECUTOR;
  }

  public static synchronized ExecutorService getRecentExecutor() {
    if (RECENT_EXECUTOR == null) {
      RECENT_EXECUTOR = Executors.newFixedThreadPool(2);
    }
    return RECENT_EXECUTOR;
  }

  public static synchronized ExecutorService getNotificationExecutor() {
    if (NOTIFICATION_EXECUTOR == null) {
      NOTIFICATION_EXECUTOR = Executors.newSingleThreadExecutor();
    }
    return NOTIFICATION_EXECUTOR;
  }


  public static synchronized ExecutorService getProfileExecutor() {
    if (PROFILE_EXECUTOR == null) {
      PROFILE_EXECUTOR = Executors.newFixedThreadPool(3);
    }
    return PROFILE_EXECUTOR;
  }

  public static synchronized ScheduledExecutorService getOnlineExecutor() {
    if (ONLINE_EXECUTOR == null) {
      ONLINE_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    }
    return ONLINE_EXECUTOR;
  }

}

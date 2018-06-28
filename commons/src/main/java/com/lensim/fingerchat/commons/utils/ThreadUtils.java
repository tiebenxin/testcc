package com.lensim.fingerchat.commons.utils;

import android.os.Handler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by LL130386 on 2017/12/27.
 */

public class ThreadUtils {

  public static Handler handler = new Handler();
  public static ExecutorService backgroundExecutor = Executors
      .newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
          Thread thread = new Thread(runnable, "MyApplication的任务运行在后台");
          thread.setPriority(Thread.MIN_PRIORITY);
          thread.setDaemon(true);
          return thread;
        }
      });


  public static void runOnUiThread(final Runnable runnable) {
    if (handler == null) {
      handler = new Handler();
    }
    handler.post(runnable);
  }

  /**
   * Submits request to be executed in background.
   */
  public static void runInBackground(final Runnable runnable) {
    if (backgroundExecutor == null){
      backgroundExecutor = Executors
          .newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
              Thread thread = new Thread(runnable, "MyApplication的任务运行在后台");
              thread.setPriority(Thread.MIN_PRIORITY);
              thread.setDaemon(true);
              return thread;
            }
          });
    }
    backgroundExecutor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          runnable.run();
        } catch (Exception e) {
          L.e(runnable.getClass().getSimpleName(), e);
        }
      }
    });
  }

}

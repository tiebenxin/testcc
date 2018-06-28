package com.lensim.fingerchat.fingerchat;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by zm on 2018/5/21.
 */

public class _FGApplication extends FGApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        initLeakCanary();
        initStetho();
    }

    /**
     * 初始化内存泄漏检测工具
     * {@link https://github.com/square/leakcanary}
     */
    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    /**
     * 初始化stetho
     * {@link https://github.com/facebook/stetho}
     */
    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }
}

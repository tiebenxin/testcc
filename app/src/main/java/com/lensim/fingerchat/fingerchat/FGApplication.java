package com.lensim.fingerchat.fingerchat;

import static com.lensim.fingerchat.commons.helper.ContextHelper.getApplication;

import android.support.multidex.MultiDexApplication;
import com.lens.chatmodel.base.ChatEnvironment;
import com.lensim.fingerchat.commons.app.BuildInfo;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.map.BaiduSDK;
import com.lensim.fingerchat.commons.router.FGRouter;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.data.me.picture.CipherImageDecoder;
import com.lensim.fingerchat.data.me.picture.CipherUnlimitedDiskCache;
import com.lensim.fingerchat.fingerchat.component.carsh.CrashHandler;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FGApplication extends MultiDexApplication {

    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCghPCWCobG8nTD24juwSVataW7iViRxcTkey/B792VZEhuHjQvA3cAJgx2Lv8GnX8NIoShZtoCg3Cx6ecs+VEPD2fBcg2L4JK7xldGpOJ3ONEAyVsLOttXZtNXvyDZRijiErQALMTorcgi79M5uVX9/jMv2Ggb2XAeZhlLD28fHwIDAQAB";
    public static final String ALLOTSERVER = "http://10.3.7.111:9696/";
    public static final String TEST_SERVER = "oa.fingerchat.net:9999";
    //    public static final String LOCAL_SERVER = "172.16.6.54:9999";
    public static final String LOCAL_SERVER = "172.16.6.211:9999";


    @Override
    public void onCreate() {
        super.onCreate();
        ContextHelper.setContext(getApplicationContext());
        ContextHelper.setApplication(this);
        initMap();
//        initHexMeet();
        initChatEnvironment();
        initImageLoader();
        initCrash();
        FGRouter.init();
    }


    /***
     * 初始化定位sdk，建议在Application中创建
     */
    private void initMap() {
        BaiduSDK.initSDK(getApplication());
    }

    private void initHexMeet() {
//        mHexMeet = new App();
//        mHexMeet.init();
    }

    private void initCrash() {
        if (!BuildConfig.DEBUG) {
            CrashHandler.getInstance().init(getApplicationContext());
        }
    }

    /*
    * 初始化聊天模块
    * */
    private void initChatEnvironment() {
        ChatEnvironment.getInstance().init();
    }

    private void initImageLoader() {
        CipherUnlimitedDiskCache diskCache;
        diskCache = new CipherUnlimitedDiskCache(
            FileUtil.getDiskCacheDirs(getApplication(), "f_images"), null,
            new HashCodeFileNameGenerator());
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
            getApplication())
            .imageDecoder(new CipherImageDecoder(BuildInfo.DEBUG))
            .diskCache(diskCache)
            .defaultDisplayImageOptions(displayImageOptions)
            // 将保存的时候的URI名称用MD5 加密
            .writeDebugLogs() // Remove for release app
            .threadPoolSize(5)//线程池内加载的数量
            .threadPriority(Thread.NORM_PRIORITY - 2)  //降低线程的优先级保证主UI线程不受太大影响
            .build();// 开始构建
        ImageLoader.getInstance().init(config);
    }
}

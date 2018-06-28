package com.lensim.fingerchat.commons.map;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;
import com.baidu.mapapi.SDKInitializer;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.map.service.LocationService;

/**
 * Created by LL130386 on 2017/12/28.
 */

public class BaiduSDK {

    private static LocationService locationService;

    public static void initSDK(Application application) {
//        Vibrator mVibrator = (Vibrator) application.getSystemService(Service.VIBRATOR_SERVICE);
        locationService = new LocationService(ContextHelper.getApplication());
        SDKInitializer.initialize(application);
    }

    public static LocationService getLocationService() {
        if (locationService == null) {
            locationService = new LocationService(ContextHelper.getApplication());
        }
        return locationService;
    }

}

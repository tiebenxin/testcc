package com.lensim.fingerchat.commons.router;

import android.util.Log;
import com.example.annotation.RouteConfig;
import java.lang.reflect.Method;

/**
 * date on 2018/3/19
 * author ll147996
 * describe
 */

public class FGRouter {

    public static void init() {
        try {
            Class<?> klass = Class.forName(RouteConfig.PACKAGE_NAME + "." + RouteConfig.CLASS_NAME);
            Method method = klass.getDeclaredMethod(RouteConfig.METHOD_NAME);
            method.invoke(null);
        } catch (Exception e) {
            Log.e("FGRouter",e.getMessage());
            e.printStackTrace();
        }

    }

}

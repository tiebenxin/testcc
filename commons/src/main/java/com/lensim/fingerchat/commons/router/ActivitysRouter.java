package com.lensim.fingerchat.commons.router;

import android.content.Context;
import android.content.Intent;
import java.util.HashMap;
import java.util.Map;

/**
 * date on 2018/3/8
 * author ll147996
 * describe 如果 ActivitysRouter 包名、类名、方法getInstance或者方法register 其中有一个有变化
 * 就需要 变更 “RouteConfig.ACTIVITYS_ROUTER”
 */

public class ActivitysRouter {

    private final Map<String, Class<?>> mRules = new HashMap<>();

    private ActivitysRouter() {

    }

    public static ActivitysRouter getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton{
        private static final ActivitysRouter INSTANCE = new ActivitysRouter();
    }


    public void register(String uri, Class<?> clazz) {
        mRules.put(uri, clazz);
    }

    public Intent invoke(Context ctx, String uri) {
        return invoke(ctx,uri, null);
    }


    public Intent invoke(Context ctx, String uri, Rule rule) {
        Class<?> clazz = mRules.get(uri);
        if (clazz == null) {
            return null;
        }
        Intent intent = new Intent(ctx, clazz);
        if (rule == null) {
            return intent;
        } else {
            return rule.setRule(intent, uri);
        }
    }


    public boolean isExistUri(String uri) {
        return mRules.get(uri) != null;
    }


}

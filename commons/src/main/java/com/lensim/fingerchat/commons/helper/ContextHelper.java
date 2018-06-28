package com.lensim.fingerchat.commons.helper;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.interf.BaseManagerInterface;
import com.lensim.fingerchat.commons.interf.BaseUIListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class ContextHelper {

    private static Context context;
    private static Application application;
    private Map<Class<? extends BaseManagerInterface>, Collection<? extends BaseManagerInterface>> managerInterfaces = new HashMap<>();
    private Map<Class<? extends BaseUIListener>, Collection<? extends BaseUIListener>> uiListeners = new HashMap<>();
    private final ArrayList<Object> registeredManagers = new ArrayList<>();

    public static void setContext(Context c) {
        context = c;
    }

    public static Context getContext() {
        return context;
    }

    public static void setApplication(Application a) {
        application = a;
    }

    public static Application getApplication() {
        return application;
    }


    public static String getString(int id) {
        return getContext().getString(id);
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static Drawable getDrawable(int drawableId) {
        return ContextCompat.getDrawable(getContext(), drawableId);
    }

    public static Bitmap getBitmap(int drawableId) {
        return BitmapFactory
            .decodeStream(getContext().getResources().openRawResource(drawableId));
    }

    public static int getColor(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    public static float getDimension(int dimen) {
        return getContext().getResources().getDimension(dimen);
    }

    public static void setBackground(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    //判断是否是gif图片
    public static EUploadFileType configFileType(String path) {
        if (!TextUtils.isEmpty(path) && path.contains(".")) {
            int index = path.lastIndexOf(".") + 1;
            String fileType = path.substring(index, path.length());
            if (!TextUtils.isEmpty(fileType)) {
                if (fileType.equalsIgnoreCase("gif")) {
                    return EUploadFileType.GIF;
                } else if (fileType.equalsIgnoreCase("mp4")) {
                    return EUploadFileType.VIDEO;
                } else if (fileType.equalsIgnoreCase("mp3")) {
                    return EUploadFileType.VOICE;
                } else if (fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("png")) {
                    return EUploadFileType.JPG;
                }
            }
        }
        return null;
    }

    //判断是否是video
    public static boolean isVideo(String path) {
        if (!TextUtils.isEmpty(path) && path.contains(".")) {
            int index = path.lastIndexOf(".") + 1;
            String fileType = path.substring(index, path.length());
            if (!TextUtils.isEmpty(fileType) && fileType.equalsIgnoreCase("mp4")) {
                return true;
            }
        }
        return false;
    }

    //判断是否是gif图片
    public static boolean isGif(String path) {
        if (!TextUtils.isEmpty(path) && path.contains(".")) {
            int index = path.lastIndexOf(".") + 1;
            String fileType = path.substring(index, path.length());
            if (!TextUtils.isEmpty(fileType) && fileType.equalsIgnoreCase("gif")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 注册新的管理者
     */
    public void addManager(Object manager) {
        registeredManagers.add(manager);
    }


    @SuppressWarnings("unchecked")
    private <T extends BaseUIListener> Collection<T> getOrCreateUIListeners(Class<T> cls) {
        Collection<T> collection = (Collection<T>) uiListeners.get(cls);
        if (collection == null) {
            collection = new ArrayList<T>();
            uiListeners.put(cls, collection);
        }
        return collection;
    }


    /**
     * @param cls Requested class of managers.
     * @return List of registered manager.
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseManagerInterface> Collection<T> getManagers(Class<T> cls) {
//        if (closed) {
//            return Collections.emptyList();
//        }
        Collection<T> collection = (Collection<T>) managerInterfaces.get(cls);
        if (collection == null) {
            collection = new ArrayList<>();
            for (Object manager : registeredManagers) {
                if (cls.isInstance(manager)) {
                    collection.add((T) manager);
                }
            }
            collection = Collections.unmodifiableCollection(collection);
            managerInterfaces.put(cls, collection);
        }
        return collection;
    }
}

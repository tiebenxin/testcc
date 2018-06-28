package com.lens.chatmodel.notification;


import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by 周哥 on 2016/10/11.
 */

public class BaseNotificationProvider<T extends NotificationItem> implements NotificationProvider<T> {
    protected final Collection<T> items;
    private final int icon;
    private boolean canClearNotifications;

    public BaseNotificationProvider(int icon) {
        super();
        this.items = new ArrayList<T>();
        this.icon = icon;
        canClearNotifications = true;
    }


    public void add(T item, Boolean notify) {
        boolean exists = items.remove(item);
        if (notify == null)
            notify = !exists;
        items.add(item);
        NotificationManager.getInstance().updateNotifications(this,
                notify ? item : null);
    }

    public boolean remove(T item) {
        boolean result = items.remove(item);
        if (result)
            NotificationManager.getInstance().updateNotifications(this, null);
        return result;
    }

    public void setCanClearNotifications(boolean canClearNotifications) {
        this.canClearNotifications = canClearNotifications;
    }

    @Override
    public Collection<T> getNotifications() {
        return Collections.unmodifiableCollection(items);
    }

    @Override
    public boolean canClearNotifications() {
        return canClearNotifications;
    }

    @Override
    public void ClearNotifications() {
        items.clear();
    }

    @Override
    public Uri getSound() {

        return Settings.System.DEFAULT_NOTIFICATION_URI;
        //return SettingsManager.eventsSound();
    }

    @Override
    public int getStreamType() {
        return AudioManager.STREAM_NOTIFICATION;
    }

    @Override
    public int getIcon() {
        return icon;
    }

}

package com.fingerchat.api.connection;

/**
 * Created by LY309313 on 2017/9/22.
 */

public interface SessionStorage {

    void saveSession(String sessionContext);

    String getSession();

    void clearSession();
}

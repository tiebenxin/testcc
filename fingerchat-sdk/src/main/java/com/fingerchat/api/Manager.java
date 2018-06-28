package com.fingerchat.api;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.util.Objects;

import java.lang.ref.WeakReference;

/**
 * Created by LY309313 on 2017/11/9.
 */

public abstract class Manager {

   final WeakReference<Connection> weakConnection;

    public Manager(Connection connection){
        Objects.requireNonNull(connection,"connection must not be null");

        weakConnection = new WeakReference<Connection>(connection);
    }

    protected final Connection connection(){
      return   weakConnection.get();
    }
}

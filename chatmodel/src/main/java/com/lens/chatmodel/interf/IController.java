package com.lens.chatmodel.interf;

import android.view.View;

/**
 * Created by LL130386 on 2017/11/25.
 */

public interface IController<T> {

  View getView();

  void setModel(T t, int postion);

}

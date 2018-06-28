//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import com.lensim.fingerchat.hexmeet.widget.wheel.WheelView;


public interface WheelViewAdapter {

  int getItemsCount();

  View getItem(int var1, View var2, ViewGroup var3);

  View getEmptyItem(View var1, ViewGroup var2);

  void registerDataSetObserver(DataSetObserver var1);

  void unregisterDataSetObserver(DataSetObserver var1);

  void setWheelView(WheelView var1);
}

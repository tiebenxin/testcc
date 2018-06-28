//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel.adapter;

import android.content.Context;

public class ArrayWheelAdapter<T> extends AbstractWheelTextAdapter {

  private T[] items;

  public ArrayWheelAdapter(Context context, T[] items) {
    super(context);
    this.items = items;
  }

  public CharSequence getItemText(int index) {
    if (index >= 0 && index < this.items.length) {
      T item = this.items[index];
      return (CharSequence) (item instanceof CharSequence ? (CharSequence) item : item.toString());
    } else {
      return null;
    }
  }

  public int getItemsCount() {
    return this.items.length;
  }
}

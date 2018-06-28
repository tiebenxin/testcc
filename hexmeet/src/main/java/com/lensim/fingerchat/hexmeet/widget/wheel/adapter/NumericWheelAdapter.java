//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel.adapter;

import android.content.Context;

public class NumericWheelAdapter extends AbstractWheelTextAdapter {

  public static final int DEFAULT_MAX_VALUE = 9;
  private static final int DEFAULT_MIN_VALUE = 0;
  private int minValue;
  private int maxValue;
  private String format;

  public NumericWheelAdapter(Context context) {
    this(context, 0, 9);
  }

  public NumericWheelAdapter(Context context, int minValue, int maxValue) {
    this(context, minValue, maxValue, (String) null);
  }

  public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
    super(context);
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.format = format;
  }

  public CharSequence getItemText(int index) {
    if (index >= 0 && index < this.getItemsCount()) {
      int value = this.minValue + index;
      return this.format != null ? String.format(this.format, new Object[]{Integer.valueOf(value)}) : Integer.toString(value);
    } else {
      return null;
    }
  }

  public int getItemsCount() {
    return this.maxValue - this.minValue + 1;
  }
}

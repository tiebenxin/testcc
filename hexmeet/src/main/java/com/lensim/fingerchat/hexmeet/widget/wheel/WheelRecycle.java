//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel;

import android.view.View;
import android.widget.LinearLayout;
import java.util.LinkedList;
import java.util.List;

public class WheelRecycle {

  private List<View> items;
  private List<View> emptyItems;
  private WheelView wheel;

  public WheelRecycle(WheelView wheel) {
    this.wheel = wheel;
  }

  public int recycleItems(LinearLayout layout, int firstItem, ItemsRange range) {
    int index = firstItem;

    for (int i = 0; i < layout.getChildCount(); ++index) {
      if (!range.contains(index)) {
        this.recycleView(layout.getChildAt(i), index);
        layout.removeViewAt(i);
        if (i == 0) {
          ++firstItem;
        }
      } else {
        ++i;
      }
    }

    return firstItem;
  }

  public View getItem() {
    return this.getCachedView(this.items);
  }

  public View getEmptyItem() {
    return this.getCachedView(this.emptyItems);
  }

  public void clearAll() {
    if (this.items != null) {
      this.items.clear();
    }

    if (this.emptyItems != null) {
      this.emptyItems.clear();
    }

  }

  private List<View> addView(View view, List<View> cache) {
    if (cache == null) {
      cache = new LinkedList();
    }

    ((List) cache).add(view);
    return (List) cache;
  }

  private void recycleView(View view, int index) {
    int count = this.wheel.getViewAdapter().getItemsCount();
    if ((index < 0 || index >= count) && !this.wheel.isCyclic()) {
      this.emptyItems = this.addView(view, this.emptyItems);
    } else {
      while (true) {
        if (index >= 0) {
          int var10000 = index % count;
          this.items = this.addView(view, this.items);
          break;
        }

        index += count;
      }
    }

  }

  private View getCachedView(List<View> cache) {
    if (cache != null && cache.size() > 0) {
      View view = (View) cache.get(0);
      cache.remove(0);
      return view;
    } else {
      return null;
    }
  }
}

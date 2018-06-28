//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.widget.wheel.WheelScroller.ScrollingListener;
import com.lensim.fingerchat.hexmeet.widget.wheel.adapter.WheelViewAdapter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WheelView extends View {

  private static final int[] SHADOWS_COLORS = new int[]{-15658735, 11184810, 11184810};
  private static final int ITEM_OFFSET_PERCENT = 10;
  private static final int PADDING = 10;
  private static final int DEF_VISIBLE_ITEMS = 5;
  private int currentItem = 0;
  private int visibleItems = 5;
  private int itemHeight = 0;
  private Drawable centerDrawable;
  private WheelScroller scroller;
  private boolean isScrollingPerformed;
  private int scrollingOffset;
  boolean isCyclic = false;
  private LinearLayout itemsLayout;
  private int firstItem;
  private WheelViewAdapter viewAdapter;
  private WheelRecycle recycle = new WheelRecycle(this);
  private List<OnWheelChangedListener> changingListeners = new LinkedList();
  private List<OnWheelScrollListener> scrollingListeners = new LinkedList();
  private List<OnWheelClickedListener> clickingListeners = new LinkedList();
  ScrollingListener scrollingListener = new ScrollingListener() {
    public void onStarted() {
      WheelView.this.isScrollingPerformed = true;
      WheelView.this.notifyScrollingListenersAboutStart();
    }

    public void onScroll(int distance) {
      WheelView.this.doScroll(distance);
      int height = WheelView.this.getHeight();
      if (WheelView.this.scrollingOffset > height) {
        WheelView.this.scrollingOffset = height;
        WheelView.this.scroller.stopScrolling();
      } else if (WheelView.this.scrollingOffset < -height) {
        WheelView.this.scrollingOffset = -height;
        WheelView.this.scroller.stopScrolling();
      }

    }

    public void onFinished() {
      if (WheelView.this.isScrollingPerformed) {
        WheelView.this.notifyScrollingListenersAboutEnd();
        WheelView.this.isScrollingPerformed = false;
      }

      WheelView.this.scrollingOffset = 0;
      WheelView.this.invalidate();
    }

    public void onJustify() {
      if (Math.abs(WheelView.this.scrollingOffset) > 1) {
        WheelView.this.scroller.scroll(WheelView.this.scrollingOffset, 0);
      }

    }
  };
  private DataSetObserver dataObserver = new DataSetObserver() {
    public void onChanged() {
      WheelView.this.invalidateWheel(true);
    }

    public void onInvalidated() {
      WheelView.this.invalidateWheel(true);
    }
  };

  public WheelView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.initData(context);
  }

  public WheelView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.initData(context);
  }

  public WheelView(Context context) {
    super(context);
    this.initData(context);
  }

  private void initData(Context context) {
    this.scroller = new WheelScroller(this.getContext(), this.scrollingListener);
  }

  public void setInterpolator(Interpolator interpolator) {
    this.scroller.setInterpolator(interpolator);
  }

  public int getVisibleItems() {
    return this.visibleItems;
  }

  public void setVisibleItems(int count) {
    this.visibleItems = count;
  }

  public WheelViewAdapter getViewAdapter() {
    return this.viewAdapter;
  }

  public void setViewAdapter(WheelViewAdapter viewAdapter) {
    if (this.viewAdapter != null) {
      this.viewAdapter.unregisterDataSetObserver(this.dataObserver);
    }

    this.viewAdapter = viewAdapter;
    this.viewAdapter.setWheelView(this);
    if (this.viewAdapter != null) {
      this.viewAdapter.registerDataSetObserver(this.dataObserver);
    }

    this.invalidateWheel(true);
  }

  public void addChangingListener(OnWheelChangedListener listener) {
    this.changingListeners.add(listener);
  }

  public void removeChangingListener(OnWheelChangedListener listener) {
    this.changingListeners.remove(listener);
  }

  protected void notifyChangingListeners(int oldValue, int newValue) {
    Iterator var4 = this.changingListeners.iterator();

    while (var4.hasNext()) {
      OnWheelChangedListener listener = (OnWheelChangedListener) var4.next();
      listener.onChanged(this, oldValue, newValue);
    }

  }

  public void addScrollingListener(OnWheelScrollListener listener) {
    this.scrollingListeners.add(listener);
  }

  public void removeScrollingListener(OnWheelScrollListener listener) {
    this.scrollingListeners.remove(listener);
  }

  protected void notifyScrollingListenersAboutStart() {
    Iterator var2 = this.scrollingListeners.iterator();

    while (var2.hasNext()) {
      OnWheelScrollListener listener = (OnWheelScrollListener) var2.next();
      listener.onScrollingStarted(this);
    }

  }

  protected void notifyScrollingListenersAboutEnd() {
    Iterator var2 = this.scrollingListeners.iterator();

    while (var2.hasNext()) {
      OnWheelScrollListener listener = (OnWheelScrollListener) var2.next();
      listener.onScrollingFinished(this);
    }

  }

  public void addClickingListener(OnWheelClickedListener listener) {
    this.clickingListeners.add(listener);
  }

  public void removeClickingListener(OnWheelClickedListener listener) {
    this.clickingListeners.remove(listener);
  }

  protected void notifyClickListenersAboutClick(int item) {
    Iterator var3 = this.clickingListeners.iterator();

    while (var3.hasNext()) {
      OnWheelClickedListener listener = (OnWheelClickedListener) var3.next();
      listener.onItemClicked(this, item);
    }

  }

  public int getCurrentItem() {
    return this.currentItem;
  }

  public void setCurrentItem(int index, boolean animated) {
    if (this.viewAdapter != null && this.viewAdapter.getItemsCount() != 0) {
      int itemCount = this.viewAdapter.getItemsCount();
      if (index < 0 || index >= itemCount) {
        if (!this.isCyclic) {
          return;
        }

        while (index < 0) {
          index += itemCount;
        }

        index %= itemCount;
      }

      if (index != this.currentItem) {
        int itemsToScroll;
        if (animated) {
          itemsToScroll = index - this.currentItem;
          if (this.isCyclic) {
            int scroll = itemCount + Math.min(index, this.currentItem) - Math.max(index, this.currentItem);
            if (scroll < Math.abs(itemsToScroll)) {
              itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
            }
          }

          this.scroll(itemsToScroll, 0);
          this.invalidateWheel(true);
        } else {
          this.scrollingOffset = 0;
          itemsToScroll = this.currentItem;
          this.currentItem = index;
          this.notifyChangingListeners(itemsToScroll, this.currentItem);
          this.invalidateWheel(true);
        }
      }

    }
  }

  public void setCurrentItem(int index) {
    this.setCurrentItem(index, false);
  }

  public boolean isCyclic() {
    return this.isCyclic;
  }

  public void setCyclic(boolean isCyclic) {
    this.isCyclic = isCyclic;
    this.invalidateWheel(false);
  }

  public void invalidateWheel(boolean clearCaches) {
    if (clearCaches) {
      this.recycle.clearAll();
      if (this.itemsLayout != null) {
        this.itemsLayout.removeAllViews();
      }

      this.scrollingOffset = 0;
    } else if (this.itemsLayout != null) {
      this.recycle.recycleItems(this.itemsLayout, this.firstItem, new ItemsRange());
    }

    this.invalidate();
  }

  private void initResourcesIfNecessary() {
    if (this.centerDrawable == null) {
      this.centerDrawable = this.getContext().getResources().getDrawable(R.drawable.wheel_val);
    }

  }

  private int getDesiredHeight(LinearLayout layout) {
    if (layout != null && layout.getChildAt(0) != null) {
      this.itemHeight = layout.getChildAt(0).getMeasuredHeight();
    }

    int desired = this.itemHeight * this.visibleItems - this.itemHeight * 10 / 50;
    return Math.max(desired, this.getSuggestedMinimumHeight());
  }

  private int getItemHeight() {
    if (this.itemHeight != 0) {
      return this.itemHeight;
    } else if (this.itemsLayout != null && this.itemsLayout.getChildAt(0) != null) {
      this.itemHeight = this.itemsLayout.getChildAt(0).getHeight();
      return this.itemHeight;
    } else {
      return this.getHeight() / this.visibleItems;
    }
  }

  private int calculateLayoutWidth(int widthSize, int mode) {
    this.initResourcesIfNecessary();
    this.itemsLayout.setLayoutParams(new LayoutParams(-2, -2));
    this.itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, 0), MeasureSpec.makeMeasureSpec(0, 0));
    int width = this.itemsLayout.getMeasuredWidth();
    if (mode == 1073741824) {
      width = widthSize;
    } else {
      width += 20;
      width = Math.max(width, this.getSuggestedMinimumWidth());
      if (mode == -2147483648 && widthSize < width) {
        width = widthSize;
      }
    }

    this.itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 20, 1073741824), MeasureSpec.makeMeasureSpec(0, 0));
    return width;
  }

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    this.buildViewForMeasuring();
    int width = this.calculateLayoutWidth(widthSize, widthMode);
    int height;
    if (heightMode == 1073741824) {
      height = heightSize;
    } else {
      height = this.getDesiredHeight(this.itemsLayout);
      if (heightMode == -2147483648) {
        height = Math.min(height, heightSize);
      }
    }

    this.setMeasuredDimension(width, height);
  }

  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    this.layout(r - l, b - t);
  }

  private void layout(int width, int height) {
    int itemsWidth = width - 20;
    this.itemsLayout.layout(0, 0, itemsWidth, height);
  }

  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (this.viewAdapter != null && this.viewAdapter.getItemsCount() > 0) {
      this.updateView();
      this.drawCenterRect(canvas);
      this.drawItems(canvas);
    }

    this.drawShadows(canvas);
  }

  private void drawShadows(Canvas canvas) {
    int height = (int) (1.5D * (double) this.getItemHeight());
  }

  private void drawItems(Canvas canvas) {
    canvas.save();
    int top = (this.currentItem - this.firstItem) * this.getItemHeight() + (this.getItemHeight() - this.getHeight()) / 2;
    canvas.translate(10.0F, (float) (-top + this.scrollingOffset));
    this.itemsLayout.draw(canvas);
    canvas.restore();
  }

  private void drawCenterRect(Canvas canvas) {
    int center = this.getHeight() / 2;
    int offset = (int) ((double) (this.getItemHeight() / 2) * 1.2D);
    this.centerDrawable.setBounds(0, center - offset, this.getWidth(), center + offset);
    this.centerDrawable.draw(canvas);
  }

  public boolean onTouchEvent(MotionEvent event) {
    if (this.isEnabled() && this.getViewAdapter() != null) {
      switch (event.getAction()) {
        case 1:
          if (!this.isScrollingPerformed) {
            int distance = (int) event.getY() - this.getHeight() / 2;
            if (distance > 0) {
              distance += this.getItemHeight() / 2;
            } else {
              distance -= this.getItemHeight() / 2;
            }

            int items = distance / this.getItemHeight();
            if (items != 0 && this.isValidItemIndex(this.currentItem + items)) {
              this.notifyClickListenersAboutClick(this.currentItem + items);
            }
          }
          break;
        case 2:
          if (this.getParent() != null) {
            this.getParent().requestDisallowInterceptTouchEvent(true);
          }
      }

      return this.scroller.onTouchEvent(event);
    } else {
      return true;
    }
  }

  private void doScroll(int delta) {
    this.scrollingOffset += delta;
    int itemHeight = this.getItemHeight();
    int count = this.scrollingOffset / itemHeight;
    int pos = this.currentItem - count;
    int itemCount = this.viewAdapter.getItemsCount();
    int fixPos = this.scrollingOffset % itemHeight;
    if (Math.abs(fixPos) <= itemHeight / 2) {
      fixPos = 0;
    }

    if (this.isCyclic && itemCount > 0) {
      if (fixPos > 0) {
        --pos;
        ++count;
      } else if (fixPos < 0) {
        ++pos;
        --count;
      }

      while (pos < 0) {
        pos += itemCount;
      }

      pos %= itemCount;
    } else if (pos < 0) {
      count = this.currentItem;
      pos = 0;
    } else if (pos >= itemCount) {
      count = this.currentItem - itemCount + 1;
      pos = itemCount - 1;
    } else if (pos > 0 && fixPos > 0) {
      --pos;
      ++count;
    } else if (pos < itemCount - 1 && fixPos < 0) {
      ++pos;
      --count;
    }

    int offset = this.scrollingOffset;
    if (pos != this.currentItem) {
      this.setCurrentItem(pos, false);
    } else {
      this.invalidate();
    }

    this.scrollingOffset = offset - count * itemHeight;
    if (this.scrollingOffset > this.getHeight()) {
      this.scrollingOffset = this.scrollingOffset % this.getHeight() + this.getHeight();
    }

  }

  public void scroll(int itemsToScroll, int time) {
    int distance = itemsToScroll * this.getItemHeight() - this.scrollingOffset;
    this.scroller.scroll(distance, time);
  }

  private ItemsRange getItemsRange() {
    if (this.getItemHeight() == 0) {
      return null;
    } else {
      int first = this.currentItem;

      int count;
      for (count = 1; count * this.getItemHeight() < this.getHeight(); count += 2) {
        --first;
      }

      if (this.scrollingOffset != 0) {
        if (this.scrollingOffset > 0) {
          --first;
        }

        ++count;
        int emptyItems = this.scrollingOffset / this.getItemHeight();
        first -= emptyItems;
        count = (int) ((double) count + Math.asin((double) emptyItems));
      }

      return new ItemsRange(first, count);
    }
  }

  private boolean rebuildItems() {
    boolean updated = false;
    ItemsRange range = this.getItemsRange();
    int first;
    if (this.itemsLayout != null) {
      first = this.recycle.recycleItems(this.itemsLayout, this.firstItem, range);
      updated = this.firstItem != first;
      this.firstItem = first;
    } else {
      this.createItemsLayout();
      updated = true;
    }

    if (!updated) {
      updated = this.firstItem != range.getFirst() || this.itemsLayout.getChildCount() != range.getCount();
    }

    if (this.firstItem > range.getFirst() && this.firstItem <= range.getLast()) {
      for (first = this.firstItem - 1; first >= range.getFirst() && this.addViewItem(first, true); this.firstItem = first--) {
        ;
      }
    } else {
      this.firstItem = range.getFirst();
    }

    first = this.firstItem;

    for (int i = this.itemsLayout.getChildCount(); i < range.getCount(); ++i) {
      if (!this.addViewItem(this.firstItem + i, false) && this.itemsLayout.getChildCount() == 0) {
        ++first;
      }
    }

    this.firstItem = first;
    return updated;
  }

  private void updateView() {
    if (this.rebuildItems()) {
      this.calculateLayoutWidth(this.getWidth(), 1073741824);
      this.layout(this.getWidth(), this.getHeight());
    }

  }

  private void createItemsLayout() {
    if (this.itemsLayout == null) {
      this.itemsLayout = new LinearLayout(this.getContext());
      this.itemsLayout.setOrientation(1);
    }

  }

  private void buildViewForMeasuring() {
    if (this.itemsLayout != null) {
      this.recycle.recycleItems(this.itemsLayout, this.firstItem, new ItemsRange());
    } else {
      this.createItemsLayout();
    }

    int addItems = this.visibleItems / 2;

    for (int i = this.currentItem + addItems; i >= this.currentItem - addItems; --i) {
      if (this.addViewItem(i, true)) {
        this.firstItem = i;
      }
    }

  }

  private boolean addViewItem(int index, boolean first) {
    View view = this.getItemView(index);
    if (view != null) {
      if (first) {
        this.itemsLayout.addView(view, 0);
      } else {
        this.itemsLayout.addView(view);
      }

      return true;
    } else {
      return false;
    }
  }

  private boolean isValidItemIndex(int index) {
    return this.viewAdapter != null && this.viewAdapter.getItemsCount() > 0 && (this.isCyclic || index >= 0 && index < this.viewAdapter.getItemsCount());
  }

  private View getItemView(int index) {
    if (this.viewAdapter != null && this.viewAdapter.getItemsCount() != 0) {
      int count = this.viewAdapter.getItemsCount();
      if (!this.isValidItemIndex(index)) {
        return this.viewAdapter.getEmptyItem(this.recycle.getEmptyItem(), this.itemsLayout);
      } else {
        while (index < 0) {
          index += count;
        }

        index %= count;
        return this.viewAdapter.getItem(index, this.recycle.getItem(), this.itemsLayout);
      }
    } else {
      return null;
    }
  }

  public void stopScrolling() {
    this.scroller.stopScrolling();
  }
}

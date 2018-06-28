//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import com.lensim.fingerchat.hexmeet.widget.wheel.WheelView;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractWheelAdapter implements WheelViewAdapter {

  private List<DataSetObserver> datasetObservers;

  public AbstractWheelAdapter() {
  }

  public View getEmptyItem(View convertView, ViewGroup parent) {
    return null;
  }

  public void registerDataSetObserver(DataSetObserver observer) {
    if (this.datasetObservers == null) {
      this.datasetObservers = new LinkedList();
    }

    this.datasetObservers.add(observer);
  }

  public void unregisterDataSetObserver(DataSetObserver observer) {
    if (this.datasetObservers != null) {
      this.datasetObservers.remove(observer);
    }

  }

  protected void notifyDataChangedEvent() {
    if (this.datasetObservers != null) {
      Iterator var2 = this.datasetObservers.iterator();

      while (var2.hasNext()) {
        DataSetObserver observer = (DataSetObserver) var2.next();
        observer.onChanged();
      }
    }

  }

  protected void notifyDataInvalidatedEvent() {
    if (this.datasetObservers != null) {
      Iterator var2 = this.datasetObservers.iterator();

      while (var2.hasNext()) {
        DataSetObserver observer = (DataSetObserver) var2.next();
        observer.onInvalidated();
      }
    }

  }

  public void setWheelView(WheelView wheel) {
  }
}

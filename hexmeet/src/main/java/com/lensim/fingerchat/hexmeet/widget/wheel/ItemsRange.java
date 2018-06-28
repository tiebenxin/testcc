//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel;

public class ItemsRange {

  private int first;
  private int count;

  public ItemsRange() {
    this(0, 0);
  }

  public ItemsRange(int first, int count) {
    this.first = first;
    this.count = count;
  }

  public int getFirst() {
    return this.first;
  }

  public int getLast() {
    return this.getFirst() + this.getCount() - 1;
  }

  public int getCount() {
    return this.count;
  }

  public boolean contains(int index) {
    return index >= this.getFirst() && index <= this.getLast();
  }
}

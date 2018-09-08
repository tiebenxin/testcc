package com.lensim.fingerchat.data.bean;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class GetFavoListBody {

  private int pageIndex;
  private int pageSize;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}

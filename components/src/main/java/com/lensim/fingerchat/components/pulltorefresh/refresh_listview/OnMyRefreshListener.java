package com.lensim.fingerchat.components.pulltorefresh.refresh_listview;

/**
 * Created by LL130386 on 2017/11/25.
 */

public interface OnMyRefreshListener {
  /**
   * 下拉刷新
   */
  void onDownPullRefresh();

  /**
   * 上拉加载更多
   */
  void onLoadingMore();

}

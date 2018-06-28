package com.lensim.fingerchat.components.pulltorefresh.refresh_listview;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.components.R;
import java.text.SimpleDateFormat;

/**
 * Created by LL130386 on 2017/11/25.
 *
 */

public class RefreshListView extends ListView implements OnScrollListener {

  private static final String TAG = "RefreshListView";
  private static final long OK_TIME = 700;
  private int firstVisibleItemPosition; // 屏幕显示在第一个的item的索引
  private int downY; // 按下时y轴的偏移量
  private int headerViewHeight; // 头布局的高度
  private View headerView; // 头布局的对象

  private final int DOWN_PULL_REFRESH = 0; // 下拉刷新状态
  private final int RELEASE_REFRESH = 1; // 松开刷新
  private final int REFRESHING = 2; // 正在刷新中
  private final int REFRESH_SUCCESS = 3; // 刷新成功
  private int currentState = DOWN_PULL_REFRESH; // 头布局的状态: 默认为下拉刷新状态

  private ProgressBar mProgressBar; // 头布局的进度条
  private TextView tvState; // 头布局的状态
//  private TextView tvLastUpdateTime; // 头布局的最后更新时间

  private OnMyRefreshListener mOnRefershListener;
  private boolean isScrollToBottom; // 是否滑动到底部
  //  private View footerView; // 脚布局的对象
  private int footerViewHeight; // 脚布局的高度
  private boolean isLoadingMore = false; // 是否正在加载更多中
  private ImageView iv_icon;
  private Handler mHandler = new Handler();
  private boolean isRefreshing = false; //是否正在刷新

  public RefreshListView(Context context) {
    super(context);
    initHeaderView();
    initFooterView();
    this.setOnScrollListener(this);
  }

  public RefreshListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initHeaderView();
    initFooterView();
    this.setOnScrollListener(this);
  }

  public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initHeaderView();
    initFooterView();
    this.setOnScrollListener(this);
  }

  private void initHeaderView() {
    if (headerView == null) {
      headerView = View.inflate(getContext(), R.layout.layout_refresh_header, null);
      iv_icon = headerView.findViewById(R.id.iv_icon);
      tvState = headerView.findViewById(R.id.tv_status);
      mProgressBar = headerView.findViewById(R.id.progressbar);
      headerView.measure(0, 0); // 系统会帮我们测量出headerView的高度
      headerViewHeight = headerView.getMeasuredHeight();
      headerView.setPadding(0, -headerViewHeight, 0, 0);
      this.addHeaderView(headerView); // 向ListView的顶部添加一个view对象
    }

  }

  private void initFooterView() {

  }

  public void hideHeaderView() {
    headerView.setPadding(0, -headerViewHeight, 0, 0);
    iv_icon.setVisibility(View.VISIBLE);
    iv_icon.setImageDrawable(ContextHelper.getDrawable(R.drawable.ic_drop_ref));
    mProgressBar.setVisibility(View.GONE);
    tvState.setText(ContextHelper.getString(R.string.pull_to_refresh));
//    tvLastUpdateTime.setText("最后刷新时间: " + getLastUpdateTime());
    currentState = DOWN_PULL_REFRESH;
  }

  public void hideFooterView() {

  }

  /**
   * 获得系统的最新时间
   */
  private String getLastUpdateTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(System.currentTimeMillis());
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case ACTION_DOWN:
        downY = (int) ev.getY();
        break;
      case ACTION_MOVE:
        int moveY = (int) ev.getY();
        // 移动中的y - 按下的y = 间距.
        int diff = (moveY - downY) / 2;
        // -头布局的高度 + 间距 = paddingTop
        int paddingTop = -headerViewHeight + diff;
        // 如果: -头布局的高度 > paddingTop的值 执行super.onTouchEvent(ev);
        if (firstVisibleItemPosition == 0
            && -headerViewHeight < paddingTop) {
          if (paddingTop >= 0 && currentState == DOWN_PULL_REFRESH) { // 完全显示了.
            Log.i(TAG, "松开刷新");
            currentState = RELEASE_REFRESH;
            refreshHeaderView();
          } else if (paddingTop < 0
              && currentState == RELEASE_REFRESH) { // 没有显示完全
            Log.i(TAG, "下拉刷新");
            currentState = DOWN_PULL_REFRESH;
            refreshHeaderView();
          }
          // 下拉头布局
//          L.i(RefreshListView.class.getSimpleName() + " paddingTop =" + paddingTop);
          headerView.setPadding(0, paddingTop, 0, 0);
          return true;
        }
        break;
      case ACTION_UP:
        // 判断当前的状态是松开刷新还是下拉刷新
        if (currentState == RELEASE_REFRESH) {
          // 把头布局设置为完全显示状态
          headerView.setPadding(0, 0, 0, 0);
          // 进入到正在刷新中状态
          currentState = REFRESHING;
          isRefreshing = true;
          refreshHeaderView();

          if (mOnRefershListener != null) {
            mOnRefershListener.onDownPullRefresh(); // 调用使用者的监听方法
          }
          L.i(RefreshListView.class.getSimpleName() + " RELEASE_REFRESH "
              + "eventUp");
        } else if (currentState == DOWN_PULL_REFRESH) {
          // 隐藏头布局
          headerView.setPadding(0, -headerViewHeight, 0, 0);
        } else if (currentState == REFRESHING) {
          // 正在刷新的时候显示头布局
          headerView.setPadding(0, 0, 0, 0);
        } else if (currentState == REFRESH_SUCCESS) {
          // 隐藏头布局
          headerView.setPadding(0, -headerViewHeight, 0, 0);
        } else {
          // 正在刷新的时候显示头布局
          headerView.setPadding(0, 0, 0, 0);
        }
        break;
    }

    return super.onTouchEvent(ev);

  }

  public boolean isRefreshing() {
    return isRefreshing;
  }

  /**
   * 根据currentState刷新头布局的状态
   */
  private void refreshHeaderView() {
    switch (currentState) {
      case DOWN_PULL_REFRESH: // 下拉刷新状态
        tvState.setText(ContextHelper.getString(R.string.pull_to_refresh));
        iv_icon.setVisibility(View.VISIBLE);
        iv_icon.setImageDrawable(ContextHelper.getDrawable(R.drawable.ic_drop_ref));
        break;
      case RELEASE_REFRESH: // 松开刷新状态
        tvState.setText(ContextHelper.getString(R.string.release_to_refresh));
        mProgressBar.setVisibility(View.GONE);
        iv_icon.setVisibility(View.VISIBLE);
        iv_icon.setImageDrawable(ContextHelper.getDrawable(R.drawable.ic_release_ref));

        break;
      case REFRESHING: // 正在刷新中状态
        iv_icon.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        tvState.setText(ContextHelper.getString(R.string.refreshing));
        break;
      case REFRESH_SUCCESS: // 刷新成功
        iv_icon.setVisibility(View.VISIBLE);
        iv_icon.setImageDrawable(ContextHelper.getDrawable(R.drawable.ic_success_ref));
        mProgressBar.setVisibility(View.GONE);
        tvState.setText(ContextHelper.getString(R.string.refresh_success));
        break;
      default:
        break;
    }
  }

  public void refreshCompleted() {
    currentState = REFRESH_SUCCESS;
    isRefreshing = false;
    refreshHeaderView();
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        hideHeaderView();
      }
    }, OK_TIME);
  }

  @Override
  public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE
        || scrollState == SCROLL_STATE_FLING) {
      // 判断当前是否已经到了底部
      if (isScrollToBottom && !isLoadingMore) {
        isLoadingMore = true;
        // 当前到底部
        Log.i(TAG, "加载更多数据");
//        footerView.setPadding(0, 0, 0, 0);
        this.setSelection(this.getCount());

        if (mOnRefershListener != null) {
          mOnRefershListener.onLoadingMore();
        }
      }
    }
  }

  @Override
  public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    firstVisibleItemPosition = firstVisibleItem;

    if (getLastVisiblePosition() == (totalItemCount - 1)) {
      isScrollToBottom = true;
    } else {
      isScrollToBottom = false;
    }
  }

  public void setOnRefreshListener(OnMyRefreshListener listener) {
    mOnRefershListener = listener;
  }
}

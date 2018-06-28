package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import java.util.ArrayList;

public class HangupPopupMenu extends PopupWindow {

  private Context mContext;

  //列表弹窗的间隔
  protected final int LIST_PADDING = 10;

  //实例化一个矩形
  private Rect mRect = new Rect();

  //坐标的位置（x、y）
  private final int[] mLocation = new int[2];

  //屏幕的宽度和高度
  private int mScreenWidth, mScreenHeight;

  //判断是否需要添加或更新列表子类项
  private boolean mIsDirty;

  //弹窗子类项选中时的监听
  private OnItemOnClickListener mItemOnClickListener;

  //定义列表对象
  private ListView mListView;

  //定义弹窗子类项列表
  private ArrayList<MenuItem> mActionItems = new ArrayList<MenuItem>();

  public HangupPopupMenu(Context context) {
    //设置布局的参数
    this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  public HangupPopupMenu(Context context, int width, int height) {
    this.mContext = context;

    //设置可以获得焦点
    setFocusable(true);
    //设置弹窗内可点击
    setTouchable(true);
    //设置弹窗外可点击
    setOutsideTouchable(true);

    //获得屏幕的宽度和高度
    mScreenWidth = ScreenUtil.getWidth(mContext);
    mScreenHeight = ScreenUtil.getHeight(mContext);

    //设置弹窗的宽度和高度
    setWidth(width);
    setHeight(height);

    setBackgroundDrawable(new BitmapDrawable());

    //设置弹窗的布局界面
    setContentView(LayoutInflater.from(mContext).inflate(R.layout.hangup_popup_menu, null));

    initUI();
  }

  /**
   * 初始化弹窗列表
   */
  private void initUI() {
    getContentView().findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    mListView = (ListView) getContentView().findViewById(R.id.title_list);
    mListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        //点击子类项后，弹窗消失
        dismiss();

        if (mItemOnClickListener != null) {
          mItemOnClickListener.onItemClick(mActionItems.get(index), index);
        }
      }
    });
  }

  /**
   * 显示弹窗列表界面
   */
  public void show(View view) {
    //判断是否需要添加或更新列表子类项
    if (mIsDirty) {
      populateActions();
    }

    //显示弹窗的位置
    showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
  }

  /**
   * 设置弹窗列表子项
   */
  private void populateActions() {
    mIsDirty = false;

    //设置列表的适配器
    mListView.setAdapter(new BaseAdapter() {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;

        if (convertView == null) {
          textView = new TextView(mContext);
          textView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dp_to_px(48)));
          textView.setBackgroundColor(Color.parseColor("#ffffff"));
          textView.setTextColor(Color.parseColor("#232323"));
          textView.setTextSize(15);
          textView.setPadding(ScreenUtil.dp_to_px(20), 0, 0, 0);
          textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
          textView.setSingleLine(false);
        } else {
          textView = (TextView) convertView;
          textView.setTextColor(Color.parseColor("#232323"));
        }

        MenuItem item = mActionItems.get(position);

        textView.setText(item.mText);
        if (item.mColor != -1) {
          textView.setTextColor(item.mColor);
        }

        return textView;
      }

      @Override
      public long getItemId(int position) {
        return position;
      }

      @Override
      public Object getItem(int position) {
        return mActionItems.get(position);
      }

      @Override
      public int getCount() {
        return mActionItems.size();
      }
    });
  }

  /**
   * 添加子类项
   */
  public void addItem(MenuItem action) {
    if (action != null) {
      mActionItems.add(action);
      mIsDirty = true;
    }
  }

  /**
   * 清除子类项
   */
  public void cleanAction() {
    if (mActionItems.isEmpty()) {
      mActionItems.clear();
      mIsDirty = true;
    }
  }

  /**
   * 根据位置得到子类项
   */
  public MenuItem getAction(int position) {
    if (position < 0 || position > mActionItems.size()) {
      return null;
    }
    return mActionItems.get(position);
  }

  /**
   * 设置监听事件
   */
  public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
    this.mItemOnClickListener = onItemOnClickListener;
  }

  /**
   * @author yangyu 功能描述：弹窗子类项按钮监听事件
   */
  public static interface OnItemOnClickListener {

    public void onItemClick(MenuItem item, int position);
  }
}

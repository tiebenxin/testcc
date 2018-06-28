package com.lensim.fingerchat.commons.map.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.mapapi.search.core.PoiInfo;
import com.lensim.fingerchat.commons.R;
import java.util.List;


public class MapPickerAdapter extends BaseAdapter {

  private final Context context;
  private LayoutInflater mInflater;
  private List<PoiInfo> resultList;
  private int notifyTip;

  public MapPickerAdapter(Context context, List<PoiInfo> items) {
    resultList = items;
    mInflater = LayoutInflater.from(context);
    this.context = context;
//    this.notifyTip = 0;
  }

  /**
   * 设置第几个item被选择
   */
  public void setNotifyTip(int notifyTip) {
    this.notifyTip = notifyTip;
  }

  @Override
  public int getCount() {
    return resultList.size();
  }

  @Override
  public Object getItem(int index) {
    return resultList.get(index);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup viewGroup) {

    MyViewHolder holder = null;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_location_adapter, viewGroup, false);
      holder = new MyViewHolder();
      holder.titleView = (TextView) convertView.findViewById(R.id.map_address);
      holder.subtitleView = (TextView) convertView.findViewById(R.id.map_des);
      holder.iconView = (ImageView) convertView.findViewById(R.id.map_check);
      convertView.setTag(holder);
    } else {
      holder = (MyViewHolder) convertView.getTag();
    }
    holder.titleView.setText(resultList.get(position).name);
    holder.subtitleView.setText(resultList.get(position).address);
    if (notifyTip == position) {
      holder.iconView.setVisibility(View.VISIBLE);
    } else {
      holder.iconView.setVisibility(View.GONE);
    }

    return convertView;
  }

  static class MyViewHolder {

    TextView titleView;
    TextView subtitleView;
    ImageView iconView;
  }

}
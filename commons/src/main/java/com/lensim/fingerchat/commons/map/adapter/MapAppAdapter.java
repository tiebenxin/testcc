package com.lensim.fingerchat.commons.map.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lensim.fingerchat.commons.R;
import java.util.List;

/**
 * Created by LL130386 on 2018/7/4.
 */

public class MapAppAdapter extends BaseAdapter {

    private final String BAIDU_PACKAGENAME = "com.baidu.BaiduMap";//百度地图包名
    private final String GAODE_PACKAGENAME = "com.autonavi.minimap";//高德地图包名
    private final String TENXUN_PACKAGENAME = "com.tencent.map";//腾讯地图包名
    private final String GOOGLE_PACKAGENAME = "com.google.android.apps.maps";//谷歌地图包名

    private final List<String> maps;
    Context context;

    public MapAppAdapter(Context context, List<String> l) {
        this.context = context;
        maps = l;
    }

    @Override
    public int getCount() {
        return maps.size();
    }

    @Override
    public String getItem(int position) {
        return maps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.item_maps, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        if (maps != null && maps.size() > 0) {
            String name = maps.get(position);
            if (name != null) {
                if (name.equals(BAIDU_PACKAGENAME)) {
                    holder.tv_context.setText("百度地图");
                } else if (name.equals(GOOGLE_PACKAGENAME)) {
                    holder.tv_context.setText("谷歌地图");
                } else if (name.equals(TENXUN_PACKAGENAME)) {
                    holder.tv_context.setText("腾讯地图");
                } else if (name.equals(GAODE_PACKAGENAME)) {
                    holder.tv_context.setText("高德地图");
                }
            }
        }

        return view;
    }

    class ViewHolder {

        private TextView tv_context;

        public ViewHolder(View v) {
            tv_context = (TextView) v.findViewById(R.id.tv_context);
        }
    }


}

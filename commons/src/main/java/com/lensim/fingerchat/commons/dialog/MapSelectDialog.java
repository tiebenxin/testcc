package com.lensim.fingerchat.commons.dialog;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.map.IMapDiglogListener;
import com.lensim.fingerchat.commons.map.adapter.MapAppAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by LL130386 on 2018/7/4.
 * 地图选择
 */

public class MapSelectDialog extends BaseDialog {

    private ListView listView;
    private Button bt_cancel;
    Context context;
    private IMapDiglogListener listener;
    private List<String> maps;

    public MapSelectDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public MapSelectDialog(Context context, int theme, List<String> maps) {
        super(context, theme);
        this.context = context;
        this.maps = maps;
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_map);
        listView = findViewById(R.id.lv_maps);
        bt_cancel = findViewById(R.id.btn_cancel);
        setMaps(maps);

    }

    public void setMaps(List<String> maps) {
        this.maps = maps;
        MapAppAdapter adapter = new MapAppAdapter(context, maps);
        listView.setAdapter(adapter);
        getTotalHeightofListView(listView, maps.size());

    }

    public static void getTotalHeightofListView(ListView listView, int total) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int count = mAdapter.getCount();
        if (count > total) {
            count = total;
        }

        for (int i = 0; i < count; i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mView.measure(0, 0);
            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    @Override
    public void initEvent() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null && maps != null) {
                    listener.onItemClick(maps.get(position));
                }

            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void processClick(View view) {

    }

    public void setListener(IMapDiglogListener l) {
        listener = l;
    }

}

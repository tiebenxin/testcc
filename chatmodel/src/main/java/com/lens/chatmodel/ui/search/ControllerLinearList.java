package com.lens.chatmodel.ui.search;

import android.database.DataSetObserver;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by LL130386 on 2018/4/27.
 */

public class ControllerLinearList {

    private LinearLayout _pnlValues;

    private BaseAdapter _adapter;

    public ControllerLinearList(LinearLayout linearLayout) {
        _pnlValues = linearLayout;
    }

    public void setAdapter(BaseAdapter adapter) {
        _adapter = adapter;
        _adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateListValues();
            }
        });
        updateListValues();
    }

    private void updateListValues() {
        _pnlValues.removeAllViews();
        int count = _adapter.getCount();
        if (count > 3) {
            count = 3;
        }
        for (int i = 0; i < count; i++) {
            _pnlValues.addView(_adapter.getView(i, null, null));
        }
    }
}

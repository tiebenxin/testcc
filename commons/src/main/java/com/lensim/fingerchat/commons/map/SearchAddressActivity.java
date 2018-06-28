package com.lensim.fingerchat.commons.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.baidu.mapapi.search.poi.PoiResult;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.map.adapter.MapSearchAdapter;
import com.lensim.fingerchat.commons.map.bean.LocationBean;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import java.util.ArrayList;
import java.util.List;

public class SearchAddressActivity extends BaseActivity implements View.OnClickListener,
    AdapterView.OnItemClickListener {

    private Button btnSearch;
    private EditText content;
    private ListView listView;
    private MapSearchAdapter adapter;
    private String searchText;
    private String city;
    private List<LocationBean> mData;
    private ProgressDialog progressDialog;
    private FGToolbar toolbar;


    @Override
    public void initView() {
        setContentView(R.layout.activity_search_address);
        toolbar = findViewById(R.id.toolbar_search_location);
        toolbar.setTitleText("搜索");
        initBackButton(toolbar, true);

        mData = new ArrayList<LocationBean>();
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);

        content = (EditText) findViewById(R.id.input_edittext);
        listView = (ListView) findViewById(R.id.listview);
        btnSearch.setOnClickListener(this);
        adapter = new MapSearchAdapter(this, mData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("搜索中...");

//    LatLng position = getIntent().getParcelableExtra("position");
        city = getIntent().getStringExtra("city");
//    lp = new LatLng(position.latitude, position.longitude);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_search) {
            dealSearch();
        }
    }

    private void dealSearch() {
        searchText = content.getText().toString().trim();
//      ToastUtil.show(this, "请输入搜索关键字");
        if (!TextUtils.isEmpty(searchText)) {
            progressDialog.show();
            searchPlaces(city, searchText, 10);
        }
    }

    void hideKeyBoard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void searchPlaces(String cityName, final String keyName, int pageNum) {
        SearchPoiUtil
            .getPoiByPoiSearch(cityName, keyName, pageNum, new SearchPoiUtil.PoiSearchListener() {
                @Override
                public void onGetSucceed(List<LocationBean> locationList, PoiResult res) {
                    progressDialog.dismiss();
                    if (keyName.length() > 0) {
                        if (mData == null) {
                            mData = new ArrayList<LocationBean>();
                        }
                        mData.clear();
                        mData.addAll(locationList);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onGetFailed() {
                    progressDialog.dismiss();
                    if (mData != null) {
                        mData.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        hideKeyBoard();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        LocationBean addressInfo = mData.get(position);
        intent.putExtra("searchResult", addressInfo);
        setResult(RESULT_OK, intent);
        finish();
    }
}

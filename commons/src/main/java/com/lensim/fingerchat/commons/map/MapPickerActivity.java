package com.lensim.fingerchat.commons.map;


import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ZoomControls;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.map.adapter.MapPickerAdapter;
import com.lensim.fingerchat.commons.map.bean.LocationBean;
import com.lensim.fingerchat.commons.map.bean.MapInfoEntity;
import com.lensim.fingerchat.commons.map.service.LocationService;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.compress.CircleImage;
import com.lensim.fingerchat.commons.utils.compress.ImageCompress;
import com.lensim.fingerchat.commons.utils.compress.ImageInterface;
import com.lensim.fingerchat.commons.utils.compress.OnCompressListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MapPickerActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String ADDDRESS = "address";
    private static final int SEARCH_ADDDRESS = 7894;
    // 位置列表
    MapPickerAdapter mAdapter;
    ArrayList<PoiInfo> mInfoList;
    PoiInfo mCurentInfo;
    private ListView list;
    private TextView status;
    private ProgressBar loading;
    private ImageButton defineMyLocationButton;
    private FGToolbar toolbar;
    //百度地图相关
    private LocationService locationService;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 当前经纬度和地理信息
    private LatLng mLoactionLatLng;
    private String mAddress;
    private String mStreet;
    private String mName;
    private String mCity;
    // 地理编码监听器
    OnGetGeoCoderResultListener GeoListener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检索到结果
            }
            // 获取地理编码结果
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有找到检索结果
                status.setText(R.string.picker_internalerror);
                status.setVisibility(View.VISIBLE);
            }
            // 获取反向地理编码结果
            else {
                status.setVisibility(View.GONE);
                // 当前位置信息
                mLoactionLatLng = result.getLocation();
                mAddress = result.getAddress();
                mName = result.getAddressDetail().street;
                mStreet = result.getAddressDetail().street;
                mCity = result.getAddressDetail().city;
                mCurentInfo = new PoiInfo();
                mCurentInfo.address = result.getAddress();
                mCurentInfo.location = result.getLocation();
                mCurentInfo.name = "[位置]";
                mInfoList.clear();
                mInfoList.add(mCurentInfo);
                // 将周边信息加入表
                if (result.getPoiList() != null) {
                    mInfoList.addAll(result.getPoiList());
                }
                mAdapter.setNotifyTip(0);
                // 通知适配数据已改变
                mAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
            }
        }
    };
    private boolean isLocateFinished = false;//定位我的位置是否结束
    private boolean isLocationParamsNull = true;
    private MapInfoEntity para_addressInfo;//Intent传入
    // 设置第一次定位标志
    private boolean isFirstLoc = true;
    // MapView中央对于的屏幕坐标
    private Point mCenterPoint = null;
    // 地理编码
    private GeoCoder mGeoCoder = null;
    // 地图触摸事件监听器
    BaiduMap.OnMapTouchListener touchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mCenterPoint == null) {
                    return;
                }
                // 获取当前MapView中心屏幕坐标对应的地理坐标
                LatLng currentLatLng;
                currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
                // 发起反地理编码检索
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(currentLatLng));
                loading.setVisibility(View.VISIBLE);
            }
        }
    };
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.e("BDLocation1", location.getCity() + location.getDistrict());
            isLocateFinished = true;
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Log.e("BDLocation", location.getCity() + location.getDistrict());
                MyLocationData data = new MyLocationData.Builder()//
                    // .direction(mCurrentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();
                mBaiduMap.setMyLocationData(data);

                if (isLocationParamsNull) {
                    // 设置自定义图标
                    MyLocationConfiguration config = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, null);
                    mBaiduMap.setMyLocationConfigeration(config);
                    mAddress = location.getAddrStr();
                    mName = location.getBuildingName();
                    mStreet = location.getStreet();
                    if (StringUtils.isEmpty(mName)) {
                        mName = mStreet;
                    }
                    mCity = location.getCity();
                    LatLng currentLatLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                    mLoactionLatLng = currentLatLng;
                    showInfoWindow(mName, mAddress, mLoactionLatLng);
                    // 实现动画跳转
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
                    mBaiduMap.animateMapStatus(u);
                    mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(currentLatLng));
                }
            }
        }
    };

    public static void openActivity(Activity context, int requestCode, MapInfoEntity addressInfo) {
        Intent intent = new Intent(context, MapPickerActivity.class);
        intent.putExtra(ADDDRESS, addressInfo);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationService = BaiduSDK.getLocationService();
        // 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，
        // 可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        // 注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        locationService.start();
        mMapView.onResume();
    }

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        super.onStop();
        locationService.stop(); // 停止定位服务
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener); // 注销掉监听
        mMapView.onDestroy();
        mGeoCoder.destroy();
    }


    protected void searchButtonClick() {
        if (isLocateFinished) {
            Intent intent = new Intent(this, SearchAddressActivity.class);
            intent.putExtra("city", mCity);
            startActivityForResult(intent, SEARCH_ADDDRESS);
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_map_picker);
        toolbar = findViewById(R.id.toolbar_share_location);
        toolbar.setTitleText("位置 ");
        initBackButton(toolbar, true);

        if (isLocateFinished) {
            toolbar.setConfirmBt("发送");
        } else {
            toolbar.setConfirmBt("确定");
        }

        toolbar.setConfirmBt(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocateFinished) {
              takeShotAndFinish();
                }
            }
        });

        toolbar.setBtImageDrawable(R.drawable.title_search, new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonClick();
            }
        });

        initMap();

        defineMyLocationButton = (ImageButton) findViewById(R.id.define_my_location);
        defineMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocateFinished) {
                    toMyPosition();
                }
            }
        });
        drawParamLocation();
    }

    public void drawParamLocation() {
        para_addressInfo = getIntent().getParcelableExtra(ADDDRESS);
        if (para_addressInfo != null) {
            PoiInfo info = new PoiInfo();
            info.name = para_addressInfo.getAddressName();
            info.address = para_addressInfo.getStreet();
            info.location = new LatLng(para_addressInfo.getLatitude(),
                para_addressInfo.getLongitude());
            mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(info.location));
            drawMarker(info);
        }
    }


    private void showInfoWindow(String name, String address, LatLng position) {
        View view = LayoutInflater.from(MapPickerActivity.this)
            .inflate(R.layout.map_infowindow, null);
        TextView tvname = (TextView) view.findViewById(R.id.infowindo_name);
        TextView tvAddress = (TextView) view.findViewById(R.id.infowindo_address);
        tvname.setText(name);
        tvAddress.setText(address);
        InfoWindow infoWindow = new InfoWindow(view, position, -40);
        mBaiduMap.showInfoWindow(infoWindow);
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.bmap_view);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(touchListener);
        // 初始化POI信息列表
        mInfoList = new ArrayList<PoiInfo>();
        // 初始化当前MapView中心屏幕坐标，初始化当前地理坐标
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;
        // 定位
        mBaiduMap.setMyLocationEnabled(true);
        // 隐藏百度logo ZoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // 隐藏比例尺
        //mMapView.showScaleControl(false);
        // 地理编码
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(GeoListener);
        list = (ListView) findViewById(R.id.list);
//    list.setOnScrollListener(this);
        list.setOnItemClickListener(this);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        loading = (ProgressBar) findViewById(R.id.loading);
        status = (TextView) findViewById(R.id.status);
        mAdapter = new MapPickerAdapter(MapPickerActivity.this, mInfoList);
        list.setAdapter(mAdapter);
    }

    public void toMyPosition() {
        MyLocationData location = mBaiduMap.getLocationData();
        // 实现动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory
            .newLatLng(new LatLng(location.latitude, location.longitude));
        mBaiduMap.animateMapStatus(u);
        mBaiduMap.clear();
        // 发起反地理编码检索
        mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
            .location(new LatLng(location.latitude, location.longitude)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SEARCH_ADDDRESS) {
            LocationBean result = null;
            if (data.getParcelableExtra("searchResult") instanceof LocationBean) {
                result = data.getParcelableExtra("searchResult");
                LatLng location = new LatLng(result.getLatitude(), result.getLongitude());
                PoiInfo info = new PoiInfo();
                info.location = location;
                info.name = result.getLocName();
                info.address = result.getAddStr();
                info.city = result.getCity();
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(info.location));
                drawMarker(info);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mAdapter.setNotifyTip(position);
        mAdapter.notifyDataSetChanged();
        PoiInfo info = (PoiInfo) mAdapter.getItem(position);
        drawMarker(info);
    }

    public void drawMarker(PoiInfo info) {
        isLocationParamsNull = false;

        BitmapDescriptor mSelectIco = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_map_marker);
        mBaiduMap.clear();
        LatLng la = info.location;
        // 动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);
        // 添加覆盖物
        OverlayOptions ooA = new MarkerOptions().position(la).icon(mSelectIco).anchor(0.5f, 0.5f);
        showInfoWindow(info.name, info.address, la);

        mBaiduMap.addOverlay(ooA);
        mLoactionLatLng = info.location;
        mAddress = info.address;
        mName = info.name;
        mStreet = info.address;
        mCity = info.city;
    }
    public void takeShotAndFinish() {
        showToast("正在保存位置信息");
        mBaiduMap.snapshot(new SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap snapshot) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if (null == snapshot) {
                    return;
                }
                String imgPath = null;
                String imgName = "test_" + sdf.format(new Date()) + ".jpg";
                try {
                    File cache = getCacheDir();
                    if (!cache.exists()) {
                        cache.mkdir();
                    }
                    imgPath = cache.getPath() + File.separator + imgName;
                    FileOutputStream fos = new FileOutputStream(imgPath);
                    boolean b = snapshot.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    compressImg(imgPath, imgName);
                }
            }
        });
    }
    public void compressImg(final String imgPath, final String imgName) {
        List<ImageInterface> images = new ArrayList<>();
        CircleImage image = new CircleImage();
        image.setPath(imgPath);
        images.add(image);
        ImageCompress.
            get(getApplicationContext())
            .load(images)
            .setFilename(imgName)
            .setCompressListener(new OnCompressListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(List<ImageInterface> results) {
                    ArrayList<String> resultList = new ArrayList<>();
                    resultList.add(imgPath);
                    Intent returnIntent = new Intent();
                    MapInfoEntity location = new MapInfoEntity(mName, mAddress, mLoactionLatLng.latitude,
                        mLoactionLatLng.longitude);
                    returnIntent.putExtra("position", location);
                    returnIntent.putStringArrayListExtra("select_result", resultList);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }
            }).launch();
    }
}

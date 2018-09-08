package com.lensim.fingerchat.commons.map;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.dialog.MapSelectDialog;
import com.lensim.fingerchat.commons.map.bean.MapInfoEntity;
import com.lensim.fingerchat.commons.map.service.LocationService;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class ShowLocationActivity extends BaseActivity {

    public static final int SHOW_ADDDRESS = 7844;
    private static final String ADDDRESS = "address";
    private ImageButton defineMyLocationButton;
    private FGToolbar toolbar;
    //百度地图相关
    private LocationService locationService;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isLocateFinished = false;//定位我的位置是否结束
    private boolean isLocationParamsNull = true;
    private MapInfoEntity mMapInfo;//Intent传入
    private TextView mAddressTitle, mAddressDES;
    // 设置第一次定位标志
    private boolean isFirstLoc = true;

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            isLocateFinished = true;
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
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
                    LatLng currentLatLng = new LatLng(location.getLatitude(),
                        location.getLongitude());

                    // 是否第一次定位
                    if (isFirstLoc) {
                        isFirstLoc = false;
                        // 实现动画跳转
                        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
                        mBaiduMap.animateMapStatus(u);
                        return;
                    }
                }
            }
        }
    };
    private ImageView iv_address;
    private MapInfoEntity startLocation;

    public static void openActivity(Activity context, int requestCode, MapInfoEntity addressInfo) {
        Intent intent = new Intent(context, ShowLocationActivity.class);
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
    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_map_show_location);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleText("位置信息");
        initBackButton(toolbar, true);
        initMap();

        mAddressTitle = (TextView) findViewById(R.id.txt_address_title);
        mAddressDES = (TextView) findViewById(R.id.txt_address_des);
        iv_address = findViewById(R.id.iv_address);

        defineMyLocationButton = (ImageButton) findViewById(R.id.define_my_location);
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

        iv_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getMapList());
            }
        });
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.mapView_show);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
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
    }

    public void drawParamLocation() {
        mMapInfo = getIntent().getParcelableExtra(ADDDRESS);
        if (mMapInfo != null) {
            isLocationParamsNull = false;
            PoiInfo info = new PoiInfo();
            info.name = mMapInfo.getAddressName();
            info.address = mMapInfo.getStreet();
            info.location = new LatLng(mMapInfo.getLatitude(),
                mMapInfo.getLongitude());
            if (StringUtils.isEmpty(info.name) || StringUtils.isEmpty(info.address)) {
                return;
            } else {
                mAddressTitle.setText(info.name);
                mAddressDES.setText(info.address);
            }
            drawMarker(info);
        }
    }

    public void toMyPosition() {
        MyLocationData location = mBaiduMap.getLocationData();
        // 实现动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory
            .newLatLng(new LatLng(location.latitude, location.longitude));
        mBaiduMap.animateMapStatus(u);
    }

    public void drawMarker(PoiInfo info) {
        BitmapDescriptor mSelectIco = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_map_marker);
        mBaiduMap.clear();
        LatLng la = info.location;
        // 动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);
        // 添加覆盖物
        OverlayOptions ooA = new MarkerOptions().position(la).icon(mSelectIco).anchor(0.5f, 0.5f);

        mBaiduMap.addOverlay(ooA);
    }

    private void showDialog(List<String> maps) {
        MapSelectDialog dialog = new MapSelectDialog(this, R.style.MyDialogTheme, maps);
        dialog.setListener(new IMapDiglogListener() {
            @Override
            public void onItemClick(String name) {
                startMapApp(name);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private final String BAIDU_PACKAGENAME = "com.baidu.BaiduMap";//百度地图包名
    private final String GAODE_PACKAGENAME = "com.autonavi.minimap";//高德地图包名
    private final String TENXUN_PACKAGENAME = "com.tencent.map";//腾讯地图包名
    private final String GOOGLE_PACKAGENAME = "com.google.android.apps.maps";//谷歌地图包名


    //判断是否安装某程序
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }


    private List<String> getMapList() {
        List<String> mapList = new ArrayList<String>();

        if (isInstallByread(BAIDU_PACKAGENAME)) {
            mapList.add(BAIDU_PACKAGENAME);
        }

        if (isInstallByread(GOOGLE_PACKAGENAME)) {
            mapList.add(GOOGLE_PACKAGENAME);
        }
        if (isInstallByread(TENXUN_PACKAGENAME)) {
            mapList.add(TENXUN_PACKAGENAME);
        }
        if (isInstallByread(GAODE_PACKAGENAME)) {
            mapList.add(GAODE_PACKAGENAME);
        }
        return mapList;
    }

    private void startMapApp(String appName) {
        Intent intent;
        MyLocationData mLocation = mBaiduMap.getLocationData();
        startLocation = new MapInfoEntity("我的位置", "", mLocation.latitude,
            mLocation.longitude);
        if (appName.equals(BAIDU_PACKAGENAME)) {
            //调起百度地图客户端
            try {
                intent = Intent.getIntent(String.format(
                    "intent://map/direction?origin=latlng:%s,%s|name:%s&destination=latlng:%s,%s|name:%s&mode=driving&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end",
                    startLocation.getLatitude(), startLocation.getLongitude(),
                    startLocation.getAddressName(),
                    mMapInfo.getLatitude(), mMapInfo.getLongitude(), mMapInfo.getAddressName()));

                if (isInstallByread(BAIDU_PACKAGENAME) && intent != null) {
                    startActivity(intent); //启动调用
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (appName.equals(GOOGLE_PACKAGENAME)) {
//                       https://maps.google.com/maps?q=31.207149,121.593086(金科路)&z=17&hl=en
            Uri gmmIntentUri = Uri.parse(String
                .format("google.navigation:q=%s,%s", mMapInfo.getLatitude(),
                    mMapInfo.getLongitude()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage(GOOGLE_PACKAGENAME);
            startActivity(mapIntent);
        } else if (appName.equals(TENXUN_PACKAGENAME)) {//目前不支持android URI?
            try {
                //"http://apis.map.qq.com/uri/v1/routeplan?type=bus&from=我的家&fromcoord=39.980683,116.302&to=中关村&tocoord=39.9836,116.3164&policy=1&referer=myapp"
                Gps mGps = GpsUtils.bd09_To_Gcj02(startLocation.getLatitude(),
                    startLocation.getLongitude());//世界坐标转变为火星坐标
                Gps oGps = GpsUtils.bd09_To_Gcj02(mMapInfo.getLatitude(),
                    mMapInfo.getLongitude());//世界坐标转变为火星坐标
                intent = Intent.getIntent(String.format(
                    "http://apis.map.qq.com/uri/v1/routeplan?type=bus&from=%s&fromcoord=%s,%s&to=%s&tocoord=%s,%s&policy=1&referer=myapp",
                    startLocation.getAddressName(), mGps.getLatitude(), mGps.getLongitude(),
                    mMapInfo.getAddressName(), oGps.getLatitude(), oGps.getLongitude()));
                if (intent != null) {
                    startActivity(intent);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            //改成Web
        } else if (appName.equals(GAODE_PACKAGENAME)) {
            try {
                Gps oGps = GpsUtils.bd09_To_Gcj02(startLocation.getLatitude(),
                    startLocation.getLongitude());//世界坐标转变为火星坐标
                if (isInstallByread(GAODE_PACKAGENAME)) {
                    //如果安装了app，callnative为1，表示调起app，0表示网页打开   经纬度，与前面相反，起点位置不填，会以定位地点为起点
                    //http://uri.amap.com/navigation?from=116.478346,39.997361,startpoint&to=116.3246,39.966577,endpoint&via=116.402796,39.936915&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0
                    intent = Intent.getIntent(String.format(
                        "http://uri.amap.com/navigation?from=%s,%s,%s&to=%s,%s,%s&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=1",
                        "", "", "", oGps.getLongitude(), oGps.getLatitude(),
                        mMapInfo.getAddressName()));

                } else {
                    intent = Intent.getIntent(String.format(
                        "http://uri.amap.com/navigation?from=%s,%s,%s&to=%s,%s,%s&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0",
                        "", "", "", oGps.getLongitude(), oGps.getLatitude(),
                        mMapInfo.getAddressName()));
                }
                if (intent != null) {
                    startActivity(intent);
                }


            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}

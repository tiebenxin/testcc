package com.lensim.fingerchat.commons.map;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
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
import com.lensim.fingerchat.commons.map.bean.MapInfoEntity;
import com.lensim.fingerchat.commons.map.service.LocationService;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;


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
  private MapInfoEntity para_addressInfo;//Intent传入
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
          MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
          mBaiduMap.setMyLocationConfigeration(config);
          LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

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
    initBackButton(toolbar,true);
    initMap();

    mAddressTitle = (TextView) findViewById(R.id.txt_address_title);
    mAddressDES = (TextView) findViewById(R.id.txt_address_des);

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
    para_addressInfo = getIntent().getParcelableExtra(ADDDRESS);
    if (para_addressInfo != null) {
      isLocationParamsNull = false;
      PoiInfo info = new PoiInfo();
      info.name = para_addressInfo.getAddressName();
      info.address = para_addressInfo.getStreet();
      info.location = new LatLng(para_addressInfo.getLatitude(), para_addressInfo.getLongitude());
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
    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.latitude, location.longitude));
    mBaiduMap.animateMapStatus(u);
  }

  public void drawMarker(PoiInfo info) {
    BitmapDescriptor mSelectIco = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_marker);
    mBaiduMap.clear();
    LatLng la = info.location;
    // 动画跳转
    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
    mBaiduMap.animateMapStatus(u);
    // 添加覆盖物
    OverlayOptions ooA = new MarkerOptions().position(la).icon(mSelectIco).anchor(0.5f, 0.5f);

    mBaiduMap.addOverlay(ooA);
  }
}

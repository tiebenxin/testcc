package com.lensim.fingerchat.fingerchat.ui.work_center.sign;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.lensim.fingerchat.commons.mvp.view.ProcessMvpView;
import java.util.List;

/**
 * date on 2018/1/26
 * author ll147996
 * describe
 */

public interface ClockInView extends ProcessMvpView {

    void drawMarker(PoiInfo info, BaiduMap baiduMap);

    void showInfoWindow(String name, String address, LatLng position, BaiduMap mBaiduMap);

//    void setMapView(MapView mapView);

    void dismissProgress();

    void showProgress(String msg, boolean bool);

    void setItems(List<String> items);

    void showClockInDialog(boolean isSuccess, String msg);

    void toMyPosition(BaiduMap baiduMap);

    void setAddPeopleViewText(String text);

    void afterLocation(String text);

    void showMsg(String msg);

}

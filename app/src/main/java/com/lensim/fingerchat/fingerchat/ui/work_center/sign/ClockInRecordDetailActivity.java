package com.lensim.fingerchat.fingerchat.ui.work_center.sign;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.lens.route.annotation.Path;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.work_center.SignInJsonRet;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityClockInRecordDetailBinding;
import java.util.ArrayList;

/**
 * 签到历史记录
 *
 * Created by LL117394 on 2017/8/23
 */
@Path(ActivityPath.CLOCK_DETAIL_ACTIVITY_PATH)
public class ClockInRecordDetailActivity extends BaseActivity {

    public final static String PARAMS_CONTENT = "content";
    private BaiduMap mBaiduMap;
    private ActivityClockInRecordDetailBinding ui;

    private ClockInRecordDetailAdapter adapter;
    private ArrayList<String> pictures = new ArrayList<>();
    private SignInJsonRet mDataItem;


    @Override
    protected void onResume() {
        super.onResume();
        ui.mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ui.mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ui.mapView.onDestroy();
    }

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_clock_in_record_detail);
        ui.clockInToolbar.setTitleText("外出打卡详情");
        initBackButton(ui.clockInToolbar, true);
        mDataItem = getIntent().getParcelableExtra(PARAMS_CONTENT);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        mBaiduMap = ui.mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        ui.mapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);

        adapter = new ClockInRecordDetailAdapter(this);
        adapter.setItems(new ArrayList<>());
        ui.statuImgContainer.setAdapter(adapter);

        if (null == mDataItem) {
            return;
        }

        try {
            String[] imgs = mDataItem.getTPSignIn().split("@");
            if (!StringUtils.isEmpty(mDataItem.getLocationData())) {
                drawMarker(getPoiInfo());
            }

            for (int i = 0, len = imgs.length; i < len; i++) {
                if (!StringUtils.isEmpty(imgs[i]) &&
                    (imgs[i].toLowerCase().startsWith("hnlensimage") ||
                        imgs[i].toLowerCase().startsWith("/hnlensimage"))) {

                    imgs[i] = Route.Host + imgs[i];
                }
                pictures.add(imgs[i]);
            }

            if (pictures.size() == 0 || StringUtils.isEmpty(pictures.get(0))) {
                ui.statuImgContainer.setVisibility(View.GONE);
            } else {
                adapter.setItems(pictures);
            }

            ui.tvClockTime.setText(TimeUtils.getDateHourString(
                TimeUtils.getTimeStampNoSeconds(mDataItem.getSignInTime()) + ""));
            ui.tvInputText.setText(mDataItem.getRemark());

            String[] attendants = mDataItem.getForReportNick().split(ClockInPresenter.SPLIT_TAG);
            if (attendants.length < 1) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = attendants.length; i < len; i++) {
                if (i == len - 1) {
                    sb.append(attendants[i]);
                } else {
                    sb.append(attendants[i] + "、");
                }
            }

            ui.tvPeopleList.setText(this.getString(R.string.sign_in_people, sb.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PoiInfo getPoiInfo() {
        String[] address = mDataItem.getLocationData().split(",");
        ui.tvClockAddress.setText(address[0] + "\n" + address[1]);
        PoiInfo info = new PoiInfo();
        info.name = address[0];
        info.address = address[1];
        info.location = new LatLng(Double.parseDouble(address[2]), Double.parseDouble(address[3]));
        return info;
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

}


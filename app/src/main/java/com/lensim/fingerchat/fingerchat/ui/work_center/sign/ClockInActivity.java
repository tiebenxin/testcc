package com.lensim.fingerchat.fingerchat.ui.work_center.sign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lensim.fingerchat.commons.base.BaseMvpActivity;
import com.lensim.fingerchat.commons.mvp.factory.CreatePresenter;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.components.dialog.DialogUtil;
import com.lensim.fingerchat.components.helper.OnClickEvent;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityClockInBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * date on 2018/1/26
 * author ll147996
 * describe
 */

@CreatePresenter(ClockInPresenter.class)
public class ClockInActivity extends BaseMvpActivity<ClockInView, ClockInPresenter> implements
    ClockInView {

    public static final int REQUEST_IMGS = 25;
    public static final int REQUEST_PEOPLE = 1233;
    public static final int REQUEST_TAKE_PHOTO = 1235;

    ActivityClockInBinding ui;
    private ClockInAdapter adapter;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_clock_in);
        initTitle();
        getMvpPresenter().initMap(ui.bmapView);
        ui.tvTimeClockIn.setVisibility(View.INVISIBLE);
        ui.tvNotifyLocation.setText("定位中...");
        initAdapter();
        initListener();
        //清空缓存
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) &&
            null != getExternalFilesDir(Environment.DIRECTORY_PICTURES)) {
            FileUtil.deleteDirectory(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        }
    }

    private void initTitle() {
        ui.clockInToolbar.setTitleText("外出打卡");
        ui.clockInToolbar.setConfirmBt("记录", (view) -> {
            Intent intent = new Intent(this, ClockInRecordActivity.class);
            this.startActivity(intent);
        });
        initBackButton(ui.clockInToolbar, true);
    }


    private void initAdapter() {
        adapter = new ClockInAdapter(this);
        adapter.setItems(new ArrayList<>());
        ui.statuImgContainer.setAdapter(adapter);

        adapter.setListener(() -> openActivity(this,
            StringUtils.isEmpty(ui.tvLocation.getText().toString()) ? ""
                : ui.tvLocation.getText().toString(),
            ClockInActivity.REQUEST_TAKE_PHOTO));
    }

    public void initListener() {
        ui.llClockInAddPeople.setOnClickListener(v -> addPeople(getMvpPresenter().addPeople()));
        ui.defineMyLocation.setOnClickListener(v -> getMvpPresenter().toMyPosition());
        ui.flLoadingLocation.setOnClickListener(new OnClickEvent(1000) {
            @Override
            public void singleClick(View v) {
                String remark = ui.statuInputMind.getText().toString().trim();
                getMvpPresenter().signIn(remark);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        getMvpPresenter().onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getMvpPresenter().onResume();
        ui.bmapView.onResume();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_loading_btn);
        ui.loadingLocation.startAnimation(animation);
        hideKeyBoard();
        MessageManager.getInstance().registerAckListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMvpPresenter().onStop();
        ui.bmapView.onPause();
        MessageManager.getInstance().removeAckListener();

    }

    @Override
    protected void onDestroy() {
        getMvpPresenter().onDestroy();
        super.onDestroy();
        ui.bmapView.onDestroy();
    }


    @Override
    public void showInfoWindow(String name, String address, LatLng position, BaiduMap mBaiduMap) {
        View view = LayoutInflater.from(ClockInActivity.this)
            .inflate(R.layout.map_infowindow, null);
        TextView tvname = view.findViewById(R.id.infowindo_name);
        TextView tvAddress = view.findViewById(R.id.infowindo_address);
        tvname.setText(name);
        tvAddress.setText(address);
        InfoWindow infoWindow = new InfoWindow(view, position, -40);
        mBaiduMap.showInfoWindow(infoWindow);
    }


    @Override
    public void setItems(List<String> items) {
        adapter.setItems(items);
    }

    @Override
    public void showClockInDialog(boolean isSuccess, String mAddress) {
        if (isSuccess) {
            DialogUtil.getClockInDialog(ClockInActivity.this, true,
                R.style.ClockinDialog, ui.tvTimeClockIn.getText().toString(), mAddress).show();
        } else {
            DialogUtil.getClockInDialog(ClockInActivity.this, false,
                R.style.ClockinDialog, "", mAddress).show();
        }

    }

    @Override
    public void drawMarker(PoiInfo info, BaiduMap mBaiduMap) {
        BitmapDescriptor mSelectIco = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_map_marker);
        mBaiduMap.clear();
        LatLng la = info.location;
        // 动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);
        // 添加覆盖物
        OverlayOptions ooA = new MarkerOptions().position(la).icon(mSelectIco).anchor(0.5f, 0.5f);
        showInfoWindow(info.name, info.address, la, mBaiduMap);
        mBaiduMap.addOverlay(ooA);
    }

    @Override
    public void setAddPeopleViewText(String text) {
        ui.tvAddPeople.setText(text);
    }


    @Override
    public void toMyPosition(BaiduMap baiduMap) {
        MyLocationData location = baiduMap.getLocationData();
        // 实现动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory
            .newLatLng(new LatLng(location.latitude, location.longitude));
        baiduMap.animateMapStatus(u);
        baiduMap.clear();
    }


    @Override
    public void afterLocation(String text) {
        ui.loadingLocation.setVisibility(View.GONE);
        ui.tvTimeClockIn.setVisibility(View.VISIBLE);
        ui.tvLocation.setText(text);
        ui.loadingLocation.clearAnimation();
        ui.tvNotifyLocation.setText("外出定位");
        ui.tvTimeClockIn.setText(TimeUtils.getTime());
    }

    @Override
    public void showMsg(String msg) {
        T.show(msg);
    }

    /**
     * 添加汇报对象
     */
    protected void addPeople(ArrayList<UserBean> resultChoosePeople) {
        Intent intent = new Intent(this, GroupSelectListActivity.class);
        intent.putExtra(Constant.KEY_OPERATION, Constant.GROUP_SELECT_MODE_CARD);
        intent.putExtra(Constant.KEY_SELECT_USER, resultChoosePeople);
        startActivityForResult(intent, REQUEST_PEOPLE);
    }


    private void openActivity(Activity context, String address, int requestCode) {
        Intent intent = new Intent(context, CameraViewActivity.class);
        intent.putExtra(CameraViewActivity.PARAMS_ADDRESS, address);
        context.startActivityForResult(intent, requestCode);
    }


    private void hideKeyBoard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_PEOPLE:
                getMvpPresenter().requestPeople(data);
                break;
            case REQUEST_TAKE_PHOTO:
                getMvpPresenter().requestTakePhoto(data);
                break;
            case REQUEST_IMGS:
                getMvpPresenter().requestImgs(data);
                break;
            default:
                break;
        }
    }


    @Override
    public void backPressed() {
        onReturn();
    }

    public void onReturn() {
        Intent intent = new Intent();
        intent.putExtra("dataChanged", false);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onReturn();
        }
        return super.onKeyDown(keyCode, event);
    }

}

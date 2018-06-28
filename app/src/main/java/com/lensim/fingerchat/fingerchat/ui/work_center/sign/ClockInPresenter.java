package com.lensim.fingerchat.fingerchat.ui.work_center.sign;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.google.gson.Gson;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.ui.group.Constant;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.map.BaiduSDK;
import com.lensim.fingerchat.commons.map.service.LocationService;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.DeviceUtils;
import com.lensim.fingerchat.commons.utils.NoteStringUtils;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.data.work_center.SignInJson;
import com.lensim.fingerchat.data.work_center.sign.SPListResponse;
import com.lensim.fingerchat.data.work_center.sign.SignInJsonAttachInfo;
import com.lensim.fingerchat.data.work_center.sign.SignInPicture;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * date on 2018/1/26
 * author ll147996
 * describe
 */

public class ClockInPresenter extends BaseMvpPresenter<ClockInView> {

    public static final String SPLIT_TAG = "；";
    //百度地图相关
    private LocationService locationService;
    private BaiduMap mBaiduMap;
    // 当前经纬度和地理信息
    private LatLng mLoactionLatLng;

    private String mAddress;
    private String mStreet;
    private String mName;
    private String mCity;
    private String mTpsignin;

    private ArrayList<String> picturesNoHost = new ArrayList<>();
    private ArrayList<String> picturesWithHost = new ArrayList<>();
    private ArrayList<UserBean> resultChoosePeople;

    private String clickInDate;
    private String peopleList;
    private String peopleListNick;

    private boolean isLocateFinished = false;//定位我的位置是否结束
    private boolean isClockedAlready = false;//是否已经打卡一次
    private static long lastTime;
    private static final long TIME_INTERVAL = 10 * 60 * 1000;//10分钟内只能打卡一次

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
                mBaiduMap.setMyLocationData(data);
                mAddress = location.getAddrStr();
                mStreet = location.getStreet();
                mName = location.getLocationDescribe();
                mCity = location.getCity();
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mLoactionLatLng = currentLatLng;
                if (isViewAttached()) {
                    getMvpView().showInfoWindow(mName, mAddress, mLoactionLatLng, mBaiduMap);
                }
                // 实现动画跳转
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);
                if (!StringUtils.isEmpty(mAddress)) {
                    isLocateFinished = true;
                }

                PoiInfo info = new PoiInfo();
                info.name = mName;
                info.address = mStreet;
                info.location = new LatLng(location.getLatitude(), location.getLongitude());
                if (!StringUtils.isEmpty(mAddress) && isViewAttached()) {
                    getMvpView().afterLocation(mAddress);
                    clickInDate = TimeUtils.getDateNoSeconds();
                }
                if (isViewAttached()) {
                    getMvpView().drawMarker(info, mBaiduMap);
                }
            }
        }
    };


    protected void onStart() {
        locationService = BaiduSDK.getLocationService();
        // 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，
        // 可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        // 注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }


    protected void onResume() {
        locationService.start();
    }


    protected void onStop() {
        locationService.stop();
    }


    protected void onDestroy() {
        locationService.unregisterListener(mListener);
    }

    protected void initMap(MapView bmapView) {

        mBaiduMap = bmapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bmapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        // 初始化当前MapView中心屏幕坐标，初始化当前地理坐标
        mLoactionLatLng = mBaiduMap.getMapStatus().target;
        // 定位
        mBaiduMap.setMyLocationEnabled(true);
        // 隐藏百度logo ZoomControl
        int count = bmapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = bmapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void toMyPosition() {
        if (isLocateFinished && isViewAttached()) {
            getMvpView().toMyPosition(mBaiduMap);
        }
    }

    public ArrayList<UserBean> addPeople() {
        if (resultChoosePeople == null) {
            return new ArrayList<>();
        }
        return resultChoosePeople;
    }

    /**
     * 是否重复点击
     */
    private boolean isDoubleClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < TIME_INTERVAL) {
            flag = true;
        }
        return flag;
    }


    /**
     * 签到
     *
     * @param remark “签到留言”
     */
    public void signIn(String remark) {
        if (resultChoosePeople == null || resultChoosePeople.size() == 0) {
            getMvpView().showMsg("您还未添加汇报对象");
            return;
        }
        if (isClockedAlready && isDoubleClick() && isViewAttached()) {
            getMvpView().showClockInDialog(false, "10分钟内只能打卡一次！");
        } else if (isLocateFinished) {
            signInHttp(remark);
        }

    }

    /***
     * 签到 发起网络请求
     * 图片最多3张，人员最多10人
     * **/
    private void signInHttp(String remark) {
        SignInJson signInJson = new SignInJson();
        String token = "token";
        String empno = UserInfoRepository.getInstance().getUserInfo().getUserid();
        String imuser = UserInfoRepository.getInstance().getUserInfo().getUserid();
        String time = StringUtils.isEmpty(clickInDate) ? "" : clickInDate;
        String locationtype = "GPS";
        //"28.225361,113.089327"
        String locationdata =
            mName + NoteStringUtils.SPLIT_COMER + mAddress + NoteStringUtils.SPLIT_COMER
                + mLoactionLatLng.latitude + NoteStringUtils.SPLIT_COMER
                + mLoactionLatLng.longitude;
        mTpsignin = "";
        if (picturesNoHost.size() < 1) {
            T.show("至少上传一张图片!");
            return;
        }

        for (String url : picturesNoHost) {
            mTpsignin += url + "@";
        }
        mTpsignin = mTpsignin.substring(0, mTpsignin.lastIndexOf("@"));

        signInJson.setToken(getEncodeStr(token));
        signInJson.setEmpno(getEncodeStr(empno));
        signInJson.setImuser(getEncodeStr(imuser));
        signInJson.setSignIP(getEncodeStr(DeviceUtils.getIp(ContextHelper.getContext())));
        signInJson.setSignTime(getEncodeStr(time));
        signInJson.setMobiletype(getEncodeStr("Android"));
        signInJson.setMobilename(getEncodeStr(DeviceUtils.getManufacturer()));
        signInJson.setMobileVer(DeviceUtils.getSDKVersion() + "");
        signInJson.setUuid(DeviceUtils.getAndroidID());
        signInJson.setImver(DeviceUtils.getVersionCode());
        signInJson.setLocationtype(getEncodeStr(locationtype));
        signInJson.setLocationdata(getEncodeStr(locationdata));
        signInJson.setTPSignIn(getEncodeStr(mTpsignin));
        signInJson.setRemark(getEncodeStr(remark));
        signInJson.setForReport(getEncodeStr(peopleList));
        signInJson.setForReportNick(getEncodeStr(peopleListNick));
        Http.signIn(signInJson)
            .compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<RetObjectResponse<String>>() {
                @Override
                public void onNext(RetObjectResponse<String> response) {
                    lastTime = System.currentTimeMillis();
                    if (1 == response.retCode) {//成功
                        isClockedAlready = true;
                        if (isViewAttached()) {
                            getMvpView().showClockInDialog(true, mAddress);
                        }
                        sendMessage(remark);
                    } else {//失败
                        if (isViewAttached()) {
                            getMvpView().showClockInDialog(false, response.retMsg);
                        }
                    }
                }
            });
    }


    private static String getEncodeStr(String str) {
        String encodeParam = null;
        try {
            encodeParam = CyptoUtils.encrypt(str.trim());
            encodeParam = URLEncoder.encode(encodeParam, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeParam;
    }


    /**
     * 上传签到图片
     */
    protected void signInPostPicture(String path) {
        if (isViewAttached()) {
            getMvpView().showProgress("正在上传图片。。。", false);
        }

        Http.signInPostPicture(path)
            .compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<SPListResponse<SignInPicture>>(false) {
                @Override
                public void onNext(SPListResponse<SignInPicture> response) {
                    if (isViewAttached()) {
                        getMvpView().dismissProgress();
                    }
                    if ("OK".equals(response.Status) && !TextUtils
                        .isEmpty(response.Value.get(0).Url.b)) {
                        String url = response.Value.get(0).Url.b;
                        picturesNoHost.add(url);
                        if (url.toLowerCase().startsWith("hnlensimage") ||
                            url.toLowerCase().startsWith("/hnlensimage")) {
                            url = Route.Host + url;
                        }
                        Log.e("url", url);
                        picturesWithHost.add(url);
                        if (isViewAttached()) {
                            getMvpView().setItems(picturesWithHost);
                        }
                    } else {
                        T.show("上传图片失败");
                    }

                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    if (isViewAttached()) {
                        getMvpView().dismissProgress();
                    }
                    T.show("上传图片失败");
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    if (isViewAttached()) {
                        getMvpView().dismissProgress();
                    }
                }
            });

    }


    /***
     * 给联系人发消息
     * */
    public void sendMessage(String remark) {
        SignInJsonAttachInfo pj = new SignInJsonAttachInfo();
        Gson gson = new Gson();
        pj.setName(UserInfoRepository.getUsernick());
        pj.setMsgtype("puchCard");
        pj.setSecret("0");
        pj.setPcardtime(StringUtils.isEmpty(clickInDate) ? "" : clickInDate);
        pj.setPcardlocationInfo(
            mName + NoteStringUtils.SPLIT_COMER + mAddress + NoteStringUtils.SPLIT_COMER
                + mLoactionLatLng.latitude + NoteStringUtils.SPLIT_COMER
                + mLoactionLatLng.longitude);
        pj.setPcardremark(remark);
        pj.setPcardimages(mTpsignin);
        pj.setPcardforreport(peopleListNick);
        final String toJson = gson.toJson(pj);

        for (int i = 0, len = resultChoosePeople.size(); i < len; i++) {
            String account = "";
            boolean isGroup = false;
            if (resultChoosePeople.get(i).getType() == 0) {//如果是个人
                account = resultChoosePeople.get(i).getUserId();
                isGroup = false;
            } else if (resultChoosePeople.get(i).getType() == 1) {//如果是群
                account = resultChoosePeople.get(i).getMucId();
                isGroup = true;
            }

            IChatRoomModel chatRoomModel = MessageManager.getInstance()
                .createTransforMessage(account, UserInfoRepository.getUserName(), toJson,
                    UserInfoRepository.getUsernick(), UserInfoRepository.getImage(), isGroup, false,
                    false, EMessageType.CARD);
            MessageManager.getInstance().sendMessage(chatRoomModel);

        }
    }


    protected void requestPeople(Intent data) {
        resultChoosePeople = data.getParcelableArrayListExtra(Constant.KEY_SELECT_USER);
        if (resultChoosePeople == null) {
            return;
        }
        int len = resultChoosePeople.size();
        if (null != resultChoosePeople && len > 0) {
            len = Math.min(10, len);
            StringBuilder sb = new StringBuilder();
            StringBuilder sbName = new StringBuilder();
            UserBean temp;
            String nick = "";
            String name = "";
            boolean isGroup = false;
            //如果是群，去获取群名。不是群，显示 一个人名
            for (int i = 0; i < len; i++) {
                temp = resultChoosePeople.get(i);
                isGroup = temp.getType() != 0;

                if (isGroup) {//如果是群
                    nick = temp.getMucName();
                    name = temp.getMucId();
                } else {
                    nick = temp.getUserId();
                    name = temp.getUserNick();
                }

                if (!StringUtils.isEmpty(nick)) {
                    sb.append(nick).append(SPLIT_TAG);
                }
                if (!StringUtils.isEmpty(name)) {
                    sbName.append(name).append(SPLIT_TAG);
                }
            }
            peopleListNick = sb.toString();
            peopleListNick = peopleListNick
                .substring(0, peopleListNick.lastIndexOf(SPLIT_TAG));
            peopleList = sbName.toString();
            peopleList = peopleList.substring(0, peopleList.lastIndexOf(SPLIT_TAG));

            sb.setLength(0);
            sb.append("汇报 ");
            sb.append(isGroup ? nick : resultChoosePeople.get(len - 1).getUserNick());
            sb.append(" 等" + len + (isGroup ? "个群" : "个人"));

            if (isViewAttached()) {
                getMvpView().setAddPeopleViewText(sb.toString());
            }
        }
    }

    protected void requestTakePhoto(Intent data) {
        final String path = data.getStringExtra(CameraViewActivity.EXTRA_RESULT);
        if (!StringUtils.isEmpty(path) && !picturesNoHost.contains(path)
            && picturesNoHost.size() < 3) {
            signInPostPicture(path);
        }
    }


    protected void requestImgs(Intent data) {
        List<String> path2 = data.getStringArrayListExtra("imgs");
        if (path2 != null) {
            picturesWithHost.clear();
            picturesNoHost.clear();
            picturesWithHost.addAll(path2);
            if (isViewAttached()) {
                getMvpView().setItems(picturesWithHost);
            }
        }
        for (int i = 0; i < picturesWithHost.size(); i++) {
            String url = picturesWithHost.get(i);
            url = url.replace(Route.Host, "").trim();
            picturesNoHost.add(url);
        }
    }

}

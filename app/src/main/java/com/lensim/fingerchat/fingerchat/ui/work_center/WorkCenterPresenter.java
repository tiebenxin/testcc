package com.lensim.fingerchat.fingerchat.ui.work_center;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.UserListener;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.proto.message.User;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.help_class.TokenHelper;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.data.work_center.OAToken;
import com.lensim.fingerchat.data.work_center.OATokenRepository;
import com.lensim.fingerchat.data.work_center.WorkItem;
import com.lensim.fingerchat.data.work_center.WorkRepository;
import com.lensim.fingerchat.fingerchat.api.UserApi;
import com.lensim.fingerchat.fingerchat.api.WorkCenterApi;
import com.lensim.fingerchat.fingerchat.model.result.GetWorkCenterListResult;
import com.lensim.fingerchat.fingerchat.model.result.NewOATokenResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * date on 2017/12/23
 * author ll147996
 * describe
 */

public class WorkCenterPresenter extends BaseMvpPresenter<WorkCenterView> implements UserListener {

    //分隔符  CHILD = "|"， GROUP = ":";
    private final static String CHILD = "|";
    private final static String GROUP = ":";
    private List<Object> items;
    private List<WorkItem> workItems;


    @Override
    public void onAttachMvpView(WorkCenterView mvpView) {
        super.onAttachMvpView(mvpView);
        ClientConfig.I.registerListener(UserListener.class, this);
    }

    @Override
    public void onDetachMvpView() {
        super.onDetachMvpView();
        ClientConfig.I.removeListener(UserListener.class, this);
    }


    @Override
    public void onReceivedUserinfo(UserInfoMessage message) {
        if (!message.userInfo.getUserid().equals(UserInfoRepository.getInstance().getUserInfo().getUserid())
            || items == null) {
            getWorkCenter();
        }
        
        UserInfoRepository.getInstance().setUserInfo(getUserInfo(message.userInfo));
    }


    private UserInfo getUserInfo(User.UserInfo userInfo) {
        return new UserInfo(userInfo.getUserid(), userInfo.getUsernick(), userInfo.getPhoneNumber(), userInfo.getWorkAddress(),
            userInfo.getEmpName(), userInfo.getSex(), userInfo.getAvatar(), userInfo.getIsvalid(), userInfo.getJobname(),
            userInfo.getDptNo(), userInfo.getDptName(), userInfo.getEmpNo(), userInfo.getRight());
    }


    public void dragEnd(List<?> endItems) {
        saveSortWorks(getWorks(endItems));
    }


    public void toHexMeetLogin() {
        if (TokenHelper.isSSOTokenValid(UserInfoRepository.getUserId()) && getMvpView() != null) {
            getMvpView().toHexMeetActivity();
        } else {
            ssoLogin(AppConfig.INSTANCE.get(AppConfig.ACCOUT),
                PasswordRespository.getPassword(),
                () -> {if (getMvpView() != null) getMvpView().toHexMeetActivity();});
        }
    }


    public void toOAActivity(String url, String title,  int isHasNav) {
        if (TokenHelper.isSSOTokenValid(UserInfoRepository.getUserId())) {
            toOAOpenUrl(url, title, isHasNav);
        } else {
            ssoLogin(AppConfig.INSTANCE.get(AppConfig.ACCOUT),
                PasswordRespository.getPassword(),
                () -> toOAOpenUrl(url, title, isHasNav));
        }
    }


    private void toOAOpenUrl(String url, String title,  int isHasNav) {
        if (TokenHelper.isOATokenValid(UserInfoRepository.getUserId())) {
            toOpenUrl(OATokenRepository.getToken(), url, title, isHasNav);
        } else {
            new UserApi().getNewOAToken(SSOTokenRepository.getToken(),
                new FXRxSubscriberHelper<NewOATokenResult>() {
                    @Override
                    public void _onNext(NewOATokenResult baseResponse) {
                        NewOATokenResult.Data data = baseResponse.getContent();
                        if (data != null) {
                            OAToken token = new OAToken();
                            token.setOaToken(data.getOaToken());
                            token.setLifetime(data.getLifetime());
                            token.setUserId(UserInfoRepository.getUserId());
                            token.setTokenValidTime(System.currentTimeMillis() + data.getLifetime());
                            OATokenRepository.getInstance().setOAToken(token);
                            toOpenUrl(data.getOaToken(), url, title, isHasNav);
                        }
                    }
                });
        }
    }



    public void toOtherActivity(String url, String title, int isHasNav) {

        if (TokenHelper.isSSOTokenValid(UserInfoRepository.getUserId())) {
            toOpenUrl(SSOTokenRepository.getInstance().getSSOToken().getFxToken(), url, title, isHasNav);
        } else {
            ssoLogin(AppConfig.INSTANCE.get(AppConfig.ACCOUT),
                PasswordRespository.getPassword(),
                () -> toOpenUrl(SSOTokenRepository.getInstance().getSSOToken().getFxToken(), url, title, isHasNav));
        }
    }


    /**
     * 获取工作中心
     */
    public void getWorkCenter() {
        new WorkCenterApi().getWorkCenterList(new FXRxSubscriberHelper<GetWorkCenterListResult>() {
            @Override
            public void _onNext(GetWorkCenterListResult getWorkCenterListResult) {
                GetWorkCenterListResult.Data data = getWorkCenterListResult.getContent();
                if (data != null && data.getData() != null) {
                    workItems = getWorkItemList(data.getData());
                    sort(workItems);
                    items = getObjectList(workItems);
                    updateItems(items);
                }
            }
        });
    }

    private void updateItems(List<?> list) {
        if (getMvpView() == null) return;

        WorkRepository.updateWorkItem(UserInfoRepository.getUserId(), getWorks(list));
        if (getWorks(list) == null
            || WorkRepository.getWorks(UserInfoRepository.getUserId()) == null
            || WorkRepository.getSortWorks(UserInfoRepository.getUserId()) == null) {
            getMvpView().setItems(list);
        } else if (!WorkRepository.getWorks(UserInfoRepository.getUserId()).equals(getWorks(list))) {
            getMvpView().setItems(list);
        } else if (WorkRepository.getSortWorks(UserInfoRepository.getUserId()).equals(getWorks(list))) {
            getMvpView().setItems(list);
        } else {
            List<?> items = getWorkItems(WorkRepository.getSortWorks(UserInfoRepository.getUserId()), list);
            getMvpView().setItems(items);
        }

    }

    /**
     * 启动 BrowserActivity
     */
    private void toOpenUrl(String token, String url, String title, int isHasNav) {
        String number = "";
        UserInfo userEntity = UserInfoRepository.getInstance().getUserInfo();
        if (userEntity == null || userEntity.getIsvalid() == 0) {
            if (isViewAttached()) getMvpView().showIdentifyDialog();
            return;
        }
        number = userEntity.getEmpNo();
        if (isViewAttached())
            getMvpView().startBrowserActivity(getURL(token, number, url), title, isHasNav);
    }

    /**
     * 拼接 url
     *
     * @param token  用户ssotoken
     * @param number 部门号
     * @param url    BaseUrl
     * @return url
     */
    private String getURL(String token, String number, String url) {
        StringBuilder builder = new StringBuilder(url);

        String userName = UserInfoRepository.getInstance().getUserInfo().getEmpName();
        if (url.contains("?")) {
            builder
                .append("&id=")
                .append(UserInfoRepository.getInstance().getUserInfo().getUserid())
                .append("&empno=")
                .append(number);
                if (TextUtils.isEmpty(userName)) {
                    builder .append("&name=")
                        .append(new String(Base64.encode(userName.getBytes(),Base64.DEFAULT)));
                }
                builder.append("&terminal=")
                .append("android")
                .append("&token=")
                .append(token);
        } else {
            builder
                .append("?id=")
                .append(UserInfoRepository.getInstance().getUserInfo().getUserid())
                .append("&empno=")
                .append(number);
                if (TextUtils.isEmpty(userName)) {
                    builder .append("&name=")
                        .append(new String(Base64.encode(userName.getBytes(),Base64.DEFAULT)));
                }
                builder.append("&terminal=")
                .append("android")
                .append("&token=")
                .append(token);
        }
        return builder.toString();
    }


    /**
     * 单点登录 拿到Token，存SP
     **/
    public void ssoLogin(final String userid, final String pwd, SSOLoginListener listener) {
        boolean isShowProgress = false;
        if (listener != null) isShowProgress = true;

        HttpUtils.getInstance().ssoLogin(userid, pwd)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject<SSOToken>>(isShowProgress) {
                @Override
                public void onHandleSuccess(ResponseObject<SSOToken> response) {
                    SSOTokenRepository.getInstance().setSSOToken(response.result);
                    //getOAToken(AppConfig.INSTANCE.get(AppConfig.ACCOUT), response.result.getFxToken());
                    if (listener != null) listener.success();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    if (getMvpView() != null) {
                        getMvpView().showMsg("token为空");
                    }
                }

                @Override
                public void onHandleError(ResponseObject<SSOToken> ssoTokenResponseObject) {
                    super.onHandleError(ssoTokenResponseObject);
                    if (getMvpView() != null) {
                        getMvpView().showMsg("token为空");
                    }
                }
            });
    }


    public interface SSOLoginListener {
        void success();
    }


        //排序
    private void sort(List<WorkItem> workItemList) {
        Collections.sort(workItemList, new Comparator<WorkItem>() {
            @Override
            public int compare(WorkItem o1, WorkItem o2) {
                if (o1.getFuncTypeIdx() < o2.getFuncTypeIdx()) {
                    return -1;
                }
                if (o1.getFuncTypeIdx() > o2.getFuncTypeIdx()) {
                    return 1;
                }
                if (o1.getFuncTypeIdx() == o2.getFuncTypeIdx()) {

                    if (o1.getFuncIdx() < o2.getFuncIdx()) {
                        return -1;
                    }
                    if (o1.getFuncIdx() > o2.getFuncIdx()) {
                        return 1;
                    }
                    if (o1.getFuncIdx() == o2.getFuncIdx()) {
                        return 0;
                    }
                }
                return -1;
            }
        });
    }


    //获取Items, List<WorkItem>转为List<Object>
    private List<Object> getObjectList(List<WorkItem> workItemList) {
        List<Object> items = new ArrayList<>();
        Map<String, List<WorkItem>> map = new LinkedHashMap<>();

        for (WorkItem workItem : workItemList) {

            List<WorkItem> beanList = map.get(workItem.getTypeName());
            if (beanList == null) {
                beanList = new ArrayList<>();
            }
            beanList.add(workItem);
            map.put(workItem.getTypeName(), beanList);

        }

        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String s = (String) iter.next();
            List<WorkItem> lists = map.get(s);
            items.add(s);
            for (WorkItem workItem : lists) {
                items.add(workItem);
                Log.d(s, workItem.getFuncName());
            }
        }
        return items;
    }


    private String getWorks(List<?> items) {
        if (items == null) {
            return null;
        }
        StringBuilder strBuilder = new StringBuilder();
        for (Object object : items) {
            if (object instanceof String) {
                strBuilder = strBuilder.append(object).append(GROUP);
            }
            if (object instanceof WorkItem) {
                strBuilder = strBuilder.append(((WorkItem)object).getFuncId()).append(CHILD);
            }
        }
        return strBuilder.toString();
    }

    private void saveSortWorks(String sortWorks) {
        WorkRepository.updateSortWorks(UserInfoRepository.getUserId(), sortWorks);
    }

    private List<?> getWorkItems(String sortWorks, List<?> items) {
        List<Object> workItems = new ArrayList<>();
        //分隔符  CHILD = "|"， GROUP = ":";
        String[] str = sortWorks.split("[:|]");

        if (str.length != items.size()) {
            return workItems;
        }

        for (int i = 0; i < items.size(); i++) {
            Object object = items.get(i);
            String string = str[i];
            if (object instanceof String && object.equals(string)) {
                workItems.add(object);
            } else if (object instanceof WorkItem) {
                for (Object o : items) {
                    if (o instanceof WorkItem && ((WorkItem) o).getFuncId().equals(string)) {
                        workItems.add(o);
                    }
                }
            }
        }

        return workItems;
    }


    private List<WorkItem> getWorkItemList(List<WorkItem> workItems) {
        List<WorkItem> workItemList = new ArrayList<>();
        if (workItems == null) return workItemList;
        for (WorkItem workItem : workItems) {
            switch (workItem.getFuncId()) {
                case "22":
                    workItem.setNodePath("/fxClient/android/workcenter/companyNews");
                    break;
                case "24":
                     workItem.setNodePath("/fxClient/android/workcenter/newsSearch");
                    break;
                case "25":
                     workItem.setNodePath("/fxClient/android/workcenter/bill");
                    break;
                case "28":
                     workItem.setNodePath("/fxClient/android/workcenter/eventTrack");
                    break;
                case "2":
                     workItem.setNodePath("/fxClient/android/workcenter/myAttendance");
                    break;
                case "29":
                     workItem.setNodePath("/fxClient/android/workcenter/questionnaire");
                    break;
                case "37":
                     workItem.setNodePath("/fxClient/android/workcenter/clockOut");
                    break;
                case "3":
                     workItem.setNodePath("/fxClient/android/workcenter/attendance");
                    break;
                case "4":
                     workItem.setNodePath("/fxClient/android/workcenter/myPerformance");
                    break;
                case "48":
                     workItem.setNodePath("/fxClient/android/workcenter/l2Leave");
                    break;
                case "49":
                     workItem.setNodePath("/fxClient/android/workcenter/businessTrip");
                    break;
                case "5":
                     workItem.setNodePath("/fxClient/android/workcenter/mySalary");
                    break;
                case "50":
                     workItem.setNodePath("/fxClient/android/workcenter/overtime");
                    break;
                case "6":
                     workItem.setNodePath("/fxClient/android/workcenter/activity");
                    break;
                case "11":
                     workItem.setNodePath("/fxClient/android/workcenter/conferenceAssist");
                    break;
                case "8":
                     workItem.setNodePath("/fxClient/android/workcenter/recommend");
                    break;
                case "9":
                     workItem.setNodePath("/fxClient/android/workcenter/suggestionBox");
                    break;
                case "10":
                     workItem.setNodePath("/fxClient/android/workcenter/myExpress");
                    break;
                case "12":
                     workItem.setNodePath("/fxClient/android/workcenter/vehicleManagement");
                    break;
                case "13":
                     workItem.setNodePath("/fxClient/android/workcenter/studyExam");
                    break;
                case "14":
                     workItem.setNodePath("/fxClient/android/workcenter/ePatrol");
                    break;
                case "15":
                     workItem.setNodePath("/fxClient/android/workcenter/meterialOut");
                    break;
                case "16":
                     workItem.setNodePath("/fxClient/android/workcenter/visitorReception");
                    break;
                case "17":
                     workItem.setNodePath("/fxClient/android/workcenter/illegals");
                    break;
                case "18":
                     workItem.setNodePath("/fxClient/android/workcenter/assetInventory");
                    break;
                case "19":
                     workItem.setNodePath("/fxClient/android/workcenter/equipmentRepair");
                    break;
                case "1":
                     workItem.setNodePath("/fxClient/android/workcenter/personalCapacity");
                    break;
                case "20":
                     workItem.setNodePath("/fxClient/android/workcenter/networkIntercom");
                    break;
                case "21":
                     workItem.setNodePath("/fxClient/android/workcenter/addMore");
                    break;
                case "23":
                     workItem.setNodePath("/fxClient/android/workcenter/companyPolicy");
                    break;
                case "7":
                     workItem.setNodePath("/fxClient/android/workcenter/teleconference");
                    break;
                case "33":
                     workItem.setNodePath("/fxClient/android/workcenter/equipments");
                    break;
                case "34":
                     workItem.setNodePath("/fxClient/android/workcenter/hr");
                    break;
                case "35":
                     workItem.setNodePath("/fxClient/android/workcenter/materials");
                    break;
                case "39":
                     workItem.setNodePath("/fxClient/android/workcenter/waterAndElectricity");
                    break;
                case "40":
                     workItem.setNodePath("/fxClient/android/workcenter/myChecking");
                    break;
                case "46":
                     workItem.setNodePath("/fxClient/android/workcenter/myChecked");
                    break;
                case "47":
                     workItem.setNodePath("/fxClient/android/workcenter/oaMyReading");
                    break;
                case "51":
                     workItem.setNodePath("/fxClient/android/workcenter/oaHome");
                    break;
                case "52":
                     workItem.setNodePath("/fxClient/android/workcenter/notes");
                    break;
                case "53":
                     workItem.setNodePath("/fxClient/android/workcenter/oaCompanyNews");
                    break;
                case "54":
                     workItem.setNodePath("/fxClient/android/workcenter/oaMyApplications");
                    break;
            }
            workItemList.add(workItem);
        }
        return workItems;
    }
}

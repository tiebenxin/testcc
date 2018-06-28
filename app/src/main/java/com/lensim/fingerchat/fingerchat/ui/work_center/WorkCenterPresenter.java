package com.lensim.fingerchat.fingerchat.ui.work_center;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.UserListener;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.proto.message.User;
import com.fingerchat.proto.message.User.Func;
import com.google.gson.Gson;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
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
import com.lensim.fingerchat.data.work_center.WorkItemRepository;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.identify.UserIdentifyResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * date on 2017/12/23
 * author ll147996
 * describe
 */

public class WorkCenterPresenter extends BaseMvpPresenter<WorkCenterView> implements UserListener{

    private List<Object> items;

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

    protected void initItems() {
//        if (UserInfoRepository.getInstance().getUserInfo() != null &&
//            !TextUtils.isEmpty(UserInfoRepository.getInstance().getUserInfo().getEmpNo())){
//            getFunctions(AppConfig.INSTANCE.get(AppConfig.ACCOUT), PasswordRespository.getPassword());
//        }

        getUserInfo();

        if (SSOTokenRepository.getInstance().getSSOToken() == null ||
            TextUtils.isEmpty(SSOTokenRepository.getInstance().getSSOToken().getFxToken())) {
            ssoLogin(AppConfig.INSTANCE.get(AppConfig.ACCOUT), PasswordRespository.getPassword());
        }

        List<WorkItem> workItems = WorkItemRepository.getInstance().getWorkItems();

        if (workItems == null) return;

        sort(workItems);
        items = getItems(workItems);
        if (isViewAttached()) getMvpView().setItems(items);
    }

    @Override
    public void onReceivedUserinfo(UserInfoMessage message) {
//        getFunctions(AppConfig.INSTANCE.get(AppConfig.ACCOUT), PasswordRespository.getPassword());
        UserInfoRepository.getInstance().setUserInfo(getUserInfo(message.userInfo));
        WorkItemRepository.getInstance().setWorkItems(WorkItem.class, getWorkItems(message.userInfo));
    }


    private UserInfo getUserInfo(User.UserInfo userInfo) {
        return new UserInfo(userInfo.getUserid(), userInfo.getUsernick(), userInfo.getPhoneNumber(), userInfo.getWorkAddress(),
            userInfo.getEmpName(), userInfo.getSex(), userInfo.getAvatar(), userInfo.getIsvalid(), userInfo.getJobname(),
            userInfo.getDptNo(), userInfo.getDptName(), userInfo.getEmpNo(), userInfo.getRight());
    }

    private List<WorkItem> getWorkItems(User.UserInfo userInfo) {
        List<Func> funcs = userInfo.getFunctionList();
        List<WorkItem> workItems = new ArrayList<>();
        for (Func func : funcs) {
            WorkItem workItem = new WorkItem(func.getFuncAddress(), func.getFuncId(),
                func.getFuncIdx(), func.getFuncLogo(), func.getFuncName(), func.getFuncType(),
                func.getFuncTypeIdx(), func.getFuncValid(), func.getTypeName(), func.getTypeValid());
            workItems.add(workItem);
        }
        return workItems;
    }


    /**
     * 启动 BrowserActivity，需要 OA令牌
     */
    public void toOAActivity(String url, String title){

        if (SSOTokenRepository.getInstance().getSSOToken() == null) {
            //获取单点登录Token
            ssoLogin(AppConfig.INSTANCE.get(AppConfig.ACCOUT),
                PasswordRespository.getPassword(),
                new SSOLoginListener() {
                    @Override
                    public void success() {
                        requestOAToken(AppConfig.INSTANCE.get(AppConfig.ACCOUT),
                            SSOTokenRepository.getInstance().getSSOToken().getFxToken(), url, title);
                    }

                    @Override
                    public void fail() {

                    }
                });
        } else if (OATokenRepository.getInstance().getOAToken() != null) {
            String mOAToken = OATokenRepository.getInstance().getOAToken().oaToken;
            long time = OATokenRepository.getInstance().getOAToken().time;
            if (StringUtils.isEmpty(mOAToken) || System.currentTimeMillis() >= time) {
                requestOAToken(AppConfig.INSTANCE.get(AppConfig.ACCOUT),
                    SSOTokenRepository.getInstance().getSSOToken().getFxToken(), url, title);
            } else {
                toOpenUrl(mOAToken, url, title);
            }
        }

    }



    private void requestOAToken(String userid, String token, String url, String title) {
        Http.getOAToken(userid,token,"android","")
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject<OAToken>>(false) {
                @Override
                public void onHandleSuccess(ResponseObject<OAToken> response) {
                    OAToken oaToken = response.result;
                    oaToken.time = 20 * 60 * 60 * 1000 + System.currentTimeMillis();
                    OATokenRepository.getInstance().setOAToken(response.result);
                    toOpenUrl(oaToken.oaToken, url, title);
                }
            });
    }



    public void toOtherActivity(String url, String title) {
        if (SSOTokenRepository.getInstance().getSSOToken() != null
            && !TextUtils.isEmpty(SSOTokenRepository.getInstance().getSSOToken().getFxToken())) {

            toOpenUrl(SSOTokenRepository.getInstance().getSSOToken().getFxToken(), url, title);
        } else {
            ssoLogin(AppConfig.INSTANCE.get(AppConfig.ACCOUT),
                PasswordRespository.getPassword(),
                new SSOLoginListener() {
                    @Override
                    public void success() {
                        toOpenUrl(SSOTokenRepository.getInstance().getSSOToken().getFxToken(), url, title);
                    }

                    @Override
                    public void fail() {

                    }
                });
        }
    }


    /**
     * 获取用户个人信息
     */
    private void getUserInfo() {
        Http.getUserInfoByAsync("getuser", UserInfoRepository.getUserName())
            .compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<List<UserIdentify>>() {
                @Override
                public void onNext(List<UserIdentify> userIdentifies) {
                    UserIdentify userEntity = userIdentifies.get(0);
                    UserIdentifyResponse.getInstance().setUserIdentify(userEntity);
                }
            });
    }

    /**
     * 启动 BrowserActivity
     * @param token
     * @param url
     * @param title
     */
    private void toOpenUrl(String token, String url, String title) {
        String number = "";
        UserIdentify userEntity = UserIdentifyResponse.getInstance().getUserIdentify();
        if (userEntity == null || userEntity.isValid == 0) {
            if (isViewAttached()) getMvpView().showIdentifyDialog();
            return;
        }
        number = userEntity.EmployeeNO;
        if (isViewAttached()) getMvpView().startBrowserActivity(getURL(token, number, url), title);
    }

    /**
     * 拼接 url
     * @param token 用户ssotoken
     * @param number 部门号
     * @param url BaseUrl
     * @return url
     */
    private String getURL(String token, String number, String url) {
        StringBuilder builder = new StringBuilder(url);

        if (url.contains("?")) {
            builder.append("&id=")
                .append(UserInfoRepository.getInstance().getUserInfo().getUserid())
                .append("&empno=")
                .append(number)
                .append("&name=")
                .append(new String(Base64
                    .encode(UserInfoRepository.getInstance().getUserInfo().getUsernick().getBytes(),
                        Base64.DEFAULT)))
//                .append(new String(Base64.encode(LensImUtil.getUserNick().getBytes(), Base64.DEFAULT)))
                .append("&terminal=")
                .append("android")
                .append("&token=")
                .append(token);
        } else {
            builder.append("?id=")
                .append(UserInfoRepository.getInstance().getUserInfo().getUserid())
                .append("&empno=")
                .append(number)
                .append("&name=")
                .append(new String(Base64
                    .encode(UserInfoRepository.getInstance().getUserInfo().getUsernick().getBytes(),
                        Base64.DEFAULT)))
//                .append(new String(Base64.encode(LensImUtil.getUserNick().getBytes(), Base64.DEFAULT)))
                .append("&terminal=")
                .append("android")
                .append("&token=")
                .append(token);
        }
        return builder.toString();
    }


    /**
     * 单点登录 拿到Token，存SP
     **/
    private void ssoLogin(final String userid, final String pwd) {
        ssoLogin(userid, pwd, null);
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
                    if (listener != null) listener.fail();
                }

                @Override
                public void onHandleError(ResponseObject<SSOToken> ssoTokenResponseObject) {
                    super.onHandleError(ssoTokenResponseObject);
                    if (listener != null) listener.fail();
                }
            });
    }


    public interface SSOLoginListener {
        void success();
        void fail();
    }



    /**
     * 获取工作中心获取子项目
     */
    private void getFunctions(String empno, String token) {
        Http.getFunctions(empno, token)
                .compose(RxSchedulers.compose())
                .subscribe(new FGObserver<ResponseBody>() {
                    @Override
                    public void onHandleSuccess(ResponseBody response) {
                        List<WorkItem> workItems = toWorkItems(response);
                        sort(workItems);
                        items = getItems(workItems);
                        if (isViewAttached()) getMvpView().setItems(items);
                    }
                });
    }


    //解析
    private List<WorkItem> toWorkItems(ResponseBody response) {
        List<WorkItem> workItemList = new ArrayList<>();
        try {
            JSONObject jsonobj = new JSONObject(response.string());
            String childstr = jsonobj.getString("GetFuncInfByEmpnoResult");
            JSONObject subObj = new JSONObject(childstr);
            int retCode = subObj.getInt("retCode");
            if (retCode != 1) {
                return workItemList;
            }
            String data = subObj.getString("retData");
            JSONArray jsonArray = new JSONArray(data);
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                String result = jsonArray.getString(i);
                WorkItem workItem = gson.fromJson(result, WorkItem.class);
                workItemList.add(workItem);
            }
            return workItemList;
        } catch (Exception e) {
            Log.e("josn","解析异常");
        }
        return workItemList;
    }


    //排序
    private void sort(List<WorkItem> workItemList) {
        Collections.sort(workItemList, new Comparator<WorkItem>() {
            @Override
            public int compare(WorkItem o1, WorkItem o2) {
                if (o1.getFuncTypeIdx() < o2.getFuncTypeIdx()){
                    return -1;
                }
                if (o1.getFuncTypeIdx() > o2.getFuncTypeIdx()){
                    return 1;
                }
                if (o1.getFuncTypeIdx() == o2.getFuncTypeIdx()){

                    if (o1.getFuncIdx() < o2.getFuncIdx()){
                        return -1;
                    }
                    if (o1.getFuncIdx() > o2.getFuncIdx()){
                        return 1;
                    }
                    if (o1.getFuncIdx() == o2.getFuncIdx()){
                        return 0;
                    }
                }
                return -1;
            }
        });
    }

    //获取Items
    private List<Object> getItems(List<WorkItem> workItemList) {

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
                Log.d(s,workItem.getFuncName());
            }
        }
        return items;
    }

}

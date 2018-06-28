package com.lensim.fingerchat.fingerchat.ui.login;


import android.util.Log;
import com.fingerchat.api.client.ClientConfig;
import com.fingerchat.api.listener.UserListener;
import com.fingerchat.api.message.UserInfoMessage;
import com.fingerchat.proto.message.User;
import com.fingerchat.proto.message.User.Func;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.global.FGEnvironment;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.work_center.WorkItem;
import com.lensim.fingerchat.data.work_center.WorkItemRepository;
import java.util.ArrayList;
import java.util.List;


/**
 * date on 2018/1/5
 * author ll147996
 * describe
 */

public class LoginPresenter extends BaseMvpPresenter<LoginView> implements UserListener {

    @Override
    public void onAttachMvpView(LoginView mvpView) {
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
        try {
            System.out.println(LoginPresenter.class.getSimpleName()+"--onReceivedUserinfo");
            UserInfo info = getUserInfo(message.userInfo);
            if (info != null) {
                ssoLogin(message.userInfo.getUserid(), PasswordRespository.getPassword());
                UserInfoRepository.getInstance().setUserInfo(info);
                WorkItemRepository.getInstance()
                    .setWorkItems(WorkItem.class, getWorkItems(message.userInfo));
                ProviderUser
                    .updateUser(ContextHelper.getContext(), info);
                FGEnvironment.getInstance()
                    .initUserInfo(RosterManager.getInstance().createUser(info));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private UserInfo getUserInfo(User.UserInfo userInfo) {
        return new UserInfo(userInfo.getUserid(), userInfo.getUsernick(), userInfo.getPhoneNumber(),
            userInfo.getWorkAddress(),
            userInfo.getEmpName(), userInfo.getSex(), userInfo.getAvatar(), userInfo.getIsvalid(),
            userInfo.getJobname(),
            userInfo.getDptNo(), userInfo.getDptName(), userInfo.getEmpNo(), userInfo.getRight());
    }


    private List<WorkItem> getWorkItems(User.UserInfo userInfo) {
        List<Func> funcs = userInfo.getFunctionList();
        List<WorkItem> workItems = new ArrayList<>();
        for (Func func : funcs) {
            WorkItem workItem = new WorkItem(func.getFuncAddress(), func.getFuncId(),
                func.getFuncIdx(), func.getFuncLogo(), func.getFuncName(), func.getFuncType(),
                func.getFuncTypeIdx(), func.getFuncValid(), func.getTypeName(),
                func.getTypeValid());
            workItems.add(workItem);
        }
        return workItems;
    }


    /**
     * 单点登录 拿到Token，存SP
     **/
    private void ssoLogin(final String userid, final String pwd) {
        HttpUtils.getInstance().ssoLogin(userid, pwd)
            .compose(RxSchedulers.compose())
            .subscribe(new FGObserver<ResponseObject<SSOToken>>(false) {
                @Override
                public void onHandleSuccess(ResponseObject<SSOToken> response) {
                    if (response.code == 10) {
                        SSOToken token = response.result;
                        if (token != null) {
                            token.setUserid(userid);
                            token.setTokenValidTime(
                                System.currentTimeMillis() + token.getLifetime() * 1000);
                            SSOTokenRepository.getInstance().setSSOToken(token);
                        }
                    } else {
                        Log.e("ssoLogin", response.msg);
                    }
                }
            });
    }

}

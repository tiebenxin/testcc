package com.lensim.fingerchat.fingerchat.ui.login;

import com.fingerchat.api.message.UserInfoMessage;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.mvp.view.ProcessMvpView;


/**
 * date on 2018/7/25
 * author ll147996
 * describe
 */

public interface LoginContract {

    interface View extends ProcessMvpView {
        void onReceivedUserinfo(UserInfoMessage msg);
        void initIMClient();
        void loginOutTime();
    }


    abstract class Presenter<V extends ProcessMvpView> extends BaseMvpPresenter<LoginContract.View> {
        public abstract void login(String accout, String password, boolean isOther);
        public abstract void NetworkListener(NetStatusEvent event);
    }


}

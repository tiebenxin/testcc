package com.lensim.fingerchat.fingerchat.ui.settings;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.global.BaseURL;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.component.databind.ClickHandle;
import com.lensim.fingerchat.fingerchat.databinding.FragmentSettingsBinding;
import com.lensim.fingerchat.fingerchat.ui.login.LoginActivity;
import com.lensim.fingerchat.fingerchat.ui.settings.clear_cache.CacheOptionActivity;

/**
 * 设置
 * Created by zm on 2018/5/25.
 */
public class SettingsFragment extends BaseFragment implements ClickHandle {

    private FragmentSettingsBinding settingsBinding;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_settings, null);
        settingsBinding = DataBindingUtil.bind(rooView);
        settingsBinding.setClick(this);
        return rooView;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_message_remind:
                //消息提醒
                toActivity(ChatGlobalSettingActivity.class);
                break;
            case R.id.menu_chat:
                //聊天设置
                toActivity(MessageSettingActivity.class);
                break;
            case R.id.menu_general:
                // 通用
                toActivity(AdjustTextSizeActivity.class);
                break;
            case R.id.menu_identify:
                // 实名认证
                toActivity(IdentifyActivity.class);
                break;
            case R.id.menu_account_safe:
                // 账号和安全
                toActivity(AccoutAndSafeActivity.class);
                break;
            case R.id.menu_about_fg:
                // 关于飞鸽
                toActivity(AboutFGActivity.class);
                break;
            case R.id.menu_help_feedback:
                // 帮组和反馈
                String mUrl = BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686
                    + BaseURL.URL_SPLITTER + "help/";
                Intent intent = new Intent(getActivity(), ViewHelpAndFeedbackActivity.class);
                intent.setData(Uri.parse(mUrl));
                intent.putExtra("title", "帮助与反馈");
                startActivity(intent);
                break;
            case R.id.menu_clear_cache:
                // 清除缓存
                toActivity(CacheOptionActivity.class);
                break;
            case R.id.menu_refresh_data:
                // 数据刷新
                T.showShort(R.string.refresh_data);
                //startActivity(new Intent(getActivity(), SecretChatMessageActivity.class));
                break;
            case R.id.btn_login_out:
                // 登出
                loginOut();
                toActivity(LoginActivity.class);
                getActivity().finish();
                break;
        }
    }

    /**
     * 登出
     */
    private void loginOut() {
        if (SSOTokenRepository.getInstance().getSSOToken() != null) {
            if (SSOTokenRepository.getTokenValidTime() > System.currentTimeMillis()) {
                HttpUtils.getInstance().ssoLoginOut(SSOTokenRepository.getToken())
                    .compose(RxSchedulers.compose())
                    .subscribe(new FGObserver<ResponseObject<SSOToken>>(false) {
                        @Override
                        public void onHandleSuccess(ResponseObject<SSOToken> response) {
                            if (response.code == 10) {
                                Log.e("ssoLogin", "成功");
                            } else {
                                Log.e("ssoLogin", response.msg);
                            }
                        }
                    });
            }
        }
        FingerIM.I.unbindAccount();
        PasswordRespository.cleanPassword();
        AppConfig.INSTANCE.remove(AppConfig.ACCOUT);
        AppConfig.INSTANCE.remove(AppConfig.PASSWORD);
    }
}

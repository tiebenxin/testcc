package com.lensim.fingerchat.fingerchat.ui.settings;

import static com.lensim.fingerchat.commons.utils.UIHelper.MIN_TEXT_SIZE;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.lens.chatmodel.im_service.FingerIM;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.dialog.CommenProgressDialog;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.global.BaseURL;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.SystemApi;
import com.lensim.fingerchat.fingerchat.component.databind.ClickHandle;
import com.lensim.fingerchat.fingerchat.databinding.FragmentSettingsBinding;
import com.lensim.fingerchat.fingerchat.model.result.UserPrivilegesResult;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;
import com.lensim.fingerchat.fingerchat.ui.login.LoginActivity;
import com.lensim.fingerchat.fingerchat.ui.settings.clear_cache.CacheOptionActivity;

/**
 * 设置
 * Created by zm on 2018/5/25.
 */
public class SettingsFragment extends BaseFragment implements ClickHandle {

    private FragmentSettingsBinding settingsBinding;
    private CommenProgressDialog mProgressDialog;

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
        int factor = SPHelper.getInt("font_size", 1) * 2;
        if (factor <= 2) {
            factor = 2;
        } else if (factor > 6) {
            factor = 6;
        }
        settingsBinding.menuMessageRemind.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuChat.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuGeneral.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuIdentify.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuAboutFg.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuHelpFeedback.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuClearCache.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuRefreshData.setTextSize(factor + MIN_TEXT_SIZE);
        settingsBinding.menuAccountSafe.setTextSize(factor + MIN_TEXT_SIZE);
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
                String mUrl = BaseURL.BASE_URL + "/help/";
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
//                T.showShort(R.string.refresh_data);
                if (mProgressDialog == null) {
                    mProgressDialog = new CommenProgressDialog(getActivity(),
                        com.lensim.fingerchat.commons.R.style.LoadingDialog, "正在刷新数据...");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    mProgressDialog.show();
                }
                FingerIM.I.getRosters();//更新通讯录数据
                loadWorkCenterResource();

                /*if (SPManager.getmSpfPassword().hasPwd().get(false)){
                    startActivity(new Intent(getActivity(), PwdToSecretChatActivity.class));
                }else {
                    startActivity(new Intent(getActivity(), SecretChatMessageActivity.class));
                }*/
                break;
            case R.id.btn_login_out:
                showLoginOutDialog();
                break;
        }
    }

    private void loadWorkCenterResource() {
        new SystemApi().getUserPrivileges(UserInfoRepository.getUserId(),
            new FXRxSubscriberHelper<UserPrivilegesResult>() {
                @Override
                public void _onNext(UserPrivilegesResult result) {
                    Object object = result.getContent();
                    String json = new Gson().toJson(object);
                    if (!TextUtils.isEmpty(json)) {
                        SPHelper.saveValue(AppConfig.WORK_RESOURCE, json);
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                    }
                }
            });
    }

    private void showLoginOutDialog() {
        final NiftyDialogBuilder builder = NiftyDialogBuilder
            .getInstance(getActivity());
        builder.withTitle("提示")
            .withMessage("确认退出登录吗？")
            .withButton1Text("取消")
            .withButton2Text("立即退出")
            .setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 登出
                ((MainActivity) getActivity()).loginOut();
                toActivity(LoginActivity.class);
                getActivity().finish();
                builder.dismiss();
            }
        }).show();
    }
}

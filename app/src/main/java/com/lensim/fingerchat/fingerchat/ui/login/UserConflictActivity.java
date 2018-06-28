package com.lensim.fingerchat.fingerchat.ui.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import com.example.annotation.Path;
import com.lens.chatmodel.im_service.FingerIM;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityConflictBinding;


/**
 * Created by LY309313 on 2017/1/17.
 */
@Path(ActivityPath.USER_CONFLICT_ACTIVITY_PATH)
public class UserConflictActivity extends BaseActivity {

    private Thread thread;
    private boolean byUser;
    private int type;
    private ActivityConflictBinding ui;


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_conflict);
        ui.mConflictToolBar.setTitleText("登陆冲突");
        type = getIntent().getIntExtra(ActivityPath.CLOSE_ERROR, 0);
        byUser = false;
        if (type == 0) {
            ui.hint.setText(getString(R.string.conflit_hint, String.valueOf(5)));
        } else {
            ui.hint.setText(getString(R.string.account_disabled, String.valueOf(5)));
        }

        //退出应用
        ui.btLogout.setOnClickListener(v -> {
            byUser = true;
            requestToClose();
        });

        thread = new Thread() {
            @Override
            public void run() {
                if (type == 1) {
                    cleanLoginInfo(UserInfoRepository.getUserName());
                }
                for (int i = 4; i >= 0 && !byUser; i--) {
                    try {
                        Thread.sleep(1000);
                        final int t = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (t == 0) {
                                    requestToClose();
                                }
                                if (type == 0) {
                                    ui.hint.setText(
                                        getString(R.string.conflit_hint, String.valueOf(t)));
                                } else {
                                    ui.hint.setText(
                                        getString(R.string.account_disabled, String.valueOf(t)));
                                }

                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }


    /**
     * 用户异常
     * 清除登录信息
     */
    private void cleanLoginInfo(String username) {

    }


    /**
     * 需要关闭应用，此处不要关闭服务，只需要将密码置空，
     * 并断开链接，释放资源，跳到登陆页面---
     */
    public void requestToClose() {
        FingerIM.I.unbindAccount();
        PasswordRespository.cleanPassword();
        AppConfig.INSTANCE.remove(AppConfig.PASSWORD);
        startActivity(new Intent(this, LoginActivity.class));
    }
}
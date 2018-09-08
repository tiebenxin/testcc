package com.lensim.fingerchat.fingerchat.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.route.annotation.Path;
import com.lens.chatmodel.im_service.FingerIM;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.helper.AppManager;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityConflictBinding;



/**
 * Created by LY309313 on 2017/1/17.
 *
 */
@Path(ActivityPath.USER_CONFLICT_ACTIVITY_PATH)
public class UserConflictActivity extends BaseActivity {

    private final int MSG = 0x01;

    private int type;
    private ActivityConflictBinding ui;
    private int t = 5;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG) {
                if (type == 1) {
                    cleanLoginInfo(UserInfoRepository.getUserName());
                }

                if (t == 0) {
                    requestToClose();
                }
                if (type == 0) {
                    ui.hint.setText(getString(R.string.conflit_hint, String.valueOf(t)));
                } else {
                    ui.hint.setText(getString(R.string.account_disabled, String.valueOf(t)));
                }
                t--;
                sendEmptyMessageDelayed(MSG, 1000);
            }

        }
    };


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_conflict);
        ui.mConflictToolBar.setTitleText("登陆冲突");
        type = getIntent().getIntExtra(ActivityPath.CLOSE_ERROR, 0);

        //退出应用
        ui.btLogout.setOnClickListener(v -> requestToClose());

        handler.sendEmptyMessage(MSG);
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
        handler.removeMessages(MSG);
        MessageManager.getInstance().clearLoginData();
        startActivity(new Intent(this, LoginActivity.class));
        AppManager.getAppManager().finishAllActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MSG);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FingerIM.I.unbindAccount();
            handler.removeMessages(MSG);
            MessageManager.getInstance().clearLoginData();
            startActivity(new Intent(this, LoginActivity.class));
            AppManager.getAppManager().finishAllActivity();
            return false;
        }
        return false;
    }
}
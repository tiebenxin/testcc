package com.lensim.fingerchat.fingerchat.ui.login;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.im_service.FingerIM;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.fingerchat.ui.login.ControllerLoginItem.OnActionListener;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
import com.lensim.fingerchat.commons.utils.T;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FragmentForgetPsw extends BaseFragment {

    public final static int MIN_TIME = 1000;


    private ControllerLoginItem viewIdentifyCode;
    private ControllerLoginItem viewInputAccout;


    private IFragmentForgetListener listener;
    private LinearLayout ll_forget;
    private ControllerLoginButton viewIdentifyBtn;
    private TextView tv_notify;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forget_psw, null);
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    protected void initView() {

        ll_forget = (LinearLayout) getView().findViewById(R.id.ll_forget);
        tv_notify = (TextView) getView().findViewById(R.id.tv_notify);

        viewInputAccout = new ControllerLoginItem(getView().findViewById(R.id.viewInputAccout));
        viewInputAccout.initIconHint(R.drawable.account_number, R.string.input_accout_lose_psw);
        viewInputAccout.addRight(ControllerLoginItem.TYPE_VERIFICATION_CODE);
        viewInputAccout.initEditType(false);
        viewInputAccout.setEidtAction(true);
        viewInputAccout.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //发送请求，获取验证码
                    if (!TextUtils.isEmpty(viewInputAccout.getText())) {
                        getPhone(viewInputAccout.getText());

                        ll_forget.setVisibility(View.VISIBLE);
                        tv_notify.setText(getUserPhone());

                    } else {
                        T.showShort(R.string.input_accout_lose_psw);
                    }
                }
            }
        });
        viewInputAccout.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                //发送请求，获取验证码
                if (!TextUtils.isEmpty(viewInputAccout.getText())) {
                    getPhone(viewInputAccout.getText());

                    ll_forget.setVisibility(View.VISIBLE);
                    tv_notify.setText(getUserPhone());
                } else {
                    T.showShort(R.string.input_accout_lose_psw);
                }
            }
        });

        viewIdentifyCode = new ControllerLoginItem(getView().findViewById(R.id.viewIdentifyCode));
        viewIdentifyCode.initIconHint(R.drawable.verification_code, R.string.input_identity_code);
        viewIdentifyCode.initEditType(true);
        viewIdentifyCode.setEidtAction(true);

        viewIdentifyCode.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (listener != null) {
                        listener
                            .clickIdentify(viewInputAccout.getText(), viewIdentifyCode.getText());
                    }
                }
            }
        });

        viewIdentifyBtn = new ControllerLoginButton(getView().findViewById(R.id.viewIdentifyBtn));
        viewIdentifyBtn.setText(R.string.identify_now);
        viewIdentifyBtn.setOnControllerClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (listener != null) {
                    if (StringUtils.isIdentifyCode(viewIdentifyCode.getText())) {
                        listener
                            .clickIdentify(viewInputAccout.getText(), viewIdentifyCode.getText());
                    } else {
                        T.showShort(R.string.input_right_identify_code);
                    }
                }
            }
        });

    }

    private void getPhone(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            String phoneNum = "";
            FingerIM.I.applyVerCode(userId, phoneNum);
            timer.start();

        }
    }

    //倒计时
    private CountDownTimer timer = new CountDownTimer(45 * MIN_TIME, MIN_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (getActivity() != null) {
                viewInputAccout.setClickable(false);
                viewInputAccout.setButtonText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送",
                    R.drawable.btn_get_identify_code_red);
            }
        }

        @Override
        public void onFinish() {
            if (getActivity() != null) {
                viewInputAccout.setClickable(true);
                viewInputAccout.setButtonText("重新获取验证码", R.drawable.btn_get_identify_code);
            }
        }
    };

    private String getUserPhone() {
        return String
            .format(ContextHelper.getString(R.string.sent_identify_code), 188 + "****" + 8888);
    }

    public void notifyDestroy() {
        timer.cancel();
        timer = null;
    }


    public void setListener(IFragmentForgetListener l) {
        listener = l;
    }


    public interface IFragmentForgetListener {

        void clickIdentify(String accout, String identifyCode);

        void clickBack();

    }
}

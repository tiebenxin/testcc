package com.lensim.fingerchat.fingerchat.ui.login;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.login.ControllerLoginItem.OnActionListener;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FragmentLogin extends BaseFragment implements View.OnClickListener {

    private ControllerLoginItem viewInputAccout;
    private ControllerLoginItem viewInputPsw;
    private TextView tv_register;
    private TextView tv_forget;
    private TextView tv_more_login;
    private ControllerLoginButton viewLoginBtn;
    private IFragmentLoginListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, null);
    }


    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    protected void initView() {

        viewInputAccout = new ControllerLoginItem(getView().findViewById(R.id.viewInputAccout));
        viewInputPsw = new ControllerLoginItem(getView().findViewById(R.id.viewInputPsw));
        tv_register = (TextView) getView().findViewById(R.id.tv_register);
        tv_forget = (TextView) getView().findViewById(R.id.tv_forget);
        tv_more_login = (TextView) getView().findViewById(R.id.tv_more_login);
        viewLoginBtn = new ControllerLoginButton(getView().findViewById(R.id.viewLoginBtn));

        viewInputAccout.initIconHint(R.drawable.account_number, R.string.input_accout);
        viewInputAccout.initEditType(false);
        viewInputAccout.setEidtAction(false);

        viewInputAccout.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    viewInputPsw.requestFocus();
                }
            }
        });

        viewInputPsw.initIconHint(R.drawable.password, R.string.input_password);
        viewInputPsw.initEditType(false);
        viewInputPsw.addRight(ControllerLoginItem.TYPE_PSW);
        viewInputPsw.setEidtAction(true);
        viewInputPsw.setShowText(false);
        viewInputPsw.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (listener != null) {
                        if (!TextUtils.isEmpty(viewInputAccout.getText()) && !TextUtils
                            .isEmpty(viewInputPsw.getText())) {
                            listener.clickLogin(viewInputAccout.getText().trim(),
                                viewInputPsw.getText().trim());
                        } else {
                            if (TextUtils.isEmpty(viewInputAccout.getText())) {
                                T.showShort(R.string.accout_empty);
                            } else if (TextUtils.isEmpty(viewInputPsw.getText())) {
                                T.showShort(R.string.password_empty);
                            }
                        }
                    }
                }
            }
        });
        viewLoginBtn.setText(R.string.login);
        viewLoginBtn.setOnControllerClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (TextUtils.isEmpty(viewInputAccout.getText().trim())) {
                    T.showShort(R.string.accout_empty);
                } else if (TextUtils.isEmpty(viewInputPsw.getText().trim())) {
                    T.showShort(R.string.password_empty);
                } else if (listener != null) {
                    listener.clickLogin(viewInputAccout.getText().trim(),
                        viewInputPsw.getText().trim());
                }
            }
        });
        String accout = UserInfoRepository.getUserName();
        String password = PasswordRespository.getPassword();
        if (!TextUtils.isEmpty(accout) && !TextUtils.isEmpty(password)) {
            viewInputAccout.setText(accout);
            viewInputPsw.setText(password);
        }

        initListener();

    }


    private void initListener() {
        tv_register.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        tv_more_login.setOnClickListener(this);

    }

    public void setListener(IFragmentLoginListener l) {
        listener = l;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                if (listener != null) {
                    listener.clickRegister();
                }
                break;
            case R.id.tv_forget:
                if (listener != null) {
                    listener.clickForget();
                }
                break;
            case R.id.tv_more_login:
                if (listener != null) {
                    listener.clickMoreLogin();
                }
                break;
        }
    }

    public interface IFragmentLoginListener {

        void clickLogin(String accout, String password);

        void clickRegister();

        void clickForget();

        void clickMoreLogin();
    }
}

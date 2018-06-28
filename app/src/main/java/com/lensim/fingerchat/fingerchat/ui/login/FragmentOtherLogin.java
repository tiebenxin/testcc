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
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.fingerchat.ui.login.ControllerLoginItem.OnActionListener;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
import com.lensim.fingerchat.commons.utils.T;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FragmentOtherLogin extends BaseFragment {

    private ControllerLoginItem viewInputAccout;
    private ControllerLoginItem viewInputPsw;
    private ControllerLoginButton viewLoginBtn;
    private IFragmentLoginOtherListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_other, null);
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    protected void initView() {
//    ControllerTitleBar viewTitleBar = new ControllerTitleBar(getView().findViewById(R.id.viewTitleBar));
//    viewTitleBar.setText(R.string.login_by_phone_num);

        viewInputAccout = new ControllerLoginItem(getView().findViewById(R.id.viewInputAccout));
        viewInputPsw = new ControllerLoginItem(getView().findViewById(R.id.viewInputPsw));

        viewLoginBtn = new ControllerLoginButton(getView().findViewById(R.id.viewLoginBtn));

        viewInputAccout.initIconHint(R.drawable.phone_number, R.string.input_phone_num);
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
        viewInputPsw.initEditType(true);
        viewInputPsw.addRight(ControllerLoginItem.TYPE_PSW);
        viewInputPsw.setEidtAction(true);
        viewInputPsw.setShowText(false);
        viewInputPsw.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewInputAccout.requestFocus();
                }
            }
        });

        viewLoginBtn.setOnControllerClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (listener != null) {
                    if (!TextUtils.isEmpty(viewInputAccout.getText()) && !TextUtils
                        .isEmpty(viewInputPsw.getText())) {
                        listener.clickLogin(viewInputAccout.getText(), viewInputPsw.getText());
                    } else {
                        if (TextUtils.isEmpty(viewInputAccout.getText())) {
                            T.show("账号不能为空");
                        } else if (TextUtils.isEmpty(viewInputPsw.getText())) {
                            T.show("密码不能为空");
                        }
                    }
                }
            }
        });
    }


    public void setListener(IFragmentLoginOtherListener l) {
        listener = l;
    }


    public interface IFragmentLoginOtherListener {

        void clickLogin(String accout, String password);

        void clickBack();

    }
}

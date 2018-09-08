package com.lensim.fingerchat.fingerchat.ui.me;

import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fingerchat.proto.message.Resp.Message;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.fingerchat.R;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LY309313 on 2016/8/17.
 */

public class InputInfoActivity extends BaseUserInfoActivity {

    private EditText mInputInfo;
    public static final int REQUEST_NICK = 31;
    public static final int REQUEST_ADDRESS = 32;
    public static final int REQUEST_SIGN = 33;
    public static final String REQUEST_CODE = "request_code";
    public static final String INPUT_RESULT = "input_result";
    private EditText mInputInfoNewPwd;
    private EditText mInputInfoNewPwdComfrim;
    private TextView mInputInfoHint;
    private Button mInputInfoSubmit;
    private int intExtra;
    private EditText mInputInfoOther;
    private LinearLayout mInputInfoContainer;
    private FGToolbar toolbar;
    private String name;
    private String newPwd;

    @Override
    public void initView() {
        setContentView(R.layout.activity_inputinfo);
        toolbar = findViewById(R.id.viewTitleBar);
        initBackButton(toolbar, true);
        mInputInfo = (EditText) findViewById(R.id.mInputInfo);
        mInputInfoOther = (EditText) findViewById(R.id.mInputInfoOther);
        mInputInfoContainer = (LinearLayout) findViewById(R.id.mInputInfoContainer);
        mInputInfoNewPwd = (EditText) findViewById(R.id.mInputInfoNewPwd);
        mInputInfoNewPwdComfrim = (EditText) findViewById(R.id.mInputInfoNewPwdComfrim);
        mInputInfoHint = (TextView) findViewById(R.id.mInputInfoHint);
        mInputInfoSubmit = (Button) findViewById(R.id.mInputInfoSubmit);
        initListener();
    }

    public void initListener() {
        mInputInfo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String str = mInputInfo.getText().toString();
//                    if (!StringUtils.isEmpty(str)) {
//                        String pwd = LensImUtil.getUserPwd();
//                        if (!pwd.equals(str)) {
//                            mInputInfoHint.setVisibility(View.VISIBLE);
//                        } else {
//                            mInputInfoHint.setVisibility(View.GONE);
//                        }
//                    }
                } else {
                    mInputInfoHint.setVisibility(View.GONE);
                }
            }
        });
        mInputInfoSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (intExtra) {
                    case REQUEST_NICK:
                        String result = mInputInfoOther.getText().toString().trim();
                        if (result.length() > 10) {
                            showToast("不能超过十个字符");
                            return;
                        }
//                        if (StringUtils.isContainSpecailChar(result)) {
//                            T.show("昵称不能包含特殊字符");
//                            return;
//                        }
                        if (!StringUtils.isEmpty(result)) {
                            Intent intent = new Intent();
                            intent.putExtra(INPUT_RESULT, result);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        break;
                    case REQUEST_ADDRESS:
                        toolbar.setTitleText("更改地址");
                        mInputInfo.setHint("输入地址");
                        break;
                    case REQUEST_SIGN:
                        String str = mInputInfo.getText().toString();
                        if (!StringUtils.isEmpty(str)) {
                            String pwd = PasswordRespository.getPassword();
                            if (!pwd.equals(str)) {
                                showToast("旧密码输入不正确");
                            } else {
                                newPwd = mInputInfoNewPwd.getText().toString();
                                String newPwdComfrim = mInputInfoNewPwdComfrim.getText().toString();
                                if (newPwd.length() < 6) {
                                    showToast("新密码太短");
                                } else if (StringUtils.isEmpty(newPwd) || StringUtils
                                    .isEmpty(newPwdComfrim)) {
                                    showToast("密码含无效字符");
                                } else if (!newPwd.equals(newPwdComfrim)) {
                                    showToast("两次输入不一致");
                                } else {
                                    FingerIM.I.changePassword(getUserId(), pwd, newPwd, false);
                                }
                            }
                        } else {
                            showToast("请先输入旧密码");
                        }
                        break;
                }

            }
        });
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        intExtra = intent.getIntExtra(REQUEST_CODE, REQUEST_SIGN);
        name = intent.getStringExtra("content");
        switch (intExtra) {
            case REQUEST_NICK:
                toolbar.setTitleText("更改名字");
                mInputInfoOther.setHint(name);
                break;
            case REQUEST_ADDRESS:
                toolbar.setTitleText("更改地址");
                mInputInfo.setHint("输入地址");
                break;
            case REQUEST_SIGN:
                toolbar.setTitleText("修改密码");
                mInputInfoOther.setVisibility(View.GONE);
                mInputInfoContainer.setVisibility(View.VISIBLE);
                mInputInfo.setHint("输入旧密码");
                mInputInfoNewPwd.setVisibility(View.VISIBLE);
                mInputInfoNewPwdComfrim.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithRequest(event);
        }
    }

    private void dealWithRequest(IEventProduct event) {
        if (event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent) event;
            if (response.getPacket() != null && response.getPacket().response != null) {
                Message msg = response.getPacket().response;
                if (msg.getCode() == Common.UPDATE_SUCCESS) {//更新密码成功
                    T.show("修改成功");
                    switch (intExtra) {
                        case REQUEST_SIGN:
                            PasswordRespository.setPassword(newPwd);
                            break;
                    }
                    this.finish();
                } else if (msg.getCode() == Common.UPDATE_FAILURE) {
                    T.show("修改失败");
                } else if (msg.getCode() == Common.PHONE_INVALID
                    || msg.getCode() == Common.OBTAIN_CODE_EXIST
                    || msg.getCode() == Common.USERNAME_INVALIDE) {
                    T.show("获取验证码失败");
                }
            }
        }
    }


}

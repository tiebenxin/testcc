package com.lensim.fingerchat.fingerchat.ui.me;

import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.fingerchat.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;

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
                        String result = mInputInfoOther.getText().toString();
                        if (result.length() > 10) {
                            showToast("不能超过十个字符");
                            return;
                        }
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
//                        if (!StringUtils.isEmpty(str)) {
//                            String pwd = LensImUtil.getUserPwd();
//                            if (!pwd.equals(str)) {
//                                showToast("旧密码输入不正确");
//                            } else {
//                                String newPwd = mInputInfoNewPwd.getText().toString();
//                                String newPwdComfrim = mInputInfoNewPwdComfrim.getText().toString();
//                                if (newPwd.length() < 6) {
//                                    showToast("新密码太短");
//                                } else if (StringUtils.isEmpty(newPwd) || StringUtils
//                                    .isEmpty(newPwdComfrim)) {
//                                    showToast("密码含无效字符");
//                                } else if (!newPwd.equals(newPwdComfrim)) {
//                                    showToast("两次输入不一致");
//                                } else {
//                                    AppConfig.getAppConfig(mContext).SaveUserPwd(pwd);
//                                    // loginUser.setPwd(pwd);
//                                    //  mUserInfoSign.setText(pwd);
//                                    LensImUtil.updateUserPwd(LensImUtil.getUserName(), pwd);
//                                    try {
//                                        AccountManager am = AccountManager.getInstance(
//                                            NetworkUtils.getInstance().getConnectionThread()
//                                                .getXmppConnection());
//                                        am.changePassword(newPwd);
//                                        MyApplication.getInstance().requestToClose();
//                                        T.showShort(mContext, "密码修改成功");
//                                        finish();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        T.showShort(mContext, "密码修改失败");
//                                    }
//                                }
//                            }
//                        } else {
//                            showToast("请先输入旧密码");
//                        }
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
}

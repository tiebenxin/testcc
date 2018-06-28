package com.lensim.fingerchat.fingerchat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.bean.QRLoginBean;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.observer.FGObserver;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityPermitLoginBinding;
import java.io.IOException;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2018/5/17.
 */

public class PermitLoginActivity extends BaseUserInfoActivity {

    private ActivityPermitLoginBinding ui;
    private QRLoginBean qrLoginBean;

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, PermitLoginActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_permit_login);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        ui.toolbar.setTitleText("授权登录");
        initBackButton(ui.toolbar, true);
        if (!TextUtils.isEmpty(url)) {
            loginQRCode(url);
            ui.tvButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptLogin();
                }
            });
        }
    }

    private void loginQRCode(String url) {
        url = url + "&token=" + SSOTokenRepository.getToken();
        HttpUtils.getInstance().getLoginInfo(url, new IDataRequestListener() {
            @Override
            public void loadFailure(String reason) {
                T.show("扫码登录失败");
                ui.tvButton.setVisibility(View.GONE);
            }

            @Override
            public void loadSuccess(Object object) {
                String result = (String) object;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.optInt("code") == 10) {
                            String content = obj.optString("content");
                            qrLoginBean = GsonHelper.getObject(content, QRLoginBean.class);

                            if (qrLoginBean != null) {
                                initControl();
                            } else {
                                ui.tvButton.setVisibility(View.GONE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void initControl() {

        ui.tvContent
            .setText(this.getString(R.string.permit_login_content, qrLoginBean.getAppName()));
        ImageHelper.loadAvatarPrivate(getUserAvatar(), ui.ivAvatar);
        ui.tvButton.setVisibility(View.VISIBLE);
    }


    private void acceptLogin() {
        if (qrLoginBean == null) {
            return;
        }
        HttpUtils.getInstance()
            .acceptQRCodeLogin(SSOTokenRepository.getToken(), qrLoginBean.getAppid(),
                qrLoginBean.getQrtcodeId())
            .compose(RxSchedulers.compose()).subscribe(new FGObserver<ResponseBody>() {
            @Override
            public void onHandleSuccess(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject obj = new JSONObject(result);
                        if (obj.optInt("code") == 10) {
                            T.show("登陆成功");
                            PermitLoginActivity.this.finish();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

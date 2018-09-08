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

    public static Intent newIntent(Context context, String appId, String qrtCodeId) {
        Intent intent = new Intent(context, PermitLoginActivity.class);
        intent.putExtra("appId", appId);
        intent.putExtra("qrtCodeId", qrtCodeId);
        return intent;
    }

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_permit_login);
        Intent intent = getIntent();
//        String url = intent.getStringExtra("url");
        String appId = intent.getStringExtra("appId");
        String qrtCodeId = intent.getStringExtra("qrtCodeId");
        ui.toolbar.setTitleText("授权登录");
        initBackButton(ui.toolbar, true);
        if (!TextUtils.isEmpty(appId) && !TextUtils.isEmpty(qrtCodeId)) {
            loginQRCode(appId, qrtCodeId);
            ui.tvButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptLogin();
                }
            });
        } else {
            T.show("二维码信息不足");
        }
    }

    public void loginQRCode(String appId, String qrCodeId) {
        String token = SSOTokenRepository.getToken();
        if (TextUtils.isEmpty(token)) {
            T.show("token为空");
            return;
        }
        HttpUtils.getInstance().qrCodeLogin(token, appId, qrCodeId)
            .compose(RxSchedulers.compose()).subscribe(new FGObserver<ResponseBody>() {
            @Override
            public void onHandleSuccess(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    if (!TextUtils.isEmpty(result)) {
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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                T.show("获取第三方信息失败");
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
                        } else {
                            T.show("登陆失败");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                T.show("登录失败");
            }
        });
    }
}

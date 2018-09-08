package com.lensim.fingerchat.fingerchat.ui.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.UserInfoBean;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.identify.UserIdentifyResponse;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityIdentifySettingBinding;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;
import java.util.List;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LY305512 on 2017/12/25.
 */

public class IdentifyActivity extends BaseActivity {

    ActivityIdentifySettingBinding ui;
    private String employeeId;


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_identify_setting);
        ui.identifyToolbar.setTitleText("实名认证");
        initBackButton(ui.identifyToolbar, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        setListener();
    }

    private void setListener() {
        ui.tvComfrim.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //工号
                employeeId = ui.etIdentifyEmployeeId.getText().toString();
                String id = ui.etIdentifyId.getText().toString();//身份证号
                if (StringUtils.isEmpty(id) || StringUtils.isEmpty(employeeId)) {
                    T.show("需要填写完整信息");
                    return;
                }
                if (employeeId.equalsIgnoreCase(UserInfoRepository.getUserId())) {
                    if (StringUtils.isIDCard(id)) {
                        identityNoImg(employeeId, id);
                    } else {
                        T.show("请填写正确的身份证号码");
                    }
                } else {
                    T.show("只能认证当前账号");
                }
            }
        });
    }


    private void identityNoImg(String employeeId, String id) {
        HttpUtils.getInstance().userAuth(UserInfoRepository.getUserId(), employeeId, id)
            .compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<ResponseBody>() {
                @Override
                public void onNext(ResponseBody response) {
                    try {
                        JSONObject jsonobj = new JSONObject(response.string());
                        int code = jsonobj.optInt("code");
                        if (code == 12) {//认证成功
                            /*
                            * {"code":12,"message":"操作成功","content":{"affects":1}}
                            * */
                            T.show("认证成功");
                            getUserInfo();
                        } else if (code == 25) {//已认证
                            T.show(jsonobj.optString("message"));
                        } else if (code == 24) {//未找到用户
                            T.show(jsonobj.optString("message"));
                        } else if (code == 20) {//请求错误
                            T.show(jsonobj.optString("message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    T.show("网络异常,认证失败..");
                }
            });
    }

    private void getUserInfo() {
        if (TextUtils.isEmpty(employeeId)) {
            return;
        }
        HttpUtils.getInstance()
            .getUserInfo(employeeId, new IDataRequestListener() {
                @Override
                public void loadFailure(String reason) {
                    System.out.println("获取用户信息失败：" + reason);
                }

                @Override
                public void loadSuccess(Object object) {
                    if (object != null && object instanceof String) {
                        String result = (String) object;
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject != null) {
                                    String json = jsonObject.optString("content");
                                    UserInfoBean bean = GsonHelper
                                        .getObject(json, UserInfoBean.class);
                                    if (bean != null) {
                                        UserInfo info = RosterManager.getInstance()
                                            .createUserInfo(bean);
                                        if (info != null) {
                                            UserInfoRepository.getInstance().setUserInfo(info);
                                        }
                                    }
                                    MainActivity.start(getBaseContext(), MainActivity.SETTING);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
    }

}

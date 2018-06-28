package com.lensim.fingerchat.fingerchat.ui.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.identify.UserIdentifyResponse;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityIdentifySettingBinding;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;
import java.util.Iterator;
import java.util.List;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by LY305512 on 2017/12/25.
 */

public class IdentifyActivity extends BaseActivity {

    ActivityIdentifySettingBinding ui;


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
                final String employeeId = ui.etIdentifyEmployeeId.getText().toString();//工号
                String id = ui.etIdentifyId.getText().toString();//身份证号
                if (StringUtils.isEmpty(id) || StringUtils.isEmpty(employeeId)) {
                    T.show("需要填写完整信息");
                    return;
                }
                identityNoImg(employeeId, id);
            }
        });
    }


    private void identityNoImg(String employeeId, String id) {
        Http.certification(Route.URL_Identify, employeeId, id,
            UserInfoRepository.getInstance().getUserInfo().getUserid())
            .compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<ResponseBody>() {
                @Override
                public void onNext(ResponseBody response) {
                    try {
                        JSONObject jsonobj = new JSONObject(response.string());
                        JSONObject obj1 = new JSONObject(jsonobj.getString("CheckIdCardResult"));
                        JSONArray jsonArray = obj1.getJSONArray("Table");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Iterator<String> keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (key.equals("ErrorMsg")) {
                                String value = jsonObject.getString(key);
                                if (StringUtils.isEmpty(value)) {
                                    T.showShort(IdentifyActivity.this, "认证失败");
                                } else {
                                    T.showShort(IdentifyActivity.this, value);
                                }
                                return;
                            }
                        }
                        String str = jsonArray.getString(0);
                        L.i("认证信息:", str);
                        getUserInfo();

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
        Http.getUserInfoByAsync("getuser", UserInfoRepository.getUserName())
            .compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<List<UserIdentify>>() {
                @Override
                public void onNext(List<UserIdentify> userIdentifies) {
                    UserIdentify userEntity = userIdentifies.get(0);
                    UserIdentifyResponse.getInstance().setUserIdentify(userEntity);

                    MainActivity.start(getBaseContext(), MainActivity.SETTING);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    MainActivity.start(getBaseContext(), MainActivity.SETTING);
                }
            });
    }


}

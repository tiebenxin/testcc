package com.lensim.fingerchat.fingerchat.ui.work_center.identify;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.Http.Listener;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.identify.UserIdentifyResponse;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityIdentifyBinding;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by LY309313 on 2016/9/5.
 */

public class IdentifyActivity extends FGActivity {

    ActivityIdentifyBinding ui;

    private Context mContext;
    private int pos;

    private static final String filename1 = "handheldid.png";
    private static final String filename2 = "frontsideid.png";
    private static final String filename3 = "backsideid.png";
    private boolean needUpImg = true;


    @SuppressWarnings("deprecation")
    @Override
    public void initView() {
        mContext = this;
        ui = DataBindingUtil.setContentView(this, R.layout.activity_identify);

        ui.identifyToolbar.setTitleText("实名认证");
//        needUpImg = MyApplication.getInstance().getBoolean("identify_realname",false);
        needUpImg = false;
        if (!needUpImg) {
            ui.desc.setVisibility(View.GONE);
            ui.idCard1.setVisibility(View.GONE);
            ui.idCard2.setVisibility(View.GONE);
            ui.idCard3.setVisibility(View.GONE);
        }

        initListener();
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/avatarCache";
        File filedir = new File(folderPath);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        File file1 = new File(folderPath + "/" + filename1);
        File file2 = new File(folderPath + "/" + filename2);
        File file3 = new File(folderPath + "/" + filename3);
        if (file1.exists()) {
            file1.delete();
        }
        if (file2.exists()) {
            file2.delete();
        }
        if (file3.exists()) {
            file3.delete();
        }

    }

    public void initListener() {
        ui.idCardComfrim.setOnClickListener((view) -> {
            final String employeeId = ui.identifyEmployeeId.getText().toString();
            String id = ui.identifyId.getText().toString();
            if (StringUtils.isEmpty(id) || StringUtils.isEmpty(employeeId)) {
                T.show("需要填写完整信息");
                return;
            }
            if (!needUpImg) {
                identityNoImg(employeeId, id);
            } else {
                identify(employeeId, id);
            }
        });

        ui.idCard1.setOnClickListener((view) -> {
            pos = 1;
            toCamara(filename1);
        });

        ui.idCard1.setOnClickListener((view) -> {
            pos = 2;
            toCamara(filename2);
        });

        ui.idCard1.setOnClickListener((view) -> {
            pos = 3;
            toCamara(filename3);
        });
    }


    private void identify(String employeeId, String id) {
        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/avatarCache";

        List<String> imgs = new ArrayList<>();
        File file1 = new File(file + File.separator + filename1);
        File file2 = new File(file + File.separator + filename2);
        File file3 = new File(file + File.separator + filename3);
        if (file1.length() < 1000 || file2.length() < 1000 || file3.length() < 1000) {
            T.show("请按提示上传三张完整照片");
            return;
        }
//        showProgress("正在认证...", true);
        imgs.add(file + File.separator + filename1);
        imgs.add(file + File.separator + filename2);
        imgs.add(file + File.separator + filename3);
        Http.sendIdcard(UserInfoRepository.getInstance().getUserInfo().getUserid(),
            imgs, id, employeeId, new Listener() {
                @Override
                public void success(Observable<RetObjectResponse<String>> observable) {
                    sendIdcard(observable);
                }
            });

    }


    private void sendIdcard(Observable<RetObjectResponse<String>> observable) {
        observable.compose(RxSchedulers.compose())
            .subscribe(new BaseObserver<RetObjectResponse<String>>() {
                @Override
                public void onNext(RetObjectResponse<String> response) {
                    if (1 != response.retCode) {
                        T.show(response.retMsg);
                        return;
                    }
//                                setProgressMessage("完成认证，正在登录...");
                    //返回登陆界面登陆？还是直接登陆
                    getUserInfo();
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
                                    T.showShort(mContext, "认证失败");
                                } else {
                                    T.showShort(mContext, value);
                                }
                                return;
                            }
                        }
                        String str = jsonArray.getString(0);
                        L.i("认证信息:", str);
//                        MyApplication.getInstance().saveString(LensImUtil.getUserName(), CyptoUtils.encrypt(str));
//                        setProgressMessage("完成认证，正在登录...");
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

                    MainActivity.start(getBaseContext(), MainActivity.WORK);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    MainActivity.start(getBaseContext(), MainActivity.WORK);
                }
            });
    }

    private <T> T fromJson(ResponseBody response, @NonNull String result, Class<T> classOfT) {
        String string = "";
        Gson gson = new Gson();
        try {
            string = response.string();
            JSONObject obj = new JSONObject(string);
            String json = obj.getString(result);
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void toCamara(String filename) {
        Intent camara = new Intent();
        camara.setAction("android.media.action.IMAGE_CAPTURE");
        camara.addCategory("android.intent.category.DEFAULT");
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/avatarCache";
        File filedir = new File(folderPath);
        L.d("注册时照片的存储路径：", folderPath + "/" + filename);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        File file = new File(folderPath + "/" + filename);
        Uri uri = Uri.fromFile(file);
        camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(camara, AppConfig.REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/avatarCache";
                switch (pos) {
                    case 1:
                        ui.idCardPic1.setScaleType(ImageView.ScaleType.FIT_XY);
                        Glide.with(this).load(new File(file, filename1))
                            .diskCacheStrategy(DiskCacheStrategy.NONE).into(ui.idCardPic1);
                        break;
                    case 2:
                        ui.idCardPic2.setScaleType(ImageView.ScaleType.FIT_XY);
                        Glide.with(this).load(new File(file, filename2))
                            .diskCacheStrategy(DiskCacheStrategy.NONE).into(ui.idCardPic2);
                        break;
                    case 3:
                        ui.idCardPic3.setScaleType(ImageView.ScaleType.FIT_XY);
                        Glide.with(this).load(new File(file, filename3))
                            .diskCacheStrategy(DiskCacheStrategy.NONE).into(ui.idCardPic3);
                        break;
                }
            }

        }

    }
}

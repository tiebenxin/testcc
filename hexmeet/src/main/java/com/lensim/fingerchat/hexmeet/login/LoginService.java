package com.lensim.fingerchat.hexmeet.login;

import android.Manifest.permission;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import com.hexmeet.sdk.IActiveSdkCallback;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.hexmeet.UserRepository;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.ApiClient;
import com.lensim.fingerchat.hexmeet.api.model.RestErrorMessage;
import com.lensim.fingerchat.hexmeet.api.model.RestLoginReq;
import com.lensim.fingerchat.hexmeet.api.model.RestLoginResp;
import com.lensim.fingerchat.hexmeet.api.model.RestResult;
import com.lensim.fingerchat.hexmeet.api.model.RestTerminal;
import com.lensim.fingerchat.hexmeet.api.model.RestTerminalProfile;
import com.lensim.fingerchat.hexmeet.api.model.RestTerminalReq;
import com.lensim.fingerchat.hexmeet.app.NetworkStateService;
import com.lensim.fingerchat.hexmeet.type.DatabaseHelper;
import com.lensim.fingerchat.hexmeet.utils.CallRecordManager;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.SHA1;
import com.lensim.fingerchat.hexmeet.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginService extends ContextWrapper implements IActiveSdkCallback {

    public static final String HEX_SP_NAME = "hex_sp";
    public static final String HEX_SERVER = "hex_rcmserver";
    public static final String HEX_NAME = "hex_name";
    public static final String HEX_PWD = "hex_password";
    public static final String HEX_ID = "hex_id";
    public static final String HEX_IS_LOGIN = "hex_is_loged_in";
    public static final String HEX_LAST_LOGIN_TIMESTAMP = "hex_last_login_timestamp";

    private SharedPreferences sp;
    private static LoginService instance = new LoginService(App.getContext());

    public static LoginService getInstance() {
        return instance;
    }

    private LoginService(Context context) {
        super(context);
        sp = getSharedPreferences(HEX_SP_NAME, Context.MODE_PRIVATE);
    }

    public void login(String username, String password, Handler handler) {
        RestLoginReq loginReq = new RestLoginReq();
        loginReq.setAccount(username);
        loginReq.setPassword(SHA1.hash(password));
        loginReq.setLanguage(Locale.getDefault().toString());

        loginInternal(loginReq, handler, false);

        String uuid = readLicenseUuid();
        String rcmserver = RuntimeData.getUcmServer();
        App.mHexmeetSdkInstance.activeHexmeetSdk(rcmserver, uuid, this);
    }

    public void autoLogin(Handler handler) {
        String rcmserver = RuntimeData.getUcmServer();
        String username = UserRepository.getInstance().getUser().userCode;
        String password = sp.getString(HEX_PWD, "");

        if (StringUtils.isEmpty(rcmserver) || StringUtils.isEmpty(username) || StringUtils
            .isEmpty(password)) {
            handlerError(handler, null);
            return;
        }

        RestLoginReq loginReq = new RestLoginReq();
        loginReq.setAccount(username);
        loginReq.setPassword(SHA1.hash(password));
        loginReq.setLanguage(Locale.getDefault().toString());

        loginInternal(loginReq, handler, true);

        String uuid = readLicenseUuid();
        App.mHexmeetSdkInstance.activeHexmeetSdk(rcmserver, uuid, this);
    }

    @Override
    public void onActiveSdkResponse(boolean isSuccess, String reason) {
        if (isSuccess) {
//      showToast(App.getContext(), "sdk激活成功");
            RuntimeData.registerSip();
        } else {
//      showToast(App.getContext(), "skd激活失败：" + reason);
        }
    }

    private String readLicenseUuid() {
        //getting the .txt file
        InputStream inputStream = getResources().openRawResource(R.raw.hexmeetsdk);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String licenseContent = byteArrayOutputStream.toString();
        String lines[] = licenseContent.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.contains("sdklic")) {
                return line.split(":")[1];
            }
        }
        return null;
    }

    private void loginInternal(final RestLoginReq loginReq, final Handler handler,
        final boolean logoutOnError) {
        ApiClient.login(loginReq, new Callback<RestLoginResp>() {
            @Override
            public void onResponse(Call<RestLoginResp> call, Response<RestLoginResp> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 404 || response.code() == 503) {
                        handlerError(handler,
                            App.getContext().getResources().getString(R.string.server_unavailable));
                    } else {
                        if (logoutOnError) {
                            try {
                                RestErrorMessage restError = ApiClient
                                    .fromErrorJson(response.errorBody().string());
                                // 2001: invalid user/password
                                if (restError != null && restError.getErrorCode() == 2001) {

                                } else if (restError != null && restError.getErrorCode() == 2003) {
                                    // 2003: not get MCS yet
                                    handlerError(handler, App.getContext().getResources()
                                        .getString(R.string.server_unavailable));
                                    return;
                                }

                                logout(false);
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        handlerError(handler, ApiClient.fromErrorResponse(response));
                    }

                    return;
                }

                RestLoginResp resp = response.body();
                RuntimeData.setToken(resp.getToken());
                RuntimeData.setLogUser(resp.getProfile());

                RestTerminalProfile terminalProfile = resp.getTerminalProfile();
                if (terminalProfile != null) {
                    String internalSipServer = terminalProfile.getInternalSipServer();
                    if (StringUtils.isNotEmpty(internalSipServer)) {
                        RuntimeData.setInternalSipServer(internalSipServer);
                        RuntimeData
                            .setInternalSipProtocol(terminalProfile.getInternalSipProtocol());
                    }

                    String externalSipServer = terminalProfile.getExternalSipServer();
                    if (StringUtils.isNotEmpty(externalSipServer)) {
                        RuntimeData.setExternalSipServer(externalSipServer);
                        RuntimeData
                            .setExternalSipProtocol(terminalProfile.getExternalSipProtocol());
                    }

                    RuntimeData.setSipUserName(terminalProfile.getSipUserName());
                    RuntimeData.setSipAuthorizationName(terminalProfile.getAuthorizationName());
                    RuntimeData.setSipPassword(terminalProfile.getSipPassword());
                    RuntimeData.registerSip();

                    Editor editor = sp.edit();
                    editor.putString("sipNumber", terminalProfile.getSipUserName());
                    editor.commit();
                }

                WifiManager wifi = (WifiManager) getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
                RuntimeData.setDeviceSN(wifi.getConnectionInfo().getMacAddress());

                RestTerminalReq req = new RestTerminalReq();
                req.setPlatform("ANDROID");
                req.setDeviceToken(
                    "10b7affa1be010d96d022a631b5bafef653edfbc665db75ed43f5926bffe119f");
                req.setDeviceSN(buildDeviceSn());
                req.setDescription(Utils.getVersion());
                req.setIpAddress(NetworkUtil.getIPAddress(true));
                req.setDeviceName(Build.MODEL.toString());
                req.setLanguage(Locale.getDefault().toString());
                req.setOsVersion(Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT);
                req.setAppVersion(Utils.getVersion());
                req.setBrand(Build.MODEL);

                ApiClient.registerTerminal(req, new Callback<RestTerminal>() {
                    @Override
                    public void onResponse(Call<RestTerminal> call,
                        Response<RestTerminal> response) {
                        if (!response.isSuccessful()) {
                            if (response.code() == 404 || response.code() == 503) {
                                handlerError(handler, App.getContext().getResources()
                                    .getString(R.string.server_unavailable));
                            } else {
                                if (logoutOnError) {
                                    logout(false);
                                    return;
                                }

                                handlerError(handler, ApiClient.fromErrorResponse(response));
                            }
                            return;
                        }

                        if (handler != null) {
                            Message msg = Message.obtain();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<RestTerminal> call, Throwable e) {
                        handlerError(handler, e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<RestLoginResp> call, Throwable e) {
                handlerError(handler, e.getMessage());
            }
        });
    }

    private void handlerError(final Handler handler, String error) {
        if (handler == null) {
            return;
        }

        if (error != null) {
            if (error.contains("java.net.SocketTimeoutException")) {
                error = getString(R.string.server_unavailable);
            }
        } else {
            error = getString(R.string.login_again);
        }

        Message msg = Message.obtain();
        msg.what = -1;
        msg.obj = error;

        handler.sendMessage(msg);
    }

    public void logout(final boolean logoutUcmServer) {
        final String token = RuntimeData.getToken();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (logoutUcmServer) {
                    ApiClient.logout(token, new Callback<RestResult>() {
                        @Override
                        public void onResponse(Call<RestResult> call, Response<RestResult> resp) {
                        }

                        @Override
                        public void onFailure(Call<RestResult> call, Throwable e) {

                        }
                    });
                }

                SharedPreferences spp = getSharedPreferences("login", Context.MODE_PRIVATE);
                spp.edit().putBoolean("login", false).commit();
                spp.edit().putString("sipNumber", "").commit();
                ApiClient.setLastModifiedTime(0);

                App.getHexmeetSdkInstance().unregisterSip();

                stopService(new Intent(App.getContext(), NetworkStateService.class));

                App.stopServiceTask();
                DatabaseHelper.close();
                CallRecordManager.reset();
                ApiClient.reset();
                NetworkUtil.shutdown();
                RuntimeData.reset();
            }
        }).start();
    }

    public String buildDeviceSn() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(
            Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;

        if (VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
                tmDevice = "";
                tmSerial = "";
            } else {
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
            }
        } else {
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
        }


        androidId = ""
            + android.provider.Settings.Secure.getString(getContentResolver(),
            android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32)
            | tmSerial.hashCode());
        return deviceUuid.toString();
    }
}

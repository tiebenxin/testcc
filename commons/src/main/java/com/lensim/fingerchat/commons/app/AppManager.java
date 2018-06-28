package com.lensim.fingerchat.commons.app;


import static com.lensim.fingerchat.commons.app.AppConfig.LOGIN_STATUS;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lensim.fingerchat.commons.helper.CodeHelper;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.ICreateListener;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.QRCodeUtil;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.io.File;

/**
 * activity堆栈式管理
 */
public class AppManager {


    private static final AppManager instance;
//    private SettingsProvider settingsProvider;

    private AppManager() {
    }

    static {
        instance = new AppManager();
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {

        return instance;
    }

    /*
    * 更新保存登录状态
    * */
    public void setLoginStatus(boolean b) {
        SPHelper.saveValue(LOGIN_STATUS, b);
    }

    /*
    * 是否已经登录
    * */
    public boolean hasLogin() {
        return SPHelper.getBoolean(LOGIN_STATUS, false);
    }

//    public SettingsProvider getSettingsProvider() {
//    	if(settingsProvider == null){
//    		settingsProvider = new DefaultSettingsProvider();
//    	}
//		return settingsProvider;
//	}

//	public void setSettingsProvider(SettingsProvider settingsProvider) {
//		this.settingsProvider = settingsProvider;
//	}
//
//	/**
//     * 新消息提示设置的提供者
//     *
//     */
//    public interface SettingsProvider {
//        boolean isMsgNotifyAllowed(Message message);
//        boolean isMsgSoundAllowed(Message message);
//        boolean isMsgVibrateAllowed(Message message);
//        boolean isSpeakerOpened();
//    }

//    /**
//     * default settings provider
//     *
//     */
//    protected class DefaultSettingsProvider implements SettingsProvider{
//
//        @Override
//        public boolean isMsgNotifyAllowed(Message message) {
//            // TODO Auto-generated method stub
//            return true;
//        }
//
//        @Override
//        public boolean isMsgSoundAllowed(Message message) {
//            return true;
//        }
//
//        @Override
//        public boolean isMsgVibrateAllowed(Message message) {
//            return true;
//        }
//
//        @Override
//        public boolean isSpeakerOpened() {
//            return true;
//        }
//
//
//    }


    /**
     * 每次启动都检查二维码是否已经存在，不存在就创建
     */
    public void initAcode(String userId, String avatarUrl, ICreateListener listener) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        String QRcodePath = AppConfig.QR_CODE_PATH + File.separator + userId + ".qr";
        if (!TextUtils.isEmpty(avatarUrl)) {
            Glide.with(ContextHelper.getContext())
                .load(avatarUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource,
                        GlideAnimation<? super Bitmap> glideAnimation) {
                        if (!FileUtil.checkFilePathExists(QRcodePath)) {
                            FileUtil.createNewFileInSDCard(
                                AppConfig.QR_CODE_PATH + File.separator + userId + ".qr");
                            boolean flag = QRCodeUtil
                                .createQRImage(CodeHelper
                                        .createContentText(userId, CodeHelper.TYPE_PRIVATE),
                                    (int) TDevice.getScreenWidth() * 3 / 4,
                                    (int) TDevice.getScreenWidth() * 3 / 4, resource,
                                    QRcodePath);
                            if (flag) {
                                listener.createCode();
                            }
                        } else {
                            listener.createCode();
                        }
                    }
                });
        } else {
            if (!FileUtil.checkFilePathExists(QRcodePath)) {
                FileUtil.createNewFileInSDCard(
                    AppConfig.QR_CODE_PATH + File.separator + userId + ".qr");
                QRCodeUtil
                    .createQRImage(CodeHelper.createContentText(userId, CodeHelper.TYPE_PRIVATE),
                        (int) TDevice.getScreenWidth() * 3 / 4,
                        (int) TDevice.getScreenWidth() * 3 / 4, null, QRcodePath);
            }
        }
    }

    /*
    * @param roomname 群id
    * 初始化群二维码
    * */
    public void initMucAcode(String roomname) {
        if (!StringUtils.isEmpty(roomname)) {
            String QRcodePath = AppConfig.QR_CODE_PATH + File.separator + roomname + ".qr";
            if (!FileUtil.checkFilePathExists(QRcodePath)) {
                FileUtil.createNewFileInSDCard(
                    AppConfig.QR_CODE_PATH + File.separator + roomname + ".qr");
                L.d("二维码路径:" + QRcodePath);
                QRCodeUtil
                    .createQRImage(CodeHelper.createContentText(roomname, CodeHelper.TYPE_MUC),
                        (int) TDevice.getScreenWidth() * 3 / 4,
                        (int) TDevice.getScreenWidth() * 3 / 4,
                        null, QRcodePath);
            }
        }
    }

//    /**
//     * 每次启动都检查头像是否存在，不存在就从网络获取
//     */
//    public static void initAvatar(){
//        String username = "";
//        if(AppConfig.getAppConfig(MyApplication.getInstance().getApplication()).containsProperty("user.account")){
//            username =  AppConfig.getAppConfig(MyApplication.getInstance().getApplication()).getProperty("user.account").toLowerCase();
//        }
//        if(!StringUtils.isEmpty(username)){
//            String AvatarPath = AppConfig.DEFAULT_HEAD_PATH + username + ".0";
//            if(!FileUtil.checkFilePathExists(AvatarPath)){
//                asyncGetAvatar(username, LensImUtil.AvatarType.ME);
//            }
//        }
//
//    }

    public boolean checkCamaraAndAudioPermisson(Activity activity) {
        int CamaraPermissonRequestCode = 2;

        if (Build.VERSION.SDK_INT >= 23) {
            int CamaraPermisson = ContextCompat
                .checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (CamaraPermisson != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO}, CamaraPermissonRequestCode);
                return false;

            } else {
                return true;
            }
        } else {
            return true;

        }
    }

//    public void startJob(){
//        int jodId = 1;
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            JobInfo.Builder builder = new JobInfo.Builder(jodId, new ComponentName(ContextHelper.getContext(),KeepLiveService.class));
//            builder.setPeriodic(60*1000);
//            builder.setPersisted(true);
//            JobScheduler scheduler = (JobScheduler) ContextHelper.getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
//            scheduler.schedule(builder.build());
//
//        }
//    }

    public void cancelJob() {
        // int jodId = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler scheduler = (JobScheduler) ContextHelper.getContext()
                .getSystemService(Context.JOB_SCHEDULER_SERVICE);
            scheduler.cancelAll();

        }
    }

    public boolean checkCamara(Activity activity) {
        int ScanRequestCode = 5;
        boolean hasPermisson;
        int camaraPermisson = ContextCompat
            .checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= 23) {
            if (camaraPermisson != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                    .requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                        ScanRequestCode);
                hasPermisson = false;
            } else {
                hasPermisson = true;
            }
        } else {
            hasPermisson = true;
        }
        return hasPermisson;
    }
}

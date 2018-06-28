package com.lensim.fingerchat.commons.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.permission.SettingsPermissions;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

public class AppConfig extends BaseConfig {

    public static final AppConfig INSTANCE = new AppConfig("App.config");


    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_SINGLE_IMAGE = 2;
    public static final int REQUEST_CLIP_IMAGE = 3;
    public static final int REQUEST_IMAGE = 21;
    //public static final int REQUEST_VCARD = 22;
    public static final int REQUEST_HEAD_IMG = 22;
    public static final int REQUEST_VIDEO = 23;
    public static final int REQUEST_VIDEO_AND_TEXT = 24;
    public static final int REQUEST_IMGS = 25;
    public static final int REQUEST_USER = 26;
    public static final int REQUEST_EX = 27;
    public static final int REQUEST_VOTE = 28;
    public static final int REQUEST_FILE = 29;
    public static final int REQUEST_COLLECTION = 30;
    public static final int REQUEST_TRANSFOR = 31;
    public static final int REQUEST_SCAN_CODE = 32;//扫描二维码
    public static final int REQUEST_CHANGE_CONFIG = 33;//更改聊天配置信息
    public static final int REQUEST_CHANGE_MEMBER = 34;//更改群成员
    public static final int REQUEST_MANAGE_MUC = 35;//群管理
    public static final int REQUEST_CHANGE_MUC_NICK = 35;//修改群名称

    public static final int REGISTER_USER = 0;
    public static final int REGISTER_MUC = 1;

    public final static String ACCOUT = "accout";
    public final static String PASSWORD = "password";
    public final static String LOGIN_STATUS = "login_status";


    private final static String APP_CONFIG = "config";


    public final static String CONF_COOKIE = "cookie";

    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";

    //好友头像的存储路径,后缀名统一以.0结尾
    public static final String DEFAULT_HEAD_PATH = Environment.getExternalStorageDirectory()
        + File.separator + "fingerChat"
        + File.separator + "avatar" + File.separator;

    //聊天中接受的图片
    public static final String MSG_IMG_PATH = Environment.getExternalStorageDirectory()
        + File.separator + "fingerChat"
        + File.separator + "image" + File.separator;

    //二维码存放地址，以用户名命名
    public static final String QR_CODE_PATH = FileUtil
        .getDiskCacheDirs(ContextHelper.getContext(), "QRCode").getPath();

    public static final String MSG_VOICE_PATH = Environment.getExternalStorageDirectory()
        + File.separator + "FingerChat"
        + File.separator + "Voice" + File.separator;

    public static final String MSG_VIDEO_PATH = Environment.getExternalStorageDirectory()
        + File.separator + "FingerChat"
        + File.separator + "Video" + File.separator;

    //public static final String CIRCLE_PATH = FGEnvironment.getExternalStorageDirectory()
    //		+ File.separator + "FingerChat"
    //		+ File.separator + "circle" + File.separator;

    public static final String AUTHORITY_SETTED = "authority";//权限是否设置完成
    public static final String DATA_IS_LOADED = "data_is_loaded";//数据是否下载完成

    public static final String POS_BRUNCH = "pos_brunch";//记录园区代号
    public static final String POS_FACTROY = "pos_factroy";//记录工厂代号
    public static final String POS_PROCESS = "pos_process";//记录部门代号

    public static final String VIBRATIONNOTIFY = "vibrationnotify";//震动
    public static final String SOUNTONNOTIFY = "sountonnotify";//震动
    public static final String TICKER = "ticker";//提示
    public static final String MESSAGE_CARBONS = "message_carbons";//消息拓展

    public static final String PRIORITY = "priority";//优先级
    public static final String AUTO_RECONNECT = "auto_reconnect";//自动重连
    public static final String SCLIENTNOTIFY = "sclientnotify";//默认提醒
    public static final String USER_SPEAKER = "user_speaker";//使用扬声器
    public static final String FOREGROUND = "foreground";//优先级
    public static final String AUTO_START = "auto_start";//自动启动

    public static final String FRIEND_NAME = "friend_name";//是否开启调试

    public static final String NEW_INVITE = "new_invite";
    public static final String ROOM_NAME = "room_name";
    public static final String LEAVE_MUC = "leave_muc";
    public static final String IS_FRIST_IN = "is_frist_in";
    public static final String CIRCLE_THEME_PATH = "circle_theme_path";

    public static final String LOGOUT_BY_USER = "logout_by_user";

    public static final String AUTO_ACCEPT_INVITE = "auto_accept_invite";

    public static final String ACCOUNT_TYPE = "com.hnlens.fingerchat.AUTH";

    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String EMPLOY_INFO = "employ_info";
    public static final String IP = "fingechat.cn";
    public static final String PHONE = "phone";

    public static final String EX_KEY = "store_expression";


    private Context mContext;
    private static AppConfig instance;
    private SettingsPermissions settingsPermissions;

    protected AppConfig(Context context, String name) {
        super(context, name);
        if (mContext == null) {
            mContext = context;
        }
    }

    private AppConfig(String name) {
        super(ContextHelper.getContext(), name);
        if (mContext == null) {
            mContext = ContextHelper.getContext();
        }

    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在files目录下
            // fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key) {
            props.remove(k);
        }
        setProps(props);
    }


    public boolean containsProperty(String key) {

        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        INSTANCE.set(ps);
    }

    public Properties getProperties() {
        return INSTANCE.get();
    }

    public void setProperty(String key, String value) {
        INSTANCE.set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     */
    public String getProperty(String key) {
        String res = INSTANCE.get(key);
        return res;
    }

    public void removeProperty(String... key) {
        INSTANCE.remove(key);
    }

    /**
     * 获取App唯一标识
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) {
            info = new PackageInfo();
        }
        return info;
    }

    public synchronized SettingsPermissions getSettingsPermissions() {
        if (settingsPermissions == null) {
            settingsPermissions = new SettingsPermissions();
            settingsPermissions.load();
        }
        return settingsPermissions;
    }

    public static String getDefaultKey() {
        return getPartOne() + getParttwo() + getPartThree() + getPartFour();
    }

    private static String getPartOne() {
        return FGBuildConfig.FILE_KEY;
    }

    private static String getParttwo() {

        return AppConfig.getTwo(3, 6);
    }

    private static String getTwo(int a, int b) {
        StringBuilder builder = new StringBuilder(a);
        builder.append(b << a);
        for (int i = 1; i < a * b; i++) {
            if (i % 8 == 0) {
                builder.append(i);
            }
        }
        return builder.toString();
    }

    private static String getPartThree() {

//    return TDevice.getIMEI();
        return "";
    }

    private static String getPartFour() {
        return ContextHelper.getString(R.string.fingerchat_key);
    }

}
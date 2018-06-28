package com.lens.chatmodel.net;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.bean.body.VideoUploadEntity;
import com.lens.chatmodel.bean.body.VoiceUploadEntity;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.interf.IGetFileSizeListener;
import com.lensim.fingerchat.commons.global.BaseURL;
import com.lensim.fingerchat.commons.global.CommonEnum.ESearchTabs;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.ThreadUtils;
import com.lensim.fingerchat.data.ApiEnum.ERequestType;
import com.lensim.fingerchat.data.HttpChannel;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.observer.FileUploadObserver;
import com.lensim.fingerchat.data.request.RequestUploadFileBody;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.data.response.ret.RetResponse;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/25.
 */

public class HttpUtils {

    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    public static final MediaType MEDIA_TYPE_MP3 = MediaType.parse("audio/mpeg");
    public static final MediaType MEDIA_TYPE_GIF = MediaType.parse("image/gif");

    public static HttpUtils instance;

    public static HttpUtils getInstance() {
        if (instance == null) {
            instance = new HttpUtils();
        }
        return instance;
    }

    /*
      * 上传图片
      * */
    public void uploadImageProgress(String path, final EUploadFileType type,
        final IUploadListener listener) {
        File file = new File(path);
        if (file == null || !file.exists()) {
            listener.onFailed();
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addFormatData(builder, file, type);

        FileUploadObserver<ResponseBody> fileUploadObserver = new FileUploadObserver<ResponseBody>() {
            @Override
            public void onUpLoadSuccess(ResponseBody responseBody) {
                if (responseBody != null) {
                    listener.onSuccess(parseUploadResponseBody(responseBody, type));
                } else {
                    listener.onFailed();
                }
            }

            @Override
            public void onUpLoadFail(Throwable e) {
                listener.onFailed();

            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);

            }
        };

        Observable<ResponseBody> observable = HttpChannel.getInstance(ERequestType.UPLOAD)
            .getRetrofitService()
            .uploadImage(new RequestUploadFileBody(builder.build(), fileUploadObserver));

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fileUploadObserver);
    }

    /*
     * 上传图片,语音,视频
     * */
    public void uploadFileProgress(String path, final EUploadFileType type,
        final IUploadListener listener) {
        File file = configFile(path, type);
        if (file == null || !file.exists()) {
            listener.onFailed();
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addFormatData(builder, file, type);

        FileUploadObserver<ResponseBody> fileUploadObserver = new FileUploadObserver<ResponseBody>() {
            @Override
            public void onUpLoadSuccess(ResponseBody responseBody) {
                if (type == EUploadFileType.VIDEO) {
                    encryptVideo(path);
                }
                if (responseBody != null) {
                    listener.onSuccess(parseUploadResponseBody(responseBody, type));
                } else {
                    listener.onFailed();
                }
            }

            @Override
            public void onUpLoadFail(Throwable e) {
                if (type == EUploadFileType.VIDEO) {
                    encryptVideo(path);
                }
                listener.onFailed();

            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);

            }
        };
        Observable<ResponseBody> observable;
        switch (type) {
            case JPG:
            case GIF:
                observable = HttpChannel.getInstance(ERequestType.UPLOAD)
                    .getRetrofitService()
                    .uploadImage(new RequestUploadFileBody(builder.build(), fileUploadObserver));
                break;
            case VOICE:
                observable = HttpChannel.getInstance(ERequestType.UPLOAD)
                    .getRetrofitService()
                    .uploadVoice(new RequestUploadFileBody(builder.build(), fileUploadObserver));
                break;
            case VIDEO:
                observable = HttpChannel.getInstance(ERequestType.UPLOAD)
                    .getRetrofitService()
                    .uploadVideo(new RequestUploadFileBody(builder.build(), fileUploadObserver));
                break;
            case FILE:
                observable = null;
            default:
                observable = null;
        }
        if (observable == null) {
            return;
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fileUploadObserver);
    }

    private void addFormatData(MultipartBody.Builder builder, File file,
        EUploadFileType type) {
        if (type == EUploadFileType.JPG) {
            builder.addFormDataPart("photoContent", file.getName(),
                RequestBody.create(getMediaType(type), file));
        } else if (type == EUploadFileType.GIF) {
            builder.addFormDataPart("photoContent", file.getName(),
                RequestBody.create(getMediaType(type), file));
        } else if (type == EUploadFileType.VOICE) {
            if (file.getName().contains(".map3")) {
                builder.addFormDataPart("photoContent", file.getName(),
                    RequestBody.create(getMediaType(type), file));
            } else {
                builder.addFormDataPart("photoContent", file.getName() + ".mp3",
                    RequestBody.create(getMediaType(type), file));
            }
        } else if (type == EUploadFileType.VIDEO) {
            builder.addFormDataPart("photoContent", file.getName(),
                RequestBody.create(getMediaType(type), file));
        }
    }

    private MediaType getMediaType(EUploadFileType type) {
        switch (type) {
            case JPG:
                return MEDIA_TYPE_JPG;
            case GIF:
                return MEDIA_TYPE_GIF;
            case VOICE:
                return MEDIA_TYPE_MP3;
            case VIDEO:
                return MEDIA_TYPE_MP4;
            default:
                return MEDIA_TYPE_JPG;
        }
    }

    //加密视频
    private void encryptVideo(String path) {
        if (FileCache.checkVideoHasEncrypt(path)) {
            FileCache.getInstance().encrypt(path);
        }
    }

    //解密视频
    private void decryptVideo(String path) {
        if (FileCache.checkVideoHasEncrypt(path)) {
            FileCache.getInstance().decrypt(path);
        }
    }


    private File configFile(String path, EUploadFileType type) {
        File file;
        switch (type) {
            case JPG:
                file = new File(path);
                break;
            case GIF:
                file = new File(path);
                break;
            case VIDEO:
                decryptVideo(path);
                file = new File(path);
                break;
            case VOICE:
                file = FileCache.getInstance().decryptVoice(path);
                break;
            default:
                file = new File(path);
                break;
        }
        return file;
    }


    private Object parseUploadResponseBody(ResponseBody response, EUploadFileType type) {
        try {
            String body = response.string();
            if (!TextUtils.isEmpty(body)) {
                JSONObject object = new JSONObject(body);
                if (object != null && object.has("Value")) {
                    String json = object.optString("Value");
                    if (!TextUtils.isEmpty(json)) {
                        switch (type) {
                            case JPG:
                                return ImageUploadEntity.fromJson(json);
                            case GIF:
                                return ImageUploadEntity.fromJson(json);
                            case VIDEO:
                                return VideoUploadEntity.fromJson(json);
                            case VOICE:
                                return VoiceUploadEntity.fromJson(json);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSearchType(ESearchTabs type) {
        int search;
        switch (type) {
            case DEFAULT:
                search = 0;
                break;
            case ACCOUT:
                search = 1;
                break;
            case NICK:
                search = 4;
                break;
            case PHONE_NUM:
                search = 2;
                break;
            case REAL_NAME:
                search = 4;
                break;
            case DEPARTMENT:
                search = 5;
                break;
            default:
                search = 0;
                break;
        }
        return search;
    }

    public void searchUserList(String info, ESearchTabs type, int page,
        IDataRequestListener listener) {
        try {
            JSONObject object = new JSONObject();
            object.put("keyword", info);
            JSONObject o = new JSONObject();
            o.put("pageIndex", page);
            o.put("pageSize", 10);
            object.put("page", o);
            object.put("queryType", String.valueOf(getSearchType(type)));
            RequestBody builder = RequestBody
                .create(MediaType.parse("application/json"), object.toString());

            Observable<ResponseBody> observable = HttpChannel.getInstance(ERequestType.SEARCH_USER)
                .getRetrofitService()
                .searchUserList(builder);
            HttpChannel.getObserverString(observable, listener);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo(String userId, IDataRequestListener listener) {
        Observable<ResponseBody> observable = HttpChannel.getInstance(ERequestType.SEARCH_USER)
            .getRetrofitService()
            .getUserInfo(userId);
        HttpChannel.getObserverString(observable, listener);
    }

    public void getUrlFileSize(final String paramUrl, final IGetFileSizeListener listener) {
        ThreadUtils.runInBackground(() -> {
            try {
                URL url = new URL(paramUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //关于下面一句, 参考: http://my.oschina.net/u/133352/blog/96582
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();

                //必须要服务器的HttpServletResponse.setContentLength(int), 否则下面的getContentLength()将返回-1
                int length = conn
                    .getContentLength(); //对应于HttpServletResponse.setContentLength(int)
                //long length1 = conn.getContentLengthLong(); //对应于HttpServletResponse.setContentLengthLong(long)
                String len = conn.getHeaderField("Content-Length");
                conn.disconnect();
                listener.getSize(len.equals(length + "") ? len : length + "");

            } catch (Exception e) {
                listener.getSize("0");
                e.printStackTrace();
            }
        });
    }

    /*
    * 获取用户权限
    * */
    public void getAuthorityById(String userId, IDataRequestListener listener) {
        String url = String.format(Route.URL_AUTHORITY_SUBJECT, userId);
        HttpChannel.getInstance().retrofitGetString(url, listener);
    }

    /*
    * sso登录
    * */
    public Observable<ResponseObject<SSOToken>> ssoLogin(String userId, String psw) {
        return HttpChannel.getInstance().getRetrofitService().SSOLogin(userId, psw, "android");
    }

    /**
     * 修改密码
     */
    public static Observable<ResponseObject> changePwd(String userId, String curPassword, String newPassword) {
        Map<String, String> map = new HashMap<>();
        map.put("userid", userId);
        map.put("curPassword", curPassword);
        map.put("newPassword", newPassword);
        return HttpChannel.getInstance().getRetrofitService().updatePassword(getRequestBody(map));
    }

    /*
    * sso登出
    * */
    public Observable<ResponseObject<SSOToken>> ssoLoginOut(String token) {
        return HttpChannel.getInstance().getRetrofitService().SSOLoginOut(token);
    }

    /*
     * 扫码授权登录
     * */
    public Observable<ResponseBody> acceptQRCodeLogin(String token, String appId, String codeId) {
        return HttpChannel.getInstance().getRetrofitService()
            .acceptQRCodeLogin(token, appId, codeId);
    }

    /*
     * 获取第三方信息
     * */
    public void getLoginInfo(String url, IDataRequestListener listener) {
        HttpChannel.getInstance().retrofitGetString(url, listener);
    }

    public void uploadAttachMessage(Map<String, String> params,
        IDataRequestListener listener) {

        Observable<ResponseBody> observable = HttpChannel.getInstance(ERequestType.MAIN)
            .getRetrofitService()
            .uploadAttackMessage(getRequestBody(params));
        HttpChannel.getObserverString(observable, listener);
    }

    public void uploadAttachTest(String userId, String title, String data,
        IDataRequestListener listener) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("UserID", userId);
        builder.addFormDataPart("FileTittle", title);
        builder.addFormDataPart("ChatFileJson", data);
        MultipartBody requestBody = builder.build();

        Observable<ResponseBody> observable = HttpChannel.getInstance(ERequestType.MAIN)
            .getRetrofitService()
            .uploadAttackMessage(requestBody);
        HttpChannel.getObserverString(observable, listener);
    }

    public void uploadAttach(String userId, String title, String data,
        IDataRequestListener listener) {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("UserID", userId);
        builder.addFormDataPart("FileTittle", title);
        builder.addFormDataPart("ChatFileJson", data);
        MultipartBody requestBody = builder.build();

        Request request = new Request.Builder()
            .url(BaseURL.BASE_URL + Route.URL_ATTACH_MESSAGES)
            .post(requestBody)
            .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.loadFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.loadSuccess(response);

            }
        });
    }

    private static RequestBody getRequestBody(Object object) {
        Gson gson = new Gson();
        String route = gson.toJson(object);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), route);
    }


    private static RequestBody getRequestBody(Map<String, String> map) {
        return RequestBody
            .create(MediaType.parse("application/json; charset=utf-8"), mapToJSON(map));
    }

    /**
     * 将Map转化为Json
     */
    private static String mapToJSON(Map<String, String> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }


}

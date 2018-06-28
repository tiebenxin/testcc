package com.lensim.fingerchat.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.lensim.fingerchat.commons.global.BaseURL;
import com.lensim.fingerchat.commons.global.Consts;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.data.ApiEnum.ERequestType;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.help_class.IProgressListener;
import com.lensim.fingerchat.data.observer.FileDownloadObserver;
import com.lensim.fingerchat.data.retrofit.converters.MGsonConverterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Http通道
 * Created by yangle on 2017/6/19.
 */

public class HttpChannel {

    private static final long DEFAULT_TIME_OUT = 12;
    private static CompositeDisposable compositeDisposable;
    private RetrofitService retrofitService;
    private static HttpChannel instance;
    OkHttpClient okHttpClient;
    static ERequestType mCurrentRequestType;//上次请求类型
    private Retrofit retrofit;


    private HttpChannel() {
        compositeDisposable = new CompositeDisposable();
        if (okHttpClient == null) {
            okHttpClient = getOkHttpClient();
        }
        // 初始化Retrofit
        retrofit = createRetrofit(ERequestType.DEFAULT);
        retrofitService = retrofit.create(RetrofitService.class);
    }

    private HttpChannel(ERequestType type) {
        compositeDisposable = new CompositeDisposable();
        if (okHttpClient == null) {
            okHttpClient = getOkHttpClient();
        }

        if (mCurrentRequestType == null) {
            retrofit = createRetrofit(type);
        } else {
            if (type == mCurrentRequestType) {
                if (retrofit == null) {
                    retrofit = createRetrofit(type);
                }
            } else {
                retrofit = createRetrofit(type);
            }
        }

        if (mCurrentRequestType == null) {
            retrofitService = retrofit.create(RetrofitService.class);
        } else {
            if (type == mCurrentRequestType) {
                if (retrofitService == null) {
                    retrofitService = retrofit.create(RetrofitService.class);
                }
            } else {
                retrofitService = retrofit.create(RetrofitService.class);
            }
        }
    }

    private Retrofit createRetrofit(ERequestType type) {
        Retrofit retrofit;
        switch (type) {
            case UPLOAD:
                retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL.BASE_URL_UPLOAD)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                    .client(okHttpClient) // 打印请求参数
                    .build();
                break;
            case SEARCH_USER:
                retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL.BASE_URL_SEARCH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                    .client(okHttpClient) // 打印请求参数
                    .build();
                break;
            case DEFAULT:
                retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL.BASE_URL_TEST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                    .client(okHttpClient) // 打印请求参数
                    .build();
                break;
            case MAIN:
                retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                    .client(okHttpClient) // 打印请求参数
                    .build();
                break;
            case MGSON:
                retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL.BASE_URL_TEST)
                    .addConverterFactory(MGsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                    .client(okHttpClient) // 打印请求参数
                    .build();
                break;
            case SSO_LOGIN:
                retrofit = new Retrofit.Builder()
                    .baseUrl(Route.SSO_HOST)
                    .addConverterFactory(MGsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                    .client(okHttpClient) // 打印请求参数
                    .build();
                break;
            default:
                retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL.BASE_URL_TEST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                    .client(okHttpClient) // 打印请求参数
                    .build();

        }
        return retrofit;
    }

    /**
     * 获取OkHttpClient
     * 用于打印请求参数
     *
     * @return OkHttpClient
     */
    private OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(Consts.DEBUG ?
            HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        // 定制OkHttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.addInterceptor(httpLoggingInterceptor);

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        return builder.build();
    }

    public static HttpChannel getInstance() {
        if (instance == null) {
            instance = new HttpChannel();
        }
        mCurrentRequestType = ERequestType.DEFAULT;
        return instance;
    }

    public static HttpChannel getInstance(ERequestType type) {
        if (mCurrentRequestType == null) {
            instance = new HttpChannel(type);
        } else {
            if (type == mCurrentRequestType) {
                if (instance == null) {
                    instance = new HttpChannel(type);
                }
            } else {
                instance = new HttpChannel(type);
            }
        }
        return instance;

    }


    public RetrofitService getRetrofitService() {
        return retrofitService;
    }


    public void clear() {
        if (null != compositeDisposable) {
            compositeDisposable.clear();
        }
    }

    public static void retrofitGetString(String url, final IDataRequestListener listener) {
        Observable<ResponseBody> observable = HttpChannel.getInstance().getRetrofitService()
            .getMethod(url);
        getObserverString(observable, listener);
    }

    public static void retrofitGetBytes(String url, final IProgressListener listener) {
        Observable<ResponseBody> observable = HttpChannel.getInstance().getRetrofitService()
            .downloadFileWithDynamicUrlAsync(url);
        getObserverBytes(observable, listener);
    }

    public static void retrofitDownLoad(String url, String savePath,
        final IDataRequestListener listener) {
        Observable<ResponseBody> observable = HttpChannel.getInstance().getRetrofitService()
            .downloadFileWithDynamicUrlAsync(url);
        getObserverDownload(observable, savePath, listener);
    }

    /**
     * 发送消息
     *
     * @param observable Observable<? extends BaseBean>
     */
    public static void getObserverString(Observable<ResponseBody> observable,
        final IDataRequestListener listener) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onNext(@NonNull ResponseBody responseBody) {
                    try {
                        if (null != responseBody) {
                            String result = responseBody.string();
                            if (!TextUtils.isEmpty(result)) {
                                listener.loadSuccess(result);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    listener.loadFailure(e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
    }

    /**
     * @param observable Observable<? extends BaseBean>
     */
    public static void getObserverBytes(Observable<ResponseBody> observable,
        final IProgressListener listener) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new FileDownloadObserver<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onSuccess(ResponseBody body) {
                    if (null != body) {
                        try {
                            byte[] bytes = body.bytes();
                            listener.onSuccess(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailed(Throwable e) {
                    listener.onFailed();
                }

                @Override
                public void onProgress(int progress) {
                    listener.progress(progress);
                }
            });
    }

    /**
     * 发送消息
     *
     * @param observable Observable<? extends BaseBean>
     */
    public static void getObserverDownload(Observable<ResponseBody> observable,
        final String savePath,
        final IDataRequestListener listener) {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onNext(@NonNull ResponseBody responseBody) {
                    if (null != responseBody) {
                        if (witeResponseBodyToDisk(responseBody, savePath)) {
                            listener.loadSuccess(true);
                        }
                    }
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    listener.loadFailure(e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
    }

    public static boolean witeResponseBodyToDisk(ResponseBody body, String path) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}

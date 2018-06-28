package com.lensim.fingerchat.commons.base;

import android.util.Log;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.reactivestreams.Subscription;

/**
 * date on 2018/2/27
 * author ll147996
 * describe
 */

public class FGConsumer {

    private final static String TAG = "FGConsumer";
    private boolean isShowProgress;
    private NetworkRequestListener listener;

    public FGConsumer setListener(NetworkRequestListener networkRequestListener) {
        listener = networkRequestListener;
        return this;
    }

    public FGConsumer() {
        //默认显示Progress
        this(true);
    }

    public FGConsumer(boolean isShowProgress) {
        this.isShowProgress = isShowProgress;
    }

    private final FlowableTransformer<?, ?> mTransformer =
        new FlowableTransformer() {
            @Override
            public Flowable apply(Flowable upstream) {
                return upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Subscription>() {
                        @Override
                        public void accept(Subscription subscription) throws Exception {
//                            Log.e("doOnSubscribe","在这可以进行网络检查");
                            if (listener != null) {
                                listener.start(isShowProgress);
                            }
                        }
                    });
            }
        };

    @SuppressWarnings("unchecked")
    public <T> FlowableTransformer<T, T> io_main() {
        return (FlowableTransformer<T, T>) mTransformer;
    }


    public final void cancel(Disposable disposable) {
        disposable.dispose();
        if (listener != null) {
            listener.end();
        }
    }

    public abstract static class NextConsumer<T> implements Consumer<T> {

        public NextConsumer<T> setListener(NetworkRequestListener listener) {
            this.listener = listener;
            return this;
        }

        private NetworkRequestListener listener;

        @Override
        public void accept(T response) throws Exception {
            onNext(response);
            if (listener != null) {
                listener.end();
            }
        }

        public abstract void onNext(T response);
    }

    public abstract static class ErrorConsumer implements Consumer<Throwable> {

        public void setListener(NetworkRequestListener listener) {
            this.listener = listener;
        }

        private NetworkRequestListener listener;

        @Override
        public void accept(Throwable e) throws Exception {
            onError(e);
            if (e instanceof SocketTimeoutException) {
                Log.e(TAG, "请求超时，请检查您的网络状态");
                if (listener != null) {
                    listener.interruptedNetwork();
                }
            } else if (e instanceof ConnectException) {
                Log.e(TAG, "网络中断，请检查您的网络状态");
                if (listener != null) {
                    listener.interruptedNetwork();
                }
            } else {
                Log.e(TAG, e.getMessage());
            }
            if (listener != null) {
                listener.end();
            }
        }

        public abstract void onError(Throwable e);
    }

}
